package miniplc0java;

import miniplc0java.analyser.SymbolEntry;
import miniplc0java.analyser.SymbolTable;
import miniplc0java.instruction.FunctionInstruction;
import miniplc0java.instruction.Instruction;
import miniplc0java.tokenizer.TokenType;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

public class Translator {
    ArrayList<Instruction> instructions;
    ArrayList<FunctionInstruction> functionInstructions;
    HashMap<String, SymbolEntry> mapGolbal;
    PrintStream output;
    static int magic = 0x72303b3e;
    static int version = 0x00000001;

    // 大端存储，高位在前，低位在后
    public static byte[] hex2Bytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }

        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] bytes = new byte[length];
        String hexDigits = "0123456789abcdef";
        for (int i = 0; i < length; i++) {
            int pos = i * 2; // 两个字符对应一个byte
            int h = hexDigits.indexOf(hexChars[pos]) << 4; // 注1
            int l = hexDigits.indexOf(hexChars[pos + 1]); // 注2
            if(h == -1 || l == -1) { // 非16进制字符
                return null;
            }
            bytes[i] = (byte) (h | l);
        }
        return bytes;
    }

    public static byte[] intToByteArray64(int i) {
        byte[] result = new byte[8];
        result[0] = (byte)(0);
        result[1] = (byte)(0);
        result[2] = (byte)(0);
        result[3] = (byte)(0);
        result[4] = (byte)((i >> 24) & 0xFF);
        result[5] = (byte)((i >> 16) & 0xFF);
        result[6] = (byte)((i >> 8) & 0xFF);
        result[7] = (byte)(i & 0xFF);
        return result;
    }

    public static byte[] intToByteArray32(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    public static byte[] intToByteArray8(int i) {
        byte[] re = new byte[1];
        re[0] = (byte)(i & 0xFF);
        return re;
    }

    public Translator(ArrayList<Instruction> ins, ArrayList<FunctionInstruction> functionInstructions, HashMap<String, SymbolEntry> mapGlobal, PrintStream output) {
        this.instructions = ins;
        this.functionInstructions = functionInstructions;
        this.mapGolbal = mapGlobal;
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

        output.printf("globals.count: %08x\n", mapGolbal.size() + functionInstructions.size());  // global.count
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

        // global function name
        for(FunctionInstruction ins: this.functionInstructions) {
            output.printf("global[%d].is_const： %02x\n", i, 1);
            output.printf("global[%d].value.count: %08x\n", i, ins.funcName.length());
            output.printf("global[%d].value.item: " + ins.funcName + "\n", i);
            i++;
        }

        i = 0;
        output.printf("functions.count: %08x\n", this.functionInstructions.size()); // function.count
        for(FunctionInstruction ins: this.functionInstructions) {
            output.printf("function[%d].name: %08x\n", i, ins.funcIndex + this.mapGolbal.size()); // function.name
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
            i++;
        }
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }

    public void translate() throws IOException{
        output.write(intToByteArray32(magic));  // magic u32
        output.write(intToByteArray32(version)); // version u32

        output.write(intToByteArray32(mapGolbal.size() + functionInstructions.size()));  // global.count u32
        for(String ss: mapGolbal.keySet()) {
            SymbolEntry entry = mapGolbal.get(ss);
            int isConst = entry.isConstant ? 1 : 0;
            output.write(intToByteArray8(isConst));  // is_const u8
            if(entry.type == TokenType.String) {
                String to16 = strTo16(ss);
                output.write(intToByteArray32(to16.length()));  // count u32
                output.write(hex2Bytes(to16));  // hex16 2 byte
            }else {
                output.write(intToByteArray32(8)); // value.count，全局变量都用0占位，都是8字节
                output.write(intToByteArray64(0)); //  value.item，u64 值为0，占位？
            }
        }

        // global function
        for(FunctionInstruction ins: this.functionInstructions) {
            output.write(intToByteArray8(1));  // is_const u8
            output.write(intToByteArray32(ins.funcName.length())); // count u32
            output.write(hex2Bytes(strTo16(ins.funcName)));
        }

        // functions
        output.write(intToByteArray32(this.functionInstructions.size())); // function.count u32
        for(FunctionInstruction ins: this.functionInstructions) {
            output.write(intToByteArray32(ins.funcIndex + this.mapGolbal.size())); // function.name u32
            output.write(intToByteArray32(ins.retSlot));  // function.ret_slots u32
            output.write(intToByteArray32(ins.paraSlot));  // function.param_slots u32
            output.write(intToByteArray32(ins.localSlot));  // function.loc_slots u32
            output.write(intToByteArray32(ins.instructions.size()));  // body.count u32
            // body.item
            for(int i=0; i < ins.instructions.size(); i++) {
                Instruction in = ins.instructions.get(i);
                output.write(intToByteArray8(in.operation2num()));
                if(in.x != -9595) {
                    output.write(intToByteArray32(in.x));
                }
            }
        }
        SystemTranslate();
    }
}
