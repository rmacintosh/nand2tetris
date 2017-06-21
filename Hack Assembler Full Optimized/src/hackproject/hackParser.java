/*
 * Hack assembly language file parser class.
 * Accepts a hack assembly file and parses it into hack macine code 
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
public class hackParser {
    // Public class fields
    public static final int A_COMMAND       =  0;                                   // When instruction starts with @xxx
    public static final int C_COMMAND       =  1;                                   // when instruction is of form dest=comp;jump
    public static final int L_COMMAND       =  2;                                   // when instruction is of form @XXX or (XXX)
    public static final int UNKNOWN_COMMAND = -1;                                   // unknown command or invalid command
    // Private class fields
    private Boolean          hasMoreCommands;                                        // true if there are more instructions to parse ie. !EOF
    private File             asmFile;
    private BufferedReader   asmFileBuf;
    private String           instruction;                                            // contains the stripped down line, hack instruction only

    public hackParser(File f) {
        initParser(f);
    }
    
    public void advance() {
        String rawLine = "";
        try {
            rawLine = asmFileBuf.readLine();                                       // get the next line in the file
        } catch (IOException ex) {
            hasMoreCommands = false;
            Logger.getLogger(hackParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(rawLine == null) {                                                      // no more lines left eg. no more commands
            hasMoreCommands = false;
        } else {
            if(rawLine.isEmpty()) {                                                 // ignore empty line, call advance again
                advance();
            } else {
                instruction = parseLine(rawLine, "//", 0);                          // cut off comments and remove white space at beginning and end of line
                if(instruction.isEmpty()) {                                         // ignore empty line, call advance again 
                    advance();
                }    
            }                                                 
        }
    }
    
    public int commandType() {
        if(instruction.startsWith("@")) {
            return A_COMMAND;
        } else if(instruction.startsWith("(") && instruction.endsWith(")")) {
            return L_COMMAND;
        } else {
            int cInstIndicator1 = instruction.indexOf(";");                         // a c-instruction has form dest=comp;jump
            int cInstIndicator2 = instruction.indexOf("=");                         // it will always contain ';' or '=' or both
            boolean cInstBool1, cInstBool2;
            if(cInstIndicator1 == -1) {
                cInstBool1 = false;
            } else {
                cInstBool1 = true;
            }
            if(cInstIndicator2 == -1) {
                cInstBool2 = false;
            } else {
                cInstBool2 = true;
            }
            
            if(cInstBool1 || cInstBool2) {
                return C_COMMAND;
            } else {
                return UNKNOWN_COMMAND;                                             // c-instruction has improper form
            }
        }
    }
    
    public String symbol() {
        String temp;
        Character c;
        if(commandType() == A_COMMAND) {
            temp = parseLine(instruction, "@", 1);
        } else { // L_COMMAND
            temp = parseLine(instruction, "\\)", 0);
            temp = parseLine(temp, "\\(", 1);
        }
        return temp;
    }
    
    public String dest() {
        if(instruction.indexOf("=") != -1) {
            return parseLine(instruction, "=", 0);
        } else {
            return "null";
        }
    }
    
    public String comp() {
        String temp = null;
        if(instruction.indexOf(";") != -1) {
            temp = parseLine(instruction, ";", 0);
        }
        if(instruction.indexOf("=") != -1) {
            temp = parseLine(instruction, "=", 1);
        }
        return temp;
    }
    
    public String jump() {
        if(instruction.indexOf(";") != -1) {
            return parseLine(instruction, ";", 1);
        } else {
            return "null";
        }
    }
    
    public void close() {                                                           // BufferedReader field should be closed
        try {                                                                       // when done with parser
            asmFileBuf.close();                          
        } catch (IOException ex) {
            Logger.getLogger(hackParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    public boolean hasMoreCommands() {
        return hasMoreCommands;
    }    

    public void resetParser() {
        try {
            asmFileBuf.close();
        } catch (IOException ex) {
            Logger.getLogger(hackParser.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Unable to reset hackParser\nClick OK to continue", "Error", JOptionPane.ERROR_MESSAGE);
        }
        initParser(asmFile);
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
            asmFile = f;
        } else {
            hasMoreCommands = false;
        }
        try {
            if(f.exists()) {
                asmFileBuf = new BufferedReader(new FileReader(f));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(hackParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        advance();
    }
    
    private String parseLine(String line, String delim, int index) {
        String[] rawLineParsed = line.split(delim);
        return rawLineParsed[index].trim();
    }
}
