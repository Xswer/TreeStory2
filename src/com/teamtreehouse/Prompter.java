package com.teamtreehouse;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.Files.readAllLines;


public class Prompter {
    private BufferedReader mReader;
    private Set<String> mCensoredWords;

    public Prompter() {
        mReader = new BufferedReader(new InputStreamReader(System.in));
        loadCensoredWords();
    }

    public String promptForTemplate() {
        System.out.printf("Write down your story templat here: ");
        String story = null;
        try {
            story = mReader.readLine();
        } catch (IOException e) {
            System.out.printf("Something is wrong with your template");
            e.printStackTrace();
        }
        return story;
    }

    private void loadCensoredWords() {
        mCensoredWords = new HashSet<String>();
        Path file = Paths.get("resources", "censored_words.txt");
        List<String> words = null;
        try {
            words = readAllLines(file);
        } catch (IOException e) {
            System.out.println("Couldn't load censored words");
            e.printStackTrace();
        }
        mCensoredWords.addAll(words);
    }

    public void run(Template tmpl) {
        List<String> results = null;
        try {
            results = promptForWords(tmpl);
        } catch (IOException e) {
            System.out.println("There was a problem prompting for words");
            e.printStackTrace();
            System.exit(0);
        }
        String stringResults = tmpl.render(results);
        System.out.printf("Your TreeStory:%n%n%s", stringResults);
    }

    /**
     * Prompts user for each of the blanks
     *
     * @param tmpl The compiled template
     * @return
     * @throws IOException
     */
    public List<String> promptForWords(Template tmpl) throws IOException {
        List<String> words = new ArrayList<String>();
        for (String phrase : tmpl.getPlaceHolders()) {
            String word = promptForWord(phrase);
            words.add(word);
        }
        return words;
    }


    /**
     * Prompts the user for the answer to the fill in the blank.  Value is guaranteed to be not in the censored words list.
     *
     * @param phrase The word that the user should be prompted.  eg: adjective, proper noun, name
     * @return What the user responded
     */
    public String promptForWord(String phrase) {
        String word = "";
        do {
            try {
                System.out.printf("Please write down a/an %s: ", phrase);
                try {
                    word = mReader.readLine();
                } catch (IOException e) {
                    System.out.printf("Something is wrong with your input");
                    e.printStackTrace();
                }
                if (mCensoredWords.contains(word.toLowerCase())) {
                    throw new IllegalArgumentException("Bad words are prohibited!%n");
                }
            } catch (IllegalArgumentException ioe) {
                System.out.printf(ioe.getMessage());
            }
        } while (mCensoredWords.contains(word.toLowerCase()));
        return word;
    }
}
