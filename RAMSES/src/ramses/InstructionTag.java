package ramses;

/**
 * Enum mit Tags für alle vorhandenen RAM Befehle
 * @author Lukas Becker
 * @author Andreas Paul
 *
 */
public enum InstructionTag {
	//Transportbefehle
	LD_REG_IMM,	//reg <- imm
	LD_REG_MEM,	//reg <- mem
	LD_REG_REG, //reg <- reg
	LD_A_MMEM,	//a <- mmem
	LD_MEM_REG,	//mem <- reg
	LD_MMEM_A,	//mmem <- a
	
	//Sprungbefehle
	JUMP,		//jump k
	JUMP_EQ,	//if reg = 0 then jump k
	JUMP_GE,	//if reg >= 0 then jump k
	JUMP_GT,	//if req > 0 then jump k
	JUMP_LE,	//if req <= 0 then jump k
	JUMP_LT,	//if req < 0 then jump k
	JUMP_NE,	//if req != 0 then jump k
	
	//Arithmetikbefehle
	ADD_A_IMM,	//a <- a + imm
	ADD_A_MEM,	//a <- a + mem
	ADD_A_MMEM,	//a <- a + mmem
	SUB_A_IMM,	//a <- a - imm
	SUB_A_MEM,	//a <- a - mem
	SUB_A_MMEM,	//a <- a - mmem
	MUL_A_IMM,	//a <- a * imm
	MUL_A_MEM,	//a <- a * mem
	MUL_A_MMEM,	//a <- a * mmem
	DIV_A_IMM,	//a <- a div imm
	DIV_A_MEM,	//a <- a div mem
	DIV_A_MMEM,	//a <- a div mmem
	MOD_A_IMM,	//a <- a mod imm
	MOD_A_MEM,	//a <- a mod mem
	MOD_A_MMEM,	//a <- a mod mmem
	
	//Indexbefehle
	IDX_INC,	//i <- i + 1
	IDX_DEC,	//i <- i - 1
	
	//HALT
	HALT;
	
	public String toString(){
		switch(this){
			case LD_REG_IMM : return "LD_REG_IMM";
			case LD_REG_MEM : return "LD_REG_MEM";
			case LD_REG_REG : return "LD_REG_REG";
			case LD_A_MMEM  : return "LD_A_MMEM";
			case LD_MEM_REG : return "LD_MEM_REG";
			case LD_MMEM_A  : return "LD_MMEM_A";
			case JUMP		: return "JUMP";
			case JUMP_EQ 	: return "JUMP_EQ";
			case JUMP_GE 	: return "JUMP_GE";
			case JUMP_GT 	: return "JUMP_GT";
			case JUMP_LE 	: return "JUMP_LE";
			case JUMP_LT 	: return "JUMP_LT";
			case JUMP_NE 	: return "JUMP_NE";
			case ADD_A_IMM 	: return "ADD_A_IMM";
			case ADD_A_MEM 	: return "ADD_A_MEM";
			case ADD_A_MMEM : return "ADD_A_MMEM";
			case SUB_A_IMM 	: return "SUB_A_IMM";
			case SUB_A_MEM 	: return "SUB_A_MEM";
			case SUB_A_MMEM : return "SUB_A_MMEM";
			case MUL_A_IMM 	: return "MUL_A_IMM";
			case MUL_A_MEM 	: return "MUL_A_MEM";
			case MUL_A_MMEM : return "MUL_A_MMEM";
			case DIV_A_IMM 	: return "DIV_A_IMM";
			case DIV_A_MEM 	: return "DIV_A_MEM";
			case DIV_A_MMEM : return "DIV_A_MMEM";
			case MOD_A_IMM 	: return "MOD_A_IMM";
			case MOD_A_MEM 	: return "MOD_A_MEM";
			case MOD_A_MMEM : return "MOD_A_MMEM";
			case IDX_INC 	: return "IDX_INC";
			case IDX_DEC 	: return "IDX_DEC";
			case HALT 		: return "HALT";	
		}
		return null;
	}
}
