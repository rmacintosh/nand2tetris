/*
 * VM language translator parser class.
 * Accepts a vm program file and parses it into hack assembly code 
 */
package hackproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Ryan MacIntosh
 */
public class VMParser {
    // Public class fields
    public static final int C_ARITHMETIC    =  0;                                   // eg. add, sub, neg, eq, gt, lt, and, or, not
    public static final int C_PUSH          =  1;                                   // push a value onto the stack
    public static final int C_POP           =  2;                                   // pop a value off of the stack
    public static final int C_LABEL         =  3;                                   // eg. loop, end...etc.
    public static final int C_GOTO          =  4;                                   // eg. goto loop...goto end...etc.
    public static final int C_IF            =  5;
    public static final int C_FUNCTION      =  6;                                   // eg. function mult 2
    public static final int C_RETURN        =  7;                                   // to return from a function call
    public static final int C_CALL          =  8;                                   // to call a functon
    public static final int UNKNOWN_COMMAND = -1;                                   // unknown command or invalid command
    // Private class fields
    private Boolean          hasMoreCommands;                                       // true if there are more instructions to parse ie. !EOF
    private File             vmFile;
    private BufferedReader   vmFileBuf;
    private String           instruction;                                           // contains the stripped down line, vm instruction only

    public VMParser(File f) {
        initParser(f);
    }
    
    public void advance() {
        try {
            instruction = vmFileBuf.readLine();                                     // get the next line in the file
        } catch (IOException ex) {
            hasMoreCommands = false;
            Logger.getLogger(VMParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(instruction == null) {                                                   // no more lines left eg. no more commands
            hasMoreCommands = false;
        } else {
            if(instruction.isEmpty()) {                                             // ignore empty line, call advance again
                advance();
            } else {
                instruction = parseLine(instruction, "//", 0);                      // cut off comments and remove white space at beginning and end of line
                if(instruction.isEmpty()) {                                         // ignore empty line, call advance again 
                    advance();
                }    
            }                                                 
        }
    }
    
    public int commandType() {
        String[] tokenStr = instruction.split("\\s+");                              // Split instruction into tokens separating on space or multiple spaces between characters
        if(tokenStr.length == 1) {
            return VMParser.C_ARITHMETIC;                                           // though, could be return.
        } else if(tokenStr.length == 2) {                                           // acutally ... label, goto, if
            return VMParser.UNKNOWN_COMMAND;
        } else if(tokenStr.length == 3) {                                           // push, pop, function, call.
            if("push".equals(tokenStr[0])) {
                return VMParser.C_PUSH;
            } else if("pop".equals(tokenStr[0])) {
                return VMParser.C_POP;
            } else {
                return VMParser.UNKNOWN_COMMAND;                                    // for now... actually.. function, call
            }
        } else {
            return VMParser.UNKNOWN_COMMAND;
        }
    }
    
    public String arg1() {
        String[] tokenStr = instruction.split("\\s");
        int retIndex = -1;
        switch(this.commandType()) {
            case VMParser.C_ARITHMETIC:
                retIndex = 0;
                break;
            case VMParser.C_PUSH:
            case VMParser.C_POP:
                retIndex = 1;
                break;
            default:
                break; 
        }
        return tokenStr[retIndex];
    }
    
    public int arg2() {                                                             // push, pop, function, call
        String[] tokenStr = instruction.split("\\s");
        return Integer.parseInt(tokenStr[2]);
    }
    
    public void close() {                                                           // BufferedReader field should be closed
        try {                                                                       // when done with parser
            vmFileBuf.close();                          
        } catch (IOException ex) {
            Logger.getLogger(VMParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    public boolean hasMoreCommands() {
        return hasMoreCommands;
    }    

    public void resetParser() {
        try {
            vmFileBuf.close();
        } catch (IOException ex) {
            Logger.getLogger(VMParser.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Unable to reset VMParser\nClick OK to continue", "Error", JOptionPane.ERROR_MESSAGE);
        }
        initParser(vmFile);
    }


    public String dec2Binary(String dec) {                                          // Takes a string that represents a decimal number and converts
        int decInt, trailingZeros;                                                  // it to a 15 bit binary 'address' string including leading zeros
        String temp;
        String temp2 = "";
        decInt = Integer.parseInt(dec);
        temp = Integer.toBinaryString(decInt);
        if(temp.length() < 15) {
            trailingZeros = 15-temp.length();
            for(int i=0; i < trailingZeros; i++) {
                temp2 = temp2.concat("0");
            }
            temp2 = temp2.concat(temp);
        } else {
            return temp;  
        }
        return temp2;
    }
    
    public String dec2Binary(int dec) {
        return dec2Binary(Integer.toString(dec));
    }

    private void initParser(File f) {
        if(f.isFile()) {
            hasMoreCommands = true;
            vmFile = f;
        } else {
            hasMoreCommands = false;
        }
        try {
            if(f.exists()) {
                vmFileBuf = new BufferedReader(new FileReader(f));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VMParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        advance();
    }
    
    private String parseLine(String line, String delim, int index) {
        String[] rawLineParsed = line.trim().split(delim);
        return rawLineParsed[index];
    }
}
