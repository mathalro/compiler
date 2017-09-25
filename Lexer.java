import java.io.*;
import java.util.*;

public class Lexer{
	public static int line = 1;
	
	private char ch = ' ';
	private FileReader file;

	private Hashtable words = new Hashtable();
	
	private void reserve(Word w){
		words.put(w.getLexeme(),w);
	}

	public Lexer(String fileName) throws FileNotFoundException{
		
		try {
			file = new FileReader(fileName);
		} catch(FileNotFoundException e){
			System.out.println("Arquivo não encontrado");
			throw e;
		}

		reserve(new Word("program",Tag.PRG));
		reserve(new Word("end",Tag.END));
		reserve(new Word("int",Tag.INT));
		reserve(new Word("string",Tag.STR));
		reserve(new Word("if",Tag.IF));
		reserve(new Word("then",Tag.THEN));
		reserve(new Word("else",Tag.ELSE));
		reserve(new Word("do",Tag.DO));
		reserve(new Word("while",Tag.WH));
		reserve(new Word("scan",Tag.SC));
		reserve(new Word("print",Tag.PRT));
	}	

	private void readch() throws IOException{
		ch = (char) file.read();
	}

	private boolean readch(char c) throws IOException{
		readch();
		if(ch != c) return false;
		ch = ' ';
		return true;
	}
	
	public Token scan() throws IOException{
		for (;;readch()) {
			if(ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b') continue;
			else if(ch == '\n') line++;
			else break;
		}

		switch(ch){
			case '/':
				if(readch('/')){
					while(!readch('\n')) {
						if (ch == 65535) {
							return new Token(Tag.EOF);
						}
					}
					return new Token(Tag.OTH);
				} else if (ch == '*'){
					readch();
					while(true){
						if (ch == '\n') line++;
						if(ch == '*'){
							if(readch('/')) break;					
						} else if (ch == 65535) {
							return new Token(Tag.UEOF);
						} else {
							readch();
						}
					}
					return new Token(Tag.OTH);
				}else{
					return new Token(Tag.DIV);
				}
			case '+':
				readch();
				return new Token(Tag.ADD);
			case '-':
				readch();
				return new Token(Tag.SUB);
			case '*':
				readch();
				return new Token(Tag.MUL);	
			case '|':
				if(readch('|')) return Word.or;
				else return new Word("|", Tag.ERRO);
			case '&':
				if(readch('&')) return Word.and;
				else return new Word("&", Tag.ERRO);
			case '!':
				if(readch('=')) return Word.noteq;
				return Word.not;
			case '=':
				if(readch('=')) return Word.eq;
				else return new Token('=');
			case '>':
				if(readch('=')) return Word.ge;
				else return new Token('>');
			case '<':
				if(readch('=')) return Word.le;
				else return new Token('<');
			case '(':
				readch();
				return new Token('(');
			case ')':
				readch();
				return new Token(')');
			case ',':
				readch();
				return new Token(',');
			case ';':
				readch();
				return new Token(';');
		}

		if(Character.isDigit(ch)){
			int value = 0;
			do{
				value = 10*value + Character.digit(ch,10);
				readch();
			}while(Character.isDigit(ch));
			return new Num(value);	
		}	

		if(Character.isLetter(ch)){
			StringBuffer sb = new StringBuffer();
			do{
				sb.append(ch);
				readch();
			}while(Character.isLetterOrDigit(ch));	

			String s = sb.toString();
			Word w = (Word) words.get(s);
			if(w != null) return w;
			w = new Word(s,Tag.ID);
			words.put(s,w);
			return w;
		}

		if(ch == '“') {
			String s = ""+'"';
			while(!readch('”')) {
				if (ch == 65535) {
					return new Token(Tag.UEOF);
				}
				s += ch;
			}	
			s += '"';
			Word w = new Word(s,Tag.STRING);
			words.put(s,w);
			return w;
		}

		if (ch == 65535) {
			return new Token(Tag.EOF);
		}

		String s = "" + ch;
		Word w = new Word(s, Tag.INV);
		ch = ' ';
		return w;
	}
	
}
