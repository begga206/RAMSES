package ramses;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Ramsesklasse emuliert eine RAM
 * 
 * @author Lukas Becker
 * @author Andreas Paul
 */
public class Ramses extends Thread {
	
	//----------------------------Konstanten-----------------------------------
	public static final int MAX_MEM = 255;
	public static final int MAX_INDEX = 5;
	public static final int MAX_INSTRUCTIONS = 2000;
	public static final int NEXT_INST = -1;
	public static final String OUTPUT = "OUTPUT";
	public static final String INSTRUCTION = "INSTRUCTION";
	
	public static final String A_ADD = "a <- a + ";
	public static final String A_SUB = "a <- a - ";
	public static final String A_MUL = "a <- a * ";
	public static final String A_DIV = "a <- a div ";
	public static final String A_MOD = "a <- a mod ";
	
	//--------------------------Fehlermeldungen--------------------------------
	public static final String ERROR_DIVISION_BY_ZERO = 
			"ERROR_DIVISION_BY_ZERO";
	public static final String ERROR_INDEX_OUT_OF_BOUNDS = 
			"ERROR_INDEX_OUT_OF_BOUNDS";
	public static final String ERROR_MEMORY_OUT_OF_BOUNDS = 
			"ERROR_MEMORY_OUT_OF_BOUNDS";
	public static final String ERROR_JUMP_INVALID = "ERROR_JUMP_INVALID";
	public static final String ERROR_NO_HALT_INST = "ERROR_NO_HALT_INST";
	public static final String ERROR_A_NOT_INIT = "ERROR_A_NOT_INIT";
	public static final String ERROR_MEM_NOT_INIT = "ERROR_MEM_NOT_INIT";
	public static final String ERROR_INDEX_NOT_INIT = "ERROR_INDEX_NOT_INIT";
	public static final String ERROR_REACHED_MAX_INST = 
			"ERROR_REACHED_MAX_INST";

	//---------------------------Attribute-------------------------------------
	/** Instruction Counter */
	private static int counter;
	/** Instruction Pointer */
	private int iP;
	/** Akkumulator */
	private Integer a;
	/** Index */
	private Integer[] i;
	/** Datenspeicher */
	private Integer[] s;
	/** Programmspeicher */
	private ArrayList<Instruction> p;
	/** Input */
	private Input[] input;
	/** Output */
	private int[] output;
	/** Tabelle */
	private ArrayList<ArrayList<String>> table;
	/** debug */
	private boolean debug = false;
	/** lock */
	private volatile boolean locked = true;
	/** Breakpoint */
	private volatile int breakpoint = 0;
	/** Internationalisierung */
	ResourceBundle messages = ResourceBundle.getBundle("ramses.MessagesBundle",
			Locale.getDefault());

	//----------------------------Konstruktor----------------------------------
	/**
	 * Konstruktor
	 * 
	 * @param input
	 * @param output
	 * @param p
	 * @throws LogicalErrorException
	 */
	public Ramses(Input[] input, int[] output, ArrayList<Instruction> p)
			throws LogicalErrorException {
		counter = 0;
		this.input = input;
		this.output = output;
		this.p = p;
		//Prüfen, ob das RAM Programm eine HALT Instruktion besitzt
		boolean hasHalt = false;
		for(int i = 0; i < p.size(); i++){
			if(p.get(i).getInstTag().equals(InstructionTag.HALT)){
				hasHalt = true;
				break;
			}
		}
		if(!hasHalt)
			throw new LogicalErrorException(messages.getString(
					ERROR_NO_HALT_INST));
		i = new Integer[MAX_INDEX];
		table = new ArrayList<ArrayList<String>>();
	}

