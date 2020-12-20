package miniplc0java.instruction;

import java.util.Objects;

public class Instruction {
    private Operation opt;
    Integer x;

    public Instruction(Operation opt) {
        this.opt = opt;
        this.x = 0;
    }

    public Instruction(Operation opt, Integer x) {
        this.opt = opt;
        this.x = x;
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
