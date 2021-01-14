package miniplc0java.instruction;

import java.util.ArrayList;

public class FunctionInstruction {
    public String funcName;
    public int funcIndex;  // 函数名称在全局变量中的位置
    public int retSlot;  // 返回值占据的 slot 数
    public int paraSlot;  // 参数值占据的 slot 数
    public int localSlot;  // 局部变量占据的 slot 数
    public ArrayList<Instruction> instructions;

    public FunctionInstruction(ArrayList<Instruction> instructions, int retSlot, int paraSlot, int localSlot, int funcIndex, String funcName) {
        this.retSlot = retSlot;
        this.paraSlot = paraSlot;
        this.localSlot = localSlot;
        this.instructions = instructions;
        this.funcIndex = funcIndex;
        this.funcName = funcName;
    }
}
