package hackproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ryan MacIntosh
 */
public class Translator {
    // Private class fields
    private VMParser hack; 
    private File     vmFile;
    private String   rand1;
    private String   rand2;
    
    // Private static fields
    private static final String pushASMstr = "D=A\r\n@0\r\nA=M\r\nM=D\r\n@0\r\nM=M+1\r\n";
    private static final String addASMstr  = "@0\r\nAM=M-1\r\nD=M\r\n@0\r\nA=M-1\r\nM=M+D\r\n";
    private static final String subASMstr  = "@0\r\nAM=M-1\r\nD=M\r\n@0\r\nA=M-1\r\nM=M-D\r\n";
    private static final String negASMstr  = "@0\r\nA=M-1\r\nM=-M\r\n";
    private static final String notASMstr  = "@0\r\nA=M-1\r\nM=!M\r\n";
    private static final String andASMstr  = "@0\r\nAM=M-1\r\nD=M\r\n@0\r\nA=M-1\r\nM=D&M\r\n";
    private static final String orASMstr   = "@0\r\nAM=M-1\r\nD=M\r\n@0\r\nA=M-1\r\nM=D|M\r\n";
    private static final String preStr     = "@0\r\nAM=M-1\r\nD=M\r\nA=A-1\r\nD=M-D\r\n@";
    private static final String midLstr    = "\r\n0;JMP\r\n(";
    private static final String midRstr    = ")\r\n@0\r\nA=M-1\r\nM=-1\r\n(";
    private static final String postStr    = ")\r\n";
    private static final String gtASMstr   = "\r\nD;JGT\r\n@0\r\nA=M-1\r\nM=0\r\n@";
    private static final String ltASMstr   = "\r\nD;JLT\r\n@0\r\nA=M-1\r\nM=0\r\n@";
    private static final String eqASMstr   = "\r\nD;JEQ\r\n@0\r\nA=M-1\r\nM=0\r\n@";
    // prestr rand xxASMstr rand2 midLstr rand midRstr rand2 poststr
    private static final String randList   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    public Translator(File f) {
        vmFile = f;
        hack = new VMParser(vmFile);                                                // Create and send file to parser
    }
    
    public String getFileContents() {
        String         rawLine = "";
        BufferedReader fileBuf = null;
        long           fileLength;
        StringBuilder  fileContents = null;
        try {
            if(vmFile.exists()) {
                fileBuf = new BufferedReader(new FileReader(vmFile));
                fileLength = vmFile.length();
                fileContents = new StringBuilder((int)fileLength);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        while(rawLine != null) {
            try {
                rawLine = fileBuf.readLine();                                       // get the next line in the file
            } catch (IOException ex) {
                Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(rawLine != null && !rawLine.isEmpty()) {                                                   // more lines left eg. more commands
                fileContents.append(rawLine);
            }
            fileContents.append("\r\n");
        }
        try {                                                                       // BufferedReader stream should be closed when done
            fileBuf.close();                          
        } catch (IOException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileContents.toString();
    }

    public String getASMCode() {
        long fileLength = vmFile.length();
        StringBuilder  asmCodeStr = new StringBuilder((int)fileLength);
        while(hack.hasMoreCommands()) {
            switch(hack.commandType()){
                case VMParser.C_ARITHMETIC:
                    asmCodeStr.append(arithmeticCode(hack.arg1()));
                    break;
                case VMParser.C_PUSH:
                    asmCodeStr.append("@").append(hack.arg2()).append("\r\n");
                    asmCodeStr.append(pushASMstr);
                    break;
                case VMParser.C_POP:
                    asmCodeStr.append("Pop command");
                    asmCodeStr.append(" arg 1 is: ").append(hack.arg1());
                    asmCodeStr.append(" arg 2 is: ").append(hack.arg2()).append("\r\n");
                    break;
                case VMParser.UNKNOWN_COMMAND:
                default:
                    asmCodeStr.append("              Oops!\r\n");
                    break;
            }
            hack.advance();
        }
        return asmCodeStr.toString();
    }
    
    public void unload() {
        hack.close();
        
    }
    
    public String getFileName() {
        return parseLine(vmFile.getName(), "\\.", 0);
    }
    
    private String parseLine(String line, String delim, int index) {
        String[] rawLineParsed = line.split(delim);
        return rawLineParsed[index];
    }
    
    private String arithmeticCode(String arithmeticCmd) {
        if("add".equals(arithmeticCmd)) {
            return addASMstr;
        } else if("sub".equals(arithmeticCmd)) {
            return subASMstr;
        } else if("neg".equals(arithmeticCmd)) {
            return negASMstr;
        } else if("not".equals(arithmeticCmd)) {
            return notASMstr;
        } else if("and".equals(arithmeticCmd)) {
            return andASMstr;
        } else if("or".equals(arithmeticCmd)) {
            return orASMstr;
        } else if("gt".equals(arithmeticCmd)) {
            rand1 = "GT" + generateString(new Random() , randList, 10);
            rand2 = "GT" + generateString(new Random() , randList, 10);
            return preStr + rand1 + gtASMstr + rand2 + midLstr + rand1 + midRstr + rand2 + postStr;
        } else if("lt".equals(arithmeticCmd)) {
            rand1 = "LT" + generateString(new Random() , randList, 10);
            rand2 = "LT" + generateString(new Random() , randList, 10);
            return preStr + rand1 + ltASMstr + rand2 + midLstr + rand1 + midRstr + rand2 + postStr;
        } else if("eq".equals(arithmeticCmd)) {
            rand1 = "EQ" + generateString(new Random() , randList, 10);
            rand2 = "EQ" + generateString(new Random() , randList, 10);
            return preStr + rand1 + eqASMstr + rand2 + midLstr + rand1 + midRstr + rand2 + postStr;
        } else {
            return "            INVALID Arithmetic Code!";
        }
    }
    
    private static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }
}
    