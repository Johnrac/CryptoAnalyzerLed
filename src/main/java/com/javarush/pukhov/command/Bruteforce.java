package com.javarush.pukhov.command;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.javarush.pukhov.constant.AlphabetCaesar;
import com.javarush.pukhov.constant.Constants;
import com.javarush.pukhov.constant.ErrorMessages;
import com.javarush.pukhov.exception.ActionException;
import com.javarush.pukhov.io.FileInput;
import com.javarush.pukhov.io.FileOutput;
import com.javarush.pukhov.valid.ValidatorCipherAction;

public final class Bruteforce extends Decrypt {

    FileInput fileInput;
    FileOutput fileOutput;
    private static final char SEARCH_SYMBOL = ' ';
    private char foundSymbol;
    private int countSpace;
    private int foundKey = -1;

    @Override
    public void decrypt(List<String> parameters) {
        validator = new ValidatorCipherAction<>(this);
        if (validator.check(parameters)) {
            getValuesFrom(validator);

            fileInput = new FileInput(src);
            fileOutput = new FileOutput(destination);

            processFileStream(fileInput, fileOutput, OptionFile.R);
            if (countSpace != 0) {
                Map<Character, Integer> alphabet = AlphabetCaesar.getAlphabet();
                foundKey = alphabet.get(foundSymbol);
            } else {
                throw new ActionException(ErrorMessages.BRUTE_FORCE_FAILED);
            }
            processFileStream(fileInput, fileOutput, OptionFile.RW);
        }
    }

    @Override
    protected char[] processSymbols(char[] buf, int countRead) {
        char[] processedSymbols = new char[countRead];
        if (foundKey == -1) {
            Map<Character, Integer> alphabet = AlphabetCaesar.getAlphabet();
            for (int key = 0; key < alphabet.size(); key++) {
                ProcessorCipherCaesar processor = new ProcessorCipherCaesar(key, DECRYPT_MODE);
                processor.setCharacterCount(SEARCH_SYMBOL);
                processor.processSymbolsWithKey(buf, countRead);
                int countSymbol = processor.getCountSymbol();
                if (countSpace < countSymbol) {
                    countSpace = countSymbol;
                    foundSymbol = AlphabetCaesar.getSymbol(key);
                }
            }

        } else {
            ProcessorCipherCaesar processor = new ProcessorCipherCaesar(foundKey, DECRYPT_MODE);
            processedSymbols = processor.processSymbolsWithKey(buf, countRead);
        }
        return processedSymbols;
    }

    @Override
    public String toString() {
        return Constants.BRUTE_FORCE;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileInput, fileOutput, foundSymbol, countSpace, foundKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Bruteforce)) {
            return false;
        }
        Bruteforce other = (Bruteforce) obj;
        return Objects.equals(fileInput, other.fileInput) && Objects.equals(fileOutput, other.fileOutput)
                && foundSymbol == other.foundSymbol && countSpace == other.countSpace && foundKey == other.foundKey;
    }

}
