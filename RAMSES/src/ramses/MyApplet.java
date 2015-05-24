package ramses;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;


/**
 * Appletklasse, welche die grafische Oberfläche realisiert
 * @author Lukas
 *
 */
public class MyApplet extends JApplet{
	
	/////////////GUI ELEMENTE//////////
	private Container c;
	private JPanel leftPanel;
	private JPanel centerPanel;
	private JPanel rightPanel;
	private JButton compile;
	private JButton start;
	private JTable table;
	private JTextArea editor;
	private JTextArea console;
	
	////////////RAMSES ELEMENTE////////
	private Input[] input;
	private int[] output;
	private ArrayList<String> data;
	private ArrayList<Instruction> inst;
	private ArrayList<ArrayList<String>> matrix;
	private Ramses ramses;
	
	/**
	 * Initialisiert die Oberfläche mit Komponenten
	 */
	public void init(){
		c = getContentPane();
		c.setLayout(new GridLayout(1,3));
		leftPanel = new JPanel();
		centerPanel = new JPanel();
		rightPanel = new JPanel();
		
		editor = new JTextArea(40,70);
		editor.setLineWrap(true);
		console = new JTextArea(10,70);
		console.setEditable(false);
		console.setLineWrap(true);
		PrintStream printStream = new PrintStream(new CustomOutputStream(console));
		System.setOut(printStream);
		System.setErr(printStream);
		
		compile = new JButton("Compile");
		start = new JButton("Start");
		start.setEnabled(false);
		
		input = null;
		data = new ArrayList<>();
		inst = new ArrayList<>();
		
		initLeftPanel();
		initCenterPanel();
		initListeners();

		c.add(leftPanel);
		c.add(centerPanel);
		c.add(rightPanel);
	}
	
	/**
	 * Initialisiert das linke Panel
	 */
	private void initLeftPanel(){
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
	private void initCenterPanel(){
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.add(new JLabel("Input: "));
		fillInputLabels();
	}
	
	/**
	 * Füllt das mittlere Panel mit den Input-eingaben
	 */
	private void fillInputLabels(){
		//existierende Inputlabels löschen
		centerPanel.removeAll();
		centerPanel.add(new JLabel("Input:"));
		//neue Inputlabels einfügen
		if(input != null){
			for(int i = 0; i < input.length; i++){
				centerPanel.add(new InputLabel(input[i]));
			}
		}
	}
	
	/**
	 * Initialisiert die Buttonlistener
	 */
	private void initListeners(){
		compile.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				data = new ArrayList<>();
				inst = new ArrayList<Instruction>();
				try(Scanner sc = new Scanner(editor.getText())){
					sc.useDelimiter("\n");
					while(sc.hasNext()){
						data.add(sc.next());
					}
				}
				try {
					input = Parser.parseInput(data.get(0));
					output = Parser.parseOutput(data.get(1));
					for(int i = 0; i < data.size()-2; i++){
						inst.add(Parser.parseInst(i, data.get(i+2)));
					}
					console.setText("");
					ramses = new Ramses(input,output,inst);
					centerPanel.setVisible(false);
					fillInputLabels();
					centerPanel.setVisible(true);
					start.setEnabled(true);
					System.out.println("Compiling successful.");
				} catch (SyntaxErrorException e) {
					start.setEnabled(false);
					System.out.println(e);
				} catch (LogicalErrorException e) {
					start.setEnabled(false);
					System.out.println(e);
				}
			}
		});
		
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				try{
				console.setText("");	
				for(int i = 1; i < centerPanel.getComponentCount(); i++){
					if(centerPanel.getComponent(i) instanceof InputLabel){
						InputLabel label = (InputLabel)centerPanel.getComponent(i);
						label.setValue();
					}
				}
				ramses.start();
				createTable();
				} catch (NumberFormatException e) {
					System.out.println(e + "\tEvery input needs a valid number");
				}  catch(LogicalErrorException e) {
					System.out.println(e);
				}
			}
		});
	}
	
	/**
	 * Erstellt einen JTable anhand der Matrix aus RAMSES
	 */
	private void createTable(){
		matrix = ramses.getTable();
		String[] columnNames = matrix.get(0).toArray(new String[matrix.get(0).size()]);
		matrix.remove(0);
		Object[][] data = new Object[matrix.size()][columnNames.length];
		for(int i = 0; i < matrix.size(); i++){
			for(int j = 0; j < columnNames.length; j++){
				data[i][j] = matrix.get(i).get(j);
			}
		}
		table = new JTable(data, columnNames);
		rightPanel.removeAll();
		rightPanel.setVisible(false);
		rightPanel.add(new JScrollPane(table));
		rightPanel.setVisible(true);
	}
	
}
