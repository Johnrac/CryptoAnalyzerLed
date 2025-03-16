package com.javarush.pukhov.command;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.javarush.pukhov.constant.Constants;
import com.javarush.pukhov.io.FileInput;
import com.javarush.pukhov.io.FileOutput;
import com.javarush.pukhov.statistic.Statistic;
import com.javarush.pukhov.util.Statistics;
import com.javarush.pukhov.valid.ValidatorAnalyzeAction;
import com.javarush.pukhov.valid.ValidatorCipherAction;

public final class DecryptAnalyze extends Decrypt {

    private static final int COUNT_TRY_FIND = 10;

    private FileInput fileDictionary;
    private FileInput fileInput;
    private FileOutput fileOutput;

    private boolean isReadFile;

    private Path dictionary;

    private Statistic statistic;

    private List<Character> bestEncryptedAlphabet;

    private Map<Character, Integer> alphabet = new HashMap<>();

    Character[] charsAlphabet;
    private boolean isAlphabetFilled;
    private int lastIndexAlphabet;

    @Override
    public void decrypt(List<String> parameters) {
        validator = new ValidatorAnalyzeAction<>(this);
        if (validator.check(parameters)) {
            getValuesFrom(validator);

            fileDictionary = new FileInput(dictionary);
            fileInput = new FileInput(src);
            fileOutput = new FileOutput(destination);

            fillAlphabet();

            double[][] statisticsMatrixDictionaries = getStatisticsMatrix(fileDictionary);
            double[][] statisticsMatrixEncryptFiles = getStatisticsMatrix(fileInput);

            decryptAnalyze(statisticsMatrixEncryptFiles, statisticsMatrixDictionaries);
        }
    }

    private void fillAlphabet() {
        processFileStream(fileDictionary, fileOutput, OptionFile.R);
        charsAlphabet = alphabet.keySet().toArray(new Character[0]);
        isAlphabetFilled = true;
    }

    private double[][] getStatisticsMatrix(FileInput fileInput) {
        statistic = getInstanceStatistic();
        readFile(fileInput);
        return statistic.getStatisticsMatrix();
    }

    private Statistic getInstanceStatistic() {
        return new Statistic(alphabet);
    }

    private void readFile(FileInput fileInput) {
        isReadFile = true;
        processFileStream(fileInput, fileOutput, OptionFile.R);
        isReadFile = false;
    }

    @Override
    protected char[] processSymbols(char[] buf, int countRead) {
        if (!isAlphabetFilled) {
            for (int i = 0; i < countRead; i++) {
                Integer index = alphabet.putIfAbsent(buf[i], lastIndexAlphabet);
                if (index == null) {
                    lastIndexAlphabet++;
                }
            }
        } else if (isReadFile) {
            statistic.fillMatrixStatistics(buf, countRead);
        } else {
            for (int i = 0; i < countRead; i++) {
                int index = bestEncryptedAlphabet.indexOf(buf[i]);
                if (index != -1) {
                    buf[i] = charsAlphabet[index];
                }
            }
        }
        return buf;
    }

    private void decryptAnalyze(double[][] firstMatrix, double[][] secondMatrix) {
        findBestAlphabet(firstMatrix, secondMatrix);
        processFileStream(fileInput, fileOutput, OptionFile.RW);
    }

    private void findBestAlphabet(double[][] sourceMatrix, double[][] original) {
        double bestDistance = Double.MAX_VALUE;
        Character[] bestChars = null;
        for (int i = COUNT_TRY_FIND; i > 0; i--) {
            Character[] chars = charsAlphabet.clone();
            double probeDistance = Statistics.findBestDistanceWithArraySwaps(chars, sourceMatrix, original);
            if (probeDistance < bestDistance) {
                i += COUNT_TRY_FIND;
                bestDistance = probeDistance;
                bestChars = chars.clone();
            }
        }
        bestEncryptedAlphabet = Arrays.asList(bestChars);
    }

    @Override
    protected void getValuesFrom(ValidatorCipherAction<List<String>> validator) {
        super.getValuesFrom(validator);
        dictionary = ((ValidatorAnalyzeAction<List<String>>) validator).getDictionary();
    }

    @Override
    public String toString() {
        return Constants.ANALYZE;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(charsAlphabet);
        result = prime * result + Objects.hash(fileDictionary, fileInput, fileOutput, isReadFile, dictionary, statistic,
                bestEncryptedAlphabet, alphabet, isAlphabetFilled, lastIndexAlphabet);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DecryptAnalyze)) {
            return false;
        }
        DecryptAnalyze other = (DecryptAnalyze) obj;
        return Objects.equals(fileDictionary, other.fileDictionary) && Objects.equals(fileInput, other.fileInput)
                && Objects.equals(fileOutput, other.fileOutput) && isReadFile == other.isReadFile
                && Objects.equals(dictionary, other.dictionary) && Objects.equals(statistic, other.statistic)
                && Objects.equals(bestEncryptedAlphabet, other.bestEncryptedAlphabet)
                && Objects.equals(alphabet, other.alphabet) && Arrays.equals(charsAlphabet, other.charsAlphabet)
                && isAlphabetFilled == other.isAlphabetFilled && lastIndexAlphabet == other.lastIndexAlphabet;
    }

}
