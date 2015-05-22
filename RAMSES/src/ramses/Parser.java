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
	public static final String COMMA = ",";
	//ERROR MESSAGES//
	public static final String ERROR_EXCPECTED_COLON = "Expected ':' after Instruction Pointer";
	public static final String ERROR_INSTPTR_CONTAINS_LETTER = "Instruction Pointer cannot contain a letter";
	public static final String ERROR_INST_UNKNOWN = "The following instruction is not known: ";
	public static final String ERROR_WRONG_INSPTR = "Given Instruction Pointer was not expected";
	public static final String ERROR_WRONG_FORMAT = "The line does not match with any known format";
	public static final String ERROR_INVALID_OPERATOR = "The operator is invalid";
	public static final String ERROR_INPUT_KEYWORD = "The keyword 'INPUT' is missing or written wrong.";
	public static final String ERROR_INPUT_FORMAT = "Unsupported INPUT format. Expected: s[x] | s[x]...s[y] | s[x]=y . Got: ";
	public static final String ERROR_OUTPUT_KEYWORD = "The keyword 'OUTPUT' is missing or written wrong.";
	public static final String ERROR_OUTPUT_FORMAT = "Unsupported OUTPUT format. Expected: s[x] | s[x]...s[y] . Got: ";

	//CASES//
	public static final String CASE_HALT = "HALT";
	public static final String CASE_JUMP = "jump";
	public static final String CASE_COND_JUMP = "if";
	public static final String CASE_AKKU = "a";
	public static final String CASE_MEM = "s";
	public static final String CASE_INDEX = "i";
	public static final String CASE_ADD = "+";
	public static final String CASE_SUB = "-";
	public static final String CASE_MUL = "*";
	public static final String CASE_DIV = "div";
	public static final String CASE_MOD = "mod";
	public static final String CASE_THEN = "then";
	public static final String CASE_NULL = "0";
	public static final String CASE_EQ = "=";
	public static final String CASE_GE = ">=";
	public static final String CASE_GT = ">";
	public static final String CASE_LE = "<=";
	public static final String CASE_LT = "<";
	public static final String CASE_NE = "!=";
	
	//INDEXES//
	public static final int INDEX_INST_PTR = 0;
	public static final int INDEX_DESTINATION = 1;
	public static final int INDEX_ARROW = 2;
	public static final int INDEX_OP1 = 3;
	public static final int INDEX_OPERATOR = 4;
	public static final int INDEX_OP2 = 5;
	public static final int INDEX_JUMP_DEST = 2;
	public static final int INDEX_COND_JUMP_REG = 2;
	public static final int INDEX_COND_JUMP_IDENT = 3;
	public static final int INDEX_COND_JUMP_DEST = 7;
	
	//PATTERN//
	public static final String PATTERN_LOAD_INST = "\\d+: (a|i\\d*|s\\[(i\\d*(\\+\\d+)*|\\d+)\\]) <- (\\d+|s\\[(i\\d*(\\+\\d+)*|\\d+)\\]|a|i\\d*)";
	public static final String PATTERN_JUMP_INST = "\\d+: jump \\d+";
	public static final String PATTERN_COND_JUMP_INST = "\\d+: if (a|i\\d*) (=|!=|<=|>=|<|>) 0 then jump \\d+";
	public static final String PATTERN_ARITH_INST = "\\d+: a <- a (\\+|\\-|\\*|div|mod) (\\d+|s\\[(i\\d*(\\+\\d+)*|\\d+)\\])";
	public static final String PATTERN_HALT_INST = "\\d+: HALT";
	public static final String PATTERN_INDEX_INST = "\\d+: i\\d* <- i\\d* (\\+|\\-) 1";
	public static final String PATTERN_AKKU = "a";
	public static final String PATTERN_IMM = "[0-9]+";
	public static final String PATTERN_INDEX = "i[0-9]*";
	public static final String PATTERN_MEM = "s\\[[0-9]+\\]";
	public static final String PATTERN_MMEM = "s\\[i[0-9]*\\+*[0-9]*\\]";
	public static final String PATTERN_REG = "(a|i\\d*)";
	public static final String PATTERN_MEMS = "s\\[\\d+\\]...s\\[\\d+\\]";
	public static final String PATTERN_INPUT_MEM_VALUE = "s\\[\\d+\\]\\s?=\\s?-?\\d+";
	public static final String PATTERN_INPUT_KEYWORD = "INPUT";
	public static final String PATTERN_OUTPUT_KEYWORD = "OUTPUT";
	
	private static int instPtr;
	
	public static Input[] parseInput(String inputLine) throws SyntaxErrorException{
		ArrayList<Input> input = new ArrayList<>();
		ArrayList<String> tokens = new ArrayList<>();
		Scanner sc = new Scanner(inputLine);
		if(!sc.next().matches(PATTERN_INPUT_KEYWORD)) {
			sc.close();
			throw new SyntaxErrorException(-2, ERROR_INPUT_KEYWORD);
		}
		sc.useDelimiter(COMMA);
		while(sc.hasNext())
			tokens.add(sc.next().trim());
		sc.close();
		
		for(String token : tokens){
			if(token.matches(PATTERN_MEM)){
				input.add(new Input(parseOperand(token)));
			}else if(token.matches(PATTERN_MEMS)){
				String[] s = token.split("\\.\\.\\.");
				int from = parseOperand(s[0]);
				int till = parseOperand(s[1]);
				
				for(int i = from; i <= till; i++){
					input.add(new Input(i));
				}
			}else if(token.matches(PATTERN_INPUT_MEM_VALUE)){
				String index = token.replaceAll("s\\[(\\d+)\\]\\s?=\\s?-?\\d+", "$1");
				String value = token.replaceAll("s\\[\\d+\\]\\s?=\\s?-?(\\d+)", "$1");
				input.add(new Input(Integer.parseInt(index),Integer.parseInt(value)));
			}else
				throw new SyntaxErrorException(-2, ERROR_INPUT_FORMAT + token);
		}
		Input[] n = new Input[input.size()];
		for(int i = 0; i < n.length; i++){
			n[i] = input.get(i);
		}
		return n;
	}
	
	public static int[] parseOutput(String outputLine) throws SyntaxErrorException{
		ArrayList<Integer> output = new ArrayList<>();
		ArrayList<String> tokens = new ArrayList<>();
		Scanner sc = new Scanner(outputLine);
		if(!sc.next().matches(PATTERN_OUTPUT_KEYWORD)) {
			sc.close();
			throw new SyntaxErrorException(-1, ERROR_OUTPUT_KEYWORD);
		}
		sc.useDelimiter(COMMA);
		while(sc.hasNext())
			tokens.add(sc.next().trim());
		sc.close();
		
		for(String token : tokens){
			if(token.matches(PATTERN_MEM)){
				output.add(parseOperand(token));
			}else if(token.matches(PATTERN_MEMS)){
				String[] s = token.split("\\.\\.\\.");
				int from = parseOperand(s[0]);
				int till = parseOperand(s[1]);
				
				for(int i = from; i < till; i++){
					output.add(i);
				}
			}else
				throw new SyntaxErrorException(-1, ERROR_INPUT_FORMAT + token);
		}
		int[] n = new int[output.size()];
		for(int i = 0; i < n.length; i++){
			n[i] = output.get(i);
		}
		return n;
		
	}
	
	
	
	/**
	 * Hauptfunktion zum Parsen der Befehlszeile
	 * @param instPtr
	 * @param instLine
	 * @return
	 * @throws SyntaxErrorException 
	 */
	public static Instruction parseInst(int instPtr, String instLine) throws SyntaxErrorException{
		Parser.instPtr = instPtr;
		ArrayList<String> tokens = new ArrayList<>();
		Scanner scanner = new Scanner(instLine);
		while(scanner.hasNext()){
			tokens.add(scanner.next());
		}
		scanner.close();
		
		if(instLine.matches(PATTERN_LOAD_INST))
			return parseLoadInst(instLine, tokens);
		if(instLine.matches(PATTERN_JUMP_INST))
			return new Instruction(InstructionTag.JUMP, Integer.parseInt(tokens.get(INDEX_JUMP_DEST)));
		if(instLine.matches(PATTERN_COND_JUMP_INST))
			return parseCondJump(instLine, tokens);
		if(instLine.matches(PATTERN_ARITH_INST))
			return parseArithInst(instLine, tokens);
		if(instLine.matches(PATTERN_HALT_INST))
			return new Instruction(InstructionTag.HALT);
		if(instLine.matches(PATTERN_INDEX_INST))
			return parseIndexInst(instLine, tokens);
		throw new SyntaxErrorException(instPtr, ERROR_WRONG_FORMAT);
	}
	
	/**
	 * Parsfunktion, wenn Befehlszeile ein bedingter Sprung ist
	 * @param instLine
	 * @param tokens
	 * @return
	 * @throws SyntaxErrorException
	 */
	private static Instruction parseCondJump(String instLine, ArrayList<String> tokens) throws SyntaxErrorException{
		int p0,p1;
		p0 = parseOperand(tokens.get(INDEX_COND_JUMP_REG));
		p1 = Integer.parseInt(tokens.get(INDEX_COND_JUMP_DEST));
		
		switch(tokens.get(INDEX_COND_JUMP_IDENT)){
			case CASE_EQ:
				return new Instruction(InstructionTag.JUMP_EQ, p0, p1);
			case CASE_GE:
				return new Instruction(InstructionTag.JUMP_GE, p0, p1);
			case CASE_GT:
				return new Instruction(InstructionTag.JUMP_GT, p0, p1);
			case CASE_LE:
				return new Instruction(InstructionTag.JUMP_LE, p0, p1);
			case CASE_LT:
				return new Instruction(InstructionTag.JUMP_LT, p0, p1);
			case CASE_NE:
				return new Instruction(InstructionTag.JUMP_NE, p0, p1);
			default:
				throw new SyntaxErrorException(instPtr,ERROR_INVALID_OPERATOR + tokens.get(INDEX_COND_JUMP_IDENT)); 
		}
	}
	
	/**
	 * Parsfunktion, wenn die Befehlszeile ein Arithmetikbefehl ist
	 * @param instLine
	 * @param tokens
	 * @return
	 * @throws SyntaxErrorException
	 */
	private static Instruction parseArithInst(String instLine, ArrayList<String> tokens) throws SyntaxErrorException{
		int[] p = new int[2];
		if(tokens.get(INDEX_OP2).matches(PATTERN_MMEM)){
			p = parseMMEM(tokens.get(INDEX_OP2));
			switch(tokens.get(INDEX_OPERATOR)){
				case CASE_ADD:
					return new Instruction(InstructionTag.ADD_A_MMEM,p[0],p[1]);
				case CASE_SUB:
					return new Instruction(InstructionTag.SUB_A_MMEM,p[0],p[1]);
				case CASE_MUL:
					return new Instruction(InstructionTag.MUL_A_MMEM,p[0],p[1]);
				case CASE_DIV:
					return new Instruction(InstructionTag.DIV_A_MMEM,p[0],p[1]);
				case CASE_MOD:
					return new Instruction(InstructionTag.MOD_A_MMEM,p[0],p[1]);
			}
		}
		p[0] = parseOperand(tokens.get(INDEX_OP2));
		if(tokens.get(INDEX_OP2).matches(PATTERN_MEM)){
			switch(tokens.get(INDEX_OPERATOR)){
				case CASE_ADD:
					return new Instruction(InstructionTag.ADD_A_MEM,p[0]);
				case CASE_SUB:
					return new Instruction(InstructionTag.SUB_A_MEM,p[0]);
				case CASE_MUL:
					return new Instruction(InstructionTag.MUL_A_MEM,p[0]);
				case CASE_DIV:
					return new Instruction(InstructionTag.DIV_A_MEM,p[0]);
				case CASE_MOD:
					return new Instruction(InstructionTag.MOD_A_MEM,p[0]);
			}
		}
		if(tokens.get(INDEX_OP2).matches(PATTERN_IMM)){
			switch(tokens.get(INDEX_OPERATOR)){
				case CASE_ADD:
					return new Instruction(InstructionTag.ADD_A_IMM,p[0]);
				case CASE_SUB:
					return new Instruction(InstructionTag.SUB_A_IMM,p[0]);
				case CASE_MUL:
					return new Instruction(InstructionTag.MUL_A_IMM,p[0]);
				case CASE_DIV:
					return new Instruction(InstructionTag.DIV_A_IMM,p[0]);
				case CASE_MOD:
					return new Instruction(InstructionTag.MOD_A_IMM,p[0]);
			}
		}
		throw new SyntaxErrorException(instPtr, ERROR_WRONG_FORMAT);
	}

	/**
	 * Parsfunktion, wenn die Befehlszeile ein Transportbefehl ist
	 * @param instLine
	 * @param tokens
	 * @return
	 * @throws SyntaxErrorException
	 */
	private static Instruction parseLoadInst(String instLine, ArrayList<String> tokens) throws SyntaxErrorException{
		String dest = tokens.get(INDEX_DESTINATION);
		String op1 = tokens.get(INDEX_OP1);
		int[] p = new int[2];
		
		if(dest.matches(PATTERN_AKKU) && op1.matches(PATTERN_MMEM)){
			p = parseMMEM(op1);
			return new Instruction(InstructionTag.LD_A_MMEM,p[0],p[1]);
		}
		if(dest.matches(PATTERN_MMEM) && op1.matches(PATTERN_AKKU)){
			p = parseMMEM(dest);
			return new Instruction(InstructionTag.LD_A_MMEM,p[0],p[1]);
		}
		p[0] = parseOperand(dest);
		p[1] = parseOperand(op1);
		if(dest.matches(PATTERN_REG) && op1.matches(PATTERN_IMM))
			return new Instruction(InstructionTag.LD_REG_IMM,p[0],p[1]);
		if(dest.matches(PATTERN_REG) && op1.matches(PATTERN_MEM))
			return new Instruction(InstructionTag.LD_REG_MEM,p[0],p[1]);
		if(dest.matches(PATTERN_MEM) && op1.matches(PATTERN_REG))
			return new Instruction(InstructionTag.LD_MEM_REG,p[0],p[1]);
		throw new SyntaxErrorException(instPtr, ERROR_WRONG_FORMAT);
	}

	/**
	 * Parsfunktion, wenn die Befehlszeile ein Indexbefehl ist
	 * @param instLine
	 * @param tokens
	 * @return
	 * @throws SyntaxErrorException
	 */
	private static Instruction parseIndexInst(String instLine, ArrayList<String> tokens) throws SyntaxErrorException{
		String p0Token = tokens.get(INDEX_DESTINATION);
		int p0;
		//Index index bekommen
		p0Token = p0Token.replace(CASE_INDEX, "0");
		p0 = Integer.parseInt(p0Token);		
		
		switch(tokens.get(INDEX_OPERATOR)){
			case CASE_ADD:
				return new Instruction(InstructionTag.IDX_INC, p0);
			case CASE_SUB:
				return new Instruction(InstructionTag.IDX_DEC, p0);
			default:
				throw new SyntaxErrorException(instPtr,ERROR_INVALID_OPERATOR + TOKEN + tokens.get(INDEX_OPERATOR));
		}
	}
	
	/**
	 * Parsfunktion zum parsen eines modifizierten Speichers
	 * @param token
	 * @return
	 * @throws SyntaxErrorException
	 */
	private static int[] parseMMEM(String token) throws SyntaxErrorException{
		int[] ops = new int[2];
		if(token.matches(PATTERN_MMEM)){
			token = token.replaceAll("s\\[i(.*)\\]", "0$1");
			String[] s = token.split("\\+");
			ops[0] = Integer.parseInt(s[0]);
			if(s.length == 2)
				ops[1] = Integer.parseInt(s[1]);
			return ops;
		}
		throw new SyntaxErrorException(instPtr, ERROR_WRONG_FORMAT + TOKEN + token);
	}
	
	/**
	 * Parsfunktion zum Parsen von allen möglichen Operanden (außer MMEM)
	 * @param token
	 * @return
	 * @throws SyntaxErrorException
	 */
	private static int parseOperand(String token) throws SyntaxErrorException{
		int op;
		if(token.matches(PATTERN_AKKU)){
			op = -1;
			return op;
		}
		if(token.matches(PATTERN_IMM)){
			op = Integer.parseInt(token);
			return op;
		}
		if(token.matches(PATTERN_MEM)){
			token = token.replaceAll("s\\[(.*)\\]", "$1");
			op = Integer.parseInt(token);
			return op;
		}
		if(token.matches(PATTERN_INDEX)){
			token = token.replaceAll("i", "0");
			op = Integer.parseInt(token);
			return op;
		}
		throw new SyntaxErrorException(instPtr, ERROR_WRONG_FORMAT + TOKEN + token);
	}
}
