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
	public static final String COMPILE = "COMPILE";
	public static final String START = "START";
	public static final String DEBUG = "DEBUG";
	public static final String NEXT_INST = "NEXT_INST";
	public static final String INPUT = "INPUT";
	public static final String VALID_INT = "VALID_INT";
	public static final String EDITOR = "EDITOR";
	public static final String CONSOLE = "CONSOLE";
	public static final String COMPILING_SUCCESSFUL = "COMPILING_SUCCESSFUL";
	public static final String INVALID_FILE_SIZE = "INVALID_FILE_SIZE";
	
	public static final String PROMPT = "RAMSES> ";
	public static final String CMD_GUI = "gui";
	public static final String CMD_COMPILE = "compile .+\\.txt";
	public static final String CMD_RUN = "run";
	public static final String CMD_DEBUG = "debug";
	public static final String CMD_CLEAR = "clear";
	public static final String ERROR_UNSUPPORTED_CMD = "This command is not supported.";
	
	public static final int MIN_VALUE = 0;
	public static final int MAX_VALUE = 50;
	
	ResourceBundle messages = ResourceBundle.getBundle("ramses.MessagesBundle", Locale.getDefault());

	// ///////////GUI ELEMENTE//////////
	Container c;
	JPanel leftPanel;
	JPanel centerPanel;
	JPanel rightPanel;
	JButton compile;
	JButton start;
	JButton debug;
	JButton next;
	JTable table;
	JTextArea editor;
	JTextArea console;

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
		c = getContentPane();
		c.setLayout(new GridLayout(1, 3));
		leftPanel = new JPanel();
		centerPanel = new JPanel();
		rightPanel = new JPanel(new BorderLayout());

		editor = new JTextArea(40, 70);
		editor.setLineWrap(true);
		console = new JTextArea(PROMPT);
		console.setFont(new Font("monospaced", Font.PLAIN, 12));
		((AbstractDocument) console.getDocument())
		.setDocumentFilter(new NonEditableLineDocumentFilter());
		PrintStream printStream = new PrintStream(new CustomOutputStream(console));
		System.setOut(printStream);
		System.setErr(printStream);
		compile = new JButton(messages.getString(COMPILE));
		start = new JButton(messages.getString(START));
		debug = new JButton(messages.getString(DEBUG));
		next = new JButton(messages.getString(NEXT_INST));
		start.setEnabled(false);
		debug.setEnabled(false);
		next.setEnabled(false);

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
		buttonPanel.add(compile);
		buttonPanel.add(debug);
		buttonPanel.add(next);
		buttonPanel.add(start);
		
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
	
	/**
	 * Füllt das mittlere Panel mit den Input-eingaben
	 */
	private void fillInputLabels() {
		// existierende Inputlabels löschen
		centerPanel.removeAll();
		centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		centerPanel.add(new JLabel(messages.getString(INPUT)));
		// neue Inputlabels einfügen
		if (input != null) {
			for (int i = 0; i < input.length; i++) {
				centerPanel.add(new InputLabel(input[i]));
			}
		}
	}

	/**
	 * Initialisiert die Buttonlistener
	 */
	private void initListeners() {
		compile.addActionListener(new ActionListener() {

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
					if(data.size() <= 2)
						throw new SyntaxErrorException(-2, messages.getString(INVALID_FILE_SIZE));
					input = Parser.parseInput(data.get(0));
					output = Parser.parseOutput(data.get(1));
					for (int i = 0; i < data.size() - 2; i++) {
						inst.add(Parser.parseInst(i, data.get(i + 2)));
					}
					ramses = new Ramses(input, output, inst);
					fillInputLabels();
					start.setEnabled(true);
					debug.setEnabled(true);
					console.setText(messages.getString(COMPILING_SUCCESSFUL));
				} catch (SyntaxErrorException | LogicalErrorException e) {
					centerPanel.removeAll();
					debug.setEnabled(false);
					start.setEnabled(false);
					console.setText(e.toString());
				} finally {
					centerPanel.setVisible(false);
					centerPanel.setVisible(true);
				}
			}
		});

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					console.setText("");
					debug.setEnabled(false);
					compile.setEnabled(false);
					ramses.setDebug(false);
					for (int i = 1; i < centerPanel.getComponentCount(); i++) {
						if (centerPanel.getComponent(i) instanceof InputLabel) {
							InputLabel label = (InputLabel) centerPanel
									.getComponent(i);
							label.setValue();
						}
					}
					ramses = new Ramses(input, output, inst);
					ramses.start();
					while(ramses.isLocked()){
						//mehr polling als im erblühenden Schwarzwald
					}
					createTable();
					synchronized(ramses){
						ramses.notify();
					}
					debug.setEnabled(true);
					compile.setEnabled(true);
				} catch (NumberFormatException e) {
					System.out.println(e + "\t" + messages.getString(VALID_INT) + "\n");
				} catch (LogicalErrorException e) {
					System.out.println(e);
				}
			}
		});

		debug.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					for (int i = 1; i < centerPanel.getComponentCount(); i++) {
						if (centerPanel.getComponent(i) instanceof InputLabel) {
							InputLabel label = (InputLabel) centerPanel
									.getComponent(i);
							label.setValue();
						}
						ramses = new Ramses(input, output, inst);
					}
				} catch (NumberFormatException e) {
					System.out.println(e + "\t" + messages.getString(VALID_INT)
							+ "\n");
				} catch (LogicalErrorException e) {
					System.out.println(e);
				}
				ramses.setDebug(true);
				ramses.start();
				debug.setEnabled(false);
				start.setEnabled(false);
				next.setEnabled(true);
				next.doClick();
			}
		});

		next.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!ramses.isAlive()){
					next.setEnabled(false);
					debug.setEnabled(true);
					start.setEnabled(true);
				}
				if(!ramses.isLocked()){
					createTable();
					synchronized(ramses){
						ramses.notify();
					}
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
		table = new JTable(data, columnNames){
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);
				if(this.isCellSelected(row, column))
					c.setBackground(this.getSelectionBackground());
				else{
					if(data[row][column].equals("")){
						c.setBackground(this.getBackground());
					}else if(column == 0){
						c.setBackground(Color.lightGray);
					}else{
						c.setBackground(Color.orange);
					}
				}
				return c;
			}
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		rightPanel.removeAll();
		rightPanel.add(new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
		rightPanel.revalidate();
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
			// As Reverend Gonzo says:
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
			if (cmd.matches(CMD_GUI)) {
				c.removeAll();
				((AbstractDocument) console.getDocument())
				.setDocumentFilter(null);
				console.setText("");
				initLeftPanel();
				initListeners();
				fillInputLabels();
				c.add(leftPanel);
				c.add(centerPanel);
				c.add(rightPanel);
				if (ramses != null)
					start.setEnabled(true);
				c.revalidate();
			} else if (cmd.matches(CMD_COMPILE)) {
				compile(cmd);
			} else if (cmd.matches(CMD_CLEAR)) {
				((AbstractDocument) console.getDocument())
						.setDocumentFilter(null);
				console.setText(PROMPT);
				((AbstractDocument) console.getDocument())
						.setDocumentFilter(new NonEditableLineDocumentFilter());
			} else if (cmd.matches(CMD_RUN)) {
				run();
			}
		}

		private void run() {
			try {
				Random random = new Random();
				ramses.setDebug(false);
				for (int i = 0; i < input.length; i++) {
					input[i].setValue(random.nextInt((MAX_VALUE - MIN_VALUE) + 1) + MIN_VALUE);
				}
				ramses = new Ramses(input, output, inst);
				ramses.start();
				while (ramses.isLocked()) {
					// mehr polling als im erblühenden Schwarzwald
				}
				matrix = ramses.getTable();
				synchronized (ramses) {
					ramses.notify();
				}
				for (int i = 0; i < matrix.size(); i++) {
					//String s = "\n";
					for (int j = 0; j < matrix.get(0).size(); j++) {
						System.out.print(String.format("%-25s|",matrix.get(i).get(j)));
						//s += "--------------------------";
					}
					System.out.println();//s);
				}
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
					inst.add(Parser.parseInst(i, data.get(i + 2)));
				}
				ramses = new Ramses(input, output, inst);
			} catch (SyntaxErrorException e) {
				System.out.println(e);
			} catch (LogicalErrorException e) {
				System.out.println(e);
			}
		}
	}
}
