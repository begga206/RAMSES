package ramses;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.text.*;

/**
 * Appletklasse, welche die grafische Oberfläche realisiert
 * 
 * @author Lukas
 *
 */
public class MyApplet extends JApplet {
	public static final String PROMPT = "RAMSES> ";
	public static final String CMD_GUI = "gui";
	public static final String CMD_COMPILE = "compile .+\\.txt";
	public static final String CMD_RUN = "run";
	public static final String CMD_DEBUG = "debug";
	public static final String CMD_CLEAR = "clear";
	public static final String ERROR_UNSUPPORTED_CMD = "This command is not supported.";

	// ///////////GUI ELEMENTE//////////
	Container c;
	JPanel leftPanel;
	JPanel centerPanel;
	JPanel rightPanel;
	JButton compile;
	JButton start;
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
		console = new JTextArea(10, 70);
		console.setEditable(true);
		console.setLineWrap(true);
		console = new JTextArea(PROMPT);
		((AbstractDocument) console.getDocument())
				.setDocumentFilter(new NonEditableLineDocumentFilter());
		console.select(PROMPT.length(), PROMPT.length());
		compile = new JButton("Compile");
		start = new JButton("Start");
		start.setEnabled(false);

		input = null;
		data = new ArrayList<>();
		inst = new ArrayList<>();

		c.add(new JScrollPane(console));
		/*
		 * c.add(leftPanel); c.add(centerPanel); c.add(rightPanel);
		 */
	}

	/**
	 * Initialisiert das linke Panel
	 */
	private void initLeftPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		buttonPanel.add(compile);
		buttonPanel.add(start);

		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		leftPanel.add(new JLabel("Editor:"));
		leftPanel.add(new JScrollPane(editor));
		leftPanel.add(new JLabel("Console:"));
		leftPanel.add(new JScrollPane(console));
		leftPanel.add(buttonPanel);
	}

	/**
	 * Initialisiert das mittlere Panel
	 */
	private void initCenterPanel() {
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.add(new JLabel("Input: "));
		fillInputLabels();
	}

	/**
	 * Füllt das mittlere Panel mit den Input-eingaben
	 */
	private void fillInputLabels() {
		// existierende Inputlabels löschen
		centerPanel.removeAll();
		centerPanel.add(new JLabel("Input:"));
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
					input = Parser.parseInput(data.get(0));
					output = Parser.parseOutput(data.get(1));
					for (int i = 0; i < data.size() - 2; i++) {
						inst.add(Parser.parseInst(i, data.get(i + 2)));
					}
					ramses = new Ramses(input, output, inst);
					fillInputLabels();
					centerPanel.revalidate();
					start.setEnabled(true);
					console.setText("Compiling successful.\n");
				} catch (SyntaxErrorException e) {
					start.setEnabled(false);
					console.append(e + "\n");
				} catch (LogicalErrorException e) {
					start.setEnabled(false);
					console.append(e + "\n");
				}
			}
		});

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					console.setText("");
					for (int i = 1; i < centerPanel.getComponentCount(); i++) {
						if (centerPanel.getComponent(i) instanceof InputLabel) {
							InputLabel label = (InputLabel) centerPanel
									.getComponent(i);
							label.setValue();
						}
					}
					ramses.run();
					createTable();
				} catch (NumberFormatException e) {
					console.append(e + "\tEvery input needs a valid number\n");
				} catch (LogicalErrorException e) {
					console.append(e + "\n");
				}
			}
		});
	}

	/**
	 * Erstellt einen JTable anhand der Matrix aus RAMSES
	 */
	private void createTable() {
		matrix = ramses.getTable();
		String[] columnNames = matrix.get(0).toArray(
				new String[matrix.get(0).size()]);
		matrix.remove(0);
		Object[][] data = new Object[matrix.size()][columnNames.length];
		for (int i = 0; i < matrix.size(); i++) {
			for (int j = 0; j < columnNames.length; j++) {
				data[i][j] = matrix.get(i).get(j);
			}
		}
		table = new JTable(data, columnNames);
		rightPanel.removeAll();
		rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
		rightPanel.revalidate();
	}

	class NonEditableLineDocumentFilter extends DocumentFilter {
		private boolean next = false;
		private boolean in = false;
		private int value = 0;

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
						text = "\n" + PROMPT;
					} else {
						parseCmd(cmd);
					}
				}
				fb.replace(offset, length, text, attrs);
			}
		}

		private void parseCmd(String cmd) {
			if (cmd.matches(CMD_GUI)) {
				c.removeAll();
				initLeftPanel();
				initCenterPanel();
				initListeners();
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
				in = true;
				for (int i = 0; i < input.length; i++) {
					console.append("s[" + input[i].getIndex() + "]\n" + PROMPT);
					next = false;
					// while(!next){};
					input[i].setValue(value);
				}
				in = false;

			} else if (in && cmd.matches("-?\\d+")) {
				value = Integer.parseInt(cmd);
				next = true;
			} else {
				console.append(ERROR_UNSUPPORTED_CMD + "\n" + PROMPT);
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
				console.append("\n" + e);
			}
			try {
				input = Parser.parseInput(data.get(0));
				output = Parser.parseOutput(data.get(1));
				for (int i = 0; i < data.size() - 2; i++) {
					inst.add(Parser.parseInst(i, data.get(i + 2)));
				}
				ramses = new Ramses(input, output, inst);
				console.append(PROMPT);
			} catch (SyntaxErrorException e) {
				console.append("\n" + e);
			} catch (LogicalErrorException e) {
				console.append("\n" + e);
			}
		}
	}

}
