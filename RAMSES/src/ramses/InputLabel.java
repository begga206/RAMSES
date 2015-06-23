package ramses;

import java.awt.Checkbox;

import javax.swing.*;

public class InputLabel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Input input;
	private JTextField tf;
	private Checkbox random;
	
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
