package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.FunctionInstruction;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;

import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;
//    public ArrayList<Instruction> instructions;
    //public ArrayList<FunctionInstruction> funcInstructions;
    public SymbolTable symbolTable;

    /**
     * 当前偷看的 token
     */
    Token peekedToken = null;

    /**
     * 优先矩阵表
     */
    HashMap<String, Integer> OPPrec = new HashMap<String, Integer>();

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
//        this.instructions = new ArrayList<>();
        this.symbolTable = new SymbolTable();

        this.OPPrec.put("*", 3);
        this.OPPrec.put("/", 3);
        this.OPPrec.put("+", 2);
        this.OPPrec.put("-", 2);
        this.OPPrec.put(">", 1);
        this.OPPrec.put("<", 1);
        this.OPPrec.put(">=", 1);
        this.OPPrec.put("<=", 1);
        this.OPPrec.put("==", 1);
        this.OPPrec.put("!=", 1);
    }

    public void analyse() throws CompileError {
        analyseProgram();
    }
    
//    public void addInstructionPrint(Instruction instruction) {
//        instructions.add(instruction);
//        System.out.println(instruction);
//    }
    
    /**
     * 查看下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    /**
     * 获取下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            var token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     * @param tt
     * @return
     * @throws TokenizeError
     */
    private boolean check(TokenType tt) throws TokenizeError {
        var token = peek();
        return token.getTokenType() == tt;
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     *
     * @param tt 类型
     * @return 如果匹配则返回这个 token，否则返回 null
     * @throws TokenizeError
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     *
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    private Token expect(TokenType tt, TokenType tt2) throws CompileError {
        var token = peek();
        if (token.getTokenType() == tt || token.getTokenType() == tt2) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    /**
     * 检查符号的index并添加instruction（globa，loca， arga）
     */
    private void addSymbolInstruction(SymbolEntry symbol, ArrayList<Instruction> instructions) {
        int index;
        if(symbol.level == 1) {
            instructions.add(new Instruction(Operation.GLOBA, symbol.index));
        } else if (symbol.level == 2) {
            instructions.add(new Instruction(Operation.LOCA, symbol.index));
        } else {
            instructions.add(new Instruction(Operation.ARGA, symbol.index));
        }
    }

    /**
     * 添加二元运算符的instrucion
     */
    private void addBinaryOPInstruction(String op, ArrayList<Instruction> instructions) throws AnalyzeError{
        switch (op) {
            case "+":
                instructions.add(new Instruction(Operation.ADD));
                break;
            case "-":
                instructions.add(new Instruction(Operation.SUB));
                break;
            case "/":
                instructions.add(new Instruction(Operation.DIV));
                break;
            case "*":
                instructions.add(new Instruction(Operation.MUL));
                break;
            case ">":
                instructions.add(new Instruction(Operation.CMP));
                instructions.add(new Instruction(Operation.SETGT));
                break;
            case "<=":
                instructions.add(new Instruction(Operation.CMP));
                instructions.add(new Instruction(Operation.SETGT));
                instructions.add(new Instruction(Operation.NOT));
                break;
            case "<":
                instructions.add(new Instruction(Operation.CMP));
                instructions.add(new Instruction(Operation.SETLT));
                break;
            case ">=":
                instructions.add(new Instruction(Operation.CMP));
                instructions.add(new Instruction(Operation.SETLT));
                instructions.add(new Instruction(Operation.NOT));
                break;
            case "==":
                instructions.add(new Instruction(Operation.CMP));
                instructions.add(new Instruction(Operation.NOT));
            case "!=":
                instructions.add(new Instruction(Operation.CMP));
        }
    }

    /**
     * program -> decl_stmt* function*
     */
    private void analyseProgram() throws CompileError {
        ArrayList<Instruction> globalInstruction = new ArrayList<>();
        while(true) {
            if(check(TokenType.LET_KW) || check(TokenType.CONST_KW)) {
                analyseDeclareStatement(globalInstruction);
            } else {
                break;
            }
        }
        while(true) {
            if(check(TokenType.FN_KW)) {
                analyseFunction();
            } else {
                break;
            }
        }
        SymbolEntry symbol = symbolTable.findSymbol("main");
        if(symbol == null) { // 检测main函数是否存在
            throw new AnalyzeError(ErrorCode.NoMainFunction, "xxxx");
        }
        expect(TokenType.EOF);
        if(symbol.type != TokenType.VOID) {
            globalInstruction.add(new Instruction(Operation.STACKALLOC, 1)); // 分配返回值
        }
        globalInstruction.add(new Instruction(Operation.CALL, symbol.index));
        if(symbol.type != TokenType.VOID) {
            globalInstruction.add(new Instruction(Operation.POP, 1));
        }
    }

    /**
     * decl_stmt -> let_decl_stmt | const_decl_stmt
     */
    private void analyseDeclareStatement(ArrayList<Instruction> instructions) throws CompileError {
        if(check(TokenType.LET_KW)){
            analyseLetDeclare(instructions);
        }
        else {
            analyseConstDeclare(instructions);
        }
    }

    /**
     * let_decl_stmt -> 'let' IDENT ':' ty ('=' expr)? ';'
     * @throws CompileError
     */
    private void analyseLetDeclare(ArrayList<Instruction> instructions) throws CompileError {
        expect(TokenType.LET_KW);

        var nameToken = expect(TokenType.Ident);
        String name = (String) nameToken.getValue();

        // : 冒号
        expect(TokenType.Colon);
        var type = expect(TokenType.INT); // 只能是int

        // 变量初始化了吗
        boolean initialized = false;
        symbolTable.addSymbolVariable(name, initialized, false,  nameToken.getStartPos(), type);

        // 下个 token 是等于号吗？如果是的话分析初始化
        if (nextIf(TokenType.Assign) != null) {
            // 分析初始化的表达式
            analyseAssignExpression(nameToken.getValueString(), nameToken, instructions);
        }

        // 分号
        expect(TokenType.Semicolon);
    }

    /**
     * const_decl_stmt -> 'const' IDENT ':' ty '=' expr ';'
     */
    private void analyseConstDeclare(ArrayList<Instruction> instructions) throws CompileError {
        expect(TokenType.CONST_KW);

        // 变量名
        var nameToken = expect(TokenType.Ident);

        // 加入符号表
        String name = (String) nameToken.getValue();

        // : 冒号
        expect(TokenType.Colon);
        var type = expect(TokenType.INT);

        symbolTable.addSymbolVariable(name, true, true, nameToken.getStartPos(), type);

        // = 等号
        expect(TokenType.Assign);

        // 常表达式
        var type1 = analyseExpression(1, instructions);
        if(type1 != type.getTokenType()) throw new AnalyzeError(ErrorCode.TypeNotMatch, nameToken.getStartPos());

        // 分号
        expect(TokenType.Semicolon);
    }


    /**
     * function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
     */
    private FunctionInstruction analyseFunction() throws CompileError {

        expect(TokenType.FN_KW);

        // 函数名
        var nameToken = expect(TokenType.Ident);

        // 函数名加入符号表
        String name = (String) nameToken.getValue();

        expect(TokenType.LParen);

        // 添加符号表
        symbolTable.addNewMap();

        if(!check(TokenType.RParen)) { // 如果下一个不是右括号
            analyseFunctionParamList();
        }
        expect(TokenType.RParen);
        expect(TokenType.Arrow);

        var type = expect(TokenType.INT, TokenType.VOID);  // 返回值类型
        symbolTable.addSymbolFunc(name, nameToken.getStartPos(), type);  // 添加到全局变量表

        symbolTable.currentFuncName = name;  // 改变当前函数名称，用于return的时候check type
        analyseBlockStatement(false, instructions); // 无需再建表了

        // 删除符号表
        symbolTable.removeMap();
        symbolTable.currentFuncName = null;

        //  新建function instruction
        FunctionInstruction instruction = new FunctionInstruction();
    }

    /**
     * function_param_list -> function_param (',' function_param)*
     */
    private void analyseFunctionParamList() throws CompileError {
        analyseFunctionParam();
        while(nextIf(TokenType.Comma) != null) {
            analyseFunctionParam();
        }
    }

    /**
     * function_param -> 'const'? IDENT ':' ty
     */
    private void analyseFunctionParam() throws CompileError {
        boolean isConst = false;
        if(nextIf(TokenType.CONST_KW) != null) {
            isConst = true;
        }

        // 变量名
        var nameToken = expect(TokenType.Ident);
        expect(TokenType.Colon);

        // 类型
        var type = expect(TokenType.INT);

        // 加入符号表
        String name = (String) nameToken.getValue();
        symbolTable.addSymbolParam(name, isConst, nameToken.getStartPos(), type);
    }

    /**
     * block_stmt -> '{' stmt* '}'
     */
    private void analyseBlockStatement(boolean NeedNewMap, ArrayList<Instruction> instructions) throws CompileError {
        expect(TokenType.LBrace);
        // 创建符号表
        if(NeedNewMap) { // 从非函数入口进入需要new table
            symbolTable.addNewMap();
        }

        while(true) {
            if(check(TokenType.RBrace)){
                break;
            }else {
                analyseStatement(instructions);
            }
        }

        expect(TokenType.RBrace);
        if(NeedNewMap) {
            symbolTable.removeMap(); // 删除符号表
        }
    }

    /**
        stmt ->
            expr_stmt
            | decl_stmt 'let' | 'const'
            | if_stmt  'if'
            | while_stmt  'while'
            | return_stmt  'return'
            | block_stmt  '{'
            | empty_stmt  ';'
     */
    private void analyseStatement(ArrayList<Instruction> instructions) throws CompileError {
        if(check(TokenType.IF_KW)) {
            analyseIfStatement(instructions);
        } else if(check(TokenType.WHILE_KW)) {
            analyseWhileStatement(instructions);
        } else if(check(TokenType.RETURN_KW)) {
            analyseReturnStatement(instructions);
        } else if(check(TokenType.LET_KW) || check(TokenType.CONST_KW)) {
            analyseDeclareStatement(instructions);
        } else if(check(TokenType.LBrace)) {
            analyseBlockStatement(true, instructions);
        } else if(check(TokenType.Semicolon)) { // empty_stmt
            expect(TokenType.Semicolon);
        } else {
            analyseExpressionStatement(instructions);
        }
    }

    /**
     * expr_stmt -> expr ';'
     */
    private void analyseExpressionStatement(ArrayList<Instruction> instructions) throws CompileError{
        var type = analyseExpression(1, instructions); // 表达式如果有值，将会被丢弃
        if(type != TokenType.VOID) {
            instructions.add(new Instruction(Operation.POP));
        }
        expect(TokenType.Semicolon);
    }

    /**
     * if_stmt -> 'if' expr block_stmt ('else' (block_stmt | if_stmt))?
     */
    private void analyseIfStatement(ArrayList<Instruction> instructions) throws CompileError {
        expect(TokenType.IF_KW);
        var type = analyseExpression(1, instructions);
        if(type != TokenType.INT) throw new AnalyzeError(ErrorCode.ConditionType, symbolTable.currentFuncName);
        instructions.add(new Instruction(Operation.BRTRUE, 1));  // 如果condition满足，跳到if block开始
        instructions.add(new Instruction(Operation.BR, 0)); // 如果condition不满足，跳到else block开始, 或者跳到外面
        int index1 = instructions.size() - 1;

        analyseBlockStatement(true, instructions);

        if(nextIf(TokenType.ELSE_KW) != null) { // 有else语句
            instructions.add(new Instruction(Operation.BR, 0)); // 进入else前跳到末尾
            int index2 = instructions.size() - 1;
            if(check(TokenType.IF_KW)) {
                analyseIfStatement(instructions);
            }
            else {
                var offset = instructions.size() - 1 - index1;
                instructions.set(index1, new Instruction(Operation.BR, offset)); // 跳到else语句开始
                analyseBlockStatement(true, instructions);
            }
            var offset = instructions.size() - 1 - index2; // index2跳到末尾
            instructions.set(index2, new Instruction(Operation.BR, offset));
        } else { // 无else语句
            var offset = instructions.size() - 1 - index1; // 直接跳到外面
            instructions.set(index1, new Instruction(Operation.BR, offset));
        }
    }

    /**
     * while_stmt -> 'while' expr block_stmt
     */
    private void analyseWhileStatement(ArrayList<Instruction> instructions) throws CompileError {
        expect(TokenType.WHILE_KW);
        int index0 = instructions.size() - 1;

        var type = analyseExpression(1, instructions); // condition
        if(type != TokenType.INT) throw new AnalyzeError(ErrorCode.ConditionType, symbolTable.currentFuncName);

        instructions.add(new Instruction(Operation.BRTRUE, 1));
        instructions.add(new Instruction(Operation.BR, 0));  // 如果condition失败了，jump 到 while 外面
        int index1 = instructions.size() - 1;

        analyseBlockStatement(true, instructions);
        instructions.add(new Instruction(Operation.BR, index0 - (instructions.size() - 1) - 1)); // // 反着跳 无条件跳转到while开头

        int offset = instructions.size() - 1 - index1;
        instructions.set(index1, new Instruction(Operation.BR, offset));
    }

    private void analyseReturnStatement(ArrayList<Instruction> instructions) throws CompileError {
        expect(TokenType.RETURN_KW);
        TokenType type = TokenType.VOID;
        instructions.add(new Instruction(Operation.ARGA, 0));  // ARGA(0)
        if(nextIf(TokenType.Semicolon) == null) { // 如果有返回值
            type = analyseExpression(1, instructions);
        }
        // 如果不一致
        if(symbolTable.findSymbol(symbolTable.currentFuncName).type != type) {
            throw new AnalyzeError(ErrorCode.ReturnTypeError, symbolTable.currentFuncName);
        }
        instructions.add(new Instruction(Operation.STO));
        instructions.add(new Instruction(Operation.RET));
    }

    /**
     * computeAtom ->
     *     assign_expr
     *     | call_expr
     *     | ident_expr
     *     | negate_expr
     *     | literal_expr
     *     | group_expr
     *
     * @return
     * @throws CompileError
     */
    private TokenType computeAtom(ArrayList<Instruction> instructions) throws CompileError {
        if(check(TokenType.Ident)) {
            var nameToken = expect(TokenType.Ident);
            String name = (String) nameToken.getValue();
            if(nextIf(TokenType.Assign) != null) {  // 赋值表达式
                return analyseAssignExpression(name, nameToken, instructions);
            } else if(check(TokenType.LParen)) {  // 函数调用表达式
                return analyseCallExpression(name, nameToken, instructions);
            } else {  // 标识符表达式
                return analyseIdentExpression(name, nameToken, instructions);
            }
        } else if(check(TokenType.Minus)) {  // 取反表达式
            return analyseNegateExpression(instructions);
        } else if(check(TokenType.LParen)) {  // 括号表达式
            return analyseParenExpression(instructions);
        } else { // 字面量表达式
            return analyseLiteralExpression(instructions);
        }
    }


    /**
     * 括号表达式
     * // literal_expr -> UINT_LITERAL | DOUBLE_LITERAL | STRING_LITERAL | CHAR_LITERAL
     */
    private TokenType analyseLiteralExpression(ArrayList<Instruction> instructions) throws CompileError {
        if(check(TokenType.Uint)) {
            var token = expect(TokenType.Uint);
            int value = (int)token.getValue();
            instructions.add(new Instruction(Operation.PUSH, value));
            return TokenType.INT;
        } else if(check(TokenType.String)) {
            var token = expect(TokenType.String);
            String value = token.getValue().toString();
            symbolTable.addSymbolString(value, token.getStartPos(), token);
            instructions.add(new Instruction(Operation.PUSH, symbolTable.findSymbol(value).index)); // push index进去
            return TokenType.VOID;
        } else if(check(TokenType.Char)) {
            var token = expect(TokenType.Char);
            String value = token.getValue().toString();
            instructions.add(new Instruction(Operation.PUSH, (Integer)token.getValue()));
            return TokenType.INT;
        } else {
            throw new ExpectedTokenError(List.of(TokenType.Ident, TokenType.Uint, TokenType.LParen), next());
        }
    }


    /**
     * 括号表达式
     * group_expr -> '(' expr ')'
     */
    private TokenType analyseParenExpression(ArrayList<Instruction> instructions) throws CompileError {
        expect(TokenType.Minus);
        // 计算结果需要被 0 减
        instructions.add(new Instruction(Operation.PUSH, 0));
        var type = analyseExpression(1, instructions);
        instructions.add(new Instruction(Operation.SUB));
        return type;
    }

    /**
     * 取反表达式
     * negate_expr -> '-' expr
     */
    private TokenType analyseNegateExpression(ArrayList<Instruction> instructions) throws CompileError {
        expect(TokenType.Minus);
        TokenType rightType =  computeAtom(instructions);
        instructions.add(new Instruction(Operation.NEG));
        return rightType;
    }

    /**
     * 标识符表达式
     * ident_expr -> IDENT
     * 这儿IDent一定在 = 右边，需要初始化
     */
    private TokenType analyseIdentExpression(String name, Token nameToken, ArrayList<Instruction> instructions) throws CompileError {
        var symbol = symbolTable.findSymbol(name);
        if (symbol == null) {
            // 没有这个标识符
            throw new AnalyzeError(ErrorCode.NotDeclared, /* 当前位置 */ nameToken.getStartPos());
        } else if (symbol.isInitialized != true) {
            // 标识符没有初始化
            throw new AnalyzeError(ErrorCode.NotInitialized, /* 当前位置 */ nameToken.getStartPos());
        } else if (symbol.isFunction) {
            // 标识符是函数
            throw new AnalyzeError(ErrorCode.ExpectedVariableOrConstant, nameToken.getStartPos());
        }

        addSymbolInstruction(symbol, instructions);
        instructions.add(new Instruction(Operation.LOD));
        return symbol.type;
    }

    /**
     * 赋值表达式
     * assign_expr -> IDENT '=' expr
     * @throws CompileError
     */
    private TokenType analyseAssignExpression(String name, Token nameToken, ArrayList<Instruction> instructions) throws CompileError{
        var symbol = symbolTable.findSymbol(name);
        if (symbol == null) {
            // 没有这个标识符
            throw new AnalyzeError(ErrorCode.NotDeclared, /* 当前位置 */ nameToken.getStartPos());
        } else if (symbol.isConstant) {
            // 标识符是常量
            throw new AnalyzeError(ErrorCode.AssignToConstant, /* 当前位置 */ nameToken.getStartPos());
        } else if (symbol.isFunction) {
            // 标识符是函数
            throw new AnalyzeError(ErrorCode.ExpectedVariableOrConstant, nameToken.getStartPos());
        }

        // 设置符号已初始化
        symbol.setInitialized(true);
        // 压入地址
        addSymbolInstruction(symbol, instructions);

        var type = analyseExpression(1, instructions);
        if(symbol.type != type) {
            throw new AnalyzeError(ErrorCode.TypeNotMatch, nameToken.getStartPos());
        }

        instructions.add(new Instruction(Operation.STO));
        return TokenType.VOID;
    }

    /**
     * 函数调用表达式
     * call_expr -> IDENT '(' call_param_list? ')'
     */
    private TokenType analyseCallExpression(String name, Token nameToken, ArrayList<Instruction> instructions) throws CompileError{
        if(symbolTable.standardFunction.get(name) == null) { // 不是标准库函数
            var symbol = symbolTable.findSymbol(name);
            if (symbol == null) {
                // 没有这个标识符
                throw new AnalyzeError(ErrorCode.NotDeclared, /* 当前位置 */ nameToken.getStartPos());
            } else if(symbol.isFunction == false) {
                // 不是函数
                throw new AnalyzeError(ErrorCode.ExpectedFunction, /* 当前位置 */ nameToken.getStartPos());
            }
            if(symbol.type != TokenType.VOID) {
                instructions.add(new Instruction(Operation.STACKALLOC, 1)); // 分配返回值
            }

            expect(TokenType.LParen);
            if(check(TokenType.RParen)) { // 检查是否有参数列表
                expect(TokenType.RParen);
            }else {
                analyseCallParamList(instructions);
                expect(TokenType.RParen);
            }
            instructions.add(new Instruction(Operation.CALL, symbol.index));
            return symbol.type;
        } else {  // 是标准库函数
            expect(TokenType.LParen);
            if(check(TokenType.RParen)) { // 检查是否有参数列表
                expect(TokenType.RParen);
            }else {
                analyseCallParamList(instructions);
                expect(TokenType.RParen);
            }
            instructions.add(new Instruction(symbolTable.StandardOP.get(name)));
            return symbolTable.standardFunction.get(name);
        }
    }

    /**
     * expr -> computeAtom (operator_expr | as_expr)
     */
    private TokenType analyseExpression(int minPrec, ArrayList<Instruction> instructions) throws CompileError {
        var leftType = computeAtom(instructions);
        if(check(TokenType.AS_KW)) { // as_expr -> expr 'as' ty
            expect(TokenType.AS_KW);
            var type = expect(TokenType.INT, TokenType.VOID);
            // TODO 附加的先不管
        } else { // operator_expr -> expr binary_operator expr 运算符表达式
            while(true) {
                var token = peek();
                String name = token.getValue().toString();
                if (OPPrec.get(name) == null || OPPrec.get(name) < minPrec) {
                    return leftType;
                }
                next();
                String op = token.getValue().toString();
                int prec = OPPrec.get(op);
                int nextMinPrec = prec + 1;

                TokenType rightType;
                if(check(TokenType.Minus)) {
                    rightType = analyseNegateExpression(instructions);
                } else {
                    rightType = analyseExpression(nextMinPrec, instructions);
                }
                if(leftType != rightType) {
                    throw new AnalyzeError(ErrorCode.TypeNotMatch, token.getStartPos());
                }
                addBinaryOPInstruction(op, instructions);
            }
        }
        return TokenType.INT;
    }

    private void analyseCallParamList(ArrayList<Instruction> instructions) throws CompileError {
        analyseExpression(1, instructions);
        while(nextIf(TokenType.Comma) != null) {
            analyseExpression(1, instructions);
        }
    }
}
