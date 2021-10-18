
/**
 * Handles and parses data files.
 * 3 data files, grouped by frequency, provide words to be used for gameplay
 */

import java.io.FileNotFoundException;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class FileInterfacer {
    // Constants
    private static final String[] FILE_NAMES = { "4-31to6-82.txt", "2-31to4-3.txt", "1-6to2-3.txt" };
    private static final HashMap<String, Integer> NUM_WORDS_PER_FILE = new HashMap<String, Integer>() {
        {
            put(FILE_NAMES[0], 248);
            put(FILE_NAMES[1], 2898);
            put(FILE_NAMES[2], 2928);
        }
    };
    private static final String FOLDER_NAME = "data";

    /**
     * Preconditions: num < max number of words, difficulty is between 0 and 2
     * 
     * Returns num random words from the appropriate difficulty file Higher
     * frequency words are easier and lower frequency words are harder
     * 
     * @param difficulty
     * @param num
     * @return
     */
    public static Word[] getRandomWords(int difficulty, int num) {
        // Uses HashSet in the offchance that random generates duplicates
        HashSet<Integer> randomIndices = new HashSet<Integer>();
        int maxNum = NUM_WORDS_PER_FILE.get(FILE_NAMES[difficulty]);

        while (randomIndices.size() < num) {
            // Generates and adds random indices
            int potentialIndex = ThreadLocalRandom.current().nextInt(maxNum);
            randomIndices.add(potentialIndex);
        }
        // Converts to int[] and returns after calling helper function
        Integer[] finalArray = new Integer[num];
        return getWordsAtPositions(difficulty, randomIndices.toArray(finalArray));
    }

    /**
     * Helper function for getRandomWords. Returns the words stored in the
     * appropriate file in the appropriate indices
     * 
     * @param fileNum
     * @param indices
     * @return
     */

    private static Word[] getWordsAtPositions(int fileNum, Integer[] indices) {
        // Indices need to be sorted because lines are read one by one in ascending
        // order
        Arrays.sort(indices);

        // Tracks how many words have been added
        int numWords = 0;
        Word[] retVar = new Word[indices.length];

        try {
            File currFile = new File(FOLDER_NAME + "/" + FILE_NAMES[fileNum]);
            Scanner currScanner = new Scanner(currFile);
            int i = 0;

            // Keep looping until end of file or array is full
            while (currScanner.hasNextLine() && numWords < indices.length) {
                String line = "";
                // Goes to the current line number
                while (i < indices[numWords]) {
                    line = currScanner.nextLine();
                    i++;
                }
                // Parses the line and stores generated word object
                String[] lineArray = line.split(","); // each line is "wordString, frequency"
                retVar[numWords] = new Word(lineArray[0], Double.parseDouble(lineArray[1]));
                numWords += 1;
            }
            currScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return retVar;
    }

}
