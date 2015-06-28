package ramses;

import java.awt.Checkbox;

import javax.swing.*;

/**
 * GUI Darstellung eines Input-Objekts
 * @author Lukas Becker
 * @author Andreas Paul
 *
 */
public class InputLabel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/** Eigentliche Input-Objekt */
	private Input input;
	/** Im Textfeld kann ein Wert für das Input-Objekt eingetragen werden*/
	private JTextField tf;
	/** Wenn gecheckt, soll ein Randomwert als input eingetragen werden*/
	private Checkbox random;
	
	/**
	 * Konstruktor
	 * @param in Darzustellende Input-Objekt
	 */
	public InputLabel(Input in){
		super();
		tf = new JTextField(10);
		if(in.hasValue())
			tf.setText(Integer.toString(in.getValue()));
		random = new Checkbox("random");
		this.input = in;
		this.add(new JLabel("s[" + input.getIndex() + "]: "));
		this.add(tf);
		this.add(random);
	}
	
	//-------------------------Getter und Setter-------------------------------
	public void setValue(){
		input.setValue(Integer.parseInt(tf.getText()));
	}
	
	public boolean isRandom(){
		return random.getState();
	}
	
	public Input getInput(){
		return input;
	}
	
	public void setText(String text){
		tf.setText(text);
	}
	
}
