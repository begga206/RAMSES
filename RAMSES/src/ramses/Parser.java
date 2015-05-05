package ramses;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Parserklasse zum erfassen einer RAM Befehlszeile
 * @author Lukas
 *
 */
public class Parser {

	public static final String TOKEN = "\tTOKEN: ";
	public static final String COLON = ":";
	//ERROR MESSAGES//
	public static final String ERROR_EXCPECTED_COLON = "Expected ':' after Instruction Pointer";
	public static final String ERROR_INSTPTR_CONTAINS_LETTER = "Instruction Pointer cannot contain a letter";
	public static final String ERROR_INST_UNKNOWN = "The following instruction is not known: ";
	public static final String ERROR_WRONG_INSPTR = "Given Instruction Pointer was not expected";
	public static final String ERROR_WRONG_FORMAT = "The line does not match with any known format";
	public static final String ERROR_INVALID_OPERATOR = "The operator is invalid";
	
	//INSTRUCTION SIZES//
	public static final int SIZE_HALT = 2;
	public static final int SIZE_JUMP = 3;
	public static final int SIZE_LOAD = 4;
	public static final int SIZE_ARITH_INDEX = 6;
	public static final int SIZE_COND_JUMP = 8;

	//CASES//
	public static final String CASE_HALT = "HALT";
	public static final String CASE_JUMP = "jump";
	public static final String CASE_COND_JUMP = "if";
	public static final String CASE_AKKU = "a";
	public static final String CASE_MEM = "s";
	public static final String CASE_INDEX = "i";
	public static final String CASE_ADD = "+";
	public static final String CASE_SUB = "-";
	
	//INDEXES//
	public static final int INDEX_INST_PTR = 0;
	public static final int INDEX_DESTINATION = 1;
	public static final int INDEX_ARROW = 2;
	public static final int INDEX_OP1 = 3;
	public static final int INDEX_OPERATOR = 4;
	public static final int INDEX_OP2 = 5;
	
	//JUMPS//
	public static final int SIMPLE_JUMP_IDENT = 2;
	public static final int SIMPLE_JUMP_DEST = 3;
	
	/**
	 * Hauptfunktion zum Parsen der Befehlszeile
	 * @param instPtr
	 * @param instLine
	 * @return
	 * @throws SyntaxErrorException 
	 */
	public Instruction parseInst(int instPtr, String instLine) throws SyntaxErrorException{
		ArrayList<String> tokens = new ArrayList<>();
		
		try(Scanner scanner = new Scanner(instLine)){
			while(scanner.hasNext()){
				tokens.add(scanner.next());
			}
		}
		//Entspricht Befehl regulärer Befehlsgröße?
		if(tokens.size() != SIZE_HALT && tokens.size() != SIZE_JUMP && 
				tokens.size() != SIZE_COND_JUMP && tokens.size() != SIZE_LOAD && tokens.size() != SIZE_ARITH_INDEX)
			throw new SyntaxErrorException(instPtr, ERROR_WRONG_FORMAT);
		//Ist der dritte Token ein '<-' ?
		if(tokens.size() > 2 && !tokens.get(INDEX_ARROW).equals("<-"))
			throw new SyntaxErrorException(instPtr, ERROR_WRONG_FORMAT);
		
		validateInstPtr(instPtr, tokens.get(INDEX_INST_PTR));	//validieren, dass der angegebene IP mit dem internen übereinstimmt

		switch(tokens.get(INDEX_DESTINATION)){	//Kategorisiere ersten Befehlstoken
			case CASE_HALT:
				return new Instruction(InstructionTag.HALT);
			case CASE_JUMP:
				return parseJump(instPtr, instLine, tokens);
			case CASE_COND_JUMP:
				return parseCondJump(instPtr, instLine, tokens);
			case CASE_AKKU:
				return parseArithInst(instPtr, instLine, tokens);
			default: //Load und Index müssen so gelöst werden, weil sie indexiert sind
				if(tokens.get(INDEX_DESTINATION).contains(CASE_MEM))
					return parseLoadInst(instPtr, instLine, tokens);
				if(tokens.get(INDEX_DESTINATION).contains(CASE_INDEX))
					return parseIndexInst(instPtr, instLine, tokens);
				throw new SyntaxErrorException(instPtr, ERROR_INST_UNKNOWN + tokens.get(INDEX_DESTINATION));
		}
	}
	
