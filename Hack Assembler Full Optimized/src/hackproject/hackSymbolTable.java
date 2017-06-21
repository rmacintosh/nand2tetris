/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hackproject;

import java.util.HashMap;

/**
 *
 * @author Ryan
 */
public class hackSymbolTable {
    
    // Private class fields
    private HashMap symbols;
    
    public hackSymbolTable() {
        int initCapacity = 1000;
        symbols = new HashMap(initCapacity);                                        // New hash table with capacity for 100 entries
        symbols.put("SP"    , new Integer(     0));
        symbols.put("LCL"   , new Integer(     1));
        symbols.put("ARG"   , new Integer(     2));
        symbols.put("THIS"  , new Integer(     3));
        symbols.put("THAT"  , new Integer(     4));
        symbols.put("R0"    , new Integer(     0));
        symbols.put("R1"    , new Integer(     1));
        symbols.put("R2"    , new Integer(     2));
        symbols.put("R3"    , new Integer(     3));
        symbols.put("R4"    , new Integer(     4));
        symbols.put("R5"    , new Integer(     5));
        symbols.put("R6"    , new Integer(     6));
        symbols.put("R7"    , new Integer(     7));
        symbols.put("R8"    , new Integer(     8));
        symbols.put("R9"    , new Integer(     9));
        symbols.put("R10"   , new Integer(    10));
        symbols.put("R11"   , new Integer(    11));
        symbols.put("R12"   , new Integer(    12));
        symbols.put("R13"   , new Integer(    13));
        symbols.put("R14"   , new Integer(    14));
        symbols.put("R15"   , new Integer(    15));
        symbols.put("SCREEN", new Integer( 16384));
        symbols.put("KBD"   , new Integer( 24576));
    }

    public void addEntry(String symbol, int address) {
        symbols.put(symbol, new Integer(address));
    }
    
    public boolean contains(String symbol) {
        return symbols.containsKey(symbol);
    }
    
    public int getAddress(String symbol) {
        return (Integer)symbols.get(symbol);
    }
}
