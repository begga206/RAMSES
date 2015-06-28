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

	//----------------------Errormessages--------------------------------------
	public static final String ERROR_MIN_GT_MAX = "ERROR_MIN_GT_MAX";
	public static final String ERROR_MAX_LT_MIN = "ERROR_MAX_LT_MIN";
	public static final String ERROR_NOT_ALL_INPUT_SET = 
			"ERROR_NOT_ALL_INPUT_SET";
	public static final String ERROR_NO_COMPILED_PROG = 
			"ERROR_NO_COMPILED_PROG";
	public static final String ERROR_WRONG_COMPILE_FORMAT = 
			"ERROR_WRONG_COMPILE_FORMAT";
	public static final String ERROR_VALID_INT = "ERROR_VALID_INT";
	//---------------------Button/Label Namen----------------------------------
	public static final String COMPILE = "COMPILE";
	public static final String START = "START";
	public static final String DEBUG = "DEBUG";
	public static final String NEXT_INST = "NEXT_INST";
	public static final String CONTINUE = "CONTINUE";
	public static final String CLI_KEY = "CLI_KEY";
	public static final String BP_BUTTON = "BP_BUTTON";
	public static final String INPUT = "INPUT";
	public static final String EDITOR = "EDITOR";
	public static final String CONSOLE = "CONSOLE";
	
	//-----------------------Konsolennachrichten-------------------------------
	public static final String COMPILING_SUCCESSFUL = "COMPILING_SUCCESSFUL";
	public static final String INVALID_FILE_SIZE = "INVALID_FILE_SIZE";

	//------------------CLI Kommandos/Prompt/Nachrichten-----------------------
	public static final String PROMPT = "RAMSES> ";
	public static final String CLI_HELP_TEXT = "CLI_HELP_TEXT";
	public static final String CMD_GUI = "gui";
	public static final String CMD_COMPILE = "compile .+\\.txt";
	public static final String CMD_WRONG_COMPILE = "compile.*";
	public static final String CMD_RUN = "run( -?\\d+)*";
	public static final String CMD_DEBUG = "debug";
	public static final String CMD_CLEAR = "clear";
	public static final String CMD_MIN = "min( -?\\d+)?";
	public static final String CMD_MAX = "max( -?\\d+)?";
	public static final String CMD_HELP = "help";
	public static final String ERROR_UNSUPPORTED_CMD = 
			"This command is not supported.";

	//-----------------------Default MIN und MAX Werte-------------------------
	public static final int MIN_VALUE = 0;
	public static final int MAX_VALUE = 50;

	//-------------------------Attribute---------------------------------------
	/** aktueller MIN Wert */
	private int minValue;
	/** aktueller MAX Wert */
	private int maxValue;
	/** Ist ein gültiges Programm kompiliert */
	private boolean compiled;
	/** aktueller Breakpoint */
	private int breakpoint;
	/** Internationalisierung */
	ResourceBundle messages = ResourceBundle.getBundle("ramses.MessagesBundle",
			Locale.getDefault());

	//----------------------------GUI Elemente---------------------------------
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

	//---------------------------RAMSES Elemente-------------------------------
	Input[] input;
	int[] output;
	ArrayList<String> data;
	ArrayList<Instruction> inst;
	ArrayList<ArrayList<String>> matrix;
	Ramses ramses;

	//--------------------------Initialisierung-------------------------------
	/**
	 * Initialisiert die Oberfläche mit Komponenten
	 */
	public void init() {
		//Setze die Attribute
		minValue = MIN_VALUE;
		maxValue = MAX_VALUE;
		breakpoint = 0;
		compiled = false;

		//Initialisiere GUI Elemente
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

		//Initialisiere RAMSES Elemente
		input = null;
		data = new ArrayList<>();
		inst = new ArrayList<>();

		//Initialisiere Listeners
		initListeners();

		//Das Applet befindet sich Anfangs im CLI, also nur die Konsole zur 
		//ContentPane adden
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

	/**
	 * Initialisere das mittlere Panel
	 */
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
		//GUI Ansicht wechselt zur CLI Ansicht
		cliButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Konsolenfenster neu aufbereiten
				console = new JTextArea(PROMPT);
				console.setFont(new Font("monospaced", Font.PLAIN, 12));
				//DocumentFilter, damit nur nach dem Prompt Text geändert 
				//werden darf
				((AbstractDocument) console.getDocument())
						.setDocumentFilter(new NonEditableLineDocumentFilter());
				//Konsole wird als Stdout konfiguriert
				PrintStream printStream = new PrintStream(
						new CustomOutputStream(console));
				System.setOut(printStream);
				System.setErr(printStream);
				//alle überflüssigen Komponenten von der Oberfläche entfernen
				leftPanel.removeAll();
				centerPanel.removeAll();
				c.removeAll();
				//CLI hinzufügen
				c.add(new JScrollPane(console));
				revalidate();
			}

		});

		//Durch Instanzieren eines Ramsesobjekts wird überprüft, ob der 
		//Programmcode kompilierbar ist
		compileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				data = new ArrayList<>();
				inst = new ArrayList<Instruction>();
				//Lies die Daten aus der EditorTextArea ein
				try (Scanner sc = new Scanner(editor.getText())) {
					sc.useDelimiter("\n");
					while (sc.hasNext()) {
						data.add(sc.next());
					}
				}
				try {
					//Daten parsen; in Zwischencode umwandeln
					if (data.size() <= 2)
						throw new SyntaxErrorException(messages
								.getString(INVALID_FILE_SIZE));
					input = Parser.parseInput(data.get(0));
					output = Parser.parseOutput(data.get(1));
					data.remove(0);	//remove INPUT Zeile
					data.remove(0); //remove OUTPUT Zeile
					inst = Parser.parseInstructions(data);
					//Neues RAMSES "kompilieren"
					ramses = new Ramses(input, output, inst);
					//Eingabefelder für den Benutzer anzeigen
					fillInputPanel();
					
					startButton.setEnabled(true);
					debugButton.setEnabled(true);
					setCompiled(true);
					console.setText(messages.getString(COMPILING_SUCCESSFUL));
				} catch (SyntaxErrorException | LogicalErrorException e) {
					inputPanel.removeAll();
					debugButton.setEnabled(false);
					startButton.setEnabled(false);
					setCompiled(false);
					console.setText(e.toString());
				} finally {
					inputPanel.setVisible(false);
					inputPanel.setVisible(true);
				}
			}
		});

		//Startet einen Ramsesthread, welcher bis zur HALT Instruktion 
		//durchläuft
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					//Konsole auf "null" setzen
					console.setText("");
					//Andere Interaktionsmöglichkeiten disablen
					debugButton.setEnabled(false);
					compileButton.setEnabled(false);
					ramses.setDebug(false);
					//Die Eingabe in den Datenspeicher schreiben
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
					//Neuen Ramsesthread instanziieren und starten
					ramses = new Ramses(input, output, inst);
					ramses.start();
					while (ramses.isLocked()) {
						//warten bis Code ausgeführt ist
					}
					//Tabelle im rechten Panel anzeigen
					createTable();
					synchronized (ramses) {
						ramses.notify();
					}
				} catch (NumberFormatException e) {
					System.out.println(e + "\t" + messages.getString(ERROR_VALID_INT)
							+ "\n");
				} catch (LogicalErrorException e) {
					System.out.println(e);
				} finally {
					//Interaktionsmöglichkeiten wieder enablen
					debugButton.setEnabled(true);
					compileButton.setEnabled(true);
				}
			}
		});

		//Startet einen Ramsesthread, welcher bis zum angegebenen Breakpoint
		//läuft und dann auf ein notify() wartet
		debugButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					//Die Eingabe in den Datenspeicher schreiben
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
					//Neuen Ramsesthread instanziieren und starten
					ramses = new Ramses(input, output, inst);
					ramses.setDebug(true);
					ramses.setBreakpoint(breakpoint);
					ramses.start();
					//Andere Interaktionen disablen
					debugButton.setVisible(false);
					startButton.setVisible(false);
					//Debuggerfunktionalitäten enablen
					continueButton.setVisible(true);
					nextButton.setVisible(true);
					//Thread wartet bis RAMSES alle Instruktionen bis inkl.
					//Breakpoint ausgeführt hat und erstellt dann die Tabelle
					//im rechten Panel des GUI
					new Thread() {
						public void run() {
							while (ramses.isLocked()) {
							}
							createTable();
						}
					}.start();
				} catch (NumberFormatException e) {
					System.out.println(e + "\t" + messages.getString(ERROR_VALID_INT)
							+ "\n");
				} catch (LogicalErrorException e) {
					System.out.println(e);
				}
			}
		});

		
		//Setzt den neuen Breakpoint und notified den Ramsesthread
		continueButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//Warten bis RAMSES im Unlocked Zustand ist
				if (!ramses.isLocked()) {
					synchronized (ramses) {
						//Neuen Breakpoint setzen und RAMSES weiterlaufen lassen
						ramses.setBreakpoint(breakpoint);
						ramses.notify();
						//Thread wartet bis RAMSES alle Instruktionen bis zum
						//Breakpoint ausgeführt hat und entnimmt dann die 
						//Tabelle
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

		//Setzt den Breakpoint auf "Nächste Instruktion" und notified den 
		//Ramsesthread
		nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//Warten bis RAMSES im "Unlocked" Zustand ist
				if (!ramses.isLocked()) {
					synchronized (ramses) {
						//Breakpoint auf nächste Instruktion zeigen lassen und
						//Ramses weiterlaufen lassen
						ramses.setBreakpoint(Ramses.NEXT_INST);
						ramses.notify();
						//Thread wartet bis RAMSES alle Instruktionen bis zum
						//Breakpoint ausgeführt hat und entnimmt dann die 
						//Tabelle
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

		//Speichert den neuen Breakpoint zum debuggen
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

	//----------------------------Methoden-------------------------------------
	/**
	 * Erstellt einen JTable anhand der Matrix aus RAMSES
	 */
	private void createTable() {
		//Die Matrix aus dem Ramsesthread erhalten
		matrix = new ArrayList<>(ramses.getTable());
		//Tabellenheader entnehmen
		String[] columnNames = matrix.get(0).toArray(
				new String[matrix.get(0).size()]);
		matrix.remove(0);
		final Object[][] data = new Object[matrix.size()][columnNames.length];
		for (int i = 0; i < matrix.size(); i++) {
			for (int j = 0; j < columnNames.length; j++) {
				data[i][j] = matrix.get(i).get(j);
			}
		}
		//Neuen JTable mit aktuellen Daten erstellen
		table = new JTable(data, columnNames) {
			private static final long serialVersionUID = 1L;

			//Render konfigurieren für besseres Look and Feel
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
		table.setEnabled(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//Alte Tabelle entfernen und neue hinzufügen
		rightPanel.removeAll();
		rightPanel.add(new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				BorderLayout.CENTER);
		rightPanel.revalidate();
	}

	/**
	 * Setzt den neuen Minwert für random Werte
	 * @param value	neuer Minwert
	 * @throws LogicalErrorException wenn Min > Max
	 */
	private void setMinValue(int value) throws LogicalErrorException {
		if (value >= maxValue)
			throw new LogicalErrorException(
					messages.getString(ERROR_MIN_GT_MAX));
		minValue = value;
	}

	/**
	 * Setzt den neuen Maxwert für random Werte
	 * @param value	neuer Maxwert
	 * @throws LogicalErrorException wenn Max < Min
	 */
	private void setMaxValue(int value) throws LogicalErrorException {
		if (value <= minValue)
			throw new LogicalErrorException(
					messages.getString(ERROR_MAX_LT_MIN));
		maxValue = value;
	}

	/**
	 * Generiert eine Zufallszahl im Berich minValue - maxValue
	 * @return Zufallszahl
	 */
	private int getRandom() {
		Random random = new Random();
		return random.nextInt((maxValue - minValue) + 1) + minValue;
	}

	public boolean isCompiled() {
		return compiled;
	}

	public void setCompiled(boolean compiled) {
		this.compiled = compiled;
	}

	//---------------------------Helperclass-----------------------------------
	/**
	 * Dokumentfilter, der dafür sorgt das CLI Eigenschaften eingehalten werden,
	 * z.B. kann nur Text vor dem Eingabeprompt bearbeitet werden
	 * @author Lukas Becker
	 * @author Andreas Paul
	 */
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
				//Falls es im Text ein "\n" gab, heißt das, dass eine Eingabe
				//getätigt wurde
				if (text.equals("\n")) {
					//Das Kommando wird ausgelesen
					String cmd = doc.getText(promptPosition, offset
							- promptPosition);
					//Wenn das Kommando leer ist, wird in der Konsole einfach
					//eine neue Zeile hinzugefügt
					if (cmd.isEmpty()) {
						text = "\n";
						//Ansonsten wird das Kommando geparset
					} else {
						parseCmd(cmd);
					}
					text += PROMPT;
				}

				fb.replace(offset, length, text, attrs);
			}
		}

		/**
		 * Parset das eingegebene Kommando in der Konsole
		 * 
		 * @param cmd
		 *            Zu parsende Kommando
		 */
		private void parseCmd(String cmd) {
			try {
				if (cmd.matches(CMD_GUI)) {// Wechsel zur GUI Ansicht
					new Thread() {
						public void run() {
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
							if (isCompiled()) {
								startButton.setEnabled(true);
								debugButton.setEnabled(true);
							}
							c.setVisible(false);
							c.setVisible(true);

						}
					}.start();
				} else if (cmd.matches(CMD_COMPILE)) {// Kompiliere die Datei
					compile(cmd);
				} else if (cmd.matches(CMD_WRONG_COMPILE)) {
					throw new LogicalErrorException(messages.getString(
							ERROR_WRONG_COMPILE_FORMAT));
				} else if (cmd.matches(CMD_CLEAR)) {//Clear die Konsole
					new Thread() {
						public void run() {
							// der Documentfilter verhindert ein einfaches
							// clear,
							// deshalb erst den Documentfilter auf null setzen
							((AbstractDocument) console.getDocument())
									.setDocumentFilter(null);
							console.setText(PROMPT);
							((AbstractDocument) console.getDocument())
									.setDocumentFilter(new 
											NonEditableLineDocumentFilter());
							console.setCaretPosition(PROMPT.length());
						}
					}.start();
				} else if (cmd.matches(CMD_RUN)) {//Führe das RAM Programm aus
					if(!isCompiled())
						throw new LogicalErrorException(messages.getString(
								ERROR_NO_COMPILED_PROG));
					//Bei run ohne Eingabeparameter werden die benötigten 
					//Parameter zufällig gewählt
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
										messages.getString
										(ERROR_NOT_ALL_INPUT_SET));
						}
					}
					run();
				} else if (cmd.matches(CMD_MIN)) { 
					//Setze Min oder erfahre Minwert
					if (cmd.equals("min"))
						System.out.println("min = " + minValue);
					else {
						cmd = cmd.replace("min ", "");
						setMinValue(Integer.parseInt(cmd));
					}
				} else if (cmd.matches(CMD_MAX)) {
					//Setze Max oder erfahre Maxwert
					if (cmd.equals("max"))
						System.out.println("max = " + maxValue);
					else {
						cmd = cmd.replace("max ", "");
						setMaxValue(Integer.parseInt(cmd));
					}
				} else if (cmd.matches(CMD_HELP)) {//Gibt Hilfeanzeige aus
					System.out.println(messages.getString(CLI_HELP_TEXT));
				}
			} catch (NumberFormatException | LogicalErrorException e) {
				System.out.println(e);
			}
		}

		/**
		 * Instanziert neues Ramsesobjekt und startet den Thread
		 */
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
									for (int j = 0; j < matrix.get(0).size(); 
											j++) {
										System.out
												.print(String.format("%-25s|",
														matrix.get(i).get(j)));
									}
									System.out.println();
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

		/**
		 * Kompiliert die Datei die im mitgegebenen Pfad gespeichert ist
		 * 
		 * @param cmd  "compile DATEIPFAD"
		 */
		private void compile(String cmd) {
			data = new ArrayList<>();
			inst = new ArrayList<Instruction>();
			String path = cmd.replace("compile ", "");

			try (BufferedReader br = new BufferedReader(new FileReader(
					new File(path)))) {
				while (br.ready()) {
					data.add(br.readLine());
				}
				input = Parser.parseInput(data.get(0));
				output = Parser.parseOutput(data.get(1));
				data.remove(0);	//remove INPUT Zeile
				data.remove(0); //remove OUTPUT Zeile
				inst = Parser.parseInstructions(data);
				ramses = new Ramses(input, output, inst);
				setCompiled(true);
				System.out.println(messages.getString(COMPILING_SUCCESSFUL));
			} catch (SyntaxErrorException | LogicalErrorException |
					IOException e) {
				setCompiled(false);
				System.out.println(e);
			}
		}
	}
}
