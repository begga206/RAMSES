package ramses;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Parserklasse zum erfassen einer RAM Befehlszeile
 * 
 * @author Lukas Becker
 * @author Andreas Paul
 */
public class Parser {

	public static final String TOKEN = "\tTOKEN: ";
	public static final String COLON = ":";
	public static final String COMMA = ",";

	// ERROR MESSAGES//
	public static final String ERROR_EXCPECTED_COLON = "ERROR_EXCPECTED_COLON";
	public static final String ERROR_INSTPTR_CONTAINS_LETTER = "ERROR_INSTPTR_CONTAINS_LETTER";
	public static final String ERROR_INST_UNKNOWN = "ERROR_INST_UNKNOWN";
	public static final String ERROR_WRONG_INSPTR = "ERROR_WRONG_INSPTR";
	public static final String ERROR_WRONG_FORMAT = "ERROR_WRONG_FORMAT";
	public static final String ERROR_INVALID_OPERATOR = "ERROR_INVALID_OPERATOR";
	public static final String ERROR_INPUT_KEYWORD = "ERROR_INPUT_KEYWORD";
	public static final String ERROR_INPUT_FORMAT = "ERROR_INPUT_FORMAT";
	public static final String ERROR_OUTPUT_KEYWORD = "ERROR_OUTPUT_KEYWORD";
	public static final String ERROR_OUTPUT_FORMAT = "ERROR_OUTPUT_FORMAT";
	public static final String ERROR_INDEX_DEST_NE_OP1 = "ERROR_INDEX_DEST_NE_OP1";

	// CASES//
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

	// INDEXES//
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

	// PATTERN//
	public static final String PATTERN_LOAD_INST = "\\d+: (a|i\\d*|s\\[(i\\d*(\\+\\d+)*|\\d+)\\]) "
			+ "<- (\\d+|s\\[(i\\d*(\\+\\d+)*|\\d+)\\]|a|i\\d*)";
	public static final String PATTERN_JUMP_INST = "\\d+: jump \\d+";
	public static final String PATTERN_COND_JUMP_INST = "\\d+: if (a|i\\d*) (=|!=|<=|>=|<|>) 0 then jump \\d+";
	public static final String PATTERN_ARITH_INST = "\\d+: a <- a (\\+|\\-|\\*|div|mod) "
			+ "(\\d+|s\\[(i\\d*(\\+\\d+)*|\\d+)\\])";
	public static final String PATTERN_HALT_INST = "\\d+: HALT";
	public static final String PATTERN_INDEX_INST = "\\d+: i\\d* <- i\\d* (\\+|\\-) 1";
	public static final String PATTERN_AKKU = "a";
	public static final String PATTERN_IMM = "\\d+";
	public static final String PATTERN_INDEX = "i\\d*";
	public static final String PATTERN_MEM = "s\\[\\d+\\]";
	public static final String PATTERN_MMEM = "s\\[i\\d*\\+*\\d*\\]";
	public static final String PATTERN_REG = "(a|i\\d*)";
	public static final String PATTERN_MEMS = "s\\[\\d+\\]...s\\[\\d+\\]";
	public static final String PATTERN_INPUT_MEM_VALUE = "s\\[\\d+\\]\\s?=\\s?-?\\d+";
	public static final String PATTERN_INPUT_KEYWORD = "INPUT";
	public static final String PATTERN_OUTPUT_KEYWORD = "OUTPUT";

