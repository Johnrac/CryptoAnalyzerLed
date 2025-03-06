package com.javarush.pukhov.command;

import java.util.Map;

import com.javarush.pukhov.constant.AlphabetCaesar;

public class ProcessorCipherCaesar {

    private final int key;
    private final int mode;
    private char symbol;
    private int countSymbol;

    /**
     * @param key
     * @param mode
     */
    public ProcessorCipherCaesar(int key, int mode) {
        this.key = key;
        this.mode = mode;
    }

    public char[] processSymbolsWithKey(char[] buf, int countSymbols) {
        char[] newArray = new char[countSymbols];
        Map<Character, Integer> alphabet = AlphabetCaesar.getAlphabet();
        for (int i = 0; i < countSymbols; i++) {
            if (alphabet.containsKey(buf[i])) {
                int indexAlphabet = alphabet.get(buf[i]);
                int indexNewChar = (indexAlphabet + key * mode + Math.abs(key) * alphabet.size()) % alphabet.size();
                char newChar = AlphabetCaesar.getSymbol(indexNewChar);
                if (symbol != Character.MIN_VALUE && symbol == newChar) {
                    countSymbol++;
                }
                newArray[i] = newChar;
            } else {
                newArray[i] = buf[i];
            }
        }
        return newArray;
    }

    public void setCharacterCount(char symbol) {
        this.symbol = symbol;
    }

    /**
     * @return the countSymbol
     */
    public int getCountSymbol() {
        return countSymbol;
    }

    

}
