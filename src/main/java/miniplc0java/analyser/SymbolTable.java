package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.util.Pos;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {

    /**
     * 符号表 * 3， 全局表，参数/返回值表，局部变量表
     */
    HashMap<String, SymbolEntry> indexMapGloba = new HashMap<>();
    ArrayList<HashMap<String, SymbolEntry>> indexTableParam = new ArrayList<>();
    ArrayList<HashMap<String, SymbolEntry>> indexTableLocal = new ArrayList<>();
    public String currentFuncName = null; // 当前所在函数的名字

    /**
     * 标准库函数
     */
    HashMap<String, TokenType> standardFunction = new HashMap<>();
    HashMap<String, Operation> StandardOP = new HashMap<>();

    public SymbolTable() {
        // 标准库
        this.StandardOP.put("getint", Operation.SCANI);
        this.StandardOP.put("getchar", Operation.SCANC);
        this.StandardOP.put("putint", Operation.PRINGI);
        this.StandardOP.put("putchar", Operation.PRINTC);
        this.StandardOP.put("putstr", Operation.PRINTS);
        this.StandardOP.put("putln", Operation.PRINTLN);

        // 标准库
        this.standardFunction.put("getint", TokenType.INT);
        //this.standardFunction.put("getdouble", TokenType.INT);
        this.standardFunction.put("getchar", TokenType.INT);
        this.standardFunction.put("putint", TokenType.VOID);
        //this.standardFunction.put("putdouble", TokenType.VOID);
        this.standardFunction.put("putchar", TokenType.VOID);
        this.standardFunction.put("putstr", TokenType.VOID);
        this.standardFunction.put("putln", TokenType.VOID);

        for (HashMap.Entry<String, TokenType> entry : standardFunction.entrySet()) {
            this.indexMapGloba.put(entry.getKey(), new SymbolEntry(false, false, true, entry.getValue()));
        }
    }

    /**
     * 在某个符号表中添加一个符号
     */
    private void addSymbol(String name, SymbolEntry entry, Pos curPos, HashMap<String, SymbolEntry> symbolTable) throws AnalyzeError {
        if (symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            symbolTable.put(name, entry);
        }
    }

    /**
     * 添加一个变量, 根据当前current func 选择加入当前的 局部/全局 变量符号表
     * @throws AnalyzeError 如果重复定义了则抛异常
     */
    public void addSymbolVariable(String name, boolean isInitialized, boolean isConstant, Pos curPos, Token typeToken) throws AnalyzeError {
        SymbolEntry entry = new SymbolEntry(isConstant, isInitialized, false, typeToken.getTokenType());
        if (currentFuncName == null) { // 加入全局表
            addSymbol(name, entry, curPos, indexMapGloba);
        }
        else { // 加入局部表
            addSymbol(name, entry, curPos, indexTableLocal.get(indexTableLocal.size() - 1));
        }
    }

    /**
     * 添加一个函数符号, 默认加入Global符号表
     * @throws AnalyzeError
     */
    public void addSymbolFunc(String name, Pos curPos, Token typeToken) throws AnalyzeError {
        SymbolEntry entry = new SymbolEntry(false, false, true, typeToken.getTokenType());
        addSymbol(name, entry, curPos, indexMapGloba);
    }

    /**
     * 添加一个函数符号, 默认加入当前的param符号表
     * @throws AnalyzeError
     */
    public void addSymbolParam(String name, boolean isConstant, Pos curPos, Token typeToken) throws AnalyzeError {
        SymbolEntry entry = new SymbolEntry(isConstant, true, false, typeToken.getTokenType());
        addSymbol(name, entry, curPos, indexTableParam.get(indexTableParam.size() - 1));
    }

    /**
     * 添加一个新的符号表
     */
    public void addNewMap() {
        this.indexTableParam.add(new HashMap<>());
        this.indexTableLocal.add(new HashMap<>());
    }

    /**
     * 移除一个新的符号表
     */
    public void removeMap() {
        this.indexTableParam.remove(indexTableParam.get(indexTableParam.size()-1));
        this.indexTableParam.remove(indexTableLocal.get(indexTableLocal.size()-1));
    }

    /**
     * 递归向上查询符号
     */
    public SymbolEntry findSymbol(String name) throws AnalyzeError {
        HashMap<String, SymbolEntry> map;
        SymbolEntry symbol;

        for(int i = indexTableLocal.size()-1; i>=0; i--) {
            // 先查local table
            map = indexTableLocal.get(i);
            symbol = map.get(name);
            if(symbol != null) return symbol;

            // 再查param table
            map = indexTableParam.get(i);
            symbol = map.get(name);
            if(symbol != null) return symbol;
        }

        // 再查全局
        symbol = indexMapGloba.get(name);
        return symbol;
    }
}
