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
	public static void includeType(int type, ArrayList<String> idents) {
		for(String s : idents){
			Word w = l.words.get(s);
			w.setType(type);
			l.words.put(s,w);
		} 
	}

	// retorna o tipo de um identificador
	public static int getType(String lexema){
		return l.words.get(lexema).getType();
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
	public static ArrayList<String> IdentList() {
		ArrayList<String> idList = new ArrayList<String>();
		switch (token.tag) {
			// ident-list -> id opt-id
			case Tag.ID:
				idList.add(((Word)token).getLexeme());
				eat(Tag.ID);
				idList.addAll(OptIdentifier());
				break;
			default:
				error(new ArrayList<>(Arrays.asList("ID")), f.identList);
		}
		return idList;
	}

	// tratamentodo simbolo opt-identifier
	public static ArrayList<String> OptIdentifier() {
		ArrayList<String> idList = new ArrayList<String>();
		switch (token.tag) {
			// opt-identifier -> , id opt-identifier
			case ',':
				eat(',');
				eat(Tag.ID);
				idList.add(((Word)token).getLexeme());
				idList.addAll(OptIdentifier());
				break;
			case ';':
				break;
			default:
				error(new ArrayList<>(Arrays.asList(",", ";")), f.optIdentifier);
		}
		return idList;
	}

	// tratamento do simbolo type
	public static int Type() {
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
				type = Type.and(Stmt(),type);
				type = Type.and(OptStmt(),type);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("IF", "DO", "SC", "PRT", "ID")), f.stmtList);
		}
		return type;
	}

	// tratamento do simbolo opt-stmt
	public static int OptStmt() {
		int type = Type.EMPTY;  
		switch (token.tag) {
			// opt-stmt -> stmt opt-stmt
			case Tag.IF:
			case Tag.DO:
			case Tag.SC:
			case Tag.PRT:
			case Tag.ID:
				type = Type.and(Stmt(),type);
				type = Type.and(OptStmt(),type);
				break;
			// opt-stmt -> #
			case Tag.END:
			case Tag.ELSE:
			case Tag.WH:
				break;
			default:
				error(new ArrayList<>(Arrays.asList("IF", "DO", "SC", "PRT", "ID", "END", "WH")), f.optStmt);
		}
		return type;
	}

	// tratamento do simbolo stmt
	public static int Stmt() {
		int type = Type.EMPTY;
		switch (token.tag) {
			case Tag.IF:		// stmt -> assign-stmt
				type = Type.and(IfStmt(),type);			
				break;
			case Tag.DO:		// stmt -> if-stmt
				type = Type.and(WhileStmt(),type);
				break;
			case Tag.SC:		// stmt -> read-stmt
				type = Type.and(ReadStmt(),type);
				eat(';');
				break;
			case Tag.PRT:
				type = Type.and(WriteStmt(),type); 	// stmt -> write-stmt
				eat(';');
				break;
			case Tag.ID:	 	// stmt -> assign-stmt 
				type = Type.and(AssignStmt(),type);
				eat(';');
				break;
			default:
				error(new ArrayList<>(Arrays.asList("IF", "DO", "SC", "PRT", "ID")), f.stmt);
		}
		return type;
	}

	// tratamento do simbolo assign-stmt
	public static int AssignStmt() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// assign-stmt -> id = simple-expr
			case Tag.ID:
				eat(Tag.ID);
				eat('=');
				type = Type.and(SimpleExpr(),getType(((Word)token).getLexeme()));
				break;
			default:
				error(new ArrayList<>(Arrays.asList("ID")), f.assignStmt);
		}
		return type;
	}

	// tratamento do simbolo if-stmt
	public static int IfStmt() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// if-stmt -> if condition then stmt-list if-stmt2
			case Tag.IF:
				eat(Tag.IF);
				type = Condition();
				eat(Tag.THEN);
				if(type == Type.BOOLEAN){
					type = Type.and(StmtList(),IfStmt2());	
				}else{
					type = Type.ERROR;
				}
				break;	
			default:
				error(new ArrayList<>(Arrays.asList("IF")), f.ifStmt);
		}
		return type;
	}

	// tratamento do simbolo if-stmt-2
	public static int IfStmt2() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// if-stmt2 -> end
			case Tag.END:
				eat(Tag.END);
				break;
			// if-stmt2 -> else stmt-list end
			case Tag.ELSE:
				eat(Tag.ELSE);
				type = Type.and(StmtList(),type);
				eat(Tag.END);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("END", "ELSE")), f.ifStmt2);
		}
		return type;
	}

	// simbolo condiion
	public static int Condition() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// condition -> expression
			case Tag.NOT:
			case Tag.NUM:
			case Tag.STRING:
			case Tag.ID:
			case '-':
			case '(':
				type = Type.and(Expression(),Type.BOOLEAN);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("NOT", "NUM", "STRING", "ID", "-", "(")), f.condition);
		}
		return type;
	}

	// simbolo while-stmt
	public static int WhileStmt() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// while-stmt -> do stmt-list stmt-sufix
			case Tag.DO:
				eat(Tag.DO);
				type = Type.and(StmtList(),StmtSufix());
				break;
			default:
				error(new ArrayList<>(Arrays.asList("DO")), f.whileStmt);
		}
		return type;
	}

	// simbolo stmt-sufix
	public static int StmtSufix() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// stmt-sufix -> while condition end
			case Tag.WH:
				eat(Tag.WH);
				if (Condition() == Type.BOOLEAN) type = Type.EMPTY;
				else type = Type.ERROR;
				eat(Tag.END);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("WH")), f.stmtSufix);
		}
		return type;
	}

	// simbolo read-stmt
	public static int ReadStmt() {
		int type = Type.EMPTY;
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
		return type;
	}

	// simbolo write-stmt
	public static int WriteStmt() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// write-stmt -> print "(" writable ")"
			case Tag.PRT:
				eat(Tag.PRT);
				eat('(');
				type = Type.and(Writable(), Type.EMPTY);
				eat(')');
				break;
			default:
				error(new ArrayList<>(Arrays.asList("PRT")), f.writeStmt);
		}
		return type;
	}

	// simbolo writable
	public static int Writable() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// writable -> simple-expr
			case Tag.NOT:
			case Tag.NUM:
			case Tag.ID:
			case '-':
			case '(':
				if (SimpleExpr() == Type.ERROR) type = Type.ERROR;
				break;
			case Tag.STRING:
				eat(Tag.STRING);
				break;
			default:
				error(new ArrayList<>(Arrays.asList("NOT", "NUM", "ID", "-", "(", "STRING")), f.writable);
		}
		return type;
	}

	// simbolo expression
	public static int Expression() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// expression -> simple-expression expression2
			case Tag.NOT:
			case Tag.NUM:
			case Tag.ID:
			case '-':
			case '(':
			case Tag.STRING:
				int typeSimple = SimpleExpr();
				int typeExpression = Expression2();

				if (typeExpression == Type.EMPTY) type = typeSimple;
				else if (typeSimple == Type.ERROR || typeExpression == Type.ERROR) type = Type.ERROR;
				else type = Type.BOOLEAN;

				break;
			default:
				error(new ArrayList<>(Arrays.asList("NOT", "NUM", "ID", "-", "(", "STRING")), f.expression);			
		}
		return type;
	}

	// simbolo expression2
	public static int Expression2() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// expression2 -> relop simple-expr
			case Tag.EQ:
			case Tag.GE:
			case Tag.LE:
			case Tag.NOTEQ:
			case '>':
			case '<':
				int typeRelop = Relop();
				int typeSimple = SimpleExpr();
				
				if (typeSimple != Type.EMPTY && typeRelop == Type.EMPTY) type = Type.EMPTY;
				else type = Type.ERROR;
				
				break;
			// expressio2 -> #
			case ')':
			case Tag.END:
			case Tag.THEN:
				break;
			default:
				error(new ArrayList<>(Arrays.asList("EQ", "GE", "LE", "NOTEQ", ">", "<", ")", "END", "THEN")), f.expression2);
		}
		return type;
	}

	// simbolo simple-expr
	public static int SimpleExpr() {
		int type = Type.EMPTY;
		// simple-expr -> temr simple-expr2
		switch (token.tag) {
			case Tag.NOT:
			case Tag.NUM:
			case Tag.ID:
			case '-':
			case '(':
			case Tag.STRING:

				int typeTerm = Term();
				int typeSimple = SimpleExpr2();
				if (typeSimple == Type.EMPTY) {
					type = typeTerm;
				} else if (typeTerm == Type.INTEGER) {
					if (typeSimple == Type.INTEGER) {
						type = Type.INTEGER;
					} else if (typeSimple == Type.BOOLEAN) {
						type = Type.BOOLEAN;
					} else {
						type = Type.ERROR;
					}
				} else if (typeTerm == Type.STRING && typeSimple == Type.STRING) {
					type = Type.STRING;
				} else if (typeTerm == Type.BOOLEAN) {
					if (typeSimple == Type.BOOLEAN || typeSimple == Type.INTEGER) {
						type = Type.BOOLEAN;
					} else {
						type = Type.ERROR;
					}
				} else {
					type = Type.ERROR;
				}

				break;
			default:
				error(new ArrayList<>(Arrays.asList("NOT", "NUM", "ID", "-", "(", "STRING")), f.simpleExpr);			
		}
		return type;
	}

	// simbolo simple-expr2
	public static int SimpleExpr2() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// simple-expr2 -> addop term simple-expr2
			case '+':
			case '-':
			case Tag.OR:
				int typeAdd = Addop();
				int typeTerm = Term();
				int typeSimple = SimpleExpr2();
	
				if (typeAdd == Type.EMPTY) {
					if (typeSimple == Type.EMPTY) {
						type = typeTerm;
					} else if (typeTerm == Type.INTEGER) {
						if (typeSimple == Type.INTEGER) {
							type = Type.INTEGER;
						} else if (typeSimple == Type.BOOLEAN) {
							type = Type.BOOLEAN;
						} else {
							type = Type.ERROR;
						}
					} else if (typeTerm == Type.STRING && typeSimple == Type.STRING) {
						type = Type.STRING;
					} else if (typeTerm == Type.BOOLEAN) {
						if (typeSimple == Type.BOOLEAN || typeSimple == Type.INTEGER) {
							type = Type.BOOLEAN;
						} else {
							type = Type.ERROR;
						}
					} else {
						type = Type.ERROR;
					}
				} else {
					type = Type.ERROR;
				}
				break;
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
		return type;
	}

	// simbolo term
	public static int Term() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// term -> 	factor-a term2
			case Tag.NOT:
			case Tag.NUM:
			case Tag.ID:
			case Tag.STRING:
			case '(':
			case '-':
				int typeFactorA = FactorA();
				int typeTerm2 = Term2();

				if (typeTerm2 == Type.EMPTY) {
					type = Type.EMPTY;
				} else if (typeFactorA == Type.INTEGER) {
					if (typeTerm2 == Type.BOOLEAN) {
						type = Type.BOOLEAN;
					} else if (typeTerm2 == Type.INTEGER){
						type = Type.INTEGER;
					} else {
						type = Type.ERROR;
					}
				} else if (typeFactorA == Type.BOOLEAN) {
					if (typeTerm2 == Type.BOOLEAN || typeTerm2 == Type.INTEGER) {
						type = Type.BOOLEAN;
					} else {
						type = Type.ERROR;
					}
				} else {
					type = Type.ERROR;
				}

				break;	
			default:
				error(new ArrayList<>(Arrays.asList("NOT", "NUM", "ID", "STRING", "(", "-")), f.term);
		}
		return type;
	}

	// simbolo term2
	public static int Term2() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// term2 -> mulop factor-a term2
			case Tag.AND:
			case '*':
			case '/':
				int typeMulop = Mulop();
				int typeFactorA = FactorA();
				int typeTerm2 = Term2();

				if (typeMulop == Type.EMPTY) {
					if (typeTerm2 == Type.EMPTY) {
						type = Type.EMPTY;
					} else if (typeFactorA == Type.INTEGER) {
						if (typeTerm2 == Type.BOOLEAN) {
							type = Type.BOOLEAN;
						} else if (typeTerm2 == Type.INTEGER){
							type = Type.INTEGER;
						} else {
							type = Type.ERROR;
						}
					} else if (typeFactorA == Type.BOOLEAN) {
						if (typeTerm2 == Type.BOOLEAN || typeTerm2 == Type.INTEGER) {
							type = Type.BOOLEAN;
						} else {
							type = Type.ERROR;
						}
					} else {
						type = Type.ERROR;
					}
				}

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
		return type;
	}

	// simbolo factor-a
	public static int FactorA() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// factor-a -> factor
			case Tag.NUM:
			case Tag.ID:
			case Tag.STRING:
			case '(':
				type = Factor();
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
		return type;
	}

	// simbolo factor
	public static int Factor() {
		int type = Type.EMPTY;
		switch (token.tag) {
			// factor -> id
			case Tag.ID:
				type = ((Word)token).getType();
				eat(Tag.ID);
				break;
			// factor -> constant
			case Tag.NUM:
			case Tag.STRING:
				type = Constant();
				break;
			// factor -> "(" expression ")"
			case '(':
				eat('(');
				type = Expression();
				eat(')');
				break;
			default:
				error(new ArrayList<>(Arrays.asList("ID", "NUM", "STRING", "(")), f.factor);
		}
		return type;
	}

	// simbolo relop
	public static int Relop() {
		int type = Type.EMPTY;
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
				type = Type.ERROR;
				error(new ArrayList<>(Arrays.asList("EQ", "GE", "LE", "NOTEQ", ">", "<")), f.relop);
		}
		return type;
	}

	// simbolo addop
	public static int Addop() {
		int type = Type.EMPTY;
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
				type = Type.ERROR;
				error(new ArrayList<>(Arrays.asList("+", "-", "OR")), f.addop);
		}
		return type;
	}

	// simbolo mulop
	public static int Mulop() {
		int type = Type.EMPTY;
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
				type = Type.ERROR;
				error(new ArrayList<>(Arrays.asList("*", "/", "AND")), f.mulop);
		}
		return type;
	}

	// simbolo constant
	public static int Constant() {
		int type = Type.EMPTY;
		switch (token.tag) {
			case Tag.NUM:
				type = Type.INTEGER;
				eat(Tag.NUM);
				break;
			case Tag.STRING:
				type = Type.STRING;
				eat(Tag.STRING);
				break;	
			default:
				error(new ArrayList<>(Arrays.asList("NUM", "STRING")), f.constant);
		}
		return type;
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
