package hackproject;

import java.util.Random;

/**
 *
 * @author Ryan MacIntosh
 */
public class VMCodeWriter {
    // Private static fields
    private static final String pushASMstr = 
            "D=A\r\n@SP\r\nA=M\r\nM=D\r\n@SP\r\nM=M+1\r\n";      
    private static final String addASMstr = 
            "@SP\r\nAM=M-1\r\nD=M\r\n@SP\r\nA=M-1\r\nM=M+D\r\n"; 
    private static final String subASMstr = 
            "@SP\r\nAM=M-1\r\nD=M\r\n@SP\r\nA=M-1\r\nM=M-D\r\n"; 
    private static final String negASMstr = 
            "@SP\r\nA=M-1\r\nM=-M\r\n";                          
    private static final String notASMstr = 
            "@SP\r\nA=M-1\r\nM=!M\r\n";                          
    private static final String andASMstr = 
            "@SP\r\nAM=M-1\r\nD=M\r\n@SP\r\nA=M-1\r\nM=D&M\r\n";
    private static final String orASMstr = 
            "@SP\r\nAM=M-1\r\nD=M\r\n@SP\r\nA=M-1\r\nM=D|M\r\n";
    private static final String preStr = 
            "@SP\r\nAM=M-1\r\nD=M\r\nA=A-1\r\nD=M-D\r\n@";
    private static final String midLstr = 
            "\r\n0;JMP\r\n(";
    private static final String midRstr = 
            ")\r\n@SP\r\nA=M-1\r\nM=-1\r\n(";
    private static final String postStr = 
            ")\r\n";
    private static final String gtASMstr = 
            "\r\nD;JGT\r\n@SP\r\nA=M-1\r\nM=0\r\n@";              
    private static final String ltASMstr = 
            "\r\nD;JLT\r\n@SP\r\nA=M-1\r\nM=0\r\n@";
    private static final String eqASMstr = 
            "\r\nD;JEQ\r\n@SP\r\nA=M-1\r\nM=0\r\n@";
    private static final String randList = 
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String postPopStr = 
            "\r\nD=D+A\r\n@R13\r\nM=D\r\nM=D\r\n@SP\r\nAM=M-1\r\nD=M\r\n@R13\r\nA=M\r\nM=D\r\n";
    private static final String postPushStr = 
            "\r\nA=D+A\r\nD=M\r\n@SP\r\nA=M\r\nM=D\r\nD=A+1\r\n@SP\r\nM=D\r\n";                 
    
    private VMCodeWriter() {}

