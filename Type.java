public class Type { 
	public final static int 
		// tipos
		EMPTY = 0,
		INTEGER = 10,
		STRING = 100,
		BOOLEAN = 1000,
		ERROR = 10000;

	public static int and(int lhs, int rhs) {
		// casos de comparacao com vazio
		if (lhs + rhs == 0) return EMPTY;
		if (lhs + rhs == 10) return ERROR;
		if (lhs + rhs == 100) return ERROR;
		if (lhs + rhs == 1000) return ERROR;
		if (lhs + rhs == 10000) return ERROR;

		// casos de comparacao com inteiro
		if (lhs + rhs == 20) return INTEGER;
		if (lhs + rhs == 110) return ERROR;
		if (lhs + rhs == 1010) return ERROR;
		if (lhs + rhs == 10010) return ERROR;

		// casos de comparacao com string
		if (lhs + rhs == 200) return STRING;
		if (lhs + rhs == 1100) return ERROR;
		if (lhs + rhs == 10100) return ERROR;

		// casos de comparacao com boolean
		if (lhs + rhs == 2000) return BOOLEAN;
		if (lhs + rhs == 11000) return ERROR;
	
		// caso de erro
		return ERROR;
	}
}
