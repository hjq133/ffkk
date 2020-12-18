package miniplc0java.error;

import miniplc0java.util.Pos;

public class AnalyzeError extends CompileError {
    private static final long serialVersionUID = 1L;

    ErrorCode code;
    Pos pos;
    String FuncName;

    @Override
    public ErrorCode getErr() {
        return code;
    }

    @Override
    public Pos getPos() {
        return pos;
    }

    /**
     * @param code
     * @param pos
     */
    public AnalyzeError(ErrorCode code, Pos pos) {
        this.code = code;
        this.pos = pos;
        this.FuncName = null;
    }

    public AnalyzeError(ErrorCode code, String name) {
        this.code = code;
        this.pos = new Pos(0, 0);
        this.FuncName = name;
    }

    @Override
    public String toString() {
        if(this.FuncName != null) return new StringBuilder().append("Analyze Error: ").append(code).append(", at function: ").append(FuncName).toString();
        return new StringBuilder().append("Analyze Error: ").append(code).append(", at: ").append(pos).toString();
    }
}