    public static void pushPopCode(StringBuilder asmCodeStr, VMParser hack) {
        String segment = hack.arg1();
        int offset = hack.arg2();
        if("constant".equals(segment)) {                                            // constant is only used with push
            asmCodeStr.append("@").append(hack.arg2()).append("\r\n");              // add 7 instructions
            asmCodeStr.append(pushASMstr);
        } else if("local".equals(segment)) {
            switch(hack.commandType()) {
                case VMParser.C_POP:                                                // add 13 instructions
                    asmCodeStr.append("@LCL\r\nD=M\r\n@");
                    asmCodeStr.append(offset);
                    asmCodeStr.append(postPopStr);
                    break;
                case VMParser.C_PUSH:                                               // add 11 instructions
                    asmCodeStr.append("@LCL\r\nD=M\r\n@");
                    asmCodeStr.append(offset);
                    asmCodeStr.append(postPushStr);
                    break;
                default:
                    asmCodeStr.append("INVALID commandType in pushPopCode()!\r\n");
                    break;
            }
        } else if("argument".equals(segment)) {
            switch(hack.commandType()) {
                case VMParser.C_POP:                                                // add 13 instructions
                    asmCodeStr.append("@ARG\r\nD=M\r\n@");
                    asmCodeStr.append(offset);
                    asmCodeStr.append(postPopStr);
                    break;
                case VMParser.C_PUSH:                                               // add 11 instructions
                    asmCodeStr.append("@ARG\r\nD=M\r\n@");
                    asmCodeStr.append(offset);
                    asmCodeStr.append(postPushStr);
                    break;
                default:
                    asmCodeStr.append("INVALID commandType in pushPopCode()!\r\n");
                    break;
            }
        } else if("this".equals(segment)) {
            switch(hack.commandType()) {
                case VMParser.C_POP:                                                // add 13 instructions
                    asmCodeStr.append("@THIS\r\nD=M\r\n@");
                    asmCodeStr.append(offset);
                    asmCodeStr.append(postPopStr);
                    break;
                case VMParser.C_PUSH:                                               // add 11 instructions
                    asmCodeStr.append("@THIS\r\nD=M\r\n@"); 
                    asmCodeStr.append(offset);
                    asmCodeStr.append(postPushStr);
                    break;
                default:
                    asmCodeStr.append("INVALID commandType in pushPopCode()!\r\n");
                    break;
            }
        } else if("that".equals(segment)) {
            switch(hack.commandType()) {
                case VMParser.C_POP:                                                // add 13 instructions
                    asmCodeStr.append("@THAT\r\nD=M\r\n@");
                    asmCodeStr.append(offset);
                    asmCodeStr.append(postPopStr);
                    break;
                case VMParser.C_PUSH:                                               // add 11 instructions
                    asmCodeStr.append("@THAT\r\nD=M\r\n@");
                    asmCodeStr.append(offset);
                    asmCodeStr.append(postPushStr);
                    break;
                default:
                    asmCodeStr.append("INVALID commandType in pushPopCode()!\r\n");
                    break;
            }
        } else if("temp".equals(segment)) {
            if(offset > 7) {
                asmCodeStr.append("'temp' offset out of bounds, using 'temp 7'\r\n");
                offset = 7;
            }
            switch(hack.commandType()) {
                case VMParser.C_POP:                                                // add 13 instructions
                    asmCodeStr.append("@R5\r\nD=A\r\n@");
                    asmCodeStr.append(offset);
                    asmCodeStr.append(postPopStr);
                    break;
                case VMParser.C_PUSH:                                               // add 11 instructions
                    asmCodeStr.append("@R5\r\nD=A\r\n@");
                    asmCodeStr.append(offset);
                    asmCodeStr.append(postPushStr);
                    break;
                default:
                    asmCodeStr.append("INVALID commandType in pushPopCode()!\r\n");
                    break;
            }
        } else if("pointer".equals(segment)) {
            if(offset > 1) {
                asmCodeStr.append("'pointer' offset out of bounds, using 'pointer 1'\r\n");
                offset = 1;
            }
            switch(hack.commandType()) {
                case VMParser.C_POP:
                    if(offset == 0) {
                        asmCodeStr.append("@SP\r\nAM=M-1\r\nD=M\r\n@THIS\r\nM=D\r\n");  // add 5 instructions
                    } else { // offset == 1
                        asmCodeStr.append("@SP\r\nAM=M-1\r\nD=M\r\n@THAT\r\nM=D\r\n");  // add 5 instructions
                    }
                    break;
                case VMParser.C_PUSH:
                    if(offset == 0) {
                        asmCodeStr.append("@THIS\r\nD=M\r\n@SP\r\nA=M\r\nM=D\r\n@SP\r\nM=M+1\r\n"); // add 7 instructions
                    } else { // offset == 1
                        asmCodeStr.append("@THAT\r\nD=M\r\n@SP\r\nA=M\r\nM=D\r\n@SP\r\nM=M+1\r\n"); // add 7 instructions
                    }
                    break;
                default:
                    asmCodeStr.append("INVALID commandType in pushPopCode()!\r\n");
                    break;
            }
        }  else if("static".equals(segment)) {
            switch(hack.commandType()) {
                case VMParser.C_POP:                                                // add 5 instructions
                    asmCodeStr.append("@SP\r\nAM=M-1\r\nD=M\r\n");
                    asmCodeStr.append("@").append(hack.getFileName().toLowerCase()).append(".").append(offset);
                    asmCodeStr.append("\r\nM=D\r\n");
                    break;
                case VMParser.C_PUSH:                                               // add 7 instructions
                    asmCodeStr.append("@").append(hack.getFileName().toLowerCase()).append(".").append(offset);
                    asmCodeStr.append("\r\nD=M\r\n@SP\r\nA=M\r\nM=D\r\n@SP\r\nM=M+1\r\n");
                    break;
                default:
                    asmCodeStr.append("INVALID commandType in pushPopCode()!\r\n");
                    break;
            }
        } else {
            asmCodeStr.append("INVALID Arg1 in pushPopCode()!\r\n");
        }
    }
    
