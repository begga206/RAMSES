package ramses;

import java.io.*;

public class ParserTest {
	public static final String PATH  = "D:/Dropbox/SySi6/Ram-Test.txt";
	
	public static void main(String[] args) {
		try(BufferedReader br = new BufferedReader(new FileReader(new File(PATH)))){
			int i = 0;
			while(br.ready()){
				try {
					System.out.println(Parser.parseInst(i, br.readLine()));
				} catch (SyntaxErrorException e) {
					System.out.println(e);
				}
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
