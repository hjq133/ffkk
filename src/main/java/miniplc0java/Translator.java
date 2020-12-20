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
    PrintStream output;

    public Translator(ArrayList<Instruction> ins, HashMap<String, SymbolEntry> mapGlobal, PrintStream output) {
        this.instructions = ins;
        this.mapGolbal = mapGlobal;
        this.output = output;
    }

    public void translate() {
        for(Instruction ins: this.instructions) {
            // TODO
        }
    }
}
