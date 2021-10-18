
import java.util.Scanner;
import java.util.Arrays;
import java.util.HashSet;

/**
 * The main class for this project. Handles all events related to the game
 */
public class Game {
    // Constants
    private static final int SCORE_MULTIPLIER = 100;
    private static final int GUESS_MULTIPLIER = 10;
    private static final boolean DEBUG_MODE = true;

    // Required fields for constructor
    private int difficulty;
    private Player[] players;
    private int numberOfWords;
    private int livesPerPlayer;

    private Player[] standings;

    /**
     * Difficulty must be between 0 - 2. All fields are initialized.
     * 
     * @param difficulty
     * @param numberOfWords
     * @param players
     * @param livesPerPlayer
     */

    public Game(int difficulty, int numberOfWords, Player[] players, int livesPerPlayer) {
        this.difficulty = difficulty;
        this.numberOfWords = numberOfWords;
        // numberOfWords is per player
        Word[] words = FileInterfacer.getRandomWords(difficulty, numberOfWords * players.length);
        this.players = players;
        this.livesPerPlayer = livesPerPlayer;
        this.standings = players.clone();
        setUpPlayers(words);
    }

    /**
     * Divides words in a fair manner among players and calls each players setupGame
     * method
     * 
     * @param words
     */

    public void setUpPlayers(Word[] words) {
        // Edge case: if only one player is playing
        if (players.length == 1) {
            players[0].setupNewGame(words);
            return;
        }
        /**
         * Words are sorted by frequency. In order to ensure that every player receives
         * a set of words that has equal frequency, words are divided in the following
         * manner
         * 
         * Example: 2 players 3 words. Words Array will have a length of 2*3 = 6
         * 
         * 0 1
         * 
         * 2 3
         * 
         * 4 5
         * 
         * Player 0 recieves words [0,2,4]
         * 
         * Player 1 recieves words [1,3,5]
         */
        // i tracks the player number, j tracks the position in each player's word
        // array, k tracks the position in the overall word array
        for (int i = 0; i < players.length; i++) {
            Player currentPlayer = players[i];
            Word[] currWordArray = new Word[numberOfWords];
            int k = i;
            for (int j = 0; j < numberOfWords; j++) {
                currWordArray[j] = words[k];
                k += numberOfWords - 1;
            }
            currentPlayer.setupNewGame(currWordArray);
        }

    }

    // For reading console input. Will be irrelevant in final version
    static Scanner myObj = new Scanner(System.in);

    public static void main(String[] args) {
        // Main method does the game setup since that code has not been written yet
        System.out.println("How many players (1-5)?");
        int numPlayers = Integer.parseInt(myObj.nextLine());
        Player[] players = new Player[numPlayers];
        int i = 0;
        while (i < numPlayers) {
            System.out.println("Enter display name for player " + (i + 1) + ":");
            String readLine = myObj.nextLine();
            players[i] = new Player(readLine, i);
            i++;
        }

        System.out.println("What difficulty(1-3)?");
        int difficulty = Integer.parseInt(myObj.nextLine()) - 1;
        System.out.println("How many words per player(1-3)?");
        int numberOfWords = Integer.parseInt(myObj.nextLine());
        System.out.println("How many lives per player(10-15 recommended)?");
        int numberOfLives = Integer.parseInt(myObj.nextLine());

        Game currentGame = new Game(difficulty, numberOfWords, players, numberOfLives);
        currentGame.runGame();

    }

    /**
     * Main game thread
     */
    public void runGame() {
        Boolean done = false; // game terminates when all players are eliminated
        int rounds = 0;
        while (!done) {
            // i tracks player number
            int i = 0;
            while (i < players.length) {
                Player currentPlayer = players[i];
                if (!currentPlayer.isStillPlaying()) {
                    System.out.println("Skipping player " + currentPlayer.getDisplayString());
                    i++;
                    continue;
                }
                // Outputting current display string
                Word currentWord = currentPlayer.getCurrentWord();

                if (DEBUG_MODE) {
                    System.out.println(currentWord.getWordString());
                }

                System.out.println(currentPlayer.getDisplayString() + ", enter your guess: ");
                // prints string with dashes
                System.out.println(currentWord.getDisplayString(currentPlayer.getCurrentGuessedChars()));

                String readLine = myObj.nextLine();
                System.out.println("");
                char currentChar = readLine.charAt(0);

                // once input is read, it needs to be processed
                cyclePlayer(currentPlayer, currentWord, currentChar);
                i++;
            }
            // assuming that all players are done
            done = true;
            for (Player player : players) {
                if (player.isStillPlaying()) {
                    // one is still playing, so we continue the game
                    done = false;
                    break;
                }
            }
            rounds++;
            System.out.println("End of round " + rounds + "\n");
        }
        endGame(rounds); // print standings
        myObj.close(); // no need to read from input anymore
    }

    /**
     * Main thread for each player
     * 
     * @param currentPlayer
     * @param currentWord
     * @param currentChar
     */

