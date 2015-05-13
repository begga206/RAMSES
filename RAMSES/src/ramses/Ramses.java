package ramses;

import java.util.ArrayList;
import java.util.Scanner;

public class Ramses {
	public static final int MAX_MEM = 255;
	public static final int MAX_INDEX = 5;
	public static final String OUTPUT = "#OUTPUT#";
	public static final String ERROR_OUTPUT_IN_INPUT = "You cannot allocate the same data slot in INPUT and OUTPUT.";
	public static final String ERROR_DIVISION_BY_ZERO = "Division by Zero";
	public static final String ERROR_INDEX_OUT_OF_BOUNDS = "Index out of bounds";
	public static final String ERROR_MEMORY_OUT_OF_BOUNDS = "Memory out of bounds";
	public static final String ERROR_JUMP_INVALID = "Jump Invalid";
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
	
	
	public Ramses(Input[] input, int[] output, ArrayList<Instruction> p) throws LogicalErrorException{
		counter = 0;
		this.input = input;
		this.output = output;
		this.p = p;
		a = 0;
		i = new int[MAX_INDEX];
		initS();
	}
	
	private void initS() throws LogicalErrorException{
		int maxIndex = 0;
		
		for(int i = 0; i < output.length; i++){
			for(int j = 0; j < input.length; j++){
				if(output[i] == input[j].getIndex()){
					throw new LogicalErrorException(-2, ERROR_OUTPUT_IN_INPUT);
				}
				if(output[i] > maxIndex)
					maxIndex = output[i];
				if(input[j].getIndex() > maxIndex)
					maxIndex = input[j].getIndex();
			}	
		}
		s = new int[maxIndex+1];
		for(int i = 0; i < input.length; i++){
			if(input[i].hasValue())
				s[input[i].getIndex()] = input[i].getValue();
		}
	}
	
	public void start(){
		counter = 0;
		Scanner sc = new Scanner(System.in);
		for(int i = 0; i < input.length; i++){
			if(!input[i].hasValue()){
				System.out.print("s[" + input[i].getIndex() + "]:\t");
				s[input[i].getIndex()] = sc.nextInt();
			}
		}
		sc.close();
		process(p.get(0));
	}
	
	private void process(Instruction inst){
		iP++;
		counter++;
		try {
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
			process(p.get(iP));
		} catch (RuntimeException e) {
			System.out.println(e);
		}
	}
	
	private String addAImm(Instruction inst){
		a += inst.getP0();
		return OUTPUT + p.indexOf(inst) + ": a <- a + " + inst.getP0() + "\t(a = " + a + ")";
	}
	
	private String addAMem(Instruction inst){
		a += s[inst.getP0()];
		return OUTPUT + p.indexOf(inst) +": a <- a + s[" + inst.getP0() + "]\t(s[" + inst.getP0() +"] = " + s[inst.getP0()] + ", a = " + a + ")";
	}
	
