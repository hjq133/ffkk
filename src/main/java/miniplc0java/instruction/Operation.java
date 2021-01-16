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
    NOT;

    public byte toByte(){
        switch (this) {
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
//            case callname:
//                return (byte)0x4a;
            case STACKALLOC:
                return (byte)0x1a;
//            case load8:
//                return (byte)0x10;
//            case addf:
//                return (byte)0x24;
//            case subf:
//                return (byte)0x25;
//            case mulf:
//                return (byte)0x26;
//            case divf:
//                return (byte)0x27;
//            case cmpf:
//                return (byte)0x32;
//            case ftoi:
//                return (byte)0x37;
//            case itof:
//                return (byte)0x36;
//            case negf:
//                return (byte)0x35;
            case NEG:
                return (byte)0x34;
            default:
                return (byte)0xfe;//panic
        }
    }

}


