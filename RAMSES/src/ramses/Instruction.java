package ramses;

/**
 * Eine einfache Klasse, die die wichtigsten Inhalte einer RAM Befehlszeile beinhaltet.
 * @author Lukas
 *
 */
public class Instruction {
	public static final int NOT_DEF = -2;
	/** Tag gibt Aufschluss über den RAM Befehl */
	private InstructionTag instTag;
	/** Array mit Parametern für diesen RAM Befehl */
	private int[] param;
	
	/**
	 * Eine RAM Befehlszeile besteht aus einem Befehlstag und 2 Paramtern
	 * @param instTag
	 * @param p0
	 * @param p1
	 */
	public Instruction(InstructionTag instTag, int p0, int p1){
		this.instTag = instTag;
		param = new int[]{p0, p1};
	}

	public Instruction(InstructionTag instTag, int p0){
		this(instTag,p0,NOT_DEF);
	}
	
	public Instruction(InstructionTag instTag){
		this(instTag, NOT_DEF, NOT_DEF);
	}
	
	public String toString(){
		return instTag.toString() + " | " + param[0] + " | " + param[1];
	}
	//---------------------Getter-----------------------------
	public InstructionTag getInstTag() {
		return instTag;
	}

	public int[] getParam() {
		return param;
	}
	
	public int getP0(){
		return param[0];
	}
	
	public int getP1(){
		return param[1];
	}
}
