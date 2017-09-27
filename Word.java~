public class Word extends Token{
	private String lexeme = "";
	
	public static final Word and = new Word("&&", Tag.AND);
	public static final Word or = new Word("||", Tag.OR);
	public static final Word eq = new Word("==", Tag.EQ);
	public static final Word noteq = new Word("!=", Tag.NOTEQ);
	public static final Word le = new Word("<=", Tag.LE);
	public static final Word ge = new Word(">=", Tag.GE);
	public static final Word not = new Word("!", Tag.NOT);

	public Word(String s, int tag){
		super(tag);
		lexeme = s;
	}

	public String getLexeme(){
		return lexeme;	
	}

	public String toString(){
		return "" + lexeme;
	}
	
}
