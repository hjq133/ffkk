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
     * 符号表 * 3， 全局表，全局函数表，参数/返回值表，局部变量表
     */
    public HashMap<String, SymbolEntry> indexMapGlobal = new HashMap<>();
    public HashMap<String, SymbolEntry> indexMapFunc = new HashMap<>();
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

//        for (HashMap.Entry<String, TokenType> entry : standardFunction.entrySet()) { // TODO 是直接提前加进去嘛
//            int index = indexMapFunc.size();
//            this.indexMapFunc.put(entry.getKey(), new SymbolEntry(false, false, true, entry.getValue(), 1, index));
//        }
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
        SymbolEntry entry = new SymbolEntry(isConstant, isInitialized, false, typeToken.getTokenType(), 0, 0);
        if (currentFuncName == null) { // 加入全局表
            entry.level = 1;
            entry.isInitialized = true; // 全局符号自动初始化
            entry.index = indexMapGlobal.size();
            addSymbol(name, entry, curPos, indexMapGlobal);
        }
        else { // 加入局部表
            entry.level = 2;
            entry.index = indexTableLocal.get(indexTableLocal.size() - 1).size(); // 从0开始编号
            addSymbol(name, entry, curPos, indexTableLocal.get(indexTableLocal.size() - 1));
        }
    }

    /**
     * 添加一个string类型全局变量
     */
    public void addSymbolString(String name, Pos curPos, Token typeToken) throws AnalyzeError {
        SymbolEntry entry = new SymbolEntry(true, true, false, typeToken.getTokenType(), 1, indexMapGlobal.size());
        addSymbol(name, entry, curPos, indexMapGlobal);
    }

    /**
     * 添加一个函数符号, 默认加入Func符号表
     * @throws AnalyzeError
     */
    public void addSymbolFunc(String name, Pos curPos, Token typeToken) throws AnalyzeError {
        int index = indexMapFunc.size() + 1; // func的编号从1开始
        SymbolEntry entry = new SymbolEntry(false, false, true, typeToken.getTokenType(), 1, index);
        addSymbol(name, entry, curPos, indexMapFunc);
    }

    /**
     * 添加一个函数符号, 默认加入当前的param符号表
     * @throws AnalyzeError
     */
    public void addSymbolParam(String name, boolean isConstant, Pos curPos, Token typeToken) throws AnalyzeError {
        int index = indexTableParam.get(indexTableParam.size() - 1).size() + 1; // 多算一个，前面有个返回地址
        SymbolEntry entry = new SymbolEntry(isConstant, true, false, typeToken.getTokenType(), 3, index);
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
        this.indexTableLocal.remove(indexTableLocal.get(indexTableLocal.size()-1));
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

        // 再查全局变量
        symbol = indexMapGlobal.get(name);
        if(symbol != null) return symbol;

        // 再查函数表
        symbol = indexMapFunc.get(name);
        return symbol;
    }
}
