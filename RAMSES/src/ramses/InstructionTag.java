package ramses;

/**
 * Enum mit Tags für alle vorhandenen RAM Befehle
 * @author Lukas
 *
 */
public enum InstructionTag {
	//Transportbefehle
	LD_REG_IMM,	//reg <-- imm
	LD_REG_MEM,	//reg <-- mem
	LD_A_MMEM,	//a <-- mmem
	LD_MEM_REG,	//mem <-- reg
	LD_MMEM_A,	//mmem <-- a
	
	//Sprungbefehle
	JUMP,		//jump k
	JUMP_EQ,	//if reg == 0 then jump k
	JUMP_GE,	//if reg >= 0 then jump k
	JUMP_GT,	//if req > 0 then jump k
	JUMP_LE,	//if req <= 0 then jump k
	JUMP_LT,	//if req < 0 then jump k
	JUMP_NE,	//if req != 0 then jump k
	
	//Arithmetikbefehle
	ADD_A_IMM,	//a <-- a + imm
	ADD_A_MEM,	//a <-- a + mem
	ADD_A_MMEM,	//a <-- a + mmem
	SUB_A_IMM,	//a <-- a - imm
	SUB_A_MEM,	//a <-- a - mem
	SUB_A_MMEM,	//a <-- a - mmem
	MUL_A_IMM,	//a <-- a * imm
	MUL_A_MEM,	//a <-- a * mem
	MUL_A_MMEM,	//a <-- a * mmem
	DIV_A_IMM,	//a <-- a div imm
	DIV_A_MEM,	//a <-- a div mem
	DIV_A_MMEM,	//a <-- a div mmem
	MOD_A_IMM,	//a <-- a mod imm
	MOD_A_MEM,	//a <-- a mod mem
	MOD_A_MMEM,	//a <-- a mod mmem
	
	//Indexbefehle
	IDX_INC,	//i <-- i + 1
	IDX_DEC,	//i <-- i - 1
	
	//HALT
	HALT
}
