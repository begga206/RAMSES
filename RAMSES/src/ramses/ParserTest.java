package ramses;

import java.io.*;
import java.util.ArrayList;

public class ParserTest {
	public static final String PATH = "C:/Users/Lukas/Desktop/RAMSES.txt";

	public static void main(String[] args) {
		ArrayList<String> data = new ArrayList<>();
		Input[] input;
		ArrayList<Instruction> inst = new ArrayList<>();
		int[] output;
		try (BufferedReader br = new BufferedReader(new FileReader(new File(
				PATH)))) {
			while (br.ready()) {
				data.add(br.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			input = Parser.parseInput(data.get(0));
			output = Parser.parseOutput(data.get(1));
		} catch (SyntaxErrorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		data.remove(0);
		data.remove(0);
		try {
			inst = Parser.parseInstructions(data);
		} catch (SyntaxErrorException | LogicalErrorException e) {
			System.out.println(e);
		}
	}

}
