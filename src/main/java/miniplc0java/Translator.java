package miniplc0java;

import miniplc0java.analyser.SymbolEntry;
import miniplc0java.analyser.SymbolTable;
import miniplc0java.instruction.Instruction;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Translator {
    ArrayList<Instruction> instructions;
    HashMap<String, SymbolEntry> mapGolbal;
    HashMap<String, SymbolEntry> mapFunc;
    PrintStream output;
    static int magic = 0x72303b3e;
    static int version = 0x00000001;

    public Translator(ArrayList<Instruction> ins, HashMap<String, SymbolEntry> mapGlobal, HashMap<String, SymbolEntry> mapFunc, PrintStream output) {
        this.instructions = ins;
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
//        for(Instruction ins: this.instructions) {
//            // TODO
//        }
    }
}
