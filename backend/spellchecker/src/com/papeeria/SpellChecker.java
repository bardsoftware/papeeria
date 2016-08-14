package com.papeeria;

import dk.dren.hunspell.Hunspell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * SpellChecker is designed to check texts spelling with hunspell (via JNAHunspell, hunspell Java API).
 *
 */
class SpellChecker {
    private HashMap<String, Hunspell.Dictionary> LanguageMap;

    public SpellChecker() {
        LanguageMap = new HashMap<>();
    }

    public SpellChecker(String path, String language) {
        this();
        addDictionary(path, language);
    }

    public void addDictionary(String path, String language) {
        try {
            Hunspell.Dictionary dictionary = Hunspell.getInstance().getDictionary(path + "/" + language);
            LanguageMap.put(language, dictionary);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + path + "/" + language);
        } catch (UnsupportedEncodingException e) {
            System.err.println("Unsupported encoding: " + path + "/" + language);
        }

    }

    /**
     * Check word spelling and return list of suggestions.
     * Use every dictionary that corresponds the given languages if
     * the current SpellChecker instance has such dictionary.
     *
     * @param word Word to check.
     * @param languages Languages chosen by user.
     * @return List of suggestions for the word.
     */
    // TODO fix punctiation issue: a word followed with a comma considered as misspelled
    public List<String> checkWord(String word, String... languages) {
        if (LanguageMap.isEmpty()) {
            return null;
        }

        List<String> suggestionsList = new ArrayList<>();

        for (String language: languages) {
            if (LanguageMap.containsKey(language) && LanguageMap.get(language).misspelled(word)) {
                List<String> suggestions = LanguageMap.get(language).suggest(word);

                if (!suggestions.isEmpty()) {
                    suggestionsList.addAll(suggestions);
                }
            }
        }

        return suggestionsList.isEmpty() ? null : suggestionsList;
    }

    /**
     * Check each word in the file with checkWord function, put result in
     * JSON structure like {"word": ["suggestions", "list", ...], ...}.
     *
     * @param path Path to file to check.
     * @param languages Languages chosen by user.
     * @return String that represents JSON of words and it's suggestions list.
     */
    public String checkFile(String path, String... languages) {
        Scanner scanner;
        try {
            scanner = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + path);
            return "{}";
        }

        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);

        JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
        ObjectNode correctionsJson = nodeFactory.objectNode();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            String[] split = line.split(" ");
            for (String word : split) {
                List<String> corrections = checkWord(word, languages);

                if (corrections != null) {
                    ArrayNode node = nodeFactory.arrayNode();

                    for (String correction : corrections) {
                        node.add(correction);
                    }

                    correctionsJson.set(word, node);
                }
            }
        }
        return correctionsJson.toString();
    }
}