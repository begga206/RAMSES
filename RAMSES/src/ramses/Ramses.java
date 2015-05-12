package ramses;

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
	private Instruction[] p;
	/** Input */
	private Input[] input;
	/** Output */
	private int[] output;
	private String outputString;
	
	
	public Ramses(Input[] input, int[] output, Instruction[] p) throws LogicalErrorException{
		counter = 0;
		this.input = input;
		this.output = output;
		this.p = p;
		a = 0;
		i = new int[MAX_INDEX];
		initS();
		outputString = "";
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
				System.out.println("s[" + input[i].getIndex() + "]:\t");
				s[input[i].getIndex()] = sc.nextInt();
			}
		}
		sc.close();
		process(p[0]);
	}
	
	public void process(Instruction inst){
		iP++;
		try {
			switch(inst.getInstTag()){
			case ADD_A_IMM:
				outputString += addAImm(inst);
				break;
			case ADD_A_MEM:
				outputString += addAMem(inst);
				break;
			case ADD_A_MMEM:
				outputString += addAMmem(inst);
				break;
			case DIV_A_IMM:
				outputString += divAImm(inst);
				break;
			case DIV_A_MEM:
				outputString += divAMem(inst);
				break;
			case DIV_A_MMEM:
				outputString += divAMmem(inst);
				break;
			case HALT:
				outputString += halt(inst);
				System.out.println(outputString);
				return;
			case IDX_DEC:
				outputString += idxDec(inst);
				break;
			case IDX_INC:
				outputString += idxInc(inst);
				break;
			case JUMP: //to do
				outputString += jump(inst);
				break;
			case JUMP_EQ: //to do
				outputString += jumpEq(inst);
				break;
			case JUMP_GE: //to do
				outputString += jumpGe(inst);
				break;
			case JUMP_GT: //to do
				outputString += jumpGt(inst);
				break;
			case JUMP_LE: //to do
				outputString += jumpLe(inst);
				break;
			case JUMP_LT: //to do
				outputString += jumpLt(inst);
				break;
			case JUMP_NE: //to do
				outputString += jumpNe(inst);
				break;
			case LD_A_MMEM: //to do
				outputString += ldAMmem(inst);
				break;
			case LD_MEM_REG: //to do
				outputString += ldMemReg(inst);
				break;
			case LD_MMEM_A: //to do
				outputString += ldMmemA(inst);
				break;
			case LD_REG_IMM: //to do
				outputString += ldRegImm(inst);
				break;
			case LD_REG_MEM: //to do
				outputString += ldRegMem(inst);
				break;
			case MOD_A_IMM:
				outputString += modAImm(inst);
				break;
			case MOD_A_MEM:
				outputString += modAMem(inst);
				break;
			case MOD_A_MMEM:
				outputString += modAMmem(inst);
				break;
			case MUL_A_IMM:
				outputString += mulAImm(inst);
				break;
			case MUL_A_MEM:
				outputString += mulAMem(inst);
				break;
			case MUL_A_MMEM:
				outputString += mulAMmem(inst);
				break;
			case SUB_A_IMM:
				outputString += subAImm(inst);
				break;
			case SUB_A_MEM:
				outputString += subAMem(inst);
				break;
			case SUB_A_MMEM:
				outputString += subAMmem(inst);
				break;
			default:
				break;
			}
			process(p[iP]);
		} catch (Exception e) {
			System.out.println(outputString);
			System.out.println(e + "FEHLER");
		}
	}
	
	private String addAImm(Instruction inst){
		a += inst.getP0();
		return "\n" + OUTPUT + "a<-a + " + inst.getP0();
	}
	
	private String addAMem(Instruction inst){
		a += s[inst.getP0()];
		return "\n" + OUTPUT + "a<-a + s[" + inst.getP0() + "]";
	}
	
	private String addAMmem(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a += s[i[inst.getP0()]+inst.getP1()];
			return "\n" + OUTPUT + "a<-a + s[i" + inst.getP0() + "+" + inst.getP1() + "]";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String divAImm(Instruction inst){
		if (inst.getP0() != 0){
			a = a / inst.getP0();
			return "\n" + OUTPUT + "a<-a div " + inst.getP0();
		}
		else
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
	}
	
	private String divAMem(Instruction inst){
		if (s[inst.getP0()] == 0)
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
		else
			a = a / s[inst.getP0()];
			return "\n" + OUTPUT + "a<-a div s[" + inst.getP0() + "]";
	}
	
	private String divAMmem(Instruction inst){
		if (s[inst.getP0()] == 0)
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a = a / s[i[inst.getP0()]+inst.getP1()];
			return "\n" + OUTPUT + "a<-a div s[i" + inst.getP0() + "+" + inst.getP1() + "]";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String subAImm(Instruction inst){
		a -= inst.getP0();
		return "\n" + OUTPUT + "a<-a - " + inst.getP0();
	}
	
	private String subAMem(Instruction inst){
		a -= s[inst.getP0()];
		return "\n" + OUTPUT + "a<-a - s[" + inst.getP0() + "]";
	}
	
	private String subAMmem(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a -= s[i[inst.getP0()]+inst.getP1()];
			return "\n" + OUTPUT + "a<-a - s[i" + inst.getP0() + "+" + inst.getP1() + "]";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String modAImm(Instruction inst){
		if (inst.getP0() != 0){
			a = a % inst.getP0();
			return "\n" + OUTPUT + "a<-a mod " + inst.getP0();
		}
		else
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
	}
	
	private String modAMem(Instruction inst){
		if (s[inst.getP0()] != 0){
			a = a % s[inst.getP0()];
			return "\n" + OUTPUT + "a<-a mod s[" + inst.getP0() + "]";
		}
		else
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
	}
	
	private String modAMmem(Instruction inst){
		if(s[i[inst.getP0()]+inst.getP1()] == 0)
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a = a % s[i[inst.getP0()]+inst.getP1()];
			return "\n" + OUTPUT + "a<-a mod s[i" + inst.getP0() + "+" + inst.getP1() + "]";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String mulAImm(Instruction inst){
		a = a * inst.getP0();
		return "\n" + OUTPUT + "a<-a * " + inst.getP0();
	}
	
	private String mulAMem(Instruction inst){
		a = a * s[inst.getP0()];
		return "\n" + OUTPUT + "a<-a * s[" + inst.getP0() + "]";
	}
	
	private String mulAMmem(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a = a * s[i[inst.getP0()]+inst.getP1()];
			return "\n" + OUTPUT + "a<-a * s[i" + inst.getP0() + "+" + inst.getP1() + "]";
		}	
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String halt(Instruction inst){
		String string = "\n" + OUTPUT + "Halt\n";
		for(int i=0; i<output.length; i++){
			string += "s[" + output[i] + "] = " + s[output[i]] + "\t";
		}
		return string;
	}
	
	private String idxDec(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			i[inst.getP0()]--;
			return "\n" + OUTPUT + "i" + inst.getP0() + "<-i" + inst.getP0() + "- 1";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String idxInc(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			i[inst.getP0()]++;
			return "\n" + OUTPUT + "i" + inst.getP0() + "<-i" + inst.getP0() + "+ 1";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String jump(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() <= p.length){
			iP = inst.getP0();
			return "\n" + OUTPUT + "jump " + inst.getP0();
		}	
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	private String ldRegImm(Instruction inst){
		if (inst.getP0() == -1){
			a = inst.getP1();
			return "\n" + OUTPUT + "a<-" + inst.getP0();
		}	
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			i[inst.getP0()] = inst.getP1();	
			return "\n" + OUTPUT + "i<-" + inst.getP0() + "<-";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String ldRegMem(Instruction inst){
		if (inst.getP0() == -1){
			a = s[inst.getP1()];
			return "\n" + OUTPUT + "a<-s[" + inst.getP0() + "]";
		}	
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			i[inst.getP0()] = s[inst.getP1()];
			return "\n" + OUTPUT + "i<-s[" + inst.getP0() + "]";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String ldAMmem(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			a = s[i[inst.getP0()]+inst.getP1()];
			return "\n" + OUTPUT + "a<-s[i" + inst.getP0() + "+" + inst.getP1() + "]";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String ldMemReg(Instruction inst){
		if (inst.getP1() == -1){
			s[inst.getP0()] = a;
			return "\n" + OUTPUT + "s[" + inst.getP0() + "]<--" + a;
		}
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			s[inst.getP0()] = i[inst.getP1()];
			return "\n" + OUTPUT + "s[" + inst.getP0() + "]<--i" + inst.getP1();
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String ldMmemA(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			s[i[inst.getP0()]+inst.getP1()] = s[i[inst.getP0()]+inst.getP1()] + a;
			return "\n" + OUTPUT + "s[i" + inst.getP0() + "+" + inst.getP1() + "]<-" + a;
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String jumpEq(Instruction inst){
		if (inst.getP0() == -1){
			if (a == 0){
				iP = inst.getP1();
				return "\n" + OUTPUT + "if a=0 then jump " + inst.getP1();	
			}
			else
				return "\n" + OUTPUT + "Sprung auf Befehl" + inst.getP1() + "nicht genommen, weil a!=0";
		}		
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (i[inst.getP0()] == 0){
				iP = inst.getP1();
				return "\n" + OUTPUT + "if i=0 then jump " + inst.getP1();
			}
			else
				return "\n" + OUTPUT + "Sprung auf Befehl" + inst.getP1() + "nicht genommen, weil i" + inst.getP0() + "!=0";
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	private String jumpGe(Instruction inst){
		if (inst.getP0() == -1){
			if (a >= 0){
				iP = inst.getP1();
				return "\n" + OUTPUT + "if a>=0 then jump " + inst.getP1();	
			}
			else
				return "\n" + OUTPUT + "Sprung auf Befehl" + inst.getP1() + "nicht genommen, weil a<0";
		}		
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() >= 0){
				iP = inst.getP1();
				return "\n" + OUTPUT + "if a>=0 then jump " + inst.getP1();
			}
			else
				return "\n" + OUTPUT + "Sprung auf Befehl" + inst.getP1() + "nicht genommen, weil i" + inst.getP0() + "<0";
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	private String jumpGt(Instruction inst){
		if (inst.getP0() == -1){
			if (a > 0){
				iP = inst.getP1();
				return "\n" + OUTPUT + "if a>0 then jump " + inst.getP1();	
			}
			else
				return "\n" + OUTPUT + "Sprung auf Befehl" + inst.getP1() + "nicht genommen, weil a<=0";
		}		
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() > 0){
				iP = inst.getP1();
				return "\n" + OUTPUT + "if a>0 then jump " + inst.getP1();
			}
			else
				return "\n" + OUTPUT + "Sprung auf Befehl" + inst.getP1() + "nicht genommen, weil i" + inst.getP0() + "<=0";
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	private String jumpLe(Instruction inst){
		if (inst.getP0() == -1){
			if (a <= 0){
				iP = inst.getP1();
				return "\n" + OUTPUT + "if a<=0 then jump " + inst.getP1();	
			}
			else
				return "\n" + OUTPUT + "Sprung auf Befehl" + inst.getP1() + "nicht genommen, weil a>0";
		}		
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() <= 0){
				iP = inst.getP1();
				return "\n" + OUTPUT + "if a<=0 then jump " + inst.getP1();
			}
			else
				return "\n" + OUTPUT + "Sprung auf Befehl" + inst.getP1() + "nicht genommen, weil i" + inst.getP0() + ">0";
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	private String jumpLt(Instruction inst){
		if (inst.getP0() == -1){
			if (a < 0){
				iP = inst.getP1();
				return "\n" + OUTPUT + "if a<0 then jump " + inst.getP1();	
			}
			else
				return "\n" + OUTPUT + "Sprung auf Befehl" + inst.getP1() + "nicht genommen, weil a>=0";
		}		
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() < 0){
				iP = inst.getP1();
				return "\n" + OUTPUT + "if a<0 then jump " + inst.getP1();
			}
			else
				return "\n" + OUTPUT + "Sprung auf Befehl" + inst.getP1() + "nicht genommen, weil i" + inst.getP0() + ">=0";
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	private String jumpNe(Instruction inst){
		if (inst.getP0() == -1){
			if (a != 0){
				iP = inst.getP1();
				return "\n" + OUTPUT + "if a!=0 then jump " + inst.getP1();	
			}
			else
				return "\n" + OUTPUT + "Sprung auf Befehl" + inst.getP1() + "nicht genommen, weil a=0";
		}		
		if (inst.getP0() >= 0 && inst.getP0() < i.length){
			if (inst.getP0() != 0){
				iP = inst.getP1();
				return "\n" + OUTPUT + "if a!=0 then jump " + inst.getP1();
			}
			else
				return "\n" + OUTPUT + "Sprung auf Befehl" + inst.getP1() + "nicht genommen, weil i" + inst.getP0() + "=0";
		}
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
}
