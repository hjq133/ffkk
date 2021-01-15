package miniplc0java;

import miniplc0java.analyser.SymbolEntry;
import miniplc0java.analyser.SymbolTable;
import miniplc0java.instruction.FunctionInstruction;
import miniplc0java.instruction.Instruction;
import miniplc0java.tokenizer.TokenType;

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

    public static String strTo16(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    public void SystemTranslate() {
        this.output = System.out;
        output.printf("magic: %08x\n", magic);  // magic u32
        output.printf("version: %08x\n", version);  // version u32

        output.printf("globals.count: %08x\n", mapGolbal.size());  // global.count
        int i = 0;
        for(String ss: mapGolbal.keySet()) {
            SymbolEntry entry = mapGolbal.get(ss);
            int isConst = entry.isConstant ? 1 : 0;
            output.printf("global[%d].is_const： %02x\n", i, isConst);  // is_const
            if(entry.type == TokenType.String) {
                String to16 = strTo16(ss);
                output.printf("global[%d].value.count: %08x\n", i, to16.length()); // value.count
                output.printf("global[%d].value.item: ", i);
                output.println(to16); // value.item
            }else {
                output.printf("global[%d].value.count: %08x\n", i, 8); // value.count
                output.printf("global[%d].value.item: %016x\n", i, 8);
            }
            i++;
        }
        i = 0;
        for(FunctionInstruction ins: this.functionInstructions) {
            output.printf("function[%d].name: %08x\n", i, ins.funcIndex); // function.name
            output.printf("function[%d].ret_slots: %08x\n", i, ins.retSlot);  // function.ret_slots
            output.printf("function[%d].param_slots: %08x\n", i, ins.paraSlot);  // function.param_slots
            output.printf("function[%d].loc_slots: %08x\n", i, ins.localSlot);  // function.loc_slots
            output.printf("function[%d].body.count: %08x\n", i, ins.instructions.size());  // body.count
            // body.item
            for(int j=0; j < ins.instructions.size(); j++) {
                Instruction in = ins.instructions.get(j);
                output.printf("%02x\n", in.operation2num());
                if(in.x != -9595) {
                    output.printf("%016x\n", in.x);
                }
            }
        }
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }

    public void translate() {
        output.printf("%08x", magic);  // magic u32
        output.printf("%08x", version);  // version u32

        output.printf("%08x", mapGolbal.size());  // global.count
        for(String ss: mapGolbal.keySet()) {
            SymbolEntry entry = mapGolbal.get(ss);
            int isConst = entry.isConstant ? 1 : 0;
            output.printf("%02x", isConst);  // is_const
            if(entry.type == TokenType.String) {
                String to16 = strTo16(ss);
                output.printf("%08x", to16.length());
                output.print(to16);
            }else {
                output.printf("%08x", 8);  // value.count，全局变量都用0占位，都是8字节
                output.printf("%016x", 0);  //  value.item，值为0，占位？
            }
        }

        for(FunctionInstruction ins: this.functionInstructions) {
            output.printf("%08x", ins.funcIndex); // function.name
            output.printf("%08x", ins.retSlot);  // function.ret_slots
            output.printf("%08x", ins.paraSlot);  // function.param_slots
            output.printf("%08x", ins.localSlot);  // function.loc_slots
            output.printf("%08x", ins.instructions.size());  // body.count
            // body.item
            for(int i=0; i < ins.instructions.size(); i++) {
                Instruction in = ins.instructions.get(i);
                output.printf("%02x", in.operation2num());
                if(in.x != -9595) {
                    output.printf("%016x", in.x);
                }
            }
        }
        SystemTranslate();
    }
}
