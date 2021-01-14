package miniplc0java.instruction;

import java.util.ArrayList;

public class FunctionInstruction {
    int name;  // 函数名称在全局变量中的位置
    int retSlot;  // 返回值占据的 slot 数
    int paraSlot;  // 参数值占据的 slot 数
    int localSlot;  // 局部变量占据的 slot 数
    private ArrayList<Instruction> instructions;

    public FunctionInstruction(ArrayList<Instruction> instructions, int retSlot, int paraSlot, int localSlot, int funcIndex) {
        this.retSlot = retSlot;
        this.paraSlot = paraSlot;
        this.localSlot = localSlot;
        this.instructions = instructions;
        this.name = funcIndex;
    }
}
