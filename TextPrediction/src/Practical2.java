import javax.sound.midi.SysexMessage;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Chris on 23/02/2016.
 */
public class Practical2 {
    //Declaring variables
    static ArrayList<Trigram> trigrams = new ArrayList<Trigram>();
    static File folder;
    static File[] listOfFiles;
    static String[] listOfPaths;

    static ArrayList<String> words = new ArrayList<String>();
    static ArrayList<Trigram> wordGrams = new ArrayList<Trigram>();

    public static void main(String[] args) {
        try {
            folder = new File(getPath());
            listOfFiles = folder.listFiles();
            listOfPaths = new String[listOfFiles.length];
        } catch (NullPointerException e) {
            System.out.println("Missing Files... Please rerun program");
            System.exit(0);
        }

        boolean quit = false;
        boolean validEntry;
        Scanner input = new Scanner(System.in);

        do {
            validEntry = false;
            System.out.println("Select what you want to do by entering the corresponding number");
            System.out.println("1. Character Grams\n2. Word Grams\n3. Text Prediction");
            do {
                switch (input.next()) {
                    case "1":
                        validEntry = true;
                        fileFinder();
                        readFile();
                        writeCSVFile(trigrams);
                        break;
                    case "2":
                        validEntry = true;
                        fileFinder();
                        readWordsFromFile();
                        createWordGrams();
                        writeCSVFile(wordGrams);
                        break;
                    case "3":
                        //System.out.print("\n2\n3\n1");
                        validEntry = true;
                        fileFinderPred();
                        readWordsFromFilePred();
                        createWordGramsPred();
                        textPrediction();
                        break;
                    default:
                        System.out.println("Please enter a valid option");
                }
            } while (!validEntry);

            System.out.println("Type 'quit' to quit or anything else to restart");
            if (input.next().equals("quit")) {
                quit = true;
            }
        } while (!quit);
    }

    public static String getPath() {
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter absolute path of folder 'Input Files");
        return input.nextLine();
    }

