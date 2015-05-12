package ramses;

public class Ramses {
	public static final int MAX_MEM = 255;
	public static final int MAX_INDEX = 5;
	public static final String OUTPUT = "#OUTPUT#";
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
				return;
			case IDX_DEC:
				break;
			case IDX_INC:
				break;
			case JUMP:
				break;
			case JUMP_EQ:
				break;
			case JUMP_GE:
				break;
			case JUMP_GT:
				break;
			case JUMP_LE:
				break;
			case JUMP_LT:
				break;
			case JUMP_NE:
				break;
			case LD_A_MMEM:
				break;
			case LD_MEM_REG:
				break;
			case LD_MMEM_A:
				break;
			case LD_REG_IMM:
				break;
			case LD_REG_MEM:
				break;
			case MOD_A_IMM:
				break;
			case MOD_A_MEM:
				break;
			case MOD_A_MMEM:
				break;
			case MUL_A_IMM:
				break;
			case MUL_A_MEM:
				break;
			case MUL_A_MMEM:
				break;
			case SUB_A_IMM:
				break;
			case SUB_A_MEM:
				break;
			case SUB_A_MMEM:
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
		return "\n" + OUTPUT + "a <-- a + " + inst.getP0();
	}
	
	private String addAMem(Instruction inst){
		a += s[inst.getP0()];
		return "\n" + OUTPUT + "a <-- a + s[" + inst.getP0() + "]";
	}
	
	private String addAMmem(Instruction inst){
		a += s[i[inst.getP0()]+inst.getP1()];
		return "\n" + OUTPUT + "a <-- a + s[i" + inst.getP0() + "+" + inst.getP1() + "]";
	}
	
	private String divAImm(Instruction inst){
		if (inst.getP0 != 0){
			a = a / inst.getP0();
			return "\n" + OUTPUT + "a <-- a div " + inst.getP0();
		}
		else
			throw new RuntimeException();
	}
	
	private String divAMem(Instruction inst){
		if (s[inst.getP0()] != 0){
			a = a / s[inst.getP0()];
			return "\n" + OUTPUT + "a <-- a div s[" + inst.getP0() + "]";
		}
		else
			throw new RuntimeException();
	}
	
	private String divAMmem(Instruction inst){
		if(s[i[inst.getP0()]+inst.getP1()] != 0){
			a = a / s[i[inst.getP0()]+inst.getP1()];
			return "\n" + OUTPUT + "a <-- a div s[i" + inst.getP0() + "+" + inst.getP1() + "]";
		}
		else
			throw new RuntimeException();
	}
}
