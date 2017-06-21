package hackproject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ryan MacIntosh
 */
public class VMCodeWriter {
    // Private class fields
    private File       vmFile;
    private FileWriter vmFileWriter;
    
    // Private static fields
    
    public VMCodeWriter(File f) {
        vmFile = f;
        try {
            vmFileWriter = new FileWriter(f);
        } catch (IOException ex) {
            Logger.getLogger(VMCodeWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setFileName(File f) {
            // do isFile or isDirectory...
            //File files[] = f.listFiles();
            //for (File f : files) {
                // do whatever you want with each  File
            //}

            try {
                String filePath = f.getPath();
                if(!filePath.toLowerCase().endsWith(".asm")) {                      // Make sure we save file with the extension of .asm
                    f = new File(filePath + ".asm");
                }
                String hackData = "";
                vmFileWriter = new FileWriter(f);
                vmFileWriter.write(hackData);
            } catch (IOException ex) {
                Logger.getLogger(TranslatorGUI.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    vmFileWriter.close();
                } catch (IOException ex) {
                    Logger.getLogger(TranslatorGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }

    public void writeArithmetic(String command) {
        
    }
    
    public void writePushPop(int command, String segment, int index) {
        
    }

    public void close() {
    
    }
    
}
    