    public static boolean allStringLetters(String trigramString) {
        for (int i = 0; i < trigramString.length(); i++) {
            if (!Character.isLetter(trigramString.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void addTrigram(String trigramString) {
        if (!(containsTrigramString(trigramString))) {
            trigrams.add(new Trigram(trigramString));
        } else {
            //finds the position of the exisitng trigram and updates the trigramCount in that position
            (trigrams.get(getIndex(trigrams, trigramString))).incrementTrigramCount();

        }
    }

    public static boolean containsTrigramString(String trigramString) {
        for (Trigram t : trigrams) {
            if (t.getTrigram().equals(trigramString)) {
                return true;
            }
        }
        return false;
    }

    public static int getIndex(ArrayList<Trigram> gram, String trigramString) {
        //gets the index of the trigram in the list by the trigrams attributes
        for (Trigram t : gram) {
            if (t != null && t.getTrigram().equals(trigramString)) {
                return gram.indexOf(t);
            }
        }
        return -1; //Missing in list exception will be thrown later
    }

    public static void writeCSVFile(ArrayList<Trigram> grams) {
        //Writes the sorted trigrams to a CSV File
        System.out.println("Writing to CSV File...");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("Output.csv");

            writer.println("Gram, Count");
            for (int i = 0; i < 10; i++) {
                writer.println(grams.get(i).getTrigram() + ", " + grams.get(i).getTrigramCount());
            }
        } catch (IOException e) {
            System.out.println("No Permission");
        } finally {
            if (writer != null) {
                writer.close();
                System.out.println("CSV File 'Output' Created.");
            }
        }
    }

    public static void readFile() {
        //Reads file that user has selected and converts the file into trigrams before sorting the trigrams
        int noOfGrams = getGramCount();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(listOfPaths[getUserInput()]));

            String line;
            String trigramString;

            System.out.println("Reading File and sorting into trigrams...");
            while ((line = reader.readLine()) != null) {
                for (int i = 0; i < line.length() - noOfGrams - 1; i++) {
                    trigramString = line.substring(i, i + noOfGrams).toLowerCase();

                    if (trigramString.charAt(noOfGrams - 1) == 32) {
                        if (allStringLetters(trigramString.substring(0, trigramString.length() - 1))) {
                            addTrigram(trigramString.substring(0, trigramString.length() - 1) + "_");
                        }
                    } else if (allStringLetters(trigramString.substring(0, trigramString.length()))) {
                        addTrigram(trigramString);
                    }
                }
            }

            System.out.println("Sorting trigrams by number of occurrences...");
            Collections.sort(trigrams, (t1, t2) -> t2.getTrigramCount() - t1.getTrigramCount());

        } catch (FileNotFoundException e) {
            System.out.println("File Missing");
        } catch (IOException e) {
            System.out.println("No Permission");
        } finally {
            //reader and writer will still close even if there is an exception
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close reader");
                }
            }
        }
    }

    public static void fileFinder() {
        //finds all files in directory and puts them in an array
        System.out.println("Available Files:");
        int counter = 0; //Numbering files
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith((".txt"))) {
                listOfPaths[counter] = listOfFiles[i].getAbsolutePath();
                counter++;
                System.out.println(counter + ". " + listOfFiles[i].getName());
            }
        }
    }

    public static int getUserInput() {
        //Lists files for user and allows them to make a choice between them validating their entry
        boolean validEntry = false;
        int userInput = 1;
        System.out.println("Please enter the number of the file you would like to convert to trigrams");
        Scanner input = new Scanner(System.in);
        while (!validEntry) {
            while (!input.hasNextInt()) {
                System.out.println("Please select an integer");
                input.next();
            }
            userInput = Integer.parseInt(input.next());
            if (userInput > 0 && userInput < listOfPaths.length) {
                validEntry = true;
            } else {
                System.out.println("Please enter an integer in the correct range");
            }
        }

        return userInput - 1;
    }

    public static int getGramCount() {
        //Lists files for user and allows them to make a choice between them validating their entry
        boolean validEntry = false;
        int userInput = 1;
        System.out.println("Please enter the number of grams you want to search for. Enter 2 or more");
        Scanner input = new Scanner(System.in);
        while (!validEntry) {
            while (!input.hasNextInt()) {
                System.out.println("Please select an integer");
                input.next();
            }
            userInput = Integer.parseInt(input.next());
            if (userInput > 1) {
                validEntry = true;
            } else {
                System.out.println("Please enter an integer larger than 1");
            }
        }

        return userInput;
    }

    public static void createWordGrams() {
        //Reads file that user has selected and converts the file into trigrams before sorting the trigrams

        int noOfGrams = getGramCount();
        String wordGramString = "";

        System.out.println("Reading File and sorting into word grams...");
        for (int i = 0; i < words.size() - noOfGrams + 1; i++) {
            for (int j = 0; j < noOfGrams; j++) {
                wordGramString += words.get(i + j) + "_";
            }
            addWordgram(wordGramString);
            wordGramString = "";
        }
        System.out.println("Sorting word grams by number of occurrences...");
        Collections.sort(wordGrams, (g1, g2) -> g2.getTrigramCount() - g1.getTrigramCount());
    }

    public static void readWordsFromFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(listOfPaths[getUserInput()]));
            Pattern p = Pattern.compile("[\\w']+");
            String line;

            while ((line = reader.readLine()) != null) {
                Matcher m = p.matcher(line);
                while (m.find()) {
                    words.add((line.substring(m.start(), m.end())).toLowerCase());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Missing");
        } catch (IOException e) {
            System.out.println("No Permission");
        } finally {
            //reader and writer will still close even if there is an exception
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close reader");
                }
            }
        }
    }

    public static void addWordgram(String wordGramString) {
        if (!(containsWordGramString(wordGramString))) {
            wordGrams.add(new Trigram(wordGramString));
        } else {
            //finds the position of the exisitng wordgram and updates the trigramCount in that position
            (wordGrams.get(getIndex(wordGrams, wordGramString))).incrementTrigramCount();
        }
    }

    public static boolean containsWordGramString(String wordGramString) {
        for (Trigram g : wordGrams) {
            if (g.getTrigram().equals(wordGramString)) {
                return true;
            }
        }
        return false;
    }


    public static void fileFinderPred() {
        //finds all files in directory and puts them in an array
        int counter = 0; //Numbering files
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith((".txt"))) {
                listOfPaths[counter] = listOfFiles[i].getAbsolutePath();
                counter++;
                System.out.println(counter + ". " + listOfFiles[i].getName());
            }
        }
    }

    public static void readWordsFromFilePred() {
        BufferedReader reader = null;
        Pattern p = Pattern.compile("[\\w']+");
        String line;
        try {
            for (int i = 0; i < 3; i++) {
                reader = new BufferedReader(new FileReader(listOfPaths[i]));
                while ((line = reader.readLine()) != null) {
                    Matcher m = p.matcher(line);
                    while (m.find()) {
                        words.add((line.substring(m.start(), m.end())).toLowerCase());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Missing");
        } catch (IOException e) {
            System.out.println("No Permission");
        } finally {
            //reader and writer will still close even if there is an exception
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close reader");
                }
            }
        }
    }

    public static void createWordGramsPred() {
        //Reads file that user has selected and converts the file into trigrams before sorting the trigrams
        int noOfGrams = 1;
        String wordGramString = "";

        System.out.println("Reading File and sorting into word grams...");
        for (int i = 0; i < words.size() - noOfGrams + 1; i++) {
            for (int j = 0; j < noOfGrams; j++) {
                wordGramString += words.get(i + j) + "_";
            }
            addWordgram(wordGramString);
            wordGramString = "";
        }
        System.out.println("Sorting word grams by number of occurrences...");
        Collections.sort(wordGrams, (g1, g2) -> g2.getTrigramCount() - g1.getTrigramCount());
    }

    public static void textPrediction() {
        boolean quit;
        Scanner input = new Scanner(System.in);
        String inputSave;

        do {
            System.out.println("\nEnter the start of a word to be predicted or 'quit' to go back to the main menu");
            inputSave = input.next();
            quit = false;
            if (!(inputSave.equals("quit"))) {
                System.out.println("Most likely word: " + findMostCommonWord(inputSave));
            }
            else {
                quit = true;
            }
        } while (!quit);
    }

    public static String findMostCommonWord(String input) {
        for (int i = 0; i < wordGrams.size(); i++) {
            if (wordGrams.get(i).getTrigram().length() > input.length() + 1) {
                if (wordGrams.get(i).getTrigram().substring(0, input.length()).equals(input)) {
                    return wordGrams.get(i).getTrigram();
                }
            }
        }

        return "Cannot Predict";
    }
}