    public static void arithmeticCode(StringBuilder asmCodeStr, VMParser hack) {
        String rand1, rand2;
        if("add".equals(hack.arg1())) {                                             // add 6 instructions
            asmCodeStr.append(addASMstr);
        } else if("sub".equals(hack.arg1())) {                                      // add 6 instructions
            asmCodeStr.append(subASMstr);
        } else if("neg".equals(hack.arg1())) {                                      // add 3 instructions
            asmCodeStr.append(negASMstr);
        } else if("not".equals(hack.arg1())) {                                      // add 3 instructions
            asmCodeStr.append(notASMstr);
        } else if("and".equals(hack.arg1())) {                                      // add 6 instructions
            asmCodeStr.append(andASMstr);
        } else if("or".equals(hack.arg1())) {                                       // add 6 instructions
            asmCodeStr.append(orASMstr);
        } else if("gt".equals(hack.arg1())) {                                       // add 15 instructions
            rand1 = "GT" + generateString(new Random() , randList, 10);
            rand2 = "GT" + generateString(new Random() , randList, 10);
            asmCodeStr.append(preStr).append(rand1).append(gtASMstr).append(rand2).append(midLstr).append(rand1).append(midRstr).append(rand2).append(postStr);
        } else if("lt".equals(hack.arg1())) {                                       // add 15 instructions
            rand1 = "LT" + generateString(new Random() , randList, 10);
            rand2 = "LT" + generateString(new Random() , randList, 10);
            asmCodeStr.append(preStr).append(rand1).append(ltASMstr).append(rand2).append(midLstr).append(rand1).append(midRstr).append(rand2).append(postStr);
        } else if("eq".equals(hack.arg1())) {                                       // add 15 instructions
            rand1 = "EQ" + generateString(new Random() , randList, 10);
            rand2 = "EQ" + generateString(new Random() , randList, 10);
            asmCodeStr.append(preStr).append(rand1).append(eqASMstr).append(rand2).append(midLstr).append(rand1).append(midRstr).append(rand2).append(postStr);
        } else {
            asmCodeStr.append("INVALID Arithmetic command in arithmeticCode()!\r\n");
        }
    }
    
    public static void initCode(StringBuilder asmCodeStr) {
        asmCodeStr.append("@256\r\nD=A\r\n@SP\r\nM=D\r\n");                         // Initiate the stack and call Sys.init
        asmCodeStr.append("@RET_ADDR_SYSINIT_ORIGIN\r\n");
        asmCodeStr.append("D=A\r\n@SP\r\nM=M+1\r\nA=M-1\r\nM=D\r\n");               // Push return address
        asmCodeStr.append("@LCL\r\nD=M\r\n@SP\r\nM=M+1\r\nA=M-1\r\nM=D\r\n");       // Push local
        asmCodeStr.append("@ARG\r\nD=M\r\n@SP\r\nM=M+1\r\nA=M-1\r\nM=D\r\n");       // Push arg
        asmCodeStr.append("@THIS\r\nD=M\r\n@SP\r\nM=M+1\r\nA=M-1\r\nM=D\r\n");      // Push this
        asmCodeStr.append("@THAT\r\nD=M\r\n@SP\r\nM=M+1\r\nA=M-1\r\nM=D\r\n");      // Push that
        asmCodeStr.append("@SP\r\nD=M\r\n@5\r\nD=D-A\r\n@ARG\r\nM=D\r\n");          // reposition arg
        asmCodeStr.append("@SP\r\nD=M\r\n@LCL\r\nM=D\r\n");                         // reposition local
        asmCodeStr.append("@Sys.init\r\n0;JMP\r\n");                                // goto Sys.init
        asmCodeStr.append("(RET_ADDR_SYSINIT_ORIGIN)\r\n");                         // return label to return from called function
    }
    
    public static void labelCode(StringBuilder asmCodeStr, String currFunction, VMParser hack) {
        asmCodeStr.append("(").append(currFunction).append("$").append(hack.arg1()).append(")\r\n");
    }
    
    public static void gotoCode(StringBuilder asmCodeStr, String currFunction, VMParser hack) {
        asmCodeStr.append("@").append(currFunction).append("$").append(hack.arg1()).append("\r\n0;JMP\r\n");
    }
    
