import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Follow {
	public final int
		// non terminal simbols
		program = 1,
		optDeclList = 2,
		declList = 3,
		optDecl = 4,
		decl = 5,
		identList = 6,
		optIdentifier = 7,
		type = 8,
		stmtList = 9,
		optStmt = 10,
		stmt = 11,
		assignStmt = 12,
		ifStmt = 13,
		ifStmt2 = 14,
		condition = 15,
		whileStmt = 16,
		stmtSufix = 17,
		readStmt = 18,
		writeStmt = 19,
		writable = 20,
		expression = 21,
		expression2 = 22,
		simpleExpr = 23,
		simpleExpr2 = 24,
		term = 25,
		term2 = 26,
		factorA = 27,
		factor = 28,
		relop = 29,
		addop = 30,
		mulop = 31,
		constant = 32;

	public static HashMap<Integer, List<Integer>> map = new HashMap<>();

	public Follow() {
		map.put(program, Arrays.asList(Tag.EOF));
		map.put(optDeclList, Arrays.asList(Tag.ID, Tag.IF, Tag.DO, Tag.SC, Tag.PRT));
		map.put(declList, Arrays.asList(Tag.ID, Tag.IF, Tag.DO, Tag.SC, Tag.PRT));
		map.put(optDecl, Arrays.asList(Tag.ID, Tag.IF, Tag.DO, Tag.SC, Tag.PRT));
		map.put(decl, Arrays.asList(Tag.INT, Tag.STR, Tag.ID, Tag.IF, Tag.DO, Tag.SC, Tag.PRT));
		map.put(identList, Arrays.asList((int)';'));
		map.put(optIdentifier, Arrays.asList((int)';'));
		map.put(type, Arrays.asList(Tag.ID));
		map.put(stmtList, Arrays.asList(Tag.END, Tag.ELSE, Tag.WH));
		map.put(optStmt, Arrays.asList(Tag.END, Tag.ELSE, Tag.WH));
		map.put(stmt, Arrays.asList(Tag.ID, Tag.IF, Tag.DO, Tag.SC, Tag.PRT, Tag.END, Tag.ELSE, Tag.WH));
		map.put(assignStmt, Arrays.asList((int)';'));
		map.put(ifStmt, Arrays.asList(Tag.ID, Tag.IF, Tag.DO, Tag.SC, Tag.PRT, Tag.END, Tag.ELSE, Tag.WH));
		map.put(ifStmt2, Arrays.asList(Tag.ID, Tag.IF, Tag.DO, Tag.SC, Tag.PRT, Tag.END, Tag.ELSE, Tag.WH));
		map.put(condition, Arrays.asList(Tag.THEN, Tag.END));
		map.put(whileStmt, Arrays.asList(Tag.ID, Tag.IF, Tag.DO, Tag.SC, Tag.PRT, Tag.END, Tag.ELSE, Tag.WH));
		map.put(stmtSufix, Arrays.asList(Tag.ID, Tag.IF, Tag.DO, Tag.SC, Tag.PRT, Tag.END, Tag.ELSE, Tag.WH));
		map.put(readStmt, Arrays.asList((int)';'));
		map.put(writeStmt, Arrays.asList((int)';'));
		map.put(writable, Arrays.asList((int)')'));
		map.put(expression, Arrays.asList(Tag.THEN, Tag.END, (int)')'));
		map.put(expression2, Arrays.asList(Tag.THEN, Tag.END, (int)')'));
		map.put(simpleExpr, Arrays.asList((int)';', Tag.THEN, Tag.END, (int)')', Tag.EQ, (int)'>', Tag.GE, (int)'<', Tag.LE, Tag.NOTEQ));
		map.put(simpleExpr2, Arrays.asList((int)';', Tag.THEN, Tag.END, (int)')', Tag.EQ, (int)'>', Tag.GE, (int)'<', Tag.LE, Tag.NOTEQ));
		map.put(term, Arrays.asList((int)';', (int)'+', (int)'-', Tag.OR, Tag.THEN, Tag.END, (int)')', Tag.EQ, (int)'>', Tag.GE, (int)'<', Tag.LE, Tag.NOTEQ));
		map.put(term2, Arrays.asList((int)';', (int)'+', (int)'-', Tag.OR, Tag.THEN, Tag.END, (int)')', Tag.EQ, (int)'>', Tag.GE, (int)'<', Tag.LE, Tag.NOTEQ));
		map.put(factorA, Arrays.asList((int)'*', (int)'/', Tag.AND, (int)';', (int)'+', (int)'-', Tag.OR, Tag.THEN, Tag.END, (int)')', Tag.EQ, (int)'>', Tag.GE, (int)'<', Tag.LE, Tag.NOTEQ));
		map.put(factor, Arrays.asList((int)'*', (int)'/', Tag.AND, (int)';', (int)'+', (int)'-', Tag.OR, Tag.THEN, Tag.END, (int)')', Tag.EQ, (int)'>', Tag.GE, (int)'<', Tag.LE, Tag.NOTEQ));
		map.put(relop, Arrays.asList(Tag.ID, Tag.NUM, Tag.STRING, (int)'(', Tag.NOT, (int)'-'));
		map.put(addop, Arrays.asList(Tag.ID, Tag.NUM, Tag.STRING, (int)'(', Tag.NOT, (int)'-'));
		map.put(mulop, Arrays.asList(Tag.ID, Tag.NUM, Tag.STRING, (int)'(', Tag.NOT, (int)'-'));
		map.put(constant, Arrays.asList((int)'*', (int)'/', Tag.AND, (int)';', (int)'+', (int)'-', Tag.OR, Tag.THEN, Tag.END, (int)')', Tag.EQ, (int)'>', Tag.GE, (int)'<', Tag.LE, Tag.NOTEQ));
	}

	boolean isFollow(int nonterminal, int token) {
		List aux = map.get(nonterminal);
		return aux.contains(token);
	}
}
