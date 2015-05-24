package ramses;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Ramsesklasse emuliert eine RAM
 * @author Lukas
 *
 */
public class Ramses {
	public static final int MAX_MEM = 255;
	public static final int MAX_INDEX = 5;
	public static final String OUTPUT = "#OUTPUT#";
	public static final String ERROR_DIVISION_BY_ZERO = "Division by Zero";
	public static final String ERROR_INDEX_OUT_OF_BOUNDS = "Index out of bounds";
	public static final String ERROR_MEMORY_OUT_OF_BOUNDS = "Memory out of bounds";
	public static final String ERROR_JUMP_INVALID = "Jump Invalid";
	
	public static final String A_ADD = "a <- a + ";
	public static final String A_SUB = "a <- a - ";
	public static final String A_MUL = "a <- a * ";
	public static final String A_DIV = "a <- a div ";
	public static final String A_MOD = "a <- a mod ";
	
	/** Instruction Counter */
	private static int counter;
	/** Instruction Pointer */
	private int iP;
	/** Akkumulator */
	private int a;
	/** Index */
	private int[] i;
	/** Datenspeicher */
	private int[] s;
	/** Programmspeicher */
	private ArrayList<Instruction> p;
	/** Input */
	private Input[] input;
	/** Output */
	private int[] output;
	/** Tabelle */
	private ArrayList<ArrayList<String>> table;
	
	/**
	 * Konstruktor
	 * @param input
	 * @param output
	 * @param p
	 * @throws LogicalErrorException
	 */
	public Ramses(Input[] input, int[] output, ArrayList<Instruction> p) throws LogicalErrorException{
		counter = 0;
		this.input = input;
		this.output = output;
		this.p = p;
		a = 0;
		i = new int[MAX_INDEX];
		table = new ArrayList<ArrayList<String>>();
	}
	
	/**
	 * Initialisiert den Datenspeicher
	 * @throws LogicalErrorException
	 */
	private void initS() throws LogicalErrorException{
		int maxIndex = 0;
		//Initialisiere die Programmtabelle und füge Header hinzu
		table = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<>();
		table.add(header);
		header.add("Instruction");
		header.add("a");
		
		//Finde größten Index und erstelle Array mit diesem als max
		for(int i = 0; i < output.length; i++){
			if(output[i] > maxIndex)
				maxIndex = output[i];
			fillTable("s["+output[i]+"]", "s["+output[i]+"]");
		}
		for(int j = 0; j < input.length; j++){
			if(input[j].getIndex() > maxIndex)
				maxIndex = input[j].getIndex();
			fillTable("s["+input[j].getIndex()+"]", "s["+input[j].getIndex()+"]");
		}	
		s = new int[maxIndex+1];
		for(int i = 0; i < input.length; i++){
			if(input[i].hasValue())
				s[input[i].getIndex()] = input[i].getValue();
		}
	}
	
	/**
	 * Startfunktion, um den Programmcode auszuführen
	 * @throws LogicalErrorException
	 */
	public void start() throws LogicalErrorException{
		counter = 0;
		iP = 0;
		a = 0;
		initS();
		i = new int[MAX_INDEX];
		process(p.get(0));
	}
	
