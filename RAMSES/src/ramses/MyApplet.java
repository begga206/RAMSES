package ramses;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;

/**
 * Appletklasse, welche die grafische Oberfläche realisiert
 * 
 * @author Lukas
 *
 */
public class MyApplet extends JApplet {
	private static final long serialVersionUID = 1L;

	public static final String ERROR_MIN_GT_MAX = "ERROR_MIN_GT_MAX";
	public static final String ERROR_MAX_LT_MIN = "ERROR_MAX_LT_MIN";
	public static final String ERROR_NOT_ALL_INPUT_SET = "ERROR_NOT_ALL_INPUT_SET";

	public static final String COMPILE = "COMPILE";
	public static final String START = "START";
	public static final String DEBUG = "DEBUG";
	public static final String NEXT_INST = "NEXT_INST";
	public static final String CONTINUE = "CONTINUE";
	public static final String CLI_KEY = "CLI_KEY";
	public static final String BP_BUTTON = "BP_BUTTON";
	public static final String INPUT = "INPUT";
	public static final String VALID_INT = "VALID_INT";
	public static final String EDITOR = "EDITOR";
	public static final String CONSOLE = "CONSOLE";
	public static final String COMPILING_SUCCESSFUL = "COMPILING_SUCCESSFUL";
	public static final String INVALID_FILE_SIZE = "INVALID_FILE_SIZE";

	public static final String PROMPT = "RAMSES> ";
	public static final String CLI_HELP_TEXT = "CLI_HELP_TEXT";
	public static final String CMD_GUI = "gui";
	public static final String CMD_COMPILE = "compile .+\\.txt";
	public static final String CMD_RUN = "run( -?\\d+)*";
	public static final String CMD_DEBUG = "debug";
	public static final String CMD_CLEAR = "clear";
	public static final String CMD_MIN = "min( -?\\d+)?";
	public static final String CMD_MAX = "max( -?\\d+)?";
	public static final String CMD_HELP = "help";
	public static final String ERROR_UNSUPPORTED_CMD = "This command is not supported.";

	public static final int MIN_VALUE = 0;
	public static final int MAX_VALUE = 50;

	private int minValue;
	private int maxValue;
	private int breakpoint;

	ResourceBundle messages = ResourceBundle.getBundle("ramses.MessagesBundle",
			Locale.getDefault());

	// ///////////GUI ELEMENTE//////////
	Container c;
	JPanel leftPanel;
	JPanel centerPanel;
	JPanel inputPanel;
	JPanel rightPanel;
	JButton compileButton;
	JButton startButton;
	JButton debugButton;
	JButton continueButton;
	JButton nextButton;
	JButton cliButton;
	JButton setBpButton;
	JTable table;
	JTextArea editor;
	JTextArea console;
	JTextField bpTf;

	// //////////RAMSES ELEMENTE////////
	Input[] input;
	int[] output;
	ArrayList<String> data;
	ArrayList<Instruction> inst;
	ArrayList<ArrayList<String>> matrix;
	Ramses ramses;

