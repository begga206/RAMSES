package ramses;

import java.io.*;
import java.util.ArrayList;

public class ParserTest {
	public static final String PATH  = "C:/Users/Lukas/Desktop/RAMSES.txt";
	
	public static void main(String[] args) {
		Ramses ramses;
		ArrayList<String> data = new ArrayList<>();
		Input[] input;
		Instruction[] inst;
		int[] output;
		try(BufferedReader br = new BufferedReader(new FileReader(new File(PATH)))){
			while(br.ready()){
					data.add(br.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			input = Parser.parseInput(data.get(0));
			output = Parser.parseOutput(data.get(1));
			inst = new Instruction[data.size()-2];
			for(int i = 0; i < inst.length; i++){
				inst[i] = Parser.parseInst(i, data.get(i+2));
			}
			ramses = new Ramses(input,output,inst);
			ramses.start();
		} catch (SyntaxErrorException e) {
			System.out.println(e);
		} catch (LogicalErrorException e) {
			System.out.println(e);
		}
		
		

	}

}