	// SYNTAX ERROR PATTERN//
	public static final String PATTERN_NO_DEST_REG = "\\d+: <- (\\d+|s\\[(i\\d*(\\+\\d+)*|\\d+)\\]|a|i\\d*)";
	public static final String PATTERN_UNKNOWN_OPERATOR = "\\d+: a <- a (.)* "
			+ "(\\d+|s\\[(i\\d*(\\+\\d+)*|\\d+)\\])";
	public static final String PATTERN_NO_FIRST_OPERAND = "\\d+: a <- (\\+|\\-|\\*|div|mod) "
			+ "(\\d+|s\\[(i\\d*(\\+\\d+)*|\\d+)\\])";
	public static final String PATTERN_NO_SECOND_OPERAND = "\\d+: a <- a (\\+|\\-|\\*|div|mod) ";
	public static final String PATTERN_NO_IP = "\\D(.)*";
	public static final String PATTERN_LD_MEM_IMM = "\\d+: (" + PATTERN_MEM
			+ "|" + PATTERN_MMEM + " <- " + PATTERN_IMM + ")";
	public static final String PATTERN_LD_MEM_MEM = "\\d+: (" + PATTERN_MEM
			+ "|" + PATTERN_MMEM + ") <- (" + PATTERN_MEM + "|" + PATTERN_MMEM
			+ ")";
	public static final String PATTERN_LD_I_MMEM = "\\d+: " + PATTERN_INDEX
			+ " <- " + PATTERN_MMEM;
	public static final String PATTEN_LD_REG_REG = "\\d+: " + PATTERN_REG
			+ " <- " + PATTERN_REG;
	public static final String PATTERN_FORGOT_COLON = "\\d+ (.)*";
	public static final String PATTERN_NO_THEN = "\\d+: if (a|i\\d*) "
			+ "(=|!=|<=|>=|<|>) 0 jump \\d+";
	public static final String PATTERN_IF_NOT_NULL = "\\d+: if (a|i\\d*) "
			+ "(=|!=|<=|>=|<|>) 0 then jump \\d+";
	public static final String PATTERN_IF_UNKNWN_OP = "\\d+: if (a|i\\d*) "
			+ ".* 0 then jump \\d+";
	public static final String PATTERN_INDEX_UNKWN_OP = 
			"\\d+: i\\d* <- i\\d* (.)* \\d+";
	public static final String PATTERN_INDEX_OP2_NOT_ONE = 
			"\\d+: i\\d* <- i\\d* (\\+|\\-) \\d+";

	// SYNTAX ERROR MELDUNGEN ZU OBEN STEHENDEN PATTERN//
	public static final String ERROR_PATTERN_NO_DEST_REG = "ERROR_PATTERN_NO_DEST_REG";
	public static final String ERROR_PATTERN_UNKNOWN_OPERATOR = "ERROR_PATTERN_UNKNOWN_OPERATOR";
	public static final String ERROR_PATTERN_NO_FIRST_OPERAND = "ERROR_PATTERN_NO_FIRST_OPERAND";
	public static final String ERROR_PATTERN_NO_SECOND_OPERAND = "ERROR_PATTERN_NO_SECOND_OPERAND";
	public static final String ERROR_PATTERN_NO_IP = "ERROR_PATTERN_NO_IP";
	public static final String ERROR_PATTERN_LD_MEM_IMM = "ERROR_PATTERN_LD_MEM_IMM";
	public static final String ERROR_PATTERN_LD_MEM_MEM = "ERROR_PATTERN_LD_MEM_MEM";
	public static final String ERROR_PATTERN_LD_I_MMEM = "ERROR_PATTERN_LD_I_MMEM";
	public static final String ERROR_PATTEN_LD_REG_REG = "ERROR_PATTEN_LD_REG_REG";
	public static final String ERROR_PATTERN_FORGOT_COLON = "ERROR_PATTERN_FORGOT_COLON";
	public static final String ERROR_PATTERN_NO_THEN = "ERROR_PATTERN_NO_THEN";
	public static final String ERROR_PATTERN_IF_NOT_NULL = "ERROR_PATTERN_IF_NOT_NULL";
	public static final String ERROR_PATTERN_IF_UNKNWN_OP = "ERROR_PATTERN_IF_UNKNWN_OP";
	public static final String ERROR_PATTERN_INDEX_UNKWN_OP = "ERROR_PATTERN_INDEX_UNKWN_OP";
	public static final String ERROR_PATTERN_INDEX_OP2_NOT_ONE = "ERROR_PATTERN_INDEX_OP2_NOT_ONE";
	

