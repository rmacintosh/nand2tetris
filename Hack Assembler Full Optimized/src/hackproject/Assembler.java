package hackproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ryan MacIntosh
 */
public class Assembler {
    // Private class fields
    private hackParser       hack; 
    private hackSymbolTable  symbolTbl;    
    private int              currPCaddr;
    private int              ramOffset;
    private File             asmFile;
    
    // Private static fields
    private static final int ramStart = 16;

    public Assembler(File f) {
        asmFile = f;
        hack = new hackParser(asmFile);                                             // Create and send file to parser
        symbolTbl = new hackSymbolTable();                                          // Add predefined symbols to the table
        currPCaddr = 0;
        ramOffset = 0;
    }
    
    public void buildSymbolTable() {
        String temp;
        Character c;
        while(hack.hasMoreCommands()) {
            switch(hack.commandType()) {
                case hackParser.C_COMMAND:
                    currPCaddr++;
                    break;
                case hackParser.A_COMMAND:
                    currPCaddr++;
                    temp = hack.symbol();
                    c = temp.charAt(0);
                    if(!Character.isDigit(c) && temp.toLowerCase().equals(temp)) {  // catch only variable symbols which are only lowercase, labels are uppercase.
                        if(!symbolTbl.contains(temp)) {                             // This effectively ignores A instructions using a label
                            symbolTbl.addEntry(temp, ramStart + ramOffset);         // Only store a variable once
                            ramOffset++;
                        }
                    }
                    break;
                case hackParser.L_COMMAND:
                    temp = hack.symbol();
                    symbolTbl.addEntry(temp, currPCaddr);                           // Don't check that symbol already exists because a symbol can be used with an
                    break;                                                          // A instruction before the label is even defined. This will cause the symbol 
                default:                                                            // to be stored in the table with the proper PC address or overwrite the 
                    break;                                                          // existing one since the symbol/key will be identical
            }
            hack.advance();
        }
        hack.resetParser();
    }    

    public String getFileContents() {
        String         rawLine = "";
        BufferedReader fileBuf = null;
        long           fileLength;
        StringBuilder  fileContents = null;
        try {
            if(asmFile.exists()) {
                fileBuf = new BufferedReader(new FileReader(asmFile));
                fileLength = asmFile.length();
                fileContents = new StringBuilder((int)fileLength);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Assembler.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        while(rawLine != null) {
            try {
                rawLine = fileBuf.readLine();                                       // get the next line in the file
            } catch (IOException ex) {
                Logger.getLogger(Assembler.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(rawLine != null && !rawLine.isEmpty()) {                                                   // more lines left eg. more commands
                fileContents.append(rawLine);
            }
            fileContents.append("\n");
        }
        try {                                                                       // BufferedReader stream should be closed when done
            fileBuf.close();                          
        } catch (IOException ex) {
            Logger.getLogger(Assembler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileContents.toString();
    }

    public String getHackCode() {
        long           fileLength = asmFile.length();
        StringBuilder  hackCodeStr = new StringBuilder((int)fileLength);
        String         temp;
        Character      c;
        while(hack.hasMoreCommands()) {
            switch(hack.commandType()){
                case hackParser.A_COMMAND:
                    temp = hack.symbol();
                    c = temp.charAt(0);
                    if(Character.isDigit(c)) {
                        hackCodeStr.append("0");
                        hackCodeStr.append(hack.dec2Binary(temp));
                        hackCodeStr.append("\r\n");
                        break;
                    } else {
                        hackCodeStr.append("0");
                        hackCodeStr.append(hack.dec2Binary(symbolTbl.getAddress(temp)));
                        hackCodeStr.append("\r\n");
                    }
                    break;
                case hackParser.C_COMMAND:                                          // test dest jump since they can
                    hackCodeStr.append(hackCode.comp(hack.comp()));                 // be omitted from an instruction
                    if(!hack.dest().equals("null")) {                       
                        hackCodeStr.append(hackCode.dest(hack.dest()));
                    } else {
                        hackCodeStr.append(hackCode.omit);
                    }
                    if(!hack.jump().equals("null")) {
                        hackCodeStr.append(hackCode.jump(hack.jump()));
                    } else {
                        hackCodeStr.append(hackCode.omit);
                    }
                    hackCodeStr.append("\r\n");
                    break;
                case hackParser.L_COMMAND:
                default:
                    break;
            }
            hack.advance();
        }
        return hackCodeStr.toString();
    }
    
    public void unload() {
        hack.close();
        
    }
    
    public String getFileName() {
        return parseLine(asmFile.getName(), "\\.", 0);
    }
    
    private String parseLine(String line, String delim, int index) {
        String[] rawLineParsed = line.split(delim);
        return rawLineParsed[index];
    }
}
    