	/**
	 *  Hauptfunktion zum verarbeiten der Instruktion
	 * @param inst Die zu verarbeitende Instruktion
	 */
	private void process(Instruction inst){
		iP++;
		counter++;
		try {		//Die Instruktion mit der richtigen Funktion aufrufen
			switch(inst.getInstTag()){
			case ADD_A_IMM:
				System.out.println(addAImm(inst));
				break;
			case ADD_A_MEM:
				System.out.println(addAMem(inst));
				break;
			case ADD_A_MMEM:
				System.out.println(addAMmem(inst));
				break;
			case DIV_A_IMM:
				System.out.println(divAImm(inst));
				break;
			case DIV_A_MEM:
				System.out.println(divAMem(inst));
				break;
			case DIV_A_MMEM:
				System.out.println(divAMmem(inst));
				break;
			case HALT:
				System.out.println(halt(inst));
				return;
			case IDX_DEC:
				System.out.println(idxDec(inst));
				break;
			case IDX_INC:
				System.out.println(idxInc(inst));
				break;
			case JUMP:
				System.out.println(jump(inst));
				break;
			case JUMP_EQ:
				System.out.println(jumpEq(inst));
				break;
			case JUMP_GE:
				System.out.println(jumpGe(inst));
				break;
			case JUMP_GT:
				System.out.println(jumpGt(inst));
				break;
			case JUMP_LE:
				System.out.println(jumpLe(inst));
				break;
			case JUMP_LT:
				System.out.println(jumpLt(inst));
				break;
			case JUMP_NE:
				System.out.println(jumpNe(inst));
				break;
			case LD_A_MMEM:
				System.out.println(ldAMmem(inst));
				break;
			case LD_MEM_REG:
				System.out.println(ldMemReg(inst));
				break;
			case LD_MMEM_A:
				System.out.println(ldMmemA(inst));
				break;
			case LD_REG_IMM:
				System.out.println(ldRegImm(inst));
				break;
			case LD_REG_MEM:
				System.out.println(ldRegMem(inst));
				break;
			case MOD_A_IMM:
				System.out.println(modAImm(inst));
				break;
			case MOD_A_MEM:
				System.out.println(modAMem(inst));
				break;
			case MOD_A_MMEM:
				System.out.println(modAMmem(inst));
				break;
			case MUL_A_IMM:
				System.out.println(mulAImm(inst));
				break;
			case MUL_A_MEM:
				System.out.println(mulAMem(inst));
				break;
			case MUL_A_MMEM:
				System.out.println(mulAMmem(inst));
				break;
			case SUB_A_IMM:
				System.out.println(subAImm(inst));
				break;
			case SUB_A_MEM:
				System.out.println(subAMem(inst));
				break;
			case SUB_A_MMEM:
				System.out.println(subAMmem(inst));
				break;
			default:
				break;
			}
			//nächste Instruktion starten
			process(p.get(iP));
		} catch (RuntimeException e) {
			System.out.println(e);
		}
	}
	
////////////////////ARITHMETIK INSTRUKTIONEN///////////////////////////////////
	
	/**
	 * a <- a + imm
	 * @param inst
	 * @return
	 */
	private String addAImm(Instruction inst){
		a += inst.getP0();
		String out = p.indexOf(inst) + ": " + A_ADD + inst.getP0();
		addRow(out);
		fillTable("a", Integer.toString(a));
		return OUTPUT + out + "\t(a = " + a + ")";
	}
	
