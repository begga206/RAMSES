package ramses;

public class Ramses {
	public static final int MAX_MEM = 255;
	public static final int MAX_INDEX = 5;
	public static final String OUTPUT = "#OUTPUT#";
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
	private String output;
	
	
	public Ramses(Instruction[] p){
		counter = 0;
		this.p = p;
		a = 0;
		i = new int[MAX_INDEX];
		s = new int[MAX_MEM];
		output = "";
	}
	
	public void process(Instruction inst){
		try {
			switch(inst.getInstTag()){
			case ADD_A_IMM:
				output += addAImm(inst);
				break;
			case ADD_A_MEM:
				output += addAMem(inst);
				break;
			case ADD_A_MMEM:
				output += addAMmem(inst);
				break;
			case DIV_A_IMM:
				output += divAImm(inst);
				break;
			case DIV_A_MEM:
				output += divAMem(inst);
				break;
			case DIV_A_MMEM:
				output += divAMmem(inst);
				break;
			case HALT:
				output += halt(inst);
				return;
			case IDX_DEC:
				output += idxDec(inst);
				break;
			case IDX_INC:
				output += idxInc(inst);
				break;
			case JUMP: //to do
				output += jump(inst);
				break;
			case JUMP_EQ: //to do
				output += jumpEq(inst);
				break;
			case JUMP_GE: //to do
				output += jumpGe(inst);
				break;
			case JUMP_GT: //to do
				output += jumpGt(inst);
				break;
			case JUMP_LE: //to do
				output += jumpLe(inst);
				break;
			case JUMP_LT: //to do
				output += jumpLt(inst);
				break;
			case JUMP_NE: //to do
				output += jumpNe(inst);
				break;
			case LD_A_MMEM: //to do
				output += ldAMmem(inst);
				break;
			case LD_MEM_REG: //to do
				output += ldMemReg(inst);
				break;
			case LD_MMEM_A: //to do
				output += ldMmemA(inst);
				break;
			case LD_REG_IMM: //to do
				output += ldRegImm(inst);
				break;
			case LD_REG_MEM: //to do
				output += ldRegMem(inst);
				break;
			case MOD_A_IMM:
				output += modAImm(inst);
				break;
			case MOD_A_MEM:
				output += modAMem(inst);
				break;
			case MOD_A_MMEM:
				output += modAMmem(inst);
				break;
			case MUL_A_IMM:
				output += mulAImm(inst);
				break;
			case MUL_A_MEM:
				output += mulAMem(inst);
				break;
			case MUL_A_MMEM:
				output += mulAMmem(inst);
				break;
			case SUB_A_IMM:
				output += subAImm(inst);
				break;
			case SUB_A_MEM:
				output += subAMem(inst);
				break;
			case SUB_A_MMEM:
				output += subAMmem(inst);
				break;
			default:
				break;
			}
			process(p[iP+1]);
		} catch (Exception e) {
			System.out.println(output);
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
		a += s[i[inst.getP0()]+inst.getP1()];
		return "\n" + OUTPUT + "a<-a + s[i" + inst.getP0() + "+" + inst.getP1() + "]";
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
		if (s[inst.getP0()] != 0){
			a = a / s[inst.getP0()];
			return "\n" + OUTPUT + "a<-a div s[" + inst.getP0() + "]";
		}
		else
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
	}
	
	private String divAMmem(Instruction inst){
		if(s[i[inst.getP0()]+inst.getP1()] != 0){
			a = a / s[i[inst.getP0()]+inst.getP1()];
			return "\n" + OUTPUT + "a<-a div s[i" + inst.getP0() + "+" + inst.getP1() + "]";
		}
		else
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
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
		a -= s[i[inst.getP0()]+inst.getP1()];
		return "\n" + OUTPUT + "a<-a - s[i" + inst.getP0() + "+" + inst.getP1() + "]";
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
		if(s[i[inst.getP0()]+inst.getP1()] != 0){
			a = a % s[i[inst.getP0()]+inst.getP1()];
			return "\n" + OUTPUT + "a<-a mod s[i" + inst.getP0() + "+" + inst.getP1() + "]";
		}
		else
			throw new RuntimeException(ERROR_DIVISION_BY_ZERO);
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
		a = a * s[i[inst.getP0()]+inst.getP1()];
		return "\n" + OUTPUT + "a<-a * s[i" + inst.getP0() + "+" + inst.getP1() + "]";
	}
	
	private String halt(Instruction inst){
		return "\n" + OUTPUT + "Halt";
	}
	
	private String idxDec(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < MAX_INDEX){
			i[inst.getP0()]--;
			return "\n" + OUTPUT + "i" + inst.getP0() + "<-i" + inst.getP0() + "- 1";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String idxInc(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < MAX_INDEX){
			i[inst.getP0()]++;
			return "\n" + OUTPUT + "i" + inst.getP0() + "<-i" + inst.getP0() + "+ 1";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String jump(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() <= p.length){
			iP = inst.getP0()-1;
			return "\n" + OUTPUT + "jump " + inst.getP0();
		}	
		else
			throw new RuntimeException(ERROR_JUMP_INVALID);
	}
	
	private String ldRegImm(Instruction inst){
		if (inst.getP0() == -1){
			a = inst.getP0();
			return "\n" + OUTPUT + "a<-" + inst.getP0();
		}	
		if (inst.getP0() >= 0 && inst.getP0() < MAX_INDEX){
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
		if (inst.getP0() >= 0 && inst.getP0() < MAX_INDEX){
			i[inst.getP0()] = s[inst.getP1()];
			return "\n" + OUTPUT + "i<-s[" + inst.getP0() + "]";
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String ldAMmem(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < MAX_INDEX){
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
		if (inst.getP0() >= 0 && inst.getP0() < MAX_INDEX){
			s[inst.getP0()] = i[inst.getP1()];
			return "\n" + OUTPUT + "s[" + inst.getP0() + "]<--i" + inst.getP1();
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
	
	private String ldMmemA(Instruction inst){
		if (inst.getP0() >= 0 && inst.getP0() < MAX_INDEX){
			s[i[inst.getP0()]+inst.getP1()] = s[i[inst.getP0()]+inst.getP1()] + a;
			return "\n" + OUTPUT + "s[i" + inst.getP0() + "+" + inst.getP1() + "]<-" + a;
		}
		else
			throw new RuntimeException(ERROR_INDEX_OUT_OF_BOUNDS);
	}
}
