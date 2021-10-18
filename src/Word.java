
/**
 * Container for storing properties of a Word: word, frequency, similarTo
 * Also has derived "helper" properties and helper methods
 */

import java.util.Arrays;
import java.util.HashSet;

public class Word implements Comparable<Word> {
    // Constants
    private static final Character[] LETTERS = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    private static final HashSet<Character> LETTERS_SET = new HashSet<Character>(Arrays.asList(LETTERS));

    // Required immutable properties
    private String word;
    private double frequency;

    // Only needed if player requests a hint on the word
    private String similarTo;

    // Derived "helper" immutable properties
    private int wordLength;
    private int wordLengthOnlyLetters; // some words could have punctuation/numbers
    private char[] charArray;

    // Sets required properties and derives helper properties
    public Word(String word, double frequency) {
        this.word = word;
        this.frequency = frequency;
        this.wordLength = word.length();
        this.charArray = new char[wordLength];

        // calculating wordLengthOnlyLetters
        for (int i = 0; i < wordLength; i++) {
            char c = word.charAt(i);
            charArray[i] = c;
            if (LETTERS_SET.contains(c)) {
                this.wordLengthOnlyLetters++;
            }
        }

    }

    @Override
    public String toString() {
        return "{Word: " + word + " | Similar To: " + similarTo + " | Frequency: " + frequency + "}";
    }

    @Override
    public boolean equals(Object obj) {
        Word o = (Word) obj;
        return this.compareTo(o) == 0;
    }

    // Contract: word and frequency must be same
    @Override
    public int hashCode() {
        return word.hashCode() + (int) frequency;
    }

    // Compares by frequency and then word
    @Override
    public int compareTo(Word o) {
        if (this.frequency == o.frequency) {
            return this.word.compareTo(o.word);
        }
        return Double.compare(this.frequency, o.frequency);
    }

    // Getters
    public String getWordString() {
        return word;
    }

    public char[] getWordCharArray() {
        return charArray.clone();
    }

    public int getWordLength(boolean totalLength) {
        return totalLength ? wordLength : wordLengthOnlyLetters;
    }

    public String getSimilarTo() {
        return similarTo;
    }

    public double getFrequency() {
        return frequency;
    }

    // Setter for similarTo when hint is required
    public void setSimilarTo() {
        this.similarTo = APIInterfacer.getSimilar(word);
    }

    /**
     * Returns the number of occurrences of a char in a string.
     * 
     * @param guessedChar
     * @return
     */
    public int returnOccurrencesOfChar(char guessedChar) {
        int retVar = 0;
        for (int i = 0; i < wordLength; i++) {
            char currChar = charArray[i];
            if (currChar == guessedChar) {
                retVar++;
            }
        }

        return retVar;
    }

    /**
     * Calculates the number of letters not in the HashSet
     * 
     * @param guessedChars
     * @return
     */
    public int getCharsRemaining(HashSet<Character> guessedChars) {
        int retVar = wordLengthOnlyLetters;
        for (char c : guessedChars) {
            retVar -= returnOccurrencesOfChar(c);
        }
        return retVar;
    }

    /**
     * Calculates new display string(string user will see) based on characters
     * guessed
     * 
     * @param guessedChars
     * @return
     */
    public String getDisplayString(HashSet<Character> guessedChars) {
        // StringBuilder is more efficient when adding repeatedly chars to a String
        StringBuilder retVar = new StringBuilder();

        for (char c : charArray) {
            // punctuation and numbers is always displayed, if a character is not
            // punctuation and not number and has
            // not been guessed then it is a dash
            retVar.append(LETTERS_SET.contains(c) && !guessedChars.contains(c) ? '_' : c);
            retVar.append(' ');
        }
        // removing the additional trailing space
        retVar.deleteCharAt(retVar.length() - 1);
        return retVar.toString();

    }

    /**
     * If a character is in a word, returns true
     * 
     * @param letter
     * @return
     */
    public boolean hasChar(char letter) {
        for (char c : charArray) {
            if (c == letter)
                return true;
        }
        return false;
    }
}
