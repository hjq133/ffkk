package miniplc0java.analyser;

import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;

public class SymbolEntry {
    boolean isConstant;
    boolean isInitialized;
    boolean isFunction;
    TokenType type;
    int level; // 1 代表全局，2 代表局部变量， 3 代表函数参数
    int index;

    public SymbolEntry(boolean isConstant, boolean isInitialized, boolean isFunction, TokenType type, int level, int index) {
        this.isConstant = isConstant;
        this.isInitialized = isInitialized;
        this.type = type;
        this.isFunction = isFunction;
        this.level = level;
        this.index = index;
    }

    /**
     * @return the isConstant
     */
    public boolean isConstant() {
        return isConstant;
    }

    /**
     * @return the isInitialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * @param isConstant the isConstant to set
     */
    public void setConstant(boolean isConstant) {
        this.isConstant = isConstant;
    }

    /**
     * @param isInitialized the isInitialized to set
     */
    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }
}