	private static int instPtr;

	static ResourceBundle messages;

	/**
	 * Methode liest aus der Inputzeile eines RAM Programms die Inputregister
	 * aus und gibt diese als Array zurück
	 * 
	 * @param inputLine
	 * @return Array mit den verschiedenen Inputregistern
	 * @throws SyntaxErrorException
	 */
	public static Input[] parseInput(String inputLine)
			throws SyntaxErrorException {
		messages = ResourceBundle.getBundle("ramses.MessagesBundle",
				Locale.getDefault());
		ArrayList<Input> input = new ArrayList<>();
		ArrayList<String> tokens = new ArrayList<>();
		Scanner sc = new Scanner(inputLine);
		if (!sc.next().matches(PATTERN_INPUT_KEYWORD)) {
			sc.close();
			throw new SyntaxErrorException(-2,
					messages.getString(ERROR_INPUT_KEYWORD));
		}
		sc.useDelimiter(COMMA);
		while (sc.hasNext())
			tokens.add(sc.next().trim());
		sc.close();

		for (String token : tokens) {
			if (token.matches(PATTERN_MEM)) {
				input.add(new Input(parseOperand(token)));
			} else if (token.matches(PATTERN_MEMS)) {
				String[] s = token.split("\\.\\.\\.");
				int from = parseOperand(s[0]);
				int till = parseOperand(s[1]);

				for (int i = from; i <= till; i++) {
					input.add(new Input(i));
				}
			} else if (token.matches(PATTERN_INPUT_MEM_VALUE)) {
				String index = token.replaceAll("s\\[(\\d+)\\]\\s?=\\s?-?\\d+",
						"$1");
				String value = token.replaceAll("s\\[\\d+\\]\\s?=\\s?-?(\\d+)",
						"$1");
				input.add(new Input(Integer.parseInt(index), Integer
						.parseInt(value)));
			} else
				throw new SyntaxErrorException(-2, messages.getString(messages
						.getString(ERROR_INPUT_FORMAT)) + token);
		}
		Input[] n = new Input[input.size()];
		for (int i = 0; i < n.length; i++) {
			n[i] = input.get(i);
		}
		return n;
	}

	/**
	 * Ermittelt aus einer gegebenen Outputbefehlszeile eines RAM-Programms alle
	 * Output Register und gibt die Indexe als Array zurück
	 * 
	 * @param outputLine
	 * @return Array mit den Indexen des Speicherregisters
	 * @throws SyntaxErrorException
	 */
	public static int[] parseOutput(String outputLine)
			throws SyntaxErrorException {
		ArrayList<Integer> output = new ArrayList<>();
		ArrayList<String> tokens = new ArrayList<>();
		Scanner sc = new Scanner(outputLine);
		if (!sc.next().matches(PATTERN_OUTPUT_KEYWORD)) {
			sc.close();
			throw new SyntaxErrorException(-1, messages.getString(messages
					.getString(ERROR_OUTPUT_KEYWORD)));
		}
		sc.useDelimiter(COMMA);
		while (sc.hasNext())
			tokens.add(sc.next().trim());
		sc.close();

		for (String token : tokens) {
			if (token.matches(PATTERN_MEM)) {
				output.add(parseOperand(token));
			} else if (token.matches(PATTERN_MEMS)) {
				String[] s = token.split("\\.\\.\\.");
				int from = parseOperand(s[0]);
				int till = parseOperand(s[1]);

				for (int i = from; i < till; i++) {
					output.add(i);
				}
			} else
				throw new SyntaxErrorException(-1,
						messages.getString(ERROR_INPUT_FORMAT) + token);
		}
		int[] n = new int[output.size()];
		for (int i = 0; i < n.length; i++) {
			n[i] = output.get(i);
		}
		return n;

	}