	/**
	 * Initialisiert die Oberfläche mit Komponenten
	 */
	public void init() {
		minValue = MIN_VALUE;
		maxValue = MAX_VALUE;
		breakpoint = 0;

		c = getContentPane();
		c.setLayout(new GridLayout(1, 3));
		leftPanel = new JPanel();
		centerPanel = new JPanel();
		inputPanel = new JPanel();
		rightPanel = new JPanel(new BorderLayout());

		editor = new JTextArea(40, 70);
		editor.setLineWrap(true);
		console = new JTextArea(PROMPT);
		console.setCaretPosition(PROMPT.length());
		console.setFont(new Font("monospaced", Font.PLAIN, 12));
		((AbstractDocument) console.getDocument())
				.setDocumentFilter(new NonEditableLineDocumentFilter());
		PrintStream printStream = new PrintStream(new CustomOutputStream(
				console));
		System.setOut(printStream);
		System.setErr(printStream);
		compileButton = new JButton(messages.getString(COMPILE));
		startButton = new JButton(messages.getString(START));
		debugButton = new JButton(messages.getString(DEBUG));
		continueButton = new JButton(messages.getString(CONTINUE));
		nextButton = new JButton(messages.getString(NEXT_INST));
		cliButton = new JButton(messages.getString(CLI_KEY));
		setBpButton = new JButton(messages.getString(BP_BUTTON));
		bpTf = new JTextField(Integer.toString(breakpoint), 5);
		startButton.setEnabled(false);
		debugButton.setEnabled(false);
		continueButton.setVisible(false);
		nextButton.setVisible(false);

		input = null;
		data = new ArrayList<>();
		inst = new ArrayList<>();

		initListeners();

		c.add(new JScrollPane(console));
	}

	/**
	 * Initialisiert das linke Panel
	 */
	private void initLeftPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		buttonPanel.add(cliButton);
		buttonPanel.add(compileButton);
		buttonPanel.add(debugButton);
		buttonPanel.add(continueButton);
		buttonPanel.add(nextButton);
		buttonPanel.add(startButton);

