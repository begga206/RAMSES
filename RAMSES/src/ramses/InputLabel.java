package ramses;

import javax.swing.*;

public class InputLabel extends JPanel {
	private Input input;
	private JTextField tf;
	
	public InputLabel(Input in){
		super();
		tf = new JTextField(10);
		if(in.hasValue()){
			tf.setText(Integer.toString(in.getValue()));
			tf.setEditable(false);
		}
		this.input = in;
		this.add(new JLabel("s[" + input.getIndex() + "]: "));
		this.add(tf);
	}
	
	public void setValue(){
		input.setValue(Integer.parseInt(tf.getText()));
	}
	
}
