package ramses;

import java.io.*;
import java.util.ArrayList;

public class ParserTest {
	public static final String PATH  = "C:/Users/Paul/Desktop/RAMSES.txt";
	
	public static void main(String[] args) {
		Ramses ramses;
		ArrayList<String> data = new ArrayList<>();
		Input[] input;
		ArrayList<Instruction> inst = new ArrayList<>();
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
			for(int i = 0; i < data.size()-2; i++){
				inst.add(Parser.parseInst(i, data.get(i+2)));
			}
			ramses = new Ramses(input,output,inst);
			ramses.run();
		} catch (SyntaxErrorException e) {
			System.out.println(e);
		} catch (LogicalErrorException e) {
			System.out.println(e);
		}
		
		

	}

}