    public static void ifCode(StringBuilder asmCodeStr, String currFunction, VMParser hack) {
        asmCodeStr.append("@SP\r\nAM=M-1\r\nD=M\r\n@").append(currFunction).append("$").append(hack.arg1()).append("\r\nD;JNE\r\n");
    }
    
    public static void returnCode(StringBuilder asmCodeStr, String currFunction, VMParser hack, String fileName, int retAddrCounter) {
        asmCodeStr.append("@LCL\r\nD=M\r\n@R13\r\nM=D\r\n@5\r\nA=D-A\r\nD=M\r\n@R14\r\nM=D\r\n@SP\r\nA=M-1\r\nD=M\r\n@ARG\r\nA=M\r\nM=D\r\nD=A+1\r\n");
        asmCodeStr.append("@SP\r\nM=D\r\n@R13\r\nAM=M-1\r\nD=M\r\n@THAT\r\nM=D\r\n@R13\r\nAM=M-1\r\nD=M\r\n@THIS\r\nM=D\r\n@R13\r\n");
        asmCodeStr.append("AM=M-1\r\nD=M\r\n@ARG\r\nM=D\r\n@R13\r\nAM=M-1\r\nD=M\r\n@LCL\r\nM=D\r\n@R14\r\nA=M\r\n0;JMP\r\n");
    }
    
    public static void callCode(StringBuilder asmCodeStr, String currFunction, VMParser hack, String fileName, int retAddrCounter) {
        asmCodeStr.append("@RET_ADDR_CALL").append(retAddrCounter).append("\r\n");
        asmCodeStr.append("D=A\r\n@SP\r\nM=M+1\r\nA=M-1\r\nM=D\r\n");               // Push return address
        asmCodeStr.append("@LCL\r\nD=M\r\n@SP\r\nM=M+1\r\nA=M-1\r\nM=D\r\n");       // Push local
        asmCodeStr.append("@ARG\r\nD=M\r\n@SP\r\nM=M+1\r\nA=M-1\r\nM=D\r\n");       // Push arg
        asmCodeStr.append("@THIS\r\nD=M\r\n@SP\r\nM=M+1\r\nA=M-1\r\nM=D\r\n");      // Push this
        asmCodeStr.append("@THAT\r\nD=M\r\n@SP\r\nM=M+1\r\nA=M-1\r\nM=D\r\n");      // Push that
        asmCodeStr.append("@SP\r\nD=M\r\n@").append(hack.arg2()).append("\r\nD=D-A\r\n@5\r\nD=D-A\r\n@ARG\r\nM=D\r\n"); // reposition arg
        asmCodeStr.append("@SP\r\nD=M\r\n@LCL\r\nM=D\r\n");                         // reposition local
        asmCodeStr.append("@").append(hack.arg1()).append("\r\n0;JMP\r\n");         // goto function
        asmCodeStr.append("(RET_ADDR_CALL").append(retAddrCounter).append(")\r\n"); // return label to return from called function
    }
    
    public static void functionCode(StringBuilder asmCodeStr, String currFunction, VMParser hack, String fileName) {
        asmCodeStr.append("(").append(hack.arg1()).append(")\r\n");                 // function label
        switch(hack.arg2()) {
            case 0:             // do nothing
                break;
            case 1:             // just push one local variable = 0
                asmCodeStr.append("@SP\r\nAM=M+1\r\nA=A-1\r\nM=0\r\n");
                break;
            default:            // loop to push x amount of variables = 0
                asmCodeStr.append("@").append(hack.arg2()).append("\r\nD=A\r\n");
                asmCodeStr.append("(LOOP_").append(fileName).append(".").append(currFunction).append(")\r\n");
                asmCodeStr.append("D=D-1\r\n@SP\r\nAM=M+1\r\nA=A-1\r\nM=0\r\n");
                asmCodeStr.append("@LOOP_").append(fileName).append(".").append(currFunction).append("\r\n");
                asmCodeStr.append("D;JGT\r\n");
                break;
        }
    }
    
    private static String parseLine(String line, String delim, int index) {
        String[] rawLineParsed = line.split(delim);
        return rawLineParsed[index];
    }
    
    private static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }
}
    