	//-----------------------------Methoden------------------------------------
	/**
	 * Initialisiert den Datenspeicher
	 * 
	 * @throws LogicalErrorException
	 */
	private void initS() throws LogicalErrorException {
		int maxIndex = 0;
		// Initialisiere die Programmtabelle und füge Header hinzu
		table = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<>();
		table.add(header);
		header.add(messages.getString(INSTRUCTION));
		header.add("a");

		// Finde größten Index und erstelle Array mit diesem als max
		for (int i = 0; i < output.length; i++) {
			if (output[i] > maxIndex)
				maxIndex = output[i];
			fillTable("s[" + output[i] + "]", "s[" + output[i] + "]");
		}
		for (int j = 0; j < input.length; j++) {
			if (input[j].getIndex() > maxIndex)
				maxIndex = input[j].getIndex();
			fillTable("s[" + input[j].getIndex() + "]",
					"s[" + input[j].getIndex() + "]");
		}
		s = new Integer[maxIndex + 1];
		for (int i = 0; i < input.length; i++) {
			if (input[i].hasValue())
				s[input[i].getIndex()] = input[i].getValue();
		}
	}

	/**
	 * Startfunktion, um den Programmcode auszuführen
	 * 
	 * @throws LogicalErrorException
	 */
	public void run() {
		synchronized (this) {
			try {
				if (!debug)
					locked = true;
				//Instruktionsdurchläufe auf Null setzen
				counter = 0;
				//Instruktionszeiger auf erste Instruktion
				iP = 0;
				//Akku noch nicht initialisert
				a = null;
				//Datenspeicher initialisieren
				initS();
				//Indexregister initialisieren
				i = new Integer[MAX_INDEX];
				//Erste Instruktion ausführen
				process(p.get(iP));
				if (!debug) {
					sleep(100);// TODO: Lock klasse
					locked = false;
					wait();
				}
			} catch (LogicalErrorException e) {
				System.out.println(e);
			} catch (InterruptedException e) {
				System.out.println(e);
			} finally {
				locked = false;
			}
		}
	}

	/**
	 * Hauptfunktion zum verarbeiten der Instruktion
	 * 
	 * @param inst
	 *            Die zu verarbeitende Instruktion
	 * @throws LogicalErrorException
	 */
	private synchronized void process(Instruction inst)
			throws LogicalErrorException {
		//Im Debugmodus, RAMSES 'locken', um inkonsestente Daten zu verhindern
		if (debug)
			locked = true;
		//Instruktionszeiger auf nächste Instruktion zeigen lassen
		iP++;
		counter++;
		try { // Die Instruktion mit der richtigen Funktion aufrufen
			switch (inst.getInstTag()) {
			case ADD_A_IMM:
				addAImm(inst);
				break;
			case ADD_A_MEM:
				addAMem(inst);
				break;
			case ADD_A_MMEM:
				addAMmem(inst);
				break;
			case DIV_A_IMM:
				divAImm(inst);
				break;
			case DIV_A_MEM:
				divAMem(inst);
				break;
			case DIV_A_MMEM:
				divAMmem(inst);
				break;
			case HALT:
				halt(inst);
				locked = false;
				return;
			case IDX_DEC:
				idxDec(inst);
				break;
			case IDX_INC:
				idxInc(inst);
				break;
			case JUMP:
				jump(inst);
				break;
			case JUMP_EQ:
				jumpEq(inst);
				break;
			case JUMP_GE:
				jumpGe(inst);
				break;
			case JUMP_GT:
				jumpGt(inst);
				break;
			case JUMP_LE:
				jumpLe(inst);
				break;
			case JUMP_LT:
				jumpLt(inst);
				break;
			case JUMP_NE:
				jumpNe(inst);
				break;
			case LD_A_MMEM:
				ldAMmem(inst);
				break;
			case LD_MEM_REG:
				ldMemReg(inst);
				break;
			case LD_MMEM_A:
				ldMmemA(inst);
				break;
			case LD_REG_IMM:
				ldRegImm(inst);
				break;
			case LD_REG_MEM:
				ldRegMem(inst);
				break;
			case LD_REG_REG:
				ldRegReg(inst);
				break;
			case MOD_A_IMM:
				modAImm(inst);
				break;
			case MOD_A_MEM:
				modAMem(inst);
				break;
			case MOD_A_MMEM:
				modAMmem(inst);
				break;
			case MUL_A_IMM:
				mulAImm(inst);
				break;
			case MUL_A_MEM:
				mulAMem(inst);
				break;
			case MUL_A_MMEM:
				mulAMmem(inst);
				break;
			case SUB_A_IMM:
				subAImm(inst);
				break;
			case SUB_A_MEM:
				subAMem(inst);
				break;
			case SUB_A_MMEM:
				subAMmem(inst);
				break;
			default:
				break;
			}
			//Im Debugmodus: Nach Ausführen der Instruktion RAMSES 'entlocken'
			//und auf ein notify() des Users warten
			if (debug && (p.indexOf(inst) == breakpoint || 
					breakpoint == NEXT_INST)) {
				locked = false;
				wait();
			}
			//Falls zu viele Instruktionen ausgeführt wurden, z.B. aufgrund 
			//einer Endlosschleife, soll eine Exception geworfen werden
			if(counter == MAX_INSTRUCTIONS)
				throw new LogicalErrorException(
						messages.getString(ERROR_REACHED_MAX_INST));
			// nächste Instruktion starten
			process(p.get(iP));
		} catch (RuntimeException e) {
			System.out.println(e);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}

	//-------------------ARITHMETIK INSTRUKTIONEN------------------------------
	/**
	 * a <- a + imm
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException 
	 */
	private void addAImm(Instruction inst) throws LogicalErrorException {
		isAInit();
		a += inst.getP0();
		String out = p.indexOf(inst) + ": " + A_ADD + inst.getP0();
		addRow(out);
		fillTable("a", Integer.toString(a));
	}

	/**
	 * a <- a + mem
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException 
	 */
	private void addAMem(Instruction inst) throws LogicalErrorException {
		isAInit();
		isMemInit(inst.getP0());
		a += s[inst.getP0()];
		String out = p.indexOf(inst) + ": " + A_ADD + "s[" + inst.getP0() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));
	}

	/**
	 * a <- a + mmem
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void addAMmem(Instruction inst) throws LogicalErrorException {
		isAInit();
		isIInit(inst.getP0());
		isMemInit(i[inst.getP0()] + inst.getP1());
		a += s[i[inst.getP0()] + inst.getP1()];
		String out = p.indexOf(inst) + ": " + A_ADD + "s[i" + inst.getP0()
				+ "+" + inst.getP1() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));
	}

	/**
	 * a <- a div imm
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void divAImm(Instruction inst) throws LogicalErrorException {
		isAInit();
		if (inst.getP0() != 0) {
			a = a / inst.getP0();
			String out = p.indexOf(inst) + ": " + A_DIV + inst.getP0();
			addRow(out);
			fillTable("a", Integer.toString(a));
		} else
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_DIVISION_BY_ZERO));
	}

	/**
	 * a <- a div mem
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void divAMem(Instruction inst) throws LogicalErrorException {
		isAInit();
		isMemInit(inst.getP0());
		if (s[inst.getP0()] == 0)
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_DIVISION_BY_ZERO));
		a = a / s[inst.getP0()];
		String out = p.indexOf(inst) + ": " + A_DIV + "s[" + inst.getP0() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));
	}

	/**
	 * a <- a div mmem
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void divAMmem(Instruction inst) throws LogicalErrorException {
		isAInit();
		isIInit(inst.getP0());
		isMemInit(i[inst.getP0()] + inst.getP1());
		if (s[inst.getP0()] == 0)
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_DIVISION_BY_ZERO));
		a = a / s[i[inst.getP0()] + inst.getP1()];
		String out = p.indexOf(inst) + ": " + A_DIV + "s[i" + inst.getP0()
				+ "+" + inst.getP1() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));
	}

	/**
	 * a <- a sub imm
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException 
	 */
	private void subAImm(Instruction inst) throws LogicalErrorException {
		isAInit();
		a -= inst.getP0();
		String out = p.indexOf(inst) + ": " + A_SUB + inst.getP0();
		addRow(out);
		fillTable("a", Integer.toString(a));
	}

	/**
	 * a <- a sub mem
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException 
	 */
	private void subAMem(Instruction inst) throws LogicalErrorException {
		isAInit();
		isMemInit(inst.getP0());
		a -= s[inst.getP0()];
		String out = p.indexOf(inst) + ": " + A_SUB + "s[" + inst.getP0() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));
	}

	/**
	 * a <- a sub mmem
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void subAMmem(Instruction inst) throws LogicalErrorException {
		isAInit();
		isIInit(inst.getP0());
		isMemInit(i[inst.getP0()] + inst.getP1());
		a -= s[i[inst.getP0()] + inst.getP1()];
		String out = p.indexOf(inst) + ": " + A_SUB + "s[i" + inst.getP0()
				+ "+" + inst.getP1() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));

	}

	/**
	 * a <- a mod imm
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void modAImm(Instruction inst) throws LogicalErrorException {
		isAInit();
		if (inst.getP0() != 0) {
			a = a % inst.getP0();
			String out = p.indexOf(inst) + ": " + A_MOD + inst.getP0();
			addRow(out);
			fillTable("a", Integer.toString(a));
		} else
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_DIVISION_BY_ZERO));
	}

	/**
	 * a <- a mod mem
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void modAMem(Instruction inst) throws LogicalErrorException {
		isAInit();
		isMemInit(inst.getP0());
		if (s[inst.getP0()] != 0) {
			a = a % s[inst.getP0()];
			String out = p.indexOf(inst) + ": " + A_MOD + "s[" + inst.getP0()
					+ "]";
			addRow(out);
			fillTable("a", Integer.toString(a));
		} else
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_DIVISION_BY_ZERO));
	}

	/**
	 * a <- a mod mmem
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void modAMmem(Instruction inst) throws LogicalErrorException {
		isAInit();
		isIInit(inst.getP0());
		isMemInit(i[inst.getP0()] + inst.getP1());
		if (s[i[inst.getP0()] + inst.getP1()] == 0)
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_DIVISION_BY_ZERO));
		a = a % s[i[inst.getP0()] + inst.getP1()];
		String out = p.indexOf(inst) + ": " + A_MOD + "s[i" + inst.getP0()
				+ "+" + inst.getP1() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));
	}

	/**
	 * a <- a * imm
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void mulAImm(Instruction inst) throws LogicalErrorException {
		isAInit();
		a = a * inst.getP0();
		String out = p.indexOf(inst) + ": " + A_MUL + inst.getP0();
		addRow(out);
		fillTable("a", Integer.toString(a));
	}

	/**
	 * a <- a * mem
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException 
	 */
	private void mulAMem(Instruction inst) throws LogicalErrorException {
		isAInit();
		isMemInit(inst.getP0());
		a = a * s[inst.getP0()];
		String out = p.indexOf(inst) + ": " + A_MUL + "s[" + inst.getP0() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));
	}

	/**
	 * a <- a * mmem
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void mulAMmem(Instruction inst) throws LogicalErrorException {
		isAInit();
		isIInit(inst.getP0());
		isMemInit(i[inst.getP0()] + inst.getP1());
		a = a * s[i[inst.getP0()] + inst.getP1()];
		String out = p.indexOf(inst) + ": " + A_MUL + "s[i" + inst.getP0()
				+ "+" + inst.getP1() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));
	}

	//------------------------HALT INSTRUKTION---------------------------------
	/**
	 * halt Instruktion
	 * 
	 * @param inst
	 * @return
	 */
	private void halt(Instruction inst) {
		addRow(p.indexOf(inst) + ": HALT");
		for (int i = 0; i < output.length; i++) {
			fillTable("s[" + output[i] + "]", Integer.toString(s[output[i]]));
		}
	}

	//------------------------INDEX INSTRUKTIONEN------------------------------
	/**
	 * i <- i - 1
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void idxDec(Instruction inst) throws LogicalErrorException {
		isIInit(inst.getP0());
		i[inst.getP0()]--;
		String out = p.indexOf(inst) + ": i" + inst.getP0() + " <- i"
				+ inst.getP0() + " - 1";
		addRow(out);
		fillTable("i" + inst.getP0(), Integer.toString(i[inst.getP0()]));
	}

	/**
	 * i <- i + 1
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void idxInc(Instruction inst) throws LogicalErrorException {
		isIInit(inst.getP0());
		i[inst.getP0()]++;
		String out = p.indexOf(inst) + ": i" + inst.getP0() + " <- i"
				+ inst.getP0() + " + 1";
		addRow(out);
		fillTable("i" + inst.getP0(), Integer.toString(i[inst.getP0()]));
	}

	//-------------------------LADE INSTRUKTIONEN------------------------------
	/**
	 * reg <- imm
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void ldRegImm(Instruction inst) throws LogicalErrorException {
		if (inst.getP0() < -1 || inst.getP0() > i.length)
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_INDEX_OUT_OF_BOUNDS));

		String out;
		String reg = null;
		String regValue = null;
		String mem = "s[" + inst.getP1() + "]";
		if (inst.getP0() == -1) {
			a = inst.getP1();
			reg = "a";
			regValue = Integer.toString(a);
		}
		if (inst.getP0() >= 0 && inst.getP0() < i.length) {
			i[inst.getP0()] = inst.getP1();
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		out = p.indexOf(inst) + ": " + reg + " <- " + mem;
		addRow(out);
		fillTable(reg, regValue);
	}

	/**
	 * reg <- mem
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void ldRegMem(Instruction inst) throws LogicalErrorException {
		isMemInit(inst.getP1());
		if (inst.getP0() < -1 || inst.getP0() > i.length)
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_INDEX_OUT_OF_BOUNDS));

		String out;
		String reg = null;
		String regValue = null;
		String mem = "s[" + inst.getP1() + "]";
		if (inst.getP0() == -1) {
			a = s[inst.getP1()];
			reg = "a";
			regValue = Integer.toString(a);
		}
		if (inst.getP0() >= 0 && inst.getP0() < i.length) {
			i[inst.getP0()] = s[inst.getP1()];
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		out = p.indexOf(inst) + ": " + reg + " <- " + mem;
		addRow(out);
		fillTable(reg, regValue);
	}
	
	private void ldRegReg(Instruction inst) throws LogicalErrorException {
		if (inst.getP0() < -1 || inst.getP0() > i.length)
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_INDEX_OUT_OF_BOUNDS));
		String out;
		String regFrom;
		String regTo;
		String regToValue;
		if (inst.getP0() == -1) {
			regTo = "a";
			if(inst.getP1() == -1){
				isAInit();
				regFrom = "a";
			}else{
				isIInit(inst.getP1());
				a = i[inst.getP1()];
				regFrom = "i" + inst.getP0();
			}
			regToValue = Integer.toString(a);
		}else{
			regTo = "i"+ Integer.toString(inst.getP0());
			if(inst.getP1() == -1){
				isAInit();
				regFrom = "a";
				i[inst.getP0()] = a;
			}else{
				isIInit(inst.getP1());
				i[inst.getP0()] = i[inst.getP1()];
				regFrom = "i" + inst.getP0();
			}
			regToValue = Integer.toString(i[inst.getP0()]);
		}
		out = p.indexOf(inst) + ": " + regTo + " <- " + regFrom;
		addRow(out);
		fillTable(regTo, regToValue);
	}

	/**
	 * a <- mmem
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void ldAMmem(Instruction inst) throws LogicalErrorException {
		isIInit(inst.getP0());
		isMemInit(i[inst.getP0()] + inst.getP1());

		a = s[i[inst.getP0()] + inst.getP1()];
		String out = p.indexOf(inst) + ": a <- s[i" + inst.getP0() + "+"
				+ inst.getP1() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));

	}

	/**
	 * mem <- reg
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void ldMemReg(Instruction inst) throws LogicalErrorException {
		String out;
		String reg = null;
		String mem = "s[" + inst.getP0() + "]";
		if (inst.getP1() == -1) {
			isAInit();
			s[inst.getP0()] = a;
			out = p.indexOf(inst) + ": s[" + inst.getP0() + "] <- a";
			reg = "a";
		}
		else{
			isIInit(inst.getP1());
			s[inst.getP0()] = i[inst.getP1()];
			reg = "i" + inst.getP1();
		}
		out = p.indexOf(inst) + ": " + mem + " <- " + reg;
		addRow(out);
		fillTable(mem, Integer.toString(s[inst.getP0()]));
	}

	/**
	 * mmem <- a
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void ldMmemA(Instruction inst) throws LogicalErrorException {
		isAInit();
		if (inst.getP0() >= 0 && inst.getP0() < i.length) {
			s[i[inst.getP0()] + inst.getP1()] = a;
			String out = p.indexOf(inst) + ": s[i" + inst.getP0() + "+"
					+ inst.getP1() + "] <- a";
			addRow(out);
			fillTable("s[i" + inst.getP0() + "+" + inst.getP1() + "]",
					Integer.toString(s[i[inst.getP0()] + inst.getP1()]));
		} else
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_INDEX_OUT_OF_BOUNDS));
	}

	//----------------------JUMP INSTRUKTIONEN---------------------------------
	/**
	 * jump x
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void jump(Instruction inst) throws LogicalErrorException {
		if (inst.getP0() >= 0 && inst.getP0() < p.size()) {
			iP = inst.getP0();
			addRow(p.indexOf(inst) + ": jump " + inst.getP0());
		} else
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_JUMP_INVALID));
	}

	/**
	 * if reg = 0 then jump x
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void jumpEq(Instruction inst) throws LogicalErrorException {
		if(inst.getP1() < 0 || inst.getP1() >= p.size())
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_JUMP_INVALID));
		String out;
		String reg = null;
		String regValue = null;
		if (inst.getP0() == -1) {
			isAInit();
			if (a == 0) {
				iP = inst.getP1();
			}
			reg = "a";
			regValue = Integer.toString(a);
		} else{
			isIInit(inst.getP0());
			if (i[inst.getP0()] == 0) {
				iP = inst.getP1();
			}
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		out = p.indexOf(inst) + ": if " + reg + " = 0 then jump "
				+ inst.getP1();
		addRow(out);
		fillTable(reg, regValue);
	}

	/**
	 * if reg >= 0 then jump x
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void jumpGe(Instruction inst) throws LogicalErrorException {
		if(inst.getP1() < 0 || inst.getP1() >= p.size())
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_JUMP_INVALID));
		String out;
		String reg = null;
		String regValue = null;
		if (inst.getP0() == -1) {
			isAInit();
			if (a >= 0) {
				iP = inst.getP1();
			}
			reg = "a";
			regValue = Integer.toString(a);
		} else{
			isIInit(inst.getP0());
			if (i[inst.getP0()] >= 0) {
				iP = inst.getP1();
			}
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		out = p.indexOf(inst) + ": if " + reg + " >= 0 then jump "
				+ inst.getP1();
		addRow(out);
		fillTable(reg, regValue);
	}

	/**
	 * if reg > 0 then jump x
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void jumpGt(Instruction inst) throws LogicalErrorException {
		if(inst.getP1() < 0 || inst.getP1() >= p.size())
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_JUMP_INVALID));
		String out;
		String reg = null;
		String regValue = null;
		if (inst.getP0() == -1) {
			isAInit();
			if (a > 0) {
				iP = inst.getP1();
			}
			reg = "a";
			regValue = Integer.toString(a);
		} else{
			isIInit(inst.getP0());
			if (i[inst.getP0()] > 0) {
				iP = inst.getP1();
			}
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}

		out = p.indexOf(inst) + ": if " + reg + " > 0 then jump "
				+ inst.getP1();
		addRow(out);
		fillTable(reg, regValue);
	}

	/**
	 * if reg <= 0 then jump x
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void jumpLe(Instruction inst) throws LogicalErrorException {
		if(inst.getP1() < 0 || inst.getP1() >= p.size())
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_JUMP_INVALID));
		String out;
		String reg = null;
		String regValue = null;
		if (inst.getP0() == -1) {
			isAInit();
			if (a <= 0) {
				iP = inst.getP1();
			}
			reg = "a";
			regValue = Integer.toString(a);
		} else{
			isIInit(inst.getP0());
			if (i[inst.getP0()] <= 0) {
				iP = inst.getP1();
			}
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		out = p.indexOf(inst) + ": if " + reg + " <= 0 then jump "
				+ inst.getP1();
		addRow(out);
		fillTable(reg, regValue);
	}

	/**
	 * if reg < 0 then jump x
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void jumpLt(Instruction inst) throws LogicalErrorException {
		if(inst.getP1() < 0 || inst.getP1() >= p.size())
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_JUMP_INVALID));
		String out;
		String reg = null;
		String regValue = null;
		if (inst.getP0() == -1) {
			isAInit();
			if (a < 0) {
				iP = inst.getP1();
			}
			reg = "a";
			regValue = Integer.toString(a);
		} else{
			isIInit(inst.getP0());
			if (i[inst.getP0()] < 0) {
				iP = inst.getP1();
			}
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		out = p.indexOf(inst) + ": if " + reg + " < 0 then jump "
				+ inst.getP1();
		addRow(out);
		fillTable(reg, regValue);
	}

	/**
	 * if reg != 0 then jump x
	 * 
	 * @param inst
	 * @return
	 * @throws LogicalErrorException
	 */
	private void jumpNe(Instruction inst) throws LogicalErrorException {
		if(inst.getP1() < 0 || inst.getP1() >= p.size())
			throw new LogicalErrorException(p.indexOf(inst),
					messages.getString(ERROR_JUMP_INVALID));
		String out;
		String reg = null;
		String regValue = null;
		if (inst.getP0() == -1) {
			isAInit();
			if (a != 0) {
				iP = inst.getP1();
			}
			reg = "a";
			regValue = Integer.toString(a);
		} else{
			isIInit(inst.getP0());
			if (i[inst.getP0()] != 0) {
				iP = inst.getP1();
			}
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		out = p.indexOf(inst) + ": if " + reg + " != 0 then jump "
				+ inst.getP1();
		addRow(out);
		fillTable(reg, regValue);
	}

	//------------------------Tabellenmethoden---------------------------------
	/**
	 * Fügt der Programmtabelle eine neue Reihe zu
	 * 
	 * @param instruction
	 *            Instruktion der Reihe
	 */
	private void addRow(String instruction) {
		ArrayList<String> newLine = new ArrayList<>(table.get(0).size());
		for (int i = 0; i < table.get(0).size(); i++)
			newLine.add("");
		newLine.set(0, instruction);
		table.add(newLine);
	}

	/**
	 * Fügt in der letzten Tabellenreihe den Wert "value" in der Spalte "id"
	 * hinzu
	 * 
	 * @param id
	 * @param value
	 */
	private void fillTable(String id, String value) {
		int index;
		ArrayList<String> line = table.get(table.size() - 1);

		// finde die Listenstelle für den Wert
		index = table.get(0).indexOf(id);
		// Wenn Listenstelle noch nicht vorhanden, eine erzeugen
		if (index < 0) {
			index = 1;
			// richtige spaltenlücke finden
			for (; index < table.get(0).size(); index++) {
				if (id.compareTo(table.get(0).get(index)) < 0)
					break;
			}
			// header einfügen
			table.get(0).add(index, id);
			// Leerstrings für die anderen Tabellenzeilen einfügen
			for (int i = 1; i < table.size(); i++)
				table.get(i).add(index, "");
		}
		for (String string : line) {
			if (string == null)
				string = "";
		}
		line.set(index, value);
	}

	//-------------------------Getter und Setter-------------------------------
	/**
	 * Gibt die Programmtabelle zurück
	 * 
	 * @return Programmtabelle
	 */
	public ArrayList<ArrayList<String>> getTable() {
		return table;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isLocked() {
		return locked;
	}
	
	public void isAInit() throws LogicalErrorException{
		if(a == null)
			throw new LogicalErrorException(iP-1, 
					messages.getString(ERROR_A_NOT_INIT));
	}
	
	public void isMemInit(int index) throws LogicalErrorException{
		if(s[index] == null)
			throw new LogicalErrorException(iP-1, 
					messages.getString(ERROR_MEM_NOT_INIT)+ index + "]");
	}
	
	public void isIInit(int index) throws LogicalErrorException{
		if (index < 0 || index >= i.length)
			throw new LogicalErrorException(iP - 1,
					messages.getString(ERROR_INDEX_OUT_OF_BOUNDS));
		if(i[index] == null)
			throw new LogicalErrorException(iP-1, 
					messages.getString(ERROR_INDEX_NOT_INIT + index));
	}

	public int getBreakpoint() {
		return breakpoint;
	}

	public void setBreakpoint(int breakpoint) {
		this.breakpoint = breakpoint;
	}
}