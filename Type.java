public class Type { 

	private static final int
	booleanSum = 200,
	stringSum = 200,
	integerSum = 20,
	emptySum = 0;

	public final static int 
		// tipos
		EMPTY = 0,
		INTEGER = 10,
		STRING = 100,
		BOOLEAN = 1000,
		ERROR = 10000;

	public static int and(int lhs, int rhs) {
		// casos de comparacao com vazio
		if (lhs + rhs == emptySum) return EMPTY;
		if (lhs + rhs == 10) return ERROR;
		if (lhs + rhs == 100) return ERROR;
		if (lhs + rhs == 1000) return ERROR;
		if (lhs + rhs == 10000) return ERROR;

		// casos de comparacao com inteiro
		if (lhs + rhs == integerSum) return INTEGER;
		if (lhs + rhs == 110) return ERROR;
		if (lhs + rhs == 1010) return ERROR;
		if (lhs + rhs == 10010) return ERROR;

		// casos de comparacao com string
		if (lhs + rhs == stringSum) return STRING;
		if (lhs + rhs == 1100) return ERROR;
		if (lhs + rhs == 10100) return ERROR;

		// casos de comparacao com boolean
		if (lhs + rhs == booleanSum) return BOOLEAN;
		if (lhs + rhs == 11000) return ERROR;
	
		// caso de erro
		return ERROR;
	}
}