		console.setEditable(false);
		console.setLineWrap(true);
		console.setRows(10);
		console.setColumns(70);

		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		leftPanel.add(new JLabel(messages.getString(EDITOR)));
		leftPanel.add(new JScrollPane(editor));
		leftPanel.add(new JLabel(messages.getString(CONSOLE)));
		leftPanel.add(new JScrollPane(console));
		leftPanel.add(buttonPanel);
	}

	private void initCenterPanel() {
		centerPanel.removeAll();
		centerPanel.setLayout(new BorderLayout());
		JPanel minMaxPanel = new JPanel();
		final JTextField minTf = new JTextField(Integer.toString(minValue), 5);
		final JTextField maxTf = new JTextField(Integer.toString(maxValue), 5);
		JButton minButton = new JButton("set");
		minButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String min = minTf.getText();
					setMinValue(Integer.parseInt(min));
				} catch (NumberFormatException | LogicalErrorException e) {
					minTf.setText(Integer.toString(minValue));
					System.out.println(e);
				}
			}

		});
		JButton maxButton = new JButton("set");
		maxButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String max = maxTf.getText();
					setMaxValue(Integer.parseInt(max));
				} catch (NumberFormatException | LogicalErrorException e1) {
					maxTf.setText(Integer.toString(maxValue));
					System.out.println(e);
				}
			}

		});
		minMaxPanel.add(new JLabel("Min:"));
		minMaxPanel.add(minTf);
		minMaxPanel.add(minButton);
		minMaxPanel.add(new JLabel("Max:"));
		minMaxPanel.add(maxTf);
		minMaxPanel.add(maxButton);
		JPanel southPanel = new JPanel();
		southPanel.add(bpTf);
		southPanel.add(setBpButton);
		centerPanel.add(minMaxPanel, BorderLayout.NORTH);
		centerPanel.add(inputPanel, BorderLayout.CENTER);
		centerPanel.add(southPanel, BorderLayout.SOUTH);
	}

	/**
	 * Füllt das mittlere Panel mit den Input-eingaben
	 */
	private void fillInputPanel() {
		// existierende Inputlabels löschen
		inputPanel.removeAll();
		inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		inputPanel.add(new JLabel(messages.getString(INPUT)));
		// neue Inputlabels einfügen
		if (input != null) {
			for (int i = 0; i < input.length; i++) {
				inputPanel.add(new InputLabel(input[i]));
			}
		}
	}

	/**
	 * Initialisiert die Buttonlistener
	 */
	private void initListeners() {
		cliButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				console = new JTextArea(PROMPT);
				console.setFont(new Font("monospaced", Font.PLAIN, 12));
				((AbstractDocument) console.getDocument())
						.setDocumentFilter(new NonEditableLineDocumentFilter());
				PrintStream printStream = new PrintStream(
						new CustomOutputStream(console));
				System.setOut(printStream);
				System.setErr(printStream);
				leftPanel.removeAll();
				centerPanel.removeAll();
				c.removeAll();
				c.add(new JScrollPane(console));
				revalidate();
			}

		});

		compileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				data = new ArrayList<>();
				inst = new ArrayList<Instruction>();
				try (Scanner sc = new Scanner(editor.getText())) {
					sc.useDelimiter("\n");
					while (sc.hasNext()) {
						data.add(sc.next());
					}
				}
				try {
					if (data.size() <= 2)
						throw new SyntaxErrorException(-2, messages
								.getString(INVALID_FILE_SIZE));
					input = Parser.parseInput(data.get(0));
					output = Parser.parseOutput(data.get(1));
					for (int i = 0; i < data.size() - 2; i++) {
						inst.add(Parser.parseInstruction(i, data.get(i + 2)));
					}
					ramses = new Ramses(input, output, inst);
					fillInputPanel();
					startButton.setEnabled(true);
					debugButton.setEnabled(true);
					console.setText(messages.getString(COMPILING_SUCCESSFUL));
				} catch (SyntaxErrorException | LogicalErrorException e) {
					inputPanel.removeAll();
					debugButton.setEnabled(false);
					startButton.setEnabled(false);
					console.setText(e.toString());
				} finally {
					inputPanel.setVisible(false);
					inputPanel.setVisible(true);
				}
			}
		});

		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					console.setText("");
					debugButton.setEnabled(false);
					compileButton.setEnabled(false);
					ramses.setDebug(false);
					for (int i = 1; i < inputPanel.getComponentCount(); i++) {
						if (inputPanel.getComponent(i) instanceof InputLabel) {
							InputLabel label = (InputLabel) inputPanel
									.getComponent(i);
							if (label.isRandom()) {
								int r = getRandom();
								label.getInput().setValue(r);
								label.setText(Integer.toString(r));
							} else
								label.setValue();
						}
					}
					ramses = new Ramses(input, output, inst);
					ramses.start();
					while (ramses.isLocked()) {
						// mehr polling als im erblühenden Schwarzwald
					}
					createTable();
					synchronized (ramses) {
						ramses.notify();
					}
					debugButton.setEnabled(true);
					compileButton.setEnabled(true);
				} catch (NumberFormatException e) {
					System.out.println(e + "\t" + messages.getString(VALID_INT)
							+ "\n");
				} catch (LogicalErrorException e) {
					System.out.println(e);
				}
			}
		});

		debugButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					for (int i = 1; i < inputPanel.getComponentCount(); i++) {
						if (inputPanel.getComponent(i) instanceof InputLabel) {
							InputLabel label = (InputLabel) inputPanel
									.getComponent(i);
							if (label.isRandom()) {
								int r = getRandom();
								label.getInput().setValue(r);
								label.setText(Integer.toString(r));
							} else
								label.setValue();
						}
					}
					ramses = new Ramses(input, output, inst);
					ramses.setDebug(true);
					ramses.setBreakpoint(breakpoint);
					ramses.start();
					debugButton.setVisible(false);
					startButton.setVisible(false);
					continueButton.setVisible(true);
					nextButton.setVisible(true);
					new Thread() {
						public void run() {
							while (ramses.isLocked()) {
							}
							createTable();
						}
					}.start();
				} catch (NumberFormatException e) {
					System.out.println(e + "\t" + messages.getString(VALID_INT)
							+ "\n");
				} catch (LogicalErrorException e) {
					System.out.println(e);
				}
			}
		});

		continueButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!ramses.isLocked()) {
					synchronized (ramses) {
						ramses.setBreakpoint(breakpoint);
						ramses.notify();
						new Thread() {
							public void run() {
								while (ramses.isLocked()) {
								}
								createTable();
								if (!ramses.isAlive()) {
									continueButton.setVisible(false);
									nextButton.setVisible(false);
									debugButton.setVisible(true);
									startButton.setVisible(true);
								}
							}
						}.start();
					}
				}
			}
		});

		nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!ramses.isLocked()) {
					synchronized (ramses) {
						ramses.setBreakpoint(-1);
						ramses.notify();
						new Thread() {
							public void run() {
								while (ramses.isLocked()) {
								}
								createTable();
								if (!ramses.isAlive()) {
									continueButton.setVisible(false);
									nextButton.setVisible(false);
									debugButton.setVisible(true);
									startButton.setVisible(true);
								}
							}
						}.start();
					}
				}
			}
		});

		setBpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String bp = bpTf.getText();
					breakpoint = Integer.parseInt(bp);
				} catch (NumberFormatException e) {
					bpTf.setText(Integer.toString(breakpoint));
					System.out.println(e);
				}
			}

		});
	}

	/**
	 * Erstellt einen JTable anhand der Matrix aus RAMSES
	 */
	private void createTable() {
		matrix = new ArrayList<>(ramses.getTable());
		String[] columnNames = matrix.get(0).toArray(
				new String[matrix.get(0).size()]);
		matrix.remove(0);
		final Object[][] data = new Object[matrix.size()][columnNames.length];
		for (int i = 0; i < matrix.size(); i++) {
			for (int j = 0; j < columnNames.length; j++) {
				data[i][j] = matrix.get(i).get(j);
			}
		}
		table = new JTable(data, columnNames) {
			private static final long serialVersionUID = 1L;

			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if (this.isCellSelected(row, column))
					c.setBackground(this.getSelectionBackground());
				else {
					if (data[row][column].equals("")) {
						c.setBackground(this.getBackground());
					} else if (column == 0) {
						c.setBackground(Color.lightGray);
					} else {
						c.setBackground(Color.orange);
					}
				}
				return c;
			}
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		rightPanel.removeAll();
		rightPanel.add(new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				BorderLayout.CENTER);
		rightPanel.revalidate();
	}

	private void setMinValue(int value) throws LogicalErrorException {
		if (value >= maxValue)
			throw new LogicalErrorException(
					messages.getString(ERROR_MIN_GT_MAX));
		minValue = value;
	}

	private void setMaxValue(int value) throws LogicalErrorException {
		if (value <= minValue)
			throw new LogicalErrorException(
					messages.getString(ERROR_MAX_LT_MIN));
		maxValue = value;
	}

	private int getRandom() {
		Random random = new Random();
		return random.nextInt((maxValue - minValue) + 1) + minValue;
	}

	private class NonEditableLineDocumentFilter extends DocumentFilter {
		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset,
				String string, AttributeSet attr) throws BadLocationException {
			if (string == null) {
				return;
			} else {
				replace(fb, offset, 0, string, attr);
			}
		}

		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset,
				int length) throws BadLocationException {
			replace(fb, offset, length, "", null);
		}

		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset,
				int length, String text, AttributeSet attrs)
				throws BadLocationException {
			Document doc = fb.getDocument();
			Element root = doc.getDefaultRootElement();
			int count = root.getElementCount();
			int index = root.getElementIndex(offset);
			Element cur = root.getElement(index);
			int promptPosition = cur.getStartOffset() + PROMPT.length();
			if (index == count - 1 && offset - promptPosition >= 0) {
				if (text.equals("\n")) {
					String cmd = doc.getText(promptPosition, offset
							- promptPosition);
					if (cmd.isEmpty()) {
						text = "\n";
					} else {
						parseCmd(cmd);
					}
					text += PROMPT;
				}

				fb.replace(offset, length, text, attrs);
			}
		}

		private void parseCmd(String cmd) {
			try {
				if (cmd.matches(CMD_GUI)) {
					c.removeAll();
					((AbstractDocument) console.getDocument())
							.setDocumentFilter(null);
					console.setText("");
					initLeftPanel();
					initCenterPanel();
					fillInputPanel();
					c.add(leftPanel);
					c.add(centerPanel);
					c.add(rightPanel);
					if (ramses != null){
						startButton.setEnabled(true);
						debugButton.setEnabled(true);
					}
					c.setVisible(false);
					c.setVisible(true);
				} else if (cmd.matches(CMD_COMPILE)) {
					compile(cmd);
				} else if (cmd.matches(CMD_CLEAR)) {
					((AbstractDocument) console.getDocument())
							.setDocumentFilter(null);
					console.setText(PROMPT);
					((AbstractDocument) console.getDocument())
							.setDocumentFilter(new NonEditableLineDocumentFilter());
				} else if (cmd.matches(CMD_RUN)) {
					if (cmd.equals("run")) {
						for (int i = 0; i < input.length; i++) {
							input[i].setValue(getRandom());
						}
					} else {
						cmd = cmd.replace("run ", "");
						try (Scanner sc = new Scanner(cmd)) {
							int i = 0;
							while (sc.hasNext()) {
								input[i].setValue(Integer.parseInt(sc.next()));
								i++;
							}
							if (i < input.length)
								throw new LogicalErrorException(
										messages.getString(ERROR_NOT_ALL_INPUT_SET));
						}
					}
					run();
				} else if (cmd.matches(CMD_MIN)) {
					if (cmd.equals("min"))
						System.out.println("min = " + minValue);
					else {
						cmd = cmd.replace("min ", "");
						setMinValue(Integer.parseInt(cmd));
					}
				} else if (cmd.matches(CMD_MAX)) {
					if (cmd.equals("max"))
						System.out.println("max = " + maxValue);
					else {
						cmd = cmd.replace("max ", "");
						setMaxValue(Integer.parseInt(cmd));
					}
				} else if (cmd.matches(CMD_HELP)) {
					System.out.println(messages.getString(CLI_HELP_TEXT));
				}
			} catch (NumberFormatException | LogicalErrorException e) {
				System.out.println(e);
			}
		}

		private void run() {
			try {
				ramses.setDebug(false);
				ramses = new Ramses(input, output, inst);
				ramses.start();
				new Thread() {
					public void run() {
						while (ramses.isAlive()) {
							if (!ramses.isLocked()) {
								matrix = ramses.getTable();
								synchronized (ramses) {
									ramses.notify();
								}
								for (int i = 0; i < matrix.size(); i++) {
									// String s = "\n";
									for (int j = 0; j < matrix.get(0).size(); j++) {
										System.out
												.print(String.format("%-25s|",
														matrix.get(i).get(j)));
										// s += "--------------------------";
									}
									System.out.println();// s);
								}
								return;
							}
							
						}
					}
				}.start();

			} catch (LogicalErrorException e) {
				System.out.println(e);
			}
		}

		private void compile(String cmd) {
			data = new ArrayList<>();
			inst = new ArrayList<Instruction>();
			String path = cmd.replace("compile ", "");

			try (BufferedReader br = new BufferedReader(new FileReader(
					new File(path)))) {
				while (br.ready()) {
					data.add(br.readLine());
				}
			} catch (IOException e) {
				System.out.println(e);
			}
			try {
				input = Parser.parseInput(data.get(0));
				output = Parser.parseOutput(data.get(1));
				for (int i = 0; i < data.size() - 2; i++) {
					inst.add(Parser.parseInstruction(i, data.get(i + 2)));
				}
				ramses = new Ramses(input, output, inst);
				System.out.println(messages.getString(COMPILING_SUCCESSFUL));
			} catch (SyntaxErrorException e) {
				System.out.println(e);
			} catch (LogicalErrorException e) {
				System.out.println(e);
			}
		}
	}
}
