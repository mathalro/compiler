import java.util.Scanner;
import java.io.*;

public class Sintatico {
	public static Token token;
	public static Lexer l;		

	/* Implementacao dos Procedimentos necessarios para o parser */
	// procedimento responsavel por ler o proximo token da entrada
	public static void consome(int tag) {
		try {
			token = l.scan();
			System.out.format("Status %15s      <Lexeme> %10s Line\n", "", "");
			System.out.println("-----------------------------------------------");

			if (token.tag == Tag.ERRO) {
				System.out.format("Malformed Token     %15s %15d\n", token.toString(), l.line);
			} else if (token.tag == Tag.INV) {
				System.out.format("Invalid Token       %15s %15d\n", token.toString(), l.line);
			} else if (token.tag == Tag.EOF) {
				System.out.format("Lexical analysis finished\n");
			} else if (token.tag == Tag.UEOF) {
				System.out.format("Unexpected EOF\n");
			} else if (token.tag == Tag.OTH) {
				System.out.format("Other token\n");;
			} else {
				System.out.format("Consumed token      %15s %15d\n", token.toString(), l.line);
			}
		} catch(IOException e) {
			System.out.format("Read error\n");
		}
	}

	// procedimento inicial S, raiz da arvore de derivacao
	public static void S() {
		switch (token.tag) {
			case Tag.PRG:
				Program();
				consome(Tag.EOF);
				break;
			default:
				System.out.println("Expected program.");
				// fazer a propagracao de erro aqui
		}
	}

	// procedimento responsavel pelo tratamento do simbolo program
	public static void Program() {
		switch (token.tag) {
			case Tag.PRG:
				consome(Tag.PRG);
				
				break;
			default:
				System.out.println("Expected program.");
				// fazer propagacao de erro aqui
		}
	}

	// simbolo opt-decl-list
	public static void OptDeclList() {
		switch (token.tag) {
			default:
				System.out.println("Expected .");
		}
	}

	// tratamento do simbolo decl-list
	public static void DeclList() {
		switch (token.tag) {
			
		}
	}

	// tratamento do simbolo opt-decl
	public static void OptDecl() {
		switch (token.tag) {
			
		}
	}

	// tratamento do simbolo decl
	public static void Decl() {
		switch (token.tag) {
			
		}
	}

	// tratamentodo simbolo idnet-list
	public static void IdentList() {
		switch (token.tag) {
			
		}
	}

	// tratamentodo simbolo opt-identifier
	public static void OptIdentifier() {
		switch (token.tag) {
			
		}
	}

	// tratamento do simbolo type
	public static void Type() {
		switch (token.tag) {
			
		}
	}
	
	// tratamento do simbolo stmt-list
	public static void StmtList() {
		switch (token.tag) {
			
		}
	}

	// tratamento do simbolo opt-stmt
	public static void OptStmt() {
		switch (token.tag) {
			
		}
	}

	// tratamento do simbolo stmt
	public static void Stmt() {
		switch (token.tag) {
			
		}
	}

	// tratamento do simbolo assign-stmt
	public static void AssignStmt() {
		switch (token.tag) {
			
		}
	}

	// tratamento do simbolo if-stmt
	public static void IfStmt() {
		switch (token.tag) {
			
		}
	}

	// tratamento do simbolo if-stmt-2
	public static void IfStmt2() {
		switch (token.tag) {
			
		}
	}

	// simbolo condiion
	public static void Condition() {
		switch (token.tag) {
			
		}
	}

	// simbolo while-stmt
	public static void WhileStmt() {
		switch (token.tag) {
			
		}
	}

	// simbolo stmt-sufix
	public static void StmtSufix() {
		switch (token.tag) {
			
		}
	}

	// simbolo read-stmt
	public static void ReadStmt() {
		switch (token.tag) {
			
		}
	}

	// simbolo write-stmt
	public static void WriteStmt() {
		switch (token.tag) {
			
		}
	}

	// simbolo writable
	public static void Writable() {
		switch (token.tag) {
			
		}
	}

	// simbolo expression
	public static void Expression() {
		switch (token.tag) {
			
		}
	}

	// simbolo expression2
	public static void Expression2() {
		switch (token.tag) {
			
		}
	}

	// simbolo simple-expr
	public static void SimpleExpr() {
		switch (token.tag) {
			
		}
	}

	// simbolo simple-expr2
	public static void SimpleExpre2() {
		switch (token.tag) {
			
		}
	}

	// simbolo term
	public static void Term() {
		switch (token.tag) {
			
		}
	}

	// simbolo term2
	public static void Term2() {
		switch (token.tag) {
			
		}
	}

	// simbolo factor-a
	public static void FactorA() {
		switch (token.tag) {
			
		}
	}

	// simbolo factor
	public static void Factor() {
		switch (token.tag) {
			
		}
	}

	// simbolo relop
	public static void Relop() {
		switch (token.tag) {
			
		}
	}

	// simbolo addop
	public static void Addop() {
		switch (token.tag) {
			
		}
	}

	// simbolo mulop
	public static void Mulop() {
		switch (token.tag) {
			
		}
	}

	// simbolo constant
	public static void Constant() {
		switch (token.tag) {
			
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
			S();
	
			System.out.println("\n\nSymbol Table: \n");
			l.printTable();
			System.out.println();
		}catch(FileNotFoundException e) {
			System.out.format("An exception ocurred");
		}
	}
}
