public class Error {
    public static void assignError(int type){
        if(type == Type.ERROR){
            System.out.println("--------------------------------------------------------------");
            System.out.format("Linha %d\n", Sintatico.l.line);
            System.out.println("Assigment error: incompatible types.");
        }
    }

    public static void assignError(Token token) {
        if (((Word) token).getType() == Type.EMPTY) {
            System.out.println("--------------------------------------------------------------");
            System.out.format("Linha %d\n", Sintatico.l.line);
            System.out.println("Assigment error: right side variable \'" + ((Word) token).getLexeme() + "\' not declared");

        }
    }

    public static void simpleExprError(int type){
        if(type == Type.ERROR){
            System.out.println("--------------------------------------------------------------");
            System.out.format("Linha %d\n", Sintatico.l.line);
            System.out.println("Expression error: operator types are incompatible");
        }
    }

    public static void factorError(int type, Token token){
        if(type == Type.EMPTY){
            System.out.println("--------------------------------------------------------------");
            System.out.format("Linha %d\n", Sintatico.l.line);
            System.out.println("Variable error: \'" +  ((Word)token).getLexeme() + "\' not declared.");
        }
    }

    public static void declarationError(String wrongVariable){
        System.out.println("--------------------------------------------------------------");
        System.out.format("Linha %d\n", Sintatico.l.line);
        System.out.println("Variable error: Double declaration of "+"\'"+wrongVariable+"\'");
    }
}
