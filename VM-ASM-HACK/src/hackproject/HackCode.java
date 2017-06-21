/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hackproject;

/**
 *
 * @author Ryan
 */

public class HackCode {

    // Public class fields
    public static final String omit = "000";

    // dest/jump mnemonics
    private static final String[][] destJumpTable = { {"M"  , "JGT", "001"}, 
                                                      {"D"  , "JEQ", "010"},
                                                      {"MD" , "JGE", "011"},
                                                      {"A"  , "JLT", "100"},
                                                      {"AM" , "JNE", "101"},
                                                      {"AD" , "JLE", "110"},
                                                      {"AMD", "JMP", "111"} };
                                                
    // comp mnemonics
    private static final String [][] compTable = { {"0"  , "0101010"},
                                                   {"1"  , "0111111"},
                                                   {"-1" , "0111010"},
                                                   {"D"  , "0001100"},
                                                   {"A"  , "0110000"},
                                                   {"!D" , "0001101"},
                                                   {"!A" , "0110001"},
                                                   {"-D" , "0001111"},
                                                   {"-A" , "0110011"},
                                                   {"D+1", "0011111"},
                                                   {"A+1", "0110111"},
                                                   {"D-1", "0001110"},
                                                   {"A-1", "0110010"},
                                                   {"D+A", "0000010"},
                                                   {"D-A", "0010011"},
                                                   {"A-D", "0000111"},
                                                   {"D&A", "0000000"},
                                                   {"D|A", "0010101"},
                                                   {"M"  , "1110000"},
                                                   {"!M" , "1110001"},
                                                   {"-M" , "1110011"},
                                                   {"M+1", "1110111"},
                                                   {"M-1", "1110010"},
                                                   {"D+M", "1000010"},
                                                   {"M+D", "1000010"},
                                                   {"D-M", "1010011"},
                                                   {"M-D", "1000111"},
                                                   {"D&M", "1000000"},
                                                   {"D|M", "1010101"} };
 
    private HackCode() {}                                                           // empty private constructor. 
                                                                                    // Class only contains static methods
    public static String dest(String destStr) {
        int row = 0;
        int col = 0;                                                                // dest mnemonics are located in col 0
        while(row < 7 && !destStr.equals(destJumpTable[row][col])) {
            row++;
        }
        if(row == 7) {
            return omit;
        } else {
            return destJumpTable[row][2];                                           // dest binary codes are in col 2
        }
    }
    
    public static String comp(String compStr) {
        int row = 0;
        int col = 0;                                                                // comp mnemonics are located in col 0
        while(row < 29 && !compStr.equals(compTable[row][col])) {
            row++;
        }
        if(row == 29) {
            return null;
        } else {
             return ("111" + compTable[row][1]);                                    // comp binary codes are in col 1
        }
    }

    public static String jump(String jumpStr) {
        int row = 0;
        int col = 1;                                                                // jump mnemonics are located in col 1
        while(row < 7 && !jumpStr.equals(destJumpTable[row][col])) {
            row++;
        }
        if(row == 7) {
            return omit;
        } else {
            return destJumpTable[row][2];                                           // jump binary codes are in col 2
        }
    }
}