	/**
	 * Funktion zum Validieren des Befehlscounter.
	 * Wenn die Funktion keine Exception wirft, ist der Befehlscounter korrekt.
	 * @param instPtr
	 * @throws SyntaxErrorException
	 */
	private static void validateInstPtr(int instPtr, String instPtrToken) throws SyntaxErrorException{
		int checkInstPtr;
		String checkInstToken;
		
		//Check, ob Format == "Zahl:"
		if(!instPtrToken.contains(COLON))
			throw new SyntaxErrorException(instPtr, ERROR_EXCPECTED_COLON + TOKEN + instPtrToken);

		checkInstToken = instPtrToken.replace(COLON, "");
		
		//Check, ob instPtrToken nur aus Ziffern besteht
		if (checkInstToken.matches("[0-9]+"))
			checkInstPtr = Integer.parseInt(checkInstToken);
		else
			throw new SyntaxErrorException(instPtr, ERROR_INSTPTR_CONTAINS_LETTER + TOKEN + instPtrToken);
		
		//Check, ob eingetragener instPtr dem internen instPtr entspricht
		if(checkInstPtr != instPtr)
			throw new SyntaxErrorException(instPtr, ERROR_WRONG_INSPTR + TOKEN + instPtrToken);

		return;
	}
	
	private Instruction parseJump(int instPtr, String instLine, ArrayList<String> tokens) throws SyntaxErrorException{
		String p0Token = tokens.get(SIMPLE_JUMP_IDENT);
		if (p0Token.equals(CASE_JUMP))
			p0Token = tokens.get(SIMPLE_JUMP_DEST);
			p0 = Integer.parseInt(p0Token);
			return new Instruction(InstructionTag.JUMP, p0);
		else
			throw new SyntaxErrorException(instPtr, ERROR_WRONG_INSPTR + "was expecting 'jump', got " + p0tToken);
		return null;
	}
	
	private Instruction parseCondJump(int instPtr, String instLine, ArrayList<String> tokens) throws SyntaxErrorException{
		//TODO
		return null;
	}
	
	private Instruction parseArithInst(int instPtr, String instLine, ArrayList<String> tokens) throws SyntaxErrorException{
		//TODO
		return null;
	}
	
	private Instruction parseLoadInst(int instPtr, String instLine, ArrayList<String> tokens) throws SyntaxErrorException{
		//TODO
		return null;
	}
	
	private Instruction parseIndexInst(int instPtr, String instLine, ArrayList<String> tokens) throws SyntaxErrorException{
		String p0Token = tokens.get(INDEX_DESTINATION);
		int p0;
		//Es handelt sich nur um eine Indexanweisung, wenn i <- i
		if(!tokens.get(INDEX_DESTINATION).equals(tokens.get(INDEX_OP1)))
			return parseLoadInst(instPtr, instLine, tokens);
		if(!tokens.get(INDEX_OP2).equals("1"))
			throw new SyntaxErrorException(instPtr, ERROR_WRONG_FORMAT + "was expecting 1, got " + tokens.get(INDEX_OP2));
		
		//Index index bekommen
		p0Token = p0Token.replace(CASE_INDEX, "0");
		if (p0Token.matches("[0-9]+") && p0Token.length() > 2)
			p0 = Integer.parseInt(p0Token);
		else
			throw new SyntaxErrorException(instPtr, ERROR_WRONG_FORMAT + TOKEN + tokens.get(INDEX_DESTINATION));		
		
		switch(tokens.get(INDEX_OPERATOR)){
			case CASE_ADD:
				return new Instruction(InstructionTag.IDX_INC, p0);
			case CASE_SUB:
				return new Instruction(InstructionTag.IDX_DEC, p0);
			default:
				throw new SyntaxErrorException(instPtr,ERROR_INVALID_OPERATOR + TOKEN + tokens.get(INDEX_OPERATOR));
		}
	}


}
