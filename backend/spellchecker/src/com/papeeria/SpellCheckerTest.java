package com.papeeria;

import org.junit.Before;

import java.util.List;

import static org.junit.Assert.*;

public class SpellCheckerTest {
    private SpellChecker checker;

    @Before
    public void initChecker() {
        checker = new SpellChecker("/usr/share/hunspell", "en_US");
    }

    @org.junit.Test
    public void checkMisspelledWord() {
        List<String> suggestions = checker.checkWord("teest", "en_US");
        assertNotEquals(suggestions, null);
    }

    @org.junit.Test
    public void checkCorrectWord() {
        List<String> suggestions = checker.checkWord("test", "en_US");
        assertEquals(suggestions, null);
    }

    @org.junit.Test
    public void checkWordWithPunctuation() {
        List<String> suggestions = checker.checkWord("test,", "en_US");
        assertEquals(suggestions, null);
    }

}