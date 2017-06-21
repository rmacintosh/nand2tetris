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
public class Translator {
    // Private class fields
    private VMParser hack; 
    private File[]   vmFiles;
    private File     vmFile;
    private int      currentFile;
    private int      totalFiles;
    private boolean  hasMoreFiles;
    private String   currFunction;
    private int      retAddrCounter;
    
    public Translator(File f) {
        vmFile = f;
        totalFiles = 1;
        vmFiles = null;
        currentFile = 0;
        hack = new VMParser(vmFile);                                                // Create and send file to parser
        currFunction = "null";
        retAddrCounter = 0;
    }
    
    public Translator(File[] f) {
        vmFiles = f;
        totalFiles = vmFiles.length;
        vmFile = vmFiles[0];
        currentFile = 0;
        if(totalFiles > 1) {
            hack = new VMParser(vmFiles);                                           // Create and send file to parser
        } else {
            hack = new VMParser(vmFile);
        }
        currFunction = "null";
        retAddrCounter = 0;
    }
    
    public String getFileContents() {
        String         rawLine      = "";
        BufferedReader fileBuf      = null;
        long           fileLength   = 0;
        StringBuilder  fileContents = null;
        int            currFile     = currentFile;
 
        while (currFile < totalFiles) {
            try {
                if(vmFile.exists()) {
                    fileBuf = new BufferedReader(new FileReader(vmFile));
                    if(vmFiles != null) {
                        for(File f : vmFiles) {
                            fileLength += f.length();
                        } 
                    } else { 
                        fileLength = vmFile.length();
                    }
                    if(fileContents == null) {
                        fileContents = new StringBuilder((int)fileLength);
                    }
                    rawLine = "";
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
            currFile++;
            if(currFile < totalFiles) {
               vmFile = vmFiles[currFile];
            } else {
                if(vmFiles != null) {
                    vmFile = vmFiles[0];
                }
            }
        }
        return fileContents.toString();
    }

    public String getASMCode() {
        long fileLength = 0;
        if(vmFiles != null) {
            for(File f : vmFiles) {
                fileLength += f.length();
            } 
        } else { 
            fileLength = vmFile.length();
        }
        StringBuilder  asmCodeStr = new StringBuilder((int)fileLength);
        do {
            asmCodeStr.append("//******* writing file: ");
            asmCodeStr.append(getFileName());
            asmCodeStr.append("*******\r\n");
            while(hack.hasMoreCommands()) {
                switch(hack.commandType()){
                    case VMParser.C_ARITHMETIC:
                        VMCodeWriter.arithmeticCode(asmCodeStr, hack);
                        break;
                    case VMParser.C_PUSH:
                        VMCodeWriter.pushPopCode(asmCodeStr, hack);
                        break;
                    case VMParser.C_POP:
                        VMCodeWriter.pushPopCode(asmCodeStr, hack);
                        break;
                    case VMParser.C_LABEL:
                        VMCodeWriter.labelCode(asmCodeStr, currFunction, hack);
                        break;
                    case VMParser.C_GOTO:
                        VMCodeWriter.gotoCode(asmCodeStr, currFunction, hack);
                        break;
                    case VMParser.C_IF:
                        VMCodeWriter.ifCode(asmCodeStr, currFunction, hack);
                        break;
                    case VMParser.C_FUNCTION:
                        currFunction = hack.arg1();
                        VMCodeWriter.functionCode(asmCodeStr, currFunction, hack, getFileName());
                        break;
                    case VMParser.C_RETURN:
                        currFunction = "null";
                        VMCodeWriter.returnCode(asmCodeStr, currFunction, hack, getFileName(), retAddrCounter);
                        break;
                    case VMParser.C_CALL:
                        VMCodeWriter.callCode(asmCodeStr, currFunction, hack, getFileName(), retAddrCounter);
                        retAddrCounter++;
                        break;
                    case VMParser.UNKNOWN_COMMAND:
                    default:
                        asmCodeStr.append("                 Oops!\r\n");
                        break;
                }
                hack.advance();
            }
            asmCodeStr.append("//******* End write file: ");
            asmCodeStr.append(getFileName());
            asmCodeStr.append("*******\r\n");
            
        } while(hack.setNextFile());
        return asmCodeStr.toString();
    }
    
    public String getFileName() {
        return hack.getFileName();
    }
    
    public void unload() {
        hack.close();
    }
}
    