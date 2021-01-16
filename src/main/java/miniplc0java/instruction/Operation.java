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

    PRINTI,
    PRINTC,
    PRINTS,
    PRINTLN,

    SCANI,
    SCANC,

    LOCA,
    ARGA,
    GLOBA,

    FTOI,
    ITOF,

    CMP,
    SETLT,
    SETGT,
    NOT,

    DIVF,
    MULF,
    SUBF,
    ADDF,
    CMPF,
    NEGF;

    public byte toByte(){
        switch (this) {
            case PRINTI:
                return (byte)0x54; // print i
            case PRINTC:
                return (byte)0x55;
            case PRINTS:
                return (byte)0x57;
            case PRINTLN:
                return (byte)0x58;
            case SCANI:
                return (byte)0x50;
            case SCANC:
                return (byte)0x51;
            case SETGT:
                return (byte)0x3a;
            case SETLT:
                return (byte)0x39;
//            case nop:
//                return (byte)0x00;
            case POP:
                return (byte)0x02;
            case STO:
                return (byte)0x17;
            case LOD:
                return (byte)0x13;
            case ADD:
                return (byte)0x20;
            case SUB:
                return (byte)0x21;
            case MUL:
                return (byte)0x22;
            case DIV:
                return (byte)0x23;
            case NOT:
                return (byte)0x2e;
            case RET:
                return (byte)0x49;
            case CMP:
                return (byte)0x30;
//            case dup:
//                return (byte)0x04;
            case PUSH:
                return (byte)0x01;
            case GLOBA:
                return (byte)0x0c;
            case LOCA:
                return (byte)0x0a;
            case BR:
                return (byte)0x41;
            case ARGA:
                return (byte)0x0b;
            case CALL:
                return (byte)0x48;
            case BRTRUE:
                return (byte)0x43;
            case BRFALSE:
                return (byte)0x42;
            case FTOI:
                return (byte)0x37;
            case ITOF:
                return (byte)0x36;
//            case callname:
//                return (byte)0x4a;
            case STACKALLOC:
                return (byte)0x1a;
//            case load8:
//                return (byte)0x10;
            case ADDF:
                return (byte)0x24;
            case SUBF:
                return (byte)0x25;
            case MULF:
                return (byte)0x26;
            case DIVF:
                return (byte)0x27;
            case CMPF:
                return (byte)0x32;
            case NEGF:
                return (byte)0x35;
            case NEG:
                return (byte)0x34;
            default:
                System.out.println("error, eroor!!!!!!!!!!!!");
                return (byte)0xfe;//panic
        }
    }

}


