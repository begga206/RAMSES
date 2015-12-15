package ramses;

import javax.swing.JApplet;
import javax.swing.JFrame;

/**
 * Appletklasse mit Main-Funktion, um sowohl als Applet als auch als Standalone Programm
 * zu agieren.
 * @author Lukas Becker
 *
 */
public class MyApplet extends JApplet{

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Ramses");
		MyPanel panel = new MyPanel();
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
	
	public void init() {
		MyPanel panel = new MyPanel();
		setContentPane(panel);
	}
}