	private String addAMmem(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a += s[i[inst.getP0()]+inst.getP1()];
			return OUTPUT + iP + ": a<-a + s[i" + inst.getP0() + "+" + inst.getP1() + 
					"]\t(s[i" + inst.getP0()+ "+" + inst.getP1() +"] = " + s[inst.getP0()+inst.getP1()] + ", a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String divAImm(Instruction inst){
		if (inst.getP0() != 0){
			a = a / inst.getP0();
			return OUTPUT + p.indexOf(inst) + ": a <- a div " + inst.getP0() + "(a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
	}
	
	private String divAMem(Instruction inst){
		if (s[inst.getP0()] == 0)
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
		else
			a = a / s[inst.getP0()];
			return OUTPUT + p.indexOf(inst) + ": a <- a div s[" + inst.getP0() + "]\t(s[" + inst.getP0() +"] = " + s[inst.getP0()] + ", a = " + a + ")";
	}
	
	private String divAMmem(Instruction inst){
		if (s[inst.getP0()] == 0)
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a = a / s[i[inst.getP0()]+inst.getP1()];
			return OUTPUT + p.indexOf(inst) + ": a <- a div s[i" + inst.getP0() + "+" + inst.getP1() + 
					"]\t(s[i" + inst.getP0()+ "+" + inst.getP1() +"] = " + s[inst.getP0()+inst.getP1()] + ", a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String subAImm(Instruction inst){
		a -= inst.getP0();
		return OUTPUT + p.indexOf(inst) + ": a <- a - " + inst.getP0() + "(a = " + a + ")";
	}
	
	private String subAMem(Instruction inst){
		a -= s[inst.getP0()];
		return OUTPUT + p.indexOf(inst) + ": a <- a - s[" + inst.getP0() + "]\t(s[" + inst.getP0() +"] = " + s[inst.getP0()] + ", a = " + a + ")";
	}
	
	private String subAMmem(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a -= s[i[inst.getP0()]+inst.getP1()];
			return OUTPUT + p.indexOf(inst) + ": a <- a - s[i" + inst.getP0() + "+" + inst.getP1() +
					"]\t(s[i" + inst.getP0()+ "+" + inst.getP1() +"] = " + s[inst.getP0()+inst.getP1()] + ", a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String modAImm(Instruction inst){
		if (inst.getP0() != 0){
			a = a % inst.getP0();
			return OUTPUT + p.indexOf(inst) + ": a <- a mod " + inst.getP0() + "(a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
	}
	
	private String modAMem(Instruction inst){
		if (s[inst.getP0()] != 0){
			a = a % s[inst.getP0()];
			return OUTPUT + p.indexOf(inst) + ": a <- a mod s[" + inst.getP0() + "]\t(s[" + inst.getP0() +"] = " + s[inst.getP0()] + ", a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
	}
	
	private String modAMmem(Instruction inst){
		if(s[i[inst.getP0()]+inst.getP1()] == 0)
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a = a % s[i[inst.getP0()]+inst.getP1()];
			return OUTPUT + p.indexOf(inst) + ": a <- a mod s[i" + inst.getP0() + "+" + inst.getP1() +
					"]\t(s[i" + inst.getP0()+ "+" + inst.getP1() +"] = " + s[inst.getP0()+inst.getP1()] + ", a = " + a + ")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String mulAImm(Instruction inst){
		a = a * inst.getP0();
		return OUTPUT + p.indexOf(inst) + ": a <- a * " + inst.getP0() + "(a = " + a + ")";
	}
	
	private String mulAMem(Instruction inst){
		a = a * s[inst.getP0()];
		return OUTPUT + p.indexOf(inst) + ": a <- a * s[" + inst.getP0() + "]\t(s[" + inst.getP0() +"] = " + s[inst.getP0()] + ", a = " + a + ")";
	}
	
	private String mulAMmem(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a = a * s[i[inst.getP0()]+inst.getP1()];
			return OUTPUT + p.indexOf(inst) + ": a <- a * s[i" + inst.getP0() + "+" + inst.getP1() +
					"]\t(s[i" + inst.getP0()+ "+" + inst.getP1() +"] = " + s[inst.getP0()+inst.getP1()] + ", a = " + a + ")";
		}	
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String halt(Instruction inst){
		String string = OUTPUT + p.indexOf(inst) + ": Halt\n" + OUTPUT + " : ";
		for(int i=0; i<output.length; i++){
			string += "s[" + output[i] + "] = " + s[output[i]] + "\t";
		}
		string += "\n#SUCCESS# your program terminated without machine errors";
		string += "\n#INFO# random-access-machine halted in instruction " + iP + " after " + counter + " steps.";
		return string;
	}
	
	private String idxDec(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			i[inst.getP0()]--;
			return OUTPUT + p.indexOf(inst) + ": i" + inst.getP0() + " <- i" + inst.getP0() + " - 1\t(i"+ inst.getP0() + "=" + i[inst.getP0()] +")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String idxInc(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			i[inst.getP0()]++;
			return OUTPUT + p.indexOf(inst) + ": i" + inst.getP0() + " <- i" + inst.getP0() + " + 1\t(i"+ inst.getP0() + "=" + i[inst.getP0()] +")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String jump(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() <= p.size()){
			iP = inst.getP0();
			return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP0();
		}	
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	private String ldRegImm(Instruction inst){
		if (inst.getP0() == -1){
			a = inst.getP1();
			return OUTPUT + p.indexOf(inst) + ": a <- " + inst.getP1();
		}	
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			i[inst.getP0()] = inst.getP1();	
			return OUTPUT + p.indexOf(inst) + ": i"+ inst.getP0() + " <- " + inst.getP1();
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String ldRegMem(Instruction inst){
		if (inst.getP0() == -1){
			a = s[inst.getP1()];
			return OUTPUT + p.indexOf(inst) + ": a <- s[" + inst.getP1() + "]";
		}	
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			i[inst.getP0()] = s[inst.getP1()];
			return OUTPUT + p.indexOf(inst) + ": i" + inst.getP0() + " <- s[" + inst.getP1() + "]";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String ldAMmem(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a = s[i[inst.getP0()]+inst.getP1()];
			return OUTPUT + p.indexOf(inst) + ": a <- s[i" + inst.getP0() + "+" + inst.getP1() + "]";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String ldMemReg(Instruction inst){
		if (inst.getP1() == -1){
			s[inst.getP0()] = a;
			return OUTPUT + p.indexOf(inst) + ": s[" + inst.getP0() + "] <- a\t(a=" + a +")";
		}
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			s[inst.getP0()] = i[inst.getP1()];
			return OUTPUT + p.indexOf(inst) + ": s[" + inst.getP0() + "] <- i" + 
					inst.getP1() + "\t(i" + inst.getP1() +"=" + i[inst.getP1()] + ")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String ldMmemA(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			s[i[inst.getP0()]+inst.getP1()] = a;
			return OUTPUT + p.indexOf(inst) +  ": s[i" + inst.getP0() + "+" + inst.getP1() + "] <- a\t(a=" + a + ")";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String jumpEq(Instruction inst){
		if (inst.getP0() == -1){
			if (a == 0){
				iP = inst.getP1();
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " genommen, weil a=0";	
			}
			else
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " nicht genommen, weil a!=0";
		}		
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (i[inst.getP0()] == 0){
				iP = inst.getP1();
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " genommen, weil i" + inst.getP0() + "=0";
			}
			else
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " nicht genommen, weil i" + inst.getP0() + "!=0";
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	private String jumpGe(Instruction inst){
		if (inst.getP0() == -1){
			if (a >= 0){
				iP = inst.getP1();
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " genommen, weil a>=0";	
			}
			else
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " nicht genommen, weil a<0";
		}		
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() >= 0){
				iP = inst.getP1();
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " genommen, weil i" + inst.getP0() + ">=0";
			}
			else
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " nicht genommen, weil i" + inst.getP0() + "<0";
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	private String jumpGt(Instruction inst){
		if (inst.getP0() == -1){
			if (a > 0){
				iP = inst.getP1();
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " genommen, weil a>0";	
			}
			else
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " nicht genommen, weil a<=0";
		}		
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() > 0){
				iP = inst.getP1();
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " genommen, weil i" + inst.getP0() + ">0";
			}
			else
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " nicht genommen, weil i" + inst.getP0() + "<=0";
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	private String jumpLe(Instruction inst){
		if (inst.getP0() == -1){
			if (a <= 0){
				iP = inst.getP1();
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " genommen, weil a<=0";
			}
			else
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " nicht genommen, weil a>0";
		}		
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() <= 0){
				iP = inst.getP1();
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " genommen, weil i" + inst.getP0() + "<=0";
			}
			else
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " nicht genommen, weil i" + inst.getP0() + ">0";
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	private String jumpLt(Instruction inst){
		if (inst.getP0() == -1){
			if (a < 0){
				iP = inst.getP1();
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " genommen, weil a<0";	
			}
			else
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " nicht genommen, weil a>=0";
		}		
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() < 0){
				iP = inst.getP1();
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " genommen, weil i" + inst.getP0() + "<0";
			}
			else
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " nicht genommen, weil i" + inst.getP0() + ">=0";
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	private String jumpNe(Instruction inst){
		if (inst.getP0() == -1){
			if (a != 0){
				iP = inst.getP1();
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " genommen, weil a!=0";		
			}
			else
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " nicht genommen, weil a=0";
		}		
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() != 0){
				iP = inst.getP1();
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " genommen, weil i" + inst.getP0() + "!=0";
			}
			else
				return OUTPUT + p.indexOf(inst) + ": Sprung auf Befehl " + inst.getP1() + " nicht genommen, weil i" + inst.getP0() + "=0";
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
}