    private void cyclePlayer(Player currentPlayer, Word currentWord, char currentChar) {
        // First, checking if character has already been guessed
        if (currentPlayer.addChar(currentChar)) {
            if (currentWord.hasChar(currentChar)) {
                playerGuessedCorrectly(currentPlayer, currentWord, currentChar);
            } else {
                playerGuessedIncorrectly(currentPlayer, currentWord, currentChar);
            }
        } else {
            System.out.println("Sorry " + currentPlayer.getDisplayString() + ", you had entered " + currentChar
                    + " previously. Skipping your turn!");
        }

    }

    /**
     * Executes if player guesses a char in the word
     * 
     * @param currentPlayer
     * @param currentWord
     * @param currentChar
     */

    private void playerGuessedCorrectly(Player currentPlayer, Word currentWord, char currentChar) {
        HashSet<Character> guessedChars = currentPlayer.getCurrentGuessedChars();
        int scoreIncrement = calculateScore(currentWord, currentPlayer);
        currentPlayer.incrementScore(scoreIncrement);
        currentPlayer.decrementLettersRemaining(currentWord.returnOccurrencesOfChar(currentChar));

        System.out.println("Great job! the letter " + currentChar + " is in your word! +" + scoreIncrement);
        System.out.println(currentWord.getDisplayString(guessedChars));
        System.out.println(currentPlayer.getCurrentGuessedCharsString()); // prints all previously guessed chars

        if (DEBUG_MODE) {
            System.out.println("Remaining: " + currentPlayer.getLettersRemaining());
        }

        if (currentPlayer.getLettersRemaining() <= 0) {
            // No more letters left to guess, move onto next word
            System.out.println("Congrats on guessing " + currentWord.getWordString() + " correctly!");
            currentPlayer.incrementWordsGuessedCorrectly();
            wordDone(currentPlayer);
        }
    }

    /**
     * Executes if a player guesses a char that is not in the word
     * 
     * @param currentPlayer
     * @param currentWord
     * @param currentChar
     */

    private void playerGuessedIncorrectly(Player currentPlayer, Word currentWord, char currentChar) {
        currentPlayer.incrementIncorrectGuesses();
        System.out.println(
                "Sorry " + currentPlayer.getDisplayString() + ", the letter " + currentChar + " is not in the word");
        System.out.println("You have " + (livesPerPlayer - currentPlayer.getIncorrectGuesses()) + " lives remaining");
        System.out.println(currentPlayer.getCurrentGuessedCharsString()); // prints all previously guessed chars

        if (currentPlayer.getIncorrectGuesses() >= livesPerPlayer) {
            // No more lives left, move onto next word
            System.out.println("You have used up all of your guesses!");
            System.out.println("The word was " + currentWord.getWordString());
            currentPlayer.incrementWordsGuessedIncorrectly();
            wordDone(currentPlayer);
        }
    }

    /**
     * Calculates score based on multipliers, word frequency, and how many letters
     * are remaining
     * 
     * @param currentWord
     * @param currentPlayer
     * @return
     */
    private int calculateScore(Word currentWord, Player currentPlayer) {
        // Less frequent words should have a higher score
        int score = (int) ((int) SCORE_MULTIPLIER / currentWord.getFrequency());
        // If the letter is guessed towards the end, then it was not luck(since the word
        // was almost revealed) and should be rewarded more.
        score += GUESS_MULTIPLIER / currentPlayer.getLettersRemaining();
        return score * (difficulty + 1);
    }

    /**
     * Executes when a player guesses all chars in a word or runs out of lives
     * 
     * @param currentPlayer
     */

    private void wordDone(Player currentPlayer) {

        if (currentPlayer.getTotalWordsGuessed() >= numberOfWords) {
            // Player has attempted to guess all words
            playerFinished(currentPlayer);
            return;
        }

        // Resetting word specific fields
        currentPlayer.setupNewWord();
        System.out.println(
                "You have to guess " + (numberOfWords - currentPlayer.getTotalWordsGuessed()) + " more words!");
        System.out.println("Your lives have reset to " + livesPerPlayer);
        System.out.println("Your score is " + currentPlayer.getScore());
    }

    /**
     * Executes when a player has attempted to guess all their words
     * 
     * @param currentPlayer
     */

    private void playerFinished(Player currentPlayer) {
        currentPlayer.eliminate();
        System.out.println("You guessed " + (currentPlayer.getWordsGuessedCorrectly()) + " out of " + numberOfWords
                + " words correctly");
        System.out.println("Your final score is " + currentPlayer.getScore());
    }

    /**
     * Executes when all players have attempted to guess all their words
     * 
     * @param rounds
     */

    private void endGame(int rounds) {
        System.out.println("This game has concluded after " + rounds + " rounds.");
        System.out.println("Final Scores: ");
        // Default implementation of player.compareTo reads ids instead of score
        Arrays.sort(standings, (Player o1, Player o2) -> o2.getScore() - o1.getScore());
        for (Player player : standings) {
            System.out.println("    " + player.getDisplayString() + ": " + player.getScore());
        }
    }

}
