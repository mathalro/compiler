import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Sintatico {
	public static Token token;
	public static Lexer l;		
	public static Follow f;
	/* Implementacao dos Procedimentos necessarios para o parser */

	// procedimento responsavel por ler o proximo token da entrada
	public static void getToken() {
		try {
			do {
				token = l.scan();
				//System.out.println("--------------------------------------------------------------");

				if (token.tag == Tag.ERRO) {
					//System.out.format("Malformed Token     %15s %15s line %d\n", token.toString(), "", 	l.line);
				} else if (token.tag == Tag.INV) {
					//System.out.format("Invalid Token       %15s line %15d\n", token.toString(), l.line);
				} else if (token.tag == Tag.EOF) {
					//System.out.format("Lexical analysis finished\n");
				} else if (token.tag == Tag.UEOF) {
					//System.out.format("Unexpected EOF\n");
				} else if (token.tag == Tag.OTH) {
					//System.out.format("Other token\n");;
				} else {
					//System.out.format("Consumed token      %15s %15s line %d\n", token.toString(), "", l.line);
				}
			} while (token.tag == Tag.OTH);
		} catch(IOException e) {
			System.out.format("Read error\n");
		}
	}

	public static void debug(int tag) {
		System.out.println(tag);
	}

	public static void error(ArrayList<String> tokens, int nonterminal) {
		System.out.println("--------------------------------------------------------------");
		System.out.format("Linha %d\n", l.line);
		System.out.print("Syntactical error - Expected tokens: ");
		for (String it : tokens) System.out.print("\""+it+"\""+", ");
		System.out.print("Consumed: \""+token.toString()+"\"");
		System.out.println();
		// error recovery
		while (!f.isFollow(nonterminal, token.tag) && token.tag != Tag.EOF) {
			advance();
		}
	}

	public static void semanticError() {
		System.out.println("Erro semantico");
	}

	public static void advance() {
		getToken();
	}

	public static void eat(int t) {
		if (token.tag == t) advance();
		else {
			System.out.println("--------------------------------------------------------------");
			System.out.format("Linha %d\n", l.line);
			System.out.println("Syntactical error: Not expected token \""+ token.toString()+"\"");
		}
	}

	// metodo responsavel pela inclusao dos tipos de uma lista
	// identificadores na tabela de simbolos
	void includeType(int type, ArrayList<> idents) {
		// 
	}

	// procedimento inicial S, raiz da arvore de derivacao
	public static void S() {
		int type = Program();
		if (type != Type.EMPTY) semanticError();
		eat(Tag.EOF);
	}

	// procedimento responsavel pelo tratamento do simbolo program
	public static int Program() {
		int type = Type.EMPTY;
		switch (token.tag) {
			case Tag.PRG:
				eat(Tag.PRG);
				type = Type.and(OptDeclList(), type);
				type = Type.and(StmtList(), type);
				eat(Tag.END);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("PRG")), f.program);
				// fazer propagacao de erro aqui
		}

		return type;
	}

	// simbolo opt-decl-list
	public static int OptDeclList() {
		int type = Type.EMPTY;
		switch (token.tag) {
			case Tag.INT:
			case Tag.STR:
				type = Type.and(DeclList(), type);
				break;
			case Tag.IF:
			case Tag.DO:
			case Tag.SC:
			case Tag.PRT:
			case Tag.ID:
				break;
			default:
				error(new ArrayList<>(Arrays.asList("INT", "STR", "IF", "DO", "SC", "PRT", "ID")), f.optDeclList);
		}
		return type;
	}

	// tratamento do simbolo decl-list
	public static int DeclList() {
		int type = Type.EMPTY;
		switch (token.tag) {
			case Tag.INT:
			case Tag.STR:
				type = Type.and(Decl(), type);
				type = Type.and(OptDecl(), type);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("INT", "STR")), f.declList);
		}
		return type;
	}

	// tratamento do simbolo opt-decl
	public static int OptDecl() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// opt-delc -> decl opt-decl
			case Tag.INT:
			case Tag.STR:
				type = Type.and(Decl(), type);
				type = Type.and(OptDecl(), type);
				break;
			case Tag.IF:
			case Tag.DO:
			case Tag.SC:
			case Tag.PRT:
			case Tag.ID:
				break;
			default:
				error(new ArrayList<>(Arrays.asList("INT", "STR", "IF", "DO", "SC", "PRT", "ID")), f.optDecl);
		}
		return type;
	}

	// tratamento do simbolo decl
	public static int Decl() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// decl -> type ident-list ";"
			case Tag.INT:
			case Tag.STR:
				includeType(Type(),	IdentList());
				eat(';');
				break;
			default:
				error(new ArrayList<>(Arrays.asList("INT", "STR")), f.decl);
		}
		return type;
	}

	// tratamentodo simbolo idnet-list
	public static int IdentList() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// ident-list -> id opt-id
			case Tag.ID:
				eat(Tag.ID);
				type = Type.and(OptIdentifier(), type);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("ID")), f.identList);
		}
		return type;
	}

	// tratamentodo simbolo opt-identifier
	public static int OptIdentifier() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// opt-identifier -> , id opt-identifier
			case ',':
				eat(',');
				eat(Tag.ID);
				type = Type.and(OptIdentifier(), type);
				break;
			case ';':
				break;
			default:
				error(new ArrayList<>(Arrays.asList(",", ";")), f.optIdentifier);
		}
		return type;
	}

	// tratamento do simbolo type
	public static void Type() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// type -> int
			case Tag.INT:
				eat(Tag.INT);
				type = Type.INTEGER;
				break;
			// type -> str
			case Tag.STR:
				eat(Tag.STR);
				type = Type.STRING;
				break;
			default:
				error(new ArrayList<>(Arrays.asList("INT", "STR")), f.type);
		}
		return type;
	}
	
	// tratamento do simbolo stmt-list
	public static int StmtList() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// stmt-list -> stmt opt-stmt
			case Tag.IF:
			case Tag.DO:
			case Tag.SC:
			case Tag.PRT:
			case Tag.ID:
				Stmt();
				OptStmt();
				break;
			default:
				error(new ArrayList<>(Arrays.asList("IF", "DO", "SC", "PRT", "ID")), f.stmtList);
		}
		return type;
	}

	// tratamento do simbolo opt-stmt
	public static void OptStmt() {
		switch (token.tag) {
			// opt-stmt -> stmt opt-stmt
			case Tag.IF:
			case Tag.DO:
			case Tag.SC:
			case Tag.PRT:
			case Tag.ID:
				Stmt();
				OptStmt();
				break;
			// opt-stmt -> #
			case Tag.END:
			case Tag.ELSE:
			case Tag.WH:
				break;
			default:
				error(new ArrayList<>(Arrays.asList("IF", "DO", "SC", "PRT", "ID", "END", "WH")), f.optStmt);
		}
	}

	// tratamento do simbolo stmt
	public static void Stmt() {
		switch (token.tag) {
			case Tag.IF:		// stmt -> assign-stmt
				IfStmt();			
				break;
			case Tag.DO:		// stmt -> if-stmt
				WhileStmt();
				break;
			case Tag.SC:		// stmt -> read-stmt
				ReadStmt();
				eat(';');
				break;
			case Tag.PRT:
				WriteStmt(); 	// stmt -> write-stmt
				eat(';');
				break;
			case Tag.ID:	 	// stmt -> assign-stmt 
				AssignStmt();
				eat(';');
				break;
			default:
				error(new ArrayList<>(Arrays.asList("IF", "DO", "SC", "PRT", "ID")), f.stmt);
		}
	}

	// tratamento do simbolo assign-stmt
	public static void AssignStmt() {
		switch (token.tag) {
			// assign-stmt -> id = simple-expr
			case Tag.ID:
				eat(Tag.ID);
				eat('=');
				SimpleExpr();
				break;
			default:
				error(new ArrayList<>(Arrays.asList("ID")), f.assignStmt);
		}
	}

	// tratamento do simbolo if-stmt
	public static void IfStmt() {
		switch (token.tag) {
			// if-stmt -> if condition then stmt-list if-stmt2
			case Tag.IF:
				eat(Tag.IF);
				Condition();
				eat(Tag.THEN);
				StmtList();
				IfStmt2();
				break;	
			default:
				error(new ArrayList<>(Arrays.asList("IF")), f.ifStmt);
		}
	}

	// tratamento do simbolo if-stmt-2
	public static void IfStmt2() {
		switch (token.tag) {
			// if-stmt2 -> end
			case Tag.END:
				eat(Tag.END);
				break;
			// if-stmt2 -> else stmt-list end
			case Tag.ELSE:
				eat(Tag.ELSE);
				StmtList();
				eat(Tag.END);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("END", "ELSE")), f.ifStmt2);
		}
	}

	// simbolo condiion
	public static void Condition() {
		switch (token.tag) {
			// condition -> expression
			case Tag.NOT:
			case Tag.NUM:
			case Tag.STRING:
			case Tag.ID:
			case '-':
			case '(':
				Expression();
				break;
			default:
				error(new ArrayList<>(Arrays.asList("NOT", "NUM", "STRING", "ID", "-", "(")), f.condition);
		}
	}

	// simbolo while-stmt
	public static void WhileStmt() {
		switch (token.tag) {
			// while-stmt -> do stmt-list stmt-sufix
			case Tag.DO:
				eat(Tag.DO);
				StmtList();
				StmtSufix();
				break;
			default:
				error(new ArrayList<>(Arrays.asList("DO")), f.whileStmt);
		}
	}

	// simbolo stmt-sufix
	public static void StmtSufix() {
		switch (token.tag) {
			// stmt-sufix -> while condition end
			case Tag.WH:
				eat(Tag.WH);
				Condition();
				eat(Tag.END);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("WH")), f.stmtSufix);
		}
	}

	// simbolo read-stmt
	public static void ReadStmt() {
		switch (token.tag) {
			// read-stmt -> scan "(" identifier ")"
			case Tag.SC:
				eat(Tag.SC);
				eat('(');
				eat(Tag.ID);
				eat(')');
				break;
			default:
				error(new ArrayList<>(Arrays.asList("SC")), f.readStmt);
		}
	}

	// simbolo write-stmt
	public static void WriteStmt() {
		switch (token.tag) {
			// write-stmt -> print "(" writable ")"
			case Tag.PRT:
				eat(Tag.PRT);
				eat('(');
				Writable();
				eat(')');
				break;
			default:
				error(new ArrayList<>(Arrays.asList("PRT")), f.writeStmt);
		}
	}

	// simbolo writable
	public static void Writable() {
		switch (token.tag) {
			// writable -> simple-expr
			case Tag.NOT:
			case Tag.NUM:
			case Tag.ID:
			case '-':
			case '(':
				SimpleExpr();
				break;
			case Tag.STRING:
				eat(Tag.STRING);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("NOT", "NUM", "ID", "-", "(", "STRING")), f.writable);
		}
	}

	// simbolo expression
	public static void Expression() {
		switch (token.tag) {
			// expression -> simple-expression expression2
			case Tag.NOT:
			case Tag.NUM:
			case Tag.ID:
			case '-':
			case '(':
			case Tag.STRING:
				SimpleExpr();
				Expression2();
				break;
			default:
				error(new ArrayList<>(Arrays.asList("NOT", "NUM", "ID", "-", "(", "STRING")), f.expression);			
		}
	}

	// simbolo expression2
	public static void Expression2() {
		switch (token.tag) {
			// expression2 -> relop simple-expr
			case Tag.EQ:
			case Tag.GE:
			case Tag.LE:
			case Tag.NOTEQ:
			case '>':
			case '<':
				Relop();
				SimpleExpr();
				break;
			// expressio2 -> #
			case ')':
			case Tag.END:
			case Tag.THEN:
				break;
			default:
				error(new ArrayList<>(Arrays.asList("EQ", "GE", "LE", "NOTEQ", ">", "<", ")", "END", "THEN")), f.expression2);
		}
	}

	// simbolo simple-expr
	public static void SimpleExpr() {
		// simple-expr -> temr simple-expr2
		switch (token.tag) {
			case Tag.NOT:
			case Tag.NUM:
			case Tag.ID:
			case '-':
			case '(':
			case Tag.STRING:
				Term();
				SimpleExpr2();
				break;
			default:
				error(new ArrayList<>(Arrays.asList("NOT", "NUM", "ID", "-", "(", "STRING")), f.simpleExpr);			
		}
	}

	// simbolo simple-expr2
	public static void SimpleExpr2() {
		switch (token.tag) {
			// simple-expr2 -> addop term simple-expr2
			case '+':
			case '-':
			case Tag.OR:
				Addop();
				Term();
				SimpleExpr2();
			// simple-expr2 -> #
			case Tag.END:
			case Tag.THEN:
			case Tag.EQ:
			case Tag.GE:
			case Tag.LE:
			case Tag.NOTEQ:
			case '>':
			case '<':
			case ')':
			case ';':
				break;
			default:
				error(new ArrayList<>(Arrays.asList("+", "-", "END", "THEN", "EQ", "GE", "LE", "NOTEQ", "<", ">", ")", ";")), f.simpleExpr2);
		}
	}

	// simbolo term
	public static void Term() {
		switch (token.tag) {
			// term -> 	factor-a term2
			case Tag.NOT:
			case Tag.NUM:
			case Tag.ID:
			case Tag.STRING:
			case '(':
			case '-':
				FactorA();
				Term2();
				break;	
			default:
				error(new ArrayList<>(Arrays.asList("NOT", "NUM", "ID", "STRING", "(", "-")), f.term);
		}
	}

	// simbolo term2
	public static void Term2() {
		switch (token.tag) {
			// term2 -> mulop factor-a term2
			case Tag.AND:
			case '*':
			case '/':
				Mulop();
				FactorA();
				Term2();
				break;
			case Tag.END:
			case Tag.THEN:
			case Tag.OR:
			case Tag.EQ:
			case Tag.GE:
			case Tag.LE:
			case Tag.NOTEQ:
			case '-':
			case '>':
			case '<':
			case '+':
			case ')':
			case ';':
				break;
			default:
				error(new ArrayList<>(Arrays.asList("AND", "*", "/", "END", "THEN", "OR", "EQ", "GE", "LE", "NOTEQ", "-", "+", ">", "<", ")", ";", "/", ")", ";")), f.term2);
		}
	}

	// simbolo factor-a
	public static void FactorA() {
		switch (token.tag) {
			// factor-a -> factor
			case Tag.NUM:
			case Tag.ID:
			case Tag.STRING:
			case '(':
				Factor();
				break;
			// factor-a -> ! factor
			case Tag.NOT:
				eat(Tag.NOT);
				Factor();
				break;
			// factor-a -: - factor
			case '-':
				eat('-');
				Factor();
				break;
			default:
				error(new ArrayList<>(Arrays.asList("NUM", "ID", "STRING", "NOT", "-")), f.factorA);
		}
	}

	// simbolo factor
	public static void Factor() {
		switch (token.tag) {
			// factor -> id
			case Tag.ID:
				eat(Tag.ID);
				break;
			// factor -> constant
			case Tag.NUM:
			case Tag.STRING:
				Constant();
				break;
			// factor -> "(" expression ")"
			case '(':
				eat('(');
				Expression();
				eat(')');
				break;
			default:
				error(new ArrayList<>(Arrays.asList("ID", "NUM", "STRING", "(")), f.factor);
		}
	}

	// simbolo relop
	public static void Relop() {
		switch (token.tag) {
			case Tag.EQ:
				eat(Tag.EQ);
				break;
			case '>':
				eat('>');
				break;
			case Tag.GE:
				eat(Tag.GE);
				break;
			case '<':
				eat('<');
				break;
			case Tag.LE:
				eat(Tag.LE);
				break;
			case Tag.NOTEQ:
				eat(Tag.NOTEQ);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("EQ", "GE", "LE", "NOTEQ", ">", "<")), f.relop);
		}
	}

	// simbolo addop
	public static void Addop() {
		switch (token.tag) {
			case '+':
				eat('+');
				break;
			case '-':
				eat('-');
				break;
			case Tag.OR:
				eat(Tag.OR);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("+", "-", "OR")), f.addop);
		}
	}

	// simbolo mulop
	public static void Mulop() {
		switch (token.tag) {
			case '*':
				eat('*');
				break;
			case '/':
				eat('/');
				break;
			case Tag.AND:
				eat(Tag.AND);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("*", "/", "AND")), f.mulop);
		}
	}

	// simbolo constant
	public static void Constant() {
		switch (token.tag) {
			case Tag.NUM:
				eat(Tag.NUM);
				break;
			case Tag.STRING:
				eat(Tag.STRING);
				break;	
			default:
				error(new ArrayList<>(Arrays.asList("NUM", "STRING")), f.constant);
		}
	}

	public static void main(String []args){
		String filename;
		Scanner cin = new Scanner(System.in);
    if (args.length == 0 || args.length > 1) {
      System.out.println("É preciso passar um unico programa a ser compilado como argumento: ");
      System.out.println("java -jar Sintatico.jar <nomeDoTeste.txt>");
      return;
    }

		filename = args[0];	
		try {
					
			l = new Lexer(filename);
			f = new Follow();
			getToken();
			S();
			System.out.println("--------------------------------------------------------------");
			System.out.format("Sintatical analysis finished\n");

			/*System.out.println("\n\nSymbol Table: \n");
			l.printTable();
			System.out.println();*/
		}catch(FileNotFoundException e) {
			System.out.format("An exception ocurred");
		}
	}
}