	/**
	 * Hauptfunktion zum Parsen der Befehlszeile
	 * 
	 * @param instPtr
	 * @param instLine
	 * @return Instruktionsobjekt, was der Befehlszeile entspricht
	 * @throws SyntaxErrorException
	 */
	public static Instruction parseInst(int instPtr, String instLine)
			throws SyntaxErrorException {
		Parser.instPtr = instPtr;
		ArrayList<String> tokens = new ArrayList<>();
		Scanner scanner = new Scanner(instLine);
		while (scanner.hasNext()) {
			tokens.add(scanner.next());
		}
		scanner.close();

		if (instLine.matches(PATTERN_LOAD_INST))
			return parseLoadInst(instLine, tokens);
		if (instLine.matches(PATTERN_JUMP_INST))
			return new Instruction(InstructionTag.JUMP, Integer.parseInt(tokens
					.get(INDEX_JUMP_DEST)));
		if (instLine.matches(PATTERN_COND_JUMP_INST))
			return parseCondJump(instLine, tokens);
		if (instLine.matches(PATTERN_ARITH_INST))
			return parseArithInst(instLine, tokens);
		if (instLine.matches(PATTERN_HALT_INST))
			return new Instruction(InstructionTag.HALT);
		if (instLine.matches(PATTERN_INDEX_INST))
			return parseIndexInst(instLine, tokens);
		checkForSyntaxErrors(instLine);
		throw new SyntaxErrorException(instPtr,
				messages.getString(ERROR_WRONG_FORMAT));
	}