	/**
	 * a <- a + mem
	 * @param inst
	 * @return
	 */
	private String addAMem(Instruction inst){
		a += s[inst.getP0()];
		String out = p.indexOf(inst) +": " + A_ADD + "s[" + inst.getP0() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));
		return OUTPUT + out +" = " + s[inst.getP0()] + ", a = " + a + ")";
	}
	
	/**
	 * a <- a + mmem
	 * @param inst
	 * @return
	 */
	private String addAMmem(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a += s[i[inst.getP0()]+inst.getP1()];
			String out = p.indexOf(inst) +": " + A_ADD + "s[i" + inst.getP0() + "+" + inst.getP1() + "]"; 
			addRow(out);
			fillTable("a", Integer.toString(a));
			return OUTPUT + out + 
					"\t(s[i" + inst.getP0()+ "+" + inst.getP1() +"] = " + s[inst.getP0()+inst.getP1()] + ", a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	/**
	 * a <- a div imm
	 * @param inst
	 * @return
	 */
	private String divAImm(Instruction inst){
		if (inst.getP0() != 0){
			a = a / inst.getP0();
			String out = p.indexOf(inst) + ": " + A_DIV + inst.getP0();
			addRow(out);
			fillTable("a", Integer.toString(a));
			return OUTPUT + out + "\t(a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
	}
	
	/**
	 * a <- a div mem
	 * @param inst
	 * @return
	 */
	private String divAMem(Instruction inst){
		if (s[inst.getP0()] == 0)
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
		a = a / s[inst.getP0()];
		String out = p.indexOf(inst) +": " + A_DIV + "s[" + inst.getP0() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));
		return OUTPUT + out +" = " + s[inst.getP0()] + ", a = " + a + ")";
	}
	
	/**
	 * a <- a div mmem
	 * @param inst
	 * @return
	 */
	private String divAMmem(Instruction inst){
		if (s[inst.getP0()] == 0)
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a = a / s[i[inst.getP0()]+inst.getP1()];
			String out = p.indexOf(inst) +": " + A_DIV + "s[i" + inst.getP0() + "+" + inst.getP1() + "]"; 
			addRow(out);
			fillTable("a", Integer.toString(a));
			return OUTPUT + out + 
					"\t(s[i" + inst.getP0()+ "+" + inst.getP1() +"] = " + s[inst.getP0()+inst.getP1()] + ", a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	/**
	 * a <- a sub imm
	 * @param inst
	 * @return
	 */
	private String subAImm(Instruction inst){
		a -= inst.getP0();
		String out = p.indexOf(inst) + ": " + A_SUB + inst.getP0();
		addRow(out);
		fillTable("a", Integer.toString(a));
		return OUTPUT + out + "\t(a = " + a + ")";
	}
	
	/**
	 * a <- a sub mem
	 * @param inst
	 * @return
	 */
	private String subAMem(Instruction inst){
		a -= s[inst.getP0()];
		String out = p.indexOf(inst) +": " + A_SUB + "s[" + inst.getP0() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));
		return OUTPUT + out +" = " + s[inst.getP0()] + ", a = " + a + ")";
	}
	
	/**
	 * a <- a sub mmem
	 * @param inst
	 * @return
	 */
	private String subAMmem(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a -= s[i[inst.getP0()]+inst.getP1()];
			String out = p.indexOf(inst) +": " + A_SUB + "s[i" + inst.getP0() + "+" + inst.getP1() + "]"; 
			addRow(out);
			fillTable("a", Integer.toString(a));
			return OUTPUT + out + 
					"\t(s[i" + inst.getP0()+ "+" + inst.getP1() +"] = " + s[inst.getP0()+inst.getP1()] + ", a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	/**
	 * a <- a mod imm
	 * @param inst
	 * @return
	 */
	private String modAImm(Instruction inst){
		if (inst.getP0() != 0){
			a = a % inst.getP0();
			String out = p.indexOf(inst) + ": " + A_MOD + inst.getP0();
			addRow(out);
			fillTable("a", Integer.toString(a));
			return OUTPUT + out + "\t(a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
	}
	
	/**
	 * a <- a mod mem
	 * @param inst
	 * @return
	 */
	private String modAMem(Instruction inst){
		if (s[inst.getP0()] != 0){
			a = a % s[inst.getP0()];
			String out = p.indexOf(inst) +": " + A_MOD + "s[" + inst.getP0() + "]";
			addRow(out);
			fillTable("a", Integer.toString(a));
			return OUTPUT + out +" = " + s[inst.getP0()] + ", a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
	}
	
	/**
	 * a <- a mod mmem
	 * @param inst
	 * @return
	 */
	private String modAMmem(Instruction inst){
		if(s[i[inst.getP0()]+inst.getP1()] == 0)
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a = a % s[i[inst.getP0()]+inst.getP1()];
			String out = p.indexOf(inst) +": " + A_MOD + "s[i" + inst.getP0() + "+" + inst.getP1() + "]"; 
			addRow(out);
			fillTable("a", Integer.toString(a));
			return OUTPUT + out + 
					"\t(s[i" + inst.getP0()+ "+" + inst.getP1() +"] = " + s[inst.getP0()+inst.getP1()] + ", a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	/**
	 * a <- a * imm
	 * @param inst
	 * @return
	 */
	private String mulAImm(Instruction inst){
		a = a * inst.getP0();
		String out = p.indexOf(inst) + ": " + A_MUL + inst.getP0();
		addRow(out);
		fillTable("a", Integer.toString(a));
		return OUTPUT + out + "\t(a = " + a + ")";
	}
	
	/**
	 * a <- a * mem
	 * @param inst
	 * @return
	 */
	private String mulAMem(Instruction inst){
		a = a * s[inst.getP0()];
		String out = p.indexOf(inst) +": " + A_MUL + "s[" + inst.getP0() + "]";
		addRow(out);
		fillTable("a", Integer.toString(a));
		return OUTPUT + out +" = " + s[inst.getP0()] + ", a = " + a + ")";
	}
	
	/**
	 * a <- a * mmem
	 * @param inst
	 * @return
	 */
	private String mulAMmem(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a = a * s[i[inst.getP0()]+inst.getP1()];
			String out = p.indexOf(inst) +": " + A_MUL + "s[i" + inst.getP0() + "+" + inst.getP1() + "]"; 
			addRow(out);
			fillTable("a", Integer.toString(a));
			return OUTPUT + out + 
					"\t(s[i" + inst.getP0()+ "+" + inst.getP1() +"] = " + s[inst.getP0()+inst.getP1()] + ", a = " + a + ")";
		}	
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
///////////////////////////////HALT INSTRUKTION////////////////////////////////
	
	/**
	 * halt Instruktion
	 * @param inst
	 * @return
	 */
	private String halt(Instruction inst){
		String string = OUTPUT + p.indexOf(inst) + ": Halt\n" + OUTPUT + " : ";
		for(int i=0; i<output.length; i++){
			string += "s[" + output[i] + "] = " + s[output[i]] + "\t";
		}
		string += "\n#SUCCESS# your program terminated without machine errors";
		string += "\n#INFO# random-access-machine halted in instruction " + iP + " after " + counter + " steps.";
		addRow(p.indexOf(inst)+": HALT");
		return string;
	}
	
////////////////////////////INDEX INSTRUKTIONEN////////////////////////////////
	
	/**
	 * i <- i - 1
	 * @param inst
	 * @return
	 */
	private String idxDec(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			i[inst.getP0()]--;
			String out = p.indexOf(inst) + ": i" + inst.getP0() + " <- i" + inst.getP0() + " - 1";
			addRow(out);
			fillTable("i"+inst.getP0(),Integer.toString(i[inst.getP0()]));
			return OUTPUT + out + "\t(i"+ inst.getP0() + "=" + i[inst.getP0()] +")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	/**
	 * i <- i + 1
	 * @param inst
	 * @return
	 */
	private String idxInc(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			i[inst.getP0()]++;
			String out = p.indexOf(inst) + ": i" + inst.getP0() + " <- i" + inst.getP0() + " + 1";
			addRow(out);
			fillTable("i"+inst.getP0(),Integer.toString(i[inst.getP0()]));
			return OUTPUT + out + "\t(i"+ inst.getP0() + "=" + i[inst.getP0()] +")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}

	
//////////////////////////////LADE INSTRUKTIONEN///////////////////////////////
	
	/**
	 * reg <- imm
	 * @param inst
	 * @return
	 */
	private String ldRegImm(Instruction inst){
		if(inst.getP0() < -1 || inst.getP0() > i.length)
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
		
		String out;
		String reg = null;
		String regValue = null;
		String mem = "s[" + inst.getP1() + "]";
		if (inst.getP0() == -1){
			a = inst.getP1();
			reg = "a";
			regValue = Integer.toString(a);
		}	
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			i[inst.getP0()] = inst.getP1();	
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		out = p.indexOf(inst) + ": " + reg + " <- " + mem;
		addRow(out);
		fillTable(reg,regValue);
		return OUTPUT + out;
	}
	
	/**
	 * reg <- mem
	 * @param inst
	 * @return
	 */
	private String ldRegMem(Instruction inst){
		if(inst.getP0() < -1 || inst.getP0() > i.length)
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
		
		String out;
		String reg = null;
		String regValue = null;
		String mem = "s[" + inst.getP1() + "]";
		if (inst.getP0() == -1){
			a = s[inst.getP1()];
			reg = "a";
			regValue = Integer.toString(a);
		}	
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			i[inst.getP0()] = s[inst.getP1()];
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		out = p.indexOf(inst) + ": " + reg + " <- " + mem;
		addRow(out);
		fillTable(reg,regValue);
		return OUTPUT + out;
	}
	
	/**
	 * a <- mmem
	 * @param inst
	 * @return
	 */
	private String ldAMmem(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a = s[i[inst.getP0()]+inst.getP1()];
			String out = p.indexOf(inst) + ": a <- s[i" + inst.getP0() + "+" + inst.getP1() + "]";
			addRow(out);
			fillTable("a", Integer.toString(a));
			return OUTPUT + out;
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	/**
	 * mem <- reg
	 * @param inst
	 * @return
	 */
	private String ldMemReg(Instruction inst){
		if(inst.getP0() < -1 || inst.getP0() > i.length)
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
		
		String out;
		String reg = null;
		String regValue = null;
		String mem = "s[" + inst.getP0() + "]";
		if (inst.getP1() == -1){
			s[inst.getP0()] = a;
			out = p.indexOf(inst) + ": s[" + inst.getP0() + "] <- a";
			reg = "a";
			regValue = Integer.toString(a);
		}
		if (inst.getP1() >= 0 && inst.getP1() < i.length){
			s[inst.getP0()] = i[inst.getP1()];
			reg = "i" + inst.getP1();
			regValue = Integer.toString(i[inst.getP1()]);
		}
		out = p.indexOf(inst) + ": " + mem + " <- " + reg;
		addRow(out);
		fillTable(mem,Integer.toString(s[inst.getP0()]));
		return OUTPUT + out;
	}
	
	/**
	 * mmem <- a
	 * @param inst
	 * @return
	 */
	private String ldMmemA(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			s[i[inst.getP0()]+inst.getP1()] = a;
			String out = p.indexOf(inst) + ": s[i" + inst.getP0() + "+" + inst.getP1() + "] <- a";
			addRow(out);
			fillTable("s[i" + inst.getP0() + "+" + inst.getP1() + "]", Integer.toString(s[i[inst.getP0()]+inst.getP1()]));
			return OUTPUT + out + "\t(a=" + a + ")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
///////////////////////////////JUMP INSTRUKTIONEN//////////////////////////////
	
	/**
	 * jump x
	 * @param inst
	 * @return
	 */
	private String jump(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < p.size()){
			iP = inst.getP0();
			addRow(p.indexOf(inst) + ": jump " + inst.getP0());
			return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP0();
		}	
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	/**
	 * if reg = 0 then jump x
	 * @param inst
	 * @return
	 */
	private String jumpEq(Instruction inst){
		String out;
		String reg = null;
		String regValue = null;
		if (inst.getP0() == -1){
			if (a == 0){
				iP = inst.getP1();
			}
			reg = "a";
			regValue = Integer.toString(a);
		}		
		else if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (i[inst.getP0()] == 0){
				iP = inst.getP1();
			}
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
		
		out = p.indexOf(inst) + ": if " + reg + " = 0 then jump " + inst.getP1();
		addRow(out);
		fillTable(reg, regValue);
		return OUTPUT + out;
	}
	
	/**
	 * if reg >= 0 then jump x
	 * @param inst
	 * @return
	 */
	private String jumpGe(Instruction inst){
		String out;
		String reg = null;
		String regValue = null;
		if (inst.getP0() == -1){
			if (a >= 0){
				iP = inst.getP1();
			}
			reg = "a";
			regValue = Integer.toString(a);
		}		
		else if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() >= 0){
				iP = inst.getP1();
			}
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
		
		out = p.indexOf(inst) + ": if " + reg + " >= 0 then jump " + inst.getP1();
		addRow(out);
		fillTable(reg, regValue);
		return OUTPUT + out;
	}
	
	/**
	 * if reg > 0 then jump x
	 * @param inst
	 * @return
	 */
	private String jumpGt(Instruction inst){
		String out;
		String reg = null;
		String regValue = null;
		if (inst.getP0() == -1){
			if (a > 0){
				iP = inst.getP1();
			}
			reg = "a";
			regValue = Integer.toString(a);
		}		
		else if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() > 0){
				iP = inst.getP1();
			}
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
		
		out = p.indexOf(inst) + ": if " + reg + " > 0 then jump " + inst.getP1();
		addRow(out);
		fillTable(reg, regValue);
		return OUTPUT + out;
	}
	
	/**
	 * if reg <= 0 then jump x
	 * @param inst
	 * @return
	 */
	private String jumpLe(Instruction inst){
		String out;
		String reg = null;
		String regValue = null;
		if (inst.getP0() == -1){
			if (a <= 0){
				iP = inst.getP1();
			}
			reg = "a";
			regValue = Integer.toString(a);
		}		
		else if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() <= 0){
				iP = inst.getP1();
			}
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
		
		out = p.indexOf(inst) + ": if " + reg + " <= 0 then jump " + inst.getP1();
		addRow(out);
		fillTable(reg, regValue);
		return OUTPUT + out;
	}
	
	/**
	 * if reg < 0 then jump x
	 * @param inst
	 * @return
	 */
	private String jumpLt(Instruction inst){
		String out;
		String reg = null;
		String regValue = null;
		if (inst.getP0() == -1){
			if (a < 0){
				iP = inst.getP1();	
			}
			reg = "a";
			regValue = Integer.toString(a);
		}		
		else if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() < 0){
				iP = inst.getP1();
			}
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
		
		out = p.indexOf(inst) + ": if " + reg + " < 0 then jump " + inst.getP1();
		addRow(out);
		fillTable(reg, regValue);
		return OUTPUT + out;
	}
	
	/**
	 * if reg != 0 then jump x
	 * @param inst
	 * @return
	 */
	private String jumpNe(Instruction inst){
		String out;
		String reg = null;
		String regValue = null;
		if (inst.getP0() == -1){
			if (a != 0){
				iP = inst.getP1();		
			}
			reg = "a";
			regValue = Integer.toString(a);
		}		
		else if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() != 0){
				iP = inst.getP1();
			}
			reg = "i" + inst.getP0();
			regValue = Integer.toString(i[inst.getP0()]);
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
		
		out = p.indexOf(inst) + ": if " + reg + " != 0 then jump " + inst.getP1();
		addRow(out);
		fillTable(reg, regValue);
		return OUTPUT + out;
	}

////////////////////TABELLEN FUNKTIONEN////////////////////////////////////////
	
	/**
	 * Fügt der Programmtabelle eine neue Reihe zu
	 * @param instruction Instruktion der Reihe
	 */
	private void addRow(String instruction){
		ArrayList<String> newLine = new ArrayList<>(table.get(0).size());
		for(int i = 0; i < table.get(0).size(); i++)
			newLine.add("");
		newLine.set(0, instruction);
		table.add(newLine);
	}
	
	/**
	 * Fügt in der letzten Tabellenreihe den Wert "value" in der Spalte "id" hinzu
	 * @param id
	 * @param value
	 */
	private void fillTable(String id, String value){
		 int index;
		 String cmpString = "";
		 ArrayList<String> line = table.get(table.size()-1);
		 
		 index = table.get(0).indexOf(id);	//finde die Listenstelle für den Wert
		 if(index < 0){						//Wenn Listenstelle noch nicht vorhanden, eine erzeugen
			 index = 1;
			 while(id.compareTo(cmpString) > 0 && index < table.get(0).size()){		//index suchen
				 cmpString = table.get(0).get(index);
				 index++;
			 }
			 table.get(0).add(index,id);				//header einfügen
			 for(int i = 1; i < table.size(); i++)		//Leerstrings für die anderen Tabellenzeilen einfügen
				 table.get(i).add(index, "");
		 }
		 
	
		 for(String string : line){
			 if(string == null)
				 string = "";
		 }
		 line.set(index, value);
	}
	
	/**
	 * Gibt die Programmtabelle zurück
	 * @return Programmtabelle
	 */
	public ArrayList<ArrayList<String>> getTable(){
		return table;
	}
}