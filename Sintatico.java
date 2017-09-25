import java.util.Scanner;
import java.io.*;

public class Sintatico {
	public static void main(String []args){
		String filename;
		Token token;
		Scanner cin = new Scanner(System.in);
		filename = args[0];
		try {
			Lexer l = new Lexer(filename);
			System.out.format("Status %15s <Lexeme> %10s Line\n", "", "");
			System.out.println("-----------------------------------------------");
			while(true){
				try {
					token = l.scan();
					if (token.tag == Tag.ERRO) {
						System.out.format("Bad Token     %15s %15d\n", token.toString(), l.line);
					} else if (token.tag == Tag.INV) {
						System.out.format("Invalid Token  %15s %15d\n", token.toString(), l.line);
					} else if (token.tag == Tag.EOF) {
						System.out.format("Lexical analysis finished\n");
						break;
					} else if (token.tag == Tag.UEOF) {
						System.out.format("Unexpected EOF\n");
						break;
					} else if (token.tag == Tag.OTH) {
						continue;		
					} else {
						System.out.format("Consumed token %15s %15d\n", token.toString(), l.line);
					}
				} catch(IOException e) {
					System.out.format("Read error\n");
				}
			
			}
		}catch(FileNotFoundException e) {
			System.out.format("An exception ocurred");
		}
	}
}