	private static void checkForSyntaxErrors(String instLine)
			throws SyntaxErrorException {
		if (instLine.matches(PATTERN_NO_DEST_REG))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_NO_DEST_REG));
		if (instLine.matches(PATTERN_UNKNOWN_OPERATOR))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_UNKNOWN_OPERATOR));
		if (instLine.matches(PATTERN_NO_FIRST_OPERAND))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_NO_FIRST_OPERAND));
		if (instLine.matches(PATTERN_NO_SECOND_OPERAND))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_NO_SECOND_OPERAND));
		if (instLine.matches(PATTERN_NO_IP))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_NO_IP));
		if (instLine.matches(PATTERN_LD_MEM_IMM))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_LD_MEM_IMM));
		if (instLine.matches(PATTERN_LD_MEM_MEM))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_LD_MEM_MEM));
		if (instLine.matches(PATTERN_LD_I_MMEM))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_LD_I_MMEM));
		if (instLine.matches(PATTEN_LD_REG_REG))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTEN_LD_REG_REG));
		if (instLine.matches(PATTERN_FORGOT_COLON))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_FORGOT_COLON));
		if (instLine.matches(PATTERN_NO_THEN))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_NO_THEN));
		if (instLine.matches(PATTERN_IF_NOT_NULL))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_IF_NOT_NULL));
		if (instLine.matches(PATTERN_IF_UNKNWN_OP))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_IF_UNKNWN_OP));
		if (instLine.matches(PATTERN_INDEX_UNKWN_OP))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_INDEX_UNKWN_OP));
		if (instLine.matches(PATTERN_INDEX_OP2_NOT_ONE))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_PATTERN_INDEX_OP2_NOT_ONE));
	}

	/**
	 * Parsfunktion, wenn Befehlszeile ein bedingter Sprung ist
	 * 
	 * @param instLine
	 * @param tokens
	 * @return Instruktionsobjekt, was der Befehlszeile entspricht
	 * @throws SyntaxErrorException
	 */
	private static Instruction parseCondJump(String instLine,
			ArrayList<String> tokens) throws SyntaxErrorException {
		int p0, p1;
		p0 = parseOperand(tokens.get(INDEX_COND_JUMP_REG));
		p1 = Integer.parseInt(tokens.get(INDEX_COND_JUMP_DEST));

		switch (tokens.get(INDEX_COND_JUMP_IDENT)) {
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
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_INVALID_OPERATOR)
							+ tokens.get(INDEX_COND_JUMP_IDENT));
		}
	}

	/**
	 * Parsfunktion, wenn die Befehlszeile ein Arithmetikbefehl ist
	 * 
	 * @param instLine
	 * @param tokens
	 * @return Instruktionsobjekt, was der Befehlszeile entspricht
	 * @throws SyntaxErrorException
	 */
	private static Instruction parseArithInst(String instLine,
			ArrayList<String> tokens) throws SyntaxErrorException {
		int[] p = new int[2];
		if (tokens.get(INDEX_OP2).matches(PATTERN_MMEM)) {
			p = parseMMEM(tokens.get(INDEX_OP2));
			switch (tokens.get(INDEX_OPERATOR)) {
			case CASE_ADD:
				return new Instruction(InstructionTag.ADD_A_MMEM, p[0], p[1]);
			case CASE_SUB:
				return new Instruction(InstructionTag.SUB_A_MMEM, p[0], p[1]);
			case CASE_MUL:
				return new Instruction(InstructionTag.MUL_A_MMEM, p[0], p[1]);
			case CASE_DIV:
				return new Instruction(InstructionTag.DIV_A_MMEM, p[0], p[1]);
			case CASE_MOD:
				return new Instruction(InstructionTag.MOD_A_MMEM, p[0], p[1]);
			}
		}
		p[0] = parseOperand(tokens.get(INDEX_OP2));
		if (tokens.get(INDEX_OP2).matches(PATTERN_MEM)) {
			switch (tokens.get(INDEX_OPERATOR)) {
			case CASE_ADD:
				return new Instruction(InstructionTag.ADD_A_MEM, p[0]);
			case CASE_SUB:
				return new Instruction(InstructionTag.SUB_A_MEM, p[0]);
			case CASE_MUL:
				return new Instruction(InstructionTag.MUL_A_MEM, p[0]);
			case CASE_DIV:
				return new Instruction(InstructionTag.DIV_A_MEM, p[0]);
			case CASE_MOD:
				return new Instruction(InstructionTag.MOD_A_MEM, p[0]);
			}
		}
		if (tokens.get(INDEX_OP2).matches(PATTERN_IMM)) {
			switch (tokens.get(INDEX_OPERATOR)) {
			case CASE_ADD:
				return new Instruction(InstructionTag.ADD_A_IMM, p[0]);
			case CASE_SUB:
				return new Instruction(InstructionTag.SUB_A_IMM, p[0]);
			case CASE_MUL:
				return new Instruction(InstructionTag.MUL_A_IMM, p[0]);
			case CASE_DIV:
				return new Instruction(InstructionTag.DIV_A_IMM, p[0]);
			case CASE_MOD:
				return new Instruction(InstructionTag.MOD_A_IMM, p[0]);
			}
		}
		throw new SyntaxErrorException(instPtr,
				messages.getString(ERROR_WRONG_FORMAT));
	}

	/**
	 * Parsfunktion, wenn die Befehlszeile ein Transportbefehl ist
	 * 
	 * @param instLine
	 * @param tokens
	 * @return Instruktionsobjekt, was der Befehlszeile entspricht
	 * @throws SyntaxErrorException
	 */
	private static Instruction parseLoadInst(String instLine,
			ArrayList<String> tokens) throws SyntaxErrorException {
		String dest = tokens.get(INDEX_DESTINATION);
		String op1 = tokens.get(INDEX_OP1);
		int[] p = new int[2];

		if (dest.matches(PATTERN_AKKU) && op1.matches(PATTERN_MMEM)) {
			p = parseMMEM(op1);
			return new Instruction(InstructionTag.LD_A_MMEM, p[0], p[1]);
		}
		if (dest.matches(PATTERN_MMEM) && op1.matches(PATTERN_AKKU)) {
			p = parseMMEM(dest);
			return new Instruction(InstructionTag.LD_MMEM_A, p[0], p[1]);
		}
		p[0] = parseOperand(dest);
		p[1] = parseOperand(op1);
		if (dest.matches(PATTERN_REG) && op1.matches(PATTERN_IMM))
			return new Instruction(InstructionTag.LD_REG_IMM, p[0], p[1]);
		if (dest.matches(PATTERN_REG) && op1.matches(PATTERN_MEM))
			return new Instruction(InstructionTag.LD_REG_MEM, p[0], p[1]);
		if (dest.matches(PATTERN_MEM) && op1.matches(PATTERN_REG))
			return new Instruction(InstructionTag.LD_MEM_REG, p[0], p[1]);
		throw new SyntaxErrorException(instPtr,
				messages.getString(ERROR_WRONG_FORMAT));
	}

	/**
	 * Parsfunktion, wenn die Befehlszeile ein Indexbefehl ist
	 * 
	 * @param instLine
	 * @param tokens
	 * @return Instruktionsobjekt, was der Befehlszeile entspricht
	 * @throws SyntaxErrorException
	 */
	private static Instruction parseIndexInst(String instLine,
			ArrayList<String> tokens) throws SyntaxErrorException {
		String p0Token = tokens.get(INDEX_DESTINATION);
		if (!p0Token.equals(tokens.get(INDEX_OP1)))
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_INDEX_DEST_NE_OP1));
		int p0;
		// Index index bekommen
		p0Token = p0Token.replace(CASE_INDEX, "0");
		p0 = Integer.parseInt(p0Token);

		switch (tokens.get(INDEX_OPERATOR)) {
		case CASE_ADD:
			return new Instruction(InstructionTag.IDX_INC, p0);
		case CASE_SUB:
			return new Instruction(InstructionTag.IDX_DEC, p0);
		default:
			throw new SyntaxErrorException(instPtr,
					messages.getString(ERROR_INVALID_OPERATOR) + TOKEN
							+ tokens.get(INDEX_OPERATOR));
		}
	}

	/**
	 * Parsfunktion zum parsen eines modifizierten Speichers
	 * 
	 * @param token
	 * @return Instruktionsobjekt, was der Befehlszeile entspricht
	 * @throws SyntaxErrorException
	 */
	private static int[] parseMMEM(String token) throws SyntaxErrorException {
		int[] ops = new int[2];
		if (token.matches(PATTERN_MMEM)) {
			token = token.replaceAll("s\\[i(.*)\\]", "0$1");
			String[] s = token.split("\\+");
			ops[0] = Integer.parseInt(s[0]);
			if (s.length == 2)
				ops[1] = Integer.parseInt(s[1]);
			return ops;
		}
		throw new SyntaxErrorException(instPtr,
				messages.getString(ERROR_WRONG_FORMAT) + TOKEN + token);
	}

	/**
	 * Parsfunktion zum Parsen von allen möglichen Operanden (außer MMEM)
	 * 
	 * @param token
	 * @return Instruktionsobjekt, was der Befehlszeile entspricht
	 * @throws SyntaxErrorException
	 */
	private static int parseOperand(String token) throws SyntaxErrorException {
		int op;
		if (token.matches(PATTERN_AKKU)) {
			op = -1;
			return op;
		}
		if (token.matches(PATTERN_IMM)) {
			op = Integer.parseInt(token);
			return op;
		}
		if (token.matches(PATTERN_MEM)) {
			token = token.replaceAll("s\\[(.*)\\]", "$1");
			op = Integer.parseInt(token);
			return op;
		}
		if (token.matches(PATTERN_INDEX)) {
			token = token.replaceAll("i", "0");
			op = Integer.parseInt(token);
			return op;
		}
		throw new SyntaxErrorException(instPtr,
				messages.getString(ERROR_WRONG_FORMAT) + TOKEN + token);
	}
}
