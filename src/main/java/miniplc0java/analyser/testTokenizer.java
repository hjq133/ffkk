package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.StringIter;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Pos;
import org.checkerframework.checker.units.qual.C;

import java.io.*;

import java.util.*;

public class testTokenizer {
    public static void main(String[] args) throws Exception {
        InputStream input;
        input = new FileInputStream("/Users/huangjunqin/Desktop/hh.txt");

        Scanner scanner;
        scanner = new Scanner(input);
        var iter = new StringIter(scanner);
        var tokenizer = new Tokenizer(iter);

        while(true) {
            Token tk = tokenizer.nextToken();
            if(tk.getTokenType() == TokenType.EOF) break;
        }

        System.out.println("finish");
    }
}
