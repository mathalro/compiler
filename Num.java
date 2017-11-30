public class Num extends Token{
	public final int value;
	public final int type = Type.INTEGER;

	public Num(int value){
		super(Tag.NUM);
		this.value = value;
	}

	public String toString(){
		return "" + value;
	}

}
