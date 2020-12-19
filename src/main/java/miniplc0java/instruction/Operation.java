package miniplc0java.instruction;

public enum Operation {
    ILL,
    LIT,
    LOD,
    STO,
    ADD,
    SUB,
    MUL,
    DIV,
    NOT,
    CALL,
    STACKALLOC,
    JUMP, // 条件跳转
    NOCONJUMP, // 无条件跳转
    PRINTS,
    PRINGI,
    PRINTC,
    PRINTLN,
    SCANI,
    SCANC,
    LOCA,
    ARGA,
    GLOBA,
    CMP,
    SETLT,
    SETGT,
    NEG,
    POP,
    RET,
    WRT
}
