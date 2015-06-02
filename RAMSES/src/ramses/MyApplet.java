package ramses;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

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
		console = new JTextArea(10, 70);
		console.setEditable(false);
		console.setLineWrap(true);
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
		
		initLeftPanel();
		initListeners();
		
		c.add(leftPanel); 
		c.add(centerPanel); 
		c.add(rightPanel);
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
					centerPanel.revalidate();
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
}
