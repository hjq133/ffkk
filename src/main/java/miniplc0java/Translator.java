package miniplc0java;

import miniplc0java.analyser.SymbolEntry;
import miniplc0java.analyser.SymbolTable;
import miniplc0java.instruction.FunctionInstruction;
import miniplc0java.instruction.Instruction;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Translator {
    ArrayList<Instruction> instructions;
    ArrayList<FunctionInstruction> functionInstructions;
    HashMap<String, SymbolEntry> mapGolbal;
    HashMap<String, SymbolEntry> mapFunc;
    PrintStream output;
    static int magic = 0x72303b3e;
    static int version = 0x00000001;

    public Translator(ArrayList<Instruction> ins, ArrayList<FunctionInstruction> functionInstructions, HashMap<String, SymbolEntry> mapGlobal, HashMap<String, SymbolEntry> mapFunc, PrintStream output) {
        this.instructions = ins;
        this.functionInstructions = functionInstructions;
        this.mapGolbal = mapGlobal;
        this.mapFunc = mapFunc;
        this.output = output;
    }

    public void translate() {
        output.printf("%08x\n", magic);  // magic u32
        output.printf("%08x\n", version);  // version u32

        output.printf("%08x\n", mapGolbal.size());  // global.count
        for(String ss: mapGolbal.keySet()) {
            SymbolEntry entry = mapGolbal.get(ss);
            int isConst = entry.isConstant ? 1 : 0;
            output.printf("%02x\n", isConst);  // is_const
            output.printf("%08x\n", 8);  // value.count，全局变量都用0占位，都是8字节
            output.printf("%016x\n", 0);  //  value.item，值为0，占位？
        }

        for(FunctionInstruction ins: this.functionInstructions) {
            output.printf("%08x\n", ins.funcIndex); // function.name
            output.printf("%08x\n", ins.retSlot);  // function.ret_slots
            output.printf("%08x\n", ins.paraSlot);  // function.param_slots
            output.printf("%08x\n", ins.localSlot);  // function.loc_slots
            output.printf("%08x\n", ins.instructions.size());  // body.count
            // body.item
            for(int i=0; i < ins.instructions.size(); i++) {
                System.out.println(i + " : " + ins.instructions.get(i));
            }
        }
    }
}
