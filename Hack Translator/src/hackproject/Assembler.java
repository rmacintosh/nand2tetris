package hackproject;

/**
 *
 * @author Ryan MacIntosh
 */
public class Assembler {
    // Private class fields
    private HackParser       hack; 
    private HackSymbolTable  symbolTbl;    
    private int              currPCaddr;
    private int              ramOffset;
    private String           asmFile;
    
    // Private static fields
    private static final int ramStart = 16;

    public Assembler(String s) {
        asmFile = s;
        hack = new HackParser(asmFile);                                            // Create and send file to parser
        symbolTbl = new HackSymbolTable();                                          // Add predefined symbols to the table
        currPCaddr = 0;
        ramOffset = 0;
    }
    
    public void buildSymbolTable() {
        String temp;
        Character c;
        while(hack.hasMoreCommands()) {
            switch(hack.commandType()) {
                case HackParser.C_COMMAND:
                    currPCaddr++;
                    break;
                case HackParser.A_COMMAND:
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
                case HackParser.L_COMMAND:
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

    public String getHackCode() {
        long           fileLength = asmFile.length();
        StringBuilder  hackCodeStr = new StringBuilder((int)fileLength);
        String         temp;
        Character      c;
        while(hack.hasMoreCommands()) {
            switch(hack.commandType()){
                case HackParser.A_COMMAND:
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
                case HackParser.C_COMMAND:                                          // test dest jump since they can
                    hackCodeStr.append(HackCode.comp(hack.comp()));                 // be omitted from an instruction
                    if(!hack.dest().equals("null")) {                       
                        hackCodeStr.append(HackCode.dest(hack.dest()));
                    } else {
                        hackCodeStr.append(HackCode.omit);
                    }
                    if(!hack.jump().equals("null")) {
                        hackCodeStr.append(HackCode.jump(hack.jump()));
                    } else {
                        hackCodeStr.append(HackCode.omit);
                    }
                    hackCodeStr.append("\r\n");
                    break;
                case HackParser.L_COMMAND:
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
    
    private String parseLine(String line, String delim, int index) {
        String[] rawLineParsed = line.split(delim);
        return rawLineParsed[index];
    }
}
    