package miniplc0java.tokenizer;

import miniplc0java.util.Pos;
import miniplc0java.error.TokenizeError;
import miniplc0java.error.ErrorCode;

import java.util.HashMap;

public class Tokenizer {
    private StringIter it;
    private HashMap<String, TokenType> keywordMap = new HashMap<>() {
        {
//            put("begin", TokenType.Begin);
//            put("end", TokenType.End);
//            put("var", TokenType.Var);
//            put("const", TokenType.Const);
//            put("print", TokenType.Print);
            put("fn", TokenType.FN_KW);
            put("let", TokenType.LET_KW);
            put("const", TokenType.CONST_KW);
            put("as", TokenType.AS_KW);
            put("while", TokenType.WHILE_KW);
            put("if", TokenType.IF_KW);
            put("else", TokenType.ELSE_KW);
            put("return", TokenType.RETURN_KW);
            put("break", TokenType.BREAK_KW);
            put("continue", TokenType.CONTINUE_KW);

            /* type */
            put("int", TokenType.INT);
            put("void", TokenType.VOID);
            put("double", TokenType.Double);
        }
    };

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了

    /**
     * 获取下一个 Token
     *
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();

        // 跳过之前的所有空白字符
        skipSpaceCharacters();

        if (it.isEOF()) {
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }

        char peek = it.peekChar();
        if (Character.isDigit(peek)) {
            return lexUInt();
        } else if (Character.isAlphabetic(peek)) {
            return lexIdentOrKeyword();
        } else if(peek == '\'') {
            return lexChar();
        } else if(peek == '\"') {
            return lexString();
        } else {
            return lexOperatorOrUnknown();
        }
    }

    private Token lexUInt() throws TokenizeError {
        String val = "";
        char peek = it.peekChar();
        Pos begin = it.currentPos();
        TokenType type = TokenType.Uint;
        while (Character.isDigit(peek)||peek=='.'||peek=='e'||peek=='E'||peek=='+'||peek=='-') {
            if(peek == '.'){
                type = TokenType.DoubleLiteral;
            }else if(peek=='e'||peek=='E'||peek=='+'||peek=='-') {
                if(type == TokenType.Uint) {
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
                }
            }
            it.nextChar();
            val = val + peek;
            peek = it.peekChar();
        }
        Pos end = it.currentPos();
        try {
            if(type==TokenType.Uint){
                return new Token(TokenType.Uint, Integer.parseInt(val), begin, end);
            }
            else {
                return new Token(TokenType.DoubleLiteral, Double.doubleToLongBits(Double.parseDouble(val)), begin, end);
            }
        }catch (Exception e) {
            throw new TokenizeError(ErrorCode.InvalidInput, begin);
        }
    }

    private Token lexString() throws TokenizeError {
        String val = "";
        char peek = it.peekChar();
        char ch;
        Pos begin = it.currentPos();
        if (peek  != '\"') {
            throw new TokenizeError(ErrorCode.InvalidInput, begin);
        }
        it.nextChar();
        peek = it.peekChar();
        while(peek != '\"') {
            it.nextChar();
            if(peek == '\n') {
                throw new TokenizeError(ErrorCode.InvalidInput, begin);
            }
            if(peek == '\\') {  // 转义字符
                peek = it.peekChar();
                ch = switch (peek) {
                    case '\\' -> '\\';
                    case '\"' -> '\"';
                    case '\'' -> '\'';
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    case 't' -> '\t';
                    default -> throw new TokenizeError(ErrorCode.InvalidInput, begin);
                };
                it.nextChar();
                peek = ch;
            }
            val = val + peek;
            peek = it.peekChar();
        }
        it.nextChar();
        Pos end = it.currentPos();
        return new Token(TokenType.String, val, begin, end);
    }

    private Token lexChar() throws TokenizeError {
        String val = "";
        char peek = it.peekChar();
        Pos begin = it.currentPos();
        if (peek  != '\'') {
            throw new TokenizeError(ErrorCode.InvalidInput, begin);
        }
        it.nextChar();
        peek = it.peekChar();
        if(peek == '\\') {
            val = val + peek;
            it.nextChar();
            peek = it.peekChar();
            if(peek == '\\' || peek == '\"' || peek == '\'' || peek == 'n' || peek == 'r' || peek == 't') {
                val = val + peek;
                it.nextChar();
            } else {
                throw new TokenizeError(ErrorCode.InvalidInput, begin);
            }
        } else {
            val = val + peek;
            it.nextChar();
        }
        peek = it.peekChar();
        if(peek != '\'') {
            throw new TokenizeError(ErrorCode.InvalidInput, begin);
        }
        it.nextChar();
        Pos end = it.currentPos();
        return switch (val) {
            case "\\n" -> new Token(TokenType.Char, '\n', begin, end);
            case "\\r" -> new Token(TokenType.Char, '\r', begin, end);
            case "\\t" -> new Token(TokenType.Char, '\t', begin, end);
            case "\\\\" -> new Token(TokenType.Char, '\\', begin, end);
            case "\\'" -> new Token(TokenType.Char, '\'', begin, end);
            case "\\\"" -> new Token(TokenType.Char, '\"', begin, end);
            default -> new Token(TokenType.Char, val.charAt(0), begin, end);
        };
    }

    // TODO done!
    private Token lexIdentOrKeyword() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字或字母为止:
        String val = "";
        char peek = it.peekChar();
        Pos begin = it.currentPos();
        while (Character.isDigit(peek) || Character.isAlphabetic(peek) || peek == '_') {
            it.nextChar();
            val = val + peek;
            peek = it.peekChar(); // 查看下一个字符
            // -- 前进一个字符，并存储这个字符
        }
        Pos end = it.currentPos();
        if (this.keywordMap.containsKey(val)) {  // 如果是关键字
            return new Token(keywordMap.get(val), val, begin, end);
        } else {  // 是标识符
            return new Token(TokenType.Ident, val, begin, end);
        }
        // 尝试将存储的字符串解释为关键字
        // -- 如果是关键字，则返回关键字类型的 token
        // -- 否则，返回标识符
        //
        // Token 的 Value 应填写标识符或关键字的字符串
    }

    private Token lexOperatorOrUnknown() throws TokenizeError {
        Pos begin, end;
        char peek;
        switch (it.nextChar()) {
            case '+':
                return new Token(TokenType.Plus, '+', it.previousPos(), it.currentPos());
            case '-':
                begin = it.previousPos();
                peek = it.peekChar();
                if(peek == '>') {
                    it.nextChar();
                    return new Token(TokenType.Arrow, "->", begin, it.currentPos());
                }
                return new Token(TokenType.Minus, '-', it.previousPos(), it.currentPos());
            case '*':
                return new Token(TokenType.Mult, '*', it.previousPos(), it.currentPos());
            case '/':
                peek = it.peekChar();
                if(peek == '/') {
                    while (peek != '\n') {
                        it.nextChar();
                        peek = it.peekChar();
                    }
                    it.nextChar();
                    return nextToken();
                } else {
                    return new Token(TokenType.Div, '/', it.previousPos(), it.currentPos());
                }
            case '=':
                begin = it.previousPos();
                peek = it.peekChar();
                if(peek == '=') {
                    it.nextChar();
                    return new Token(TokenType.Eq, "==", begin, it.currentPos());
                }
                return new Token(TokenType.Assign, '=', it.previousPos(), it.currentPos());
            case '!':
                begin = it.previousPos();
                peek = it.peekChar();
                if(peek == '=') {
                    it.nextChar();
                    return new Token(TokenType.Eq, "!=", begin, it.currentPos());
                } else {
                    throw new TokenizeError(ErrorCode.InvalidInput, begin);
                }
            case ';':
                return new Token(TokenType.Semicolon, ';', it.previousPos(), it.currentPos());
            case ':':
                return new Token(TokenType.Colon, ':', it.previousPos(), it.currentPos());
            case ',':
                return new Token(TokenType.Comma, ',', it.previousPos(), it.currentPos());
            case '(':
                return new Token(TokenType.LParen, '(', it.previousPos(), it.currentPos());
            case ')':
                return new Token(TokenType.RParen, ')', it.previousPos(), it.currentPos());
            case '{':
                return new Token(TokenType.LBrace, '{', it.previousPos(), it.currentPos());
            case '}':
                return new Token(TokenType.RBrace, '}', it.previousPos(), it.currentPos());
            case '<':
                begin = it.previousPos();
                peek = it.peekChar();
                if(peek == '=') {
                    it.nextChar();
                    return new Token(TokenType.Le, "<=", begin, it.currentPos());
                }
                return new Token(TokenType.Lt, '<', it.previousPos(), it.currentPos());
            case '>':
                begin = it.previousPos();
                peek = it.peekChar();
                if(peek == '=') {
                    it.nextChar();
                    return new Token(TokenType.Ge, ">=", begin, it.currentPos());
                }
                return new Token(TokenType.Gt, '>', it.previousPos(), it.currentPos());
            default:
                // 不认识这个输入，摸了
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}