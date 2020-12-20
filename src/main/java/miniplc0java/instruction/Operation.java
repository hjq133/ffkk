package miniplc0java.instruction;

public enum Operation {
    PUSH,
    POP,
    LOD,
    STO,

    ADD,
    SUB,
    MUL,
    DIV,
    NEG,

    CALL,
    RET,
    STACKALLOC,

    BRTRUE,
    BRFALSE,
    BR, // 无条件跳转

    PRINGI,
    PRINTC,
    PRINTS,
    PRINTLN,

    SCANI,
    SCANC,

    LOCA,
    ARGA,
    GLOBA,

    CMP,
    SETLT,
    SETGT,
    NOT,
}
