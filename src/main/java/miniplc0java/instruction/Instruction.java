package miniplc0java.instruction;

import miniplc0java.error.AnalyzeError;

import java.util.Objects;

public class Instruction {
    public Operation opt;
    Integer x;

    public Instruction(Operation opt) {
        this.opt = opt;
        this.x = 0;
    }

    public Instruction(Operation opt, Integer x) {
        this.opt = opt;
        this.x = x;
    }

    public static int operation2byte(Operation operation) {
        switch (operation) {
            // 没有操作数的
            case PUSH:
                return 0x01;
            case POP:
                return 0x02;
            case LOCA:
                return 0x0a;
            case ARGA:
                return 0x0b;
            case GLOBA:
                return 0x0c;
            case LOD:
                return 0x13; // load 64
            case STO:
                return 0x17; // sto 64
            case STACKALLOC:
                return 0x1a;
            case ADD:
                return 0x20; // add i
            case SUB:
                return 0x21; // sub i
            case MUL:
                return 0x22; // mul i
            case DIV:
                return 0x23; // div i
            case NOT:
                return 0x2e;
            case CMP:
                return 0x30; // cmp i
            case NEG:
                return 0x34;
            case SETLT:
                return 0x39;
            case SETGT:
                return 0x3a;
            case BR:
                return 0x41;// 无条件跳转
            case BRFALSE:
                return 0x42;
            case BRTRUE:
                return 0x43;
            case CALL:
                return 0x48;
            case RET:
                return 0x49;
            case PRINGI:
                return 0x54; // print i
            case PRINTC:
                return 0x55;
            case PRINTS:
                return 0x57;
            case PRINTLN:
                return 0x58;
            case SCANI:
                return 0x50;
            case SCANC:
                return 0x51;
            default:
                System.out.println("errrrrrooorrroooorrooorrr!!!!!!!!!!");
                return 0xff; // error
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return opt == that.opt && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opt, x);
    }

    public Operation getOpt() {
        return opt;
    }

    public void setOpt(Operation opt) {
        this.opt = opt;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    @Override
    public String toString() {
        switch (this.opt) {
            // 没有操作数的
            case POP:
            case ADD:
            case DIV:
            case MUL:
            case SUB:
            case NEG:
            case NOT:
            case LOD:
            case STO:
            case RET:
            case PRINGI:
            case PRINTC:
            case PRINTS:
            case PRINTLN:
            case SCANI:
            case SCANC:
            case SETLT:
            case SETGT:
            case CMP:
                return String.format("%s", this.opt);

            // 操作数为1的
            case PUSH:
            case CALL:
            case BRTRUE:
            case BRFALSE:
            case BR:
            case STACKALLOC:
            case LOCA:
            case GLOBA:
            case ARGA:
                return String.format("%s %s", this.opt, this.x);
            default:
                return "ILL";
        }
    }
}
