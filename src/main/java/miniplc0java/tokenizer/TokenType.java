package miniplc0java.tokenizer;

public enum TokenType {
    /** 关键字 */
    FN_KW,
    LET_KW,
    CONST_KW,
    AS_KW,
    WHILE_KW,
    IF_KW,
    ELSE_KW,
    RETURN_KW,
    Ident,

    // 字面量
    Uint,
    DoubleLiteral,
    String,
    Char,

    BREAK_KW,
    CONTINUE_KW,

    /** 运算符 */
    /** 加号 */
    Plus,
    /** 减号 */
    Minus,
    /** 乘号 */
    Mult,
    /** 除号 */
    Div,
    /** 左括号 */
    LParen,
    /** 右括号 */
    RParen,
    /** 左大括号 */
    LBrace,
    /** 右大括号 */
    RBrace,

    Assign, // =
    Eq, // ==
    Neq, // !=
    Lt,
    Gt,
    Le,
    Ge,
    Arrow,
    Comma,
    Colon,
    Semicolon,

    /** type */
    INT,
    VOID,
    Double,

    /** 文件尾 */
    EOF;

    @Override
    public String toString() {
        switch (this) {
            case FN_KW:
                return "FN_KW";
            case LET_KW:
                return "LET_KW";
            case CONST_KW:
                return "CONST_KW";
            case AS_KW:
                return "AS_KW";
            case WHILE_KW:
                return "WHILE_KW";
            case IF_KW:
                return "IF_KW";
            case ELSE_KW:
                return "ELSE_KW";
            case RETURN_KW:
                return "RETURN_KW";
            case Ident:
                return "Identifier";

            case Uint:
                return "UnsignedInteger";
            case DoubleLiteral:
                return "DoubleLiteral";
            case String:
                return "String";
            case Char:
                return "Char";

            case Double:
                return "Double";
            case INT:
                return "int";
            case VOID:
                return "void";

            /* 运算符 */
            case LParen:
                return "LeftBracket";
            case RParen:
                return "RightBracket";
            case Minus:
                return "MinusSign";
            case Mult:
                return "MultiplicationSign";
            case Plus:
                return "PlusSign";
            case Div:
                return "DivisionSign";
            case Semicolon:
                return "Semicolon";
            case Assign:
                return "Assign";
            case Eq:
                return "eq";
            case Ge:
                return "ge";
            case Gt:
                return "gt";
            case Le:
                return "le";
            case Lt:
                return "lt";
            case Neq:
                return "neq";
            case Arrow:
                return "arrow";
            case Colon:
                return "colon"; // :
            case Comma:
                return "comma";
            case LBrace:
                return "lbrace";
            case RBrace:
                return "rbrace";
            case EOF:
                return "EOF";
            default:
                return "InvalidToken";
        }
    }
}
