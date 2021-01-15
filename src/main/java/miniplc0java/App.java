package miniplc0java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import miniplc0java.analyser.Analyser;
import miniplc0java.error.CompileError;
import miniplc0java.instruction.FunctionInstruction;
import miniplc0java.instruction.Instruction;
import miniplc0java.tokenizer.StringIter;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;

import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class App {
    public static void main(String[] args) throws CompileError {
        var argparse = buildArgparse();
        Namespace result;
        try {
            result = argparse.parseArgs(args);
        } catch (ArgumentParserException e1) {
            argparse.handleError(e1);
            return;
        }

        var inputFileName = result.getString("input");
        var outputFileName = result.getString("output");

//        var inputFileName = "/Users/huangjunqin/Desktop/test_data/input.txt";
//        var outputFileName = "/Users/huangjunqin/Desktop/test_data/output.txt";

        InputStream input;
        try {
            input = new FileInputStream(inputFileName);
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find input file.");
            e.printStackTrace();
            System.exit(2);
            return;
        }

        PrintStream output;

        try {
            output = new PrintStream(new FileOutputStream(outputFileName));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open output file.");
            e.printStackTrace();
            System.exit(2);
            return;
        }

        Scanner scanner;
        scanner = new Scanner(input);
        var iter = new StringIter(scanner);
        var tokenizer = tokenize(iter);

        // analyze
        var analyzer = new Analyser(tokenizer);
        List<Instruction> instructions;
        try {
            analyzer.analyse();
        } catch (Exception e) {
            // 遇到错误不输出，直接退出
            for(int i=0; i < analyzer.instructions.size(); i++) {
                Instruction ins = analyzer.instructions.get(i);
                System.out.println(i + " : " + ins);
            }
            //System.out.println(analyzer.instructions);
            System.err.println(e);
            System.exit(0);
            return;
        }
        var translator = new Translator(analyzer.instructions, analyzer.instructionsFunctions, analyzer.symbolTable.indexMapGlobal, analyzer.symbolTable.indexMapFunc, output);
        translator.translate();

        System.out.println("\nglobal");
        for(int i=0; i < analyzer.instructions.size(); i++) {
            Instruction ins = analyzer.instructions.get(i);
            System.out.println(i + " : " + ins);
        }
        for(FunctionInstruction ins: analyzer.instructionsFunctions) {
            System.out.println();
            System.out.println(ins.funcName + ' ' + '[' + (ins.funcIndex + analyzer.symbolTable.indexMapGlobal.size()) + ']' + ' ' + ins.localSlot + ' ' + ins.paraSlot + "->" + ins.retSlot);
            for(int i=0; i < ins.instructions.size(); i++) {
                System.out.println(i + " : " + ins.instructions.get(i));
            }
        }

    }

    private static ArgumentParser buildArgparse() {
        var builder = ArgumentParsers.newFor("plc0-java");
        var parser = builder.build();
        parser.addArgument("-o", "--output").help("Set the output file").required(true).dest("output")
                .action(Arguments.store());
        parser.addArgument("file").required(true).dest("input").action(Arguments.store()).help("Input file");
        return parser;
    }

    private static Tokenizer tokenize(StringIter iter) {
        var tokenizer = new Tokenizer(iter);
        return tokenizer;
    }
}
