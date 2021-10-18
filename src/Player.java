import java.util.Arrays;
import java.util.HashSet;

/**
 * Container for each Player. Each player keeps track of their own words,
 * guesses, and score in each game.
 */
public class Player implements Comparable<Player> {
    // Required properties for initialization
    private String displayString;
    private int id;

    // Properties needed for each new word
    private HashSet<Character> currentGuessedChars; // all characters guessed during this word
    private int lettersRemaining; // how many more letters left to guess
    private int incorrectGuesses;

    // Properties needed for each new game
    private Word[] words;
    private int score;
    private int wordsGuessedCorrectly;
    private int wordsGuessedIncorrectly;
    private boolean stillPlaying; // false when all words have been attempted

    /**
     * Constructor requires a player name(display string) and a unique id
     * 
     * @param displayString
     * @param id
     */
    public Player(String displayString, int id) {
        this.displayString = displayString;
        this.id = id;
        currentGuessedChars = new HashSet<Character>();
    }

    @Override
    public String toString() {
        return "{Player Name: " + displayString + ", Id: " + id + "}";
    }

    // Contract: id is relevant field for equals, hashCode and compareTo
    @Override
    public int compareTo(Player o) {
        return this.id - o.id;
    }

    @Override
    public boolean equals(Object obj) {
        Player other = (Player) obj;
        return compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        return id;
    }

    // Getters
    public String getDisplayString() {
        return displayString;
    }

    public int getPlayerId() {
        return id;
    }

    public HashSet<Character> getCurrentGuessedChars() {
        return currentGuessedChars;
    }

    /**
     * Returns a string representation of all characters guessed in a sorted manner
     * 
     * @return
     */
    public String getCurrentGuessedCharsString() {
        StringBuilder retVar = new StringBuilder();
        retVar.append("P.S. You have guessed the following characters: ");

        // need to convert to array because hashsets are inherently unordered
        Character[] sortedChars = new Character[currentGuessedChars.size()];
        currentGuessedChars.toArray(sortedChars);
        Arrays.sort(sortedChars);
        for (char c : sortedChars) {
            retVar.append(c);
            retVar.append(", ");
        }
        // Remove trailing comma and space
        retVar.deleteCharAt(retVar.length() - 1);
        retVar.deleteCharAt(retVar.length() - 1);
        return retVar.toString();

    }

    public int getScore() {
        return score;
    }

    public int getIncorrectGuesses() {
        return incorrectGuesses;
    }

    public int getWordsGuessedCorrectly() {
        return wordsGuessedCorrectly;
    }

    public int getWordsGuessedIncorrectly() {
        return wordsGuessedIncorrectly;
    }

    public int getTotalWordsGuessed() {
        return wordsGuessedCorrectly + wordsGuessedIncorrectly;
    }

    public Word getCurrentWord() {
        return words[getTotalWordsGuessed()];
    }

    public int getLettersRemaining() {
        return lettersRemaining;
    }

    public boolean isStillPlaying() {
        return stillPlaying;
    }

    // Setters
    // Set up functions for new word and new game, intializes/resets relevant fields
    public void setupNewWord() {
        lettersRemaining = getCurrentWord().getWordLength(false);
        currentGuessedChars = new HashSet<Character>();
        incorrectGuesses = 0;
    }

    public void setupNewGame(Word[] newWords) {
        score = 0;
        wordsGuessedCorrectly = 0;
        wordsGuessedIncorrectly = 0;
        words = newWords;
        stillPlaying = true;
        setupNewWord();
    }

    public boolean addChar(char c) {
        if (currentGuessedChars.contains(c)) {
            return false;
        }
        currentGuessedChars.add(c);
        return true;
    }

    public void incrementScore(int delta) {
        score += delta;
    }

    public void decrementLettersRemaining(int delta) {
        lettersRemaining -= delta;
    }

    public void incrementIncorrectGuesses() {
        incorrectGuesses++;
    }

    public void incrementWordsGuessedCorrectly() {
        wordsGuessedCorrectly++;
    }

    public void incrementWordsGuessedIncorrectly() {
        wordsGuessedIncorrectly++;
    }

    public void eliminate() {
        stillPlaying = false;
    }

}
