package com.scammers;

import com.scammers.lab4.KMP;
import com.scammers.lab4.RegexInterpreter;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class RegexTests {
    private boolean check(NFA<String, String> nfa, String text) {
        List<String> input;
        if (text.isEmpty()) {
            input = Collections.emptyList();
        } else {
            input = Arrays.asList(text.split(""));
        }
        return nfa.accepts(input);
    }

    @Test
    void testConcat() {
        NFA<String, String> nfa = RegexInterpreter.compile("ab");
        assertTrue(check(nfa, "ab"));

        assertFalse(check(nfa, "a"));
        assertFalse(check(nfa, "b"));
        assertFalse(check(nfa, "ba"));
        assertFalse(check(nfa, "aba"));
    }

    @Test
    void testUnion() {
        NFA<String, String> nfa = RegexInterpreter.compile("a|b");
        assertTrue(check(nfa, "a"));
        assertTrue(check(nfa, "b"));

        assertFalse(check(nfa, "ab"));
        assertFalse(check(nfa, "c"));
    }

    @Test
    void testKleeneStar() {
        NFA<String, String> nfa = RegexInterpreter.compile("a*");
        assertTrue(check(nfa, ""));
        assertTrue(check(nfa, "a"));
        assertTrue(check(nfa, "aaaaa"));

        assertFalse(check(nfa, "b"));
        assertFalse(check(nfa, "ab"));
    }

    @Test
    void testComplexLogic() {
        NFA<String, String> nfa = RegexInterpreter.compile("(a|b)*c");

        assertTrue(check(nfa, "c"));
        assertTrue(check(nfa, "ac"));
        assertTrue(check(nfa, "bc"));
        assertTrue(check(nfa, "bbbaac"));

        assertFalse(check(nfa, "ca"));
        assertFalse(check(nfa, ""));
    }

    @Test
    void testParentheses() {
        NFA<String, String> nfa = RegexInterpreter.compile("a(b|c)d");

        assertTrue(check(nfa, "abd"));
        assertTrue(check(nfa, "acd"));

        assertFalse(check(nfa, "ad"));
        assertFalse(check(nfa, "abcd"));
    }


    @Test
    void testIntegrationWithKMP_Found() {
        String text = "abacabadabacaba";
        String pattern = "dab";

        String regex = "(a|b|c|d)*" + pattern + "(a|b|c|d)*";

        boolean kmpResult = KMP.contains(text, pattern);
        boolean regexResult = check(RegexInterpreter.compile(regex), text);

        assertTrue(kmpResult, "KMP должен найти");
        assertTrue(regexResult, "Regex должен найти");
    }

    @Test
    void testIntegrationWithKMP_NotFound() {
        String text = "abacaba";
        String pattern = "z";

        String regex = "(a|b|c|z)*" + pattern + "(a|b|c|z)*";

        boolean kmpResult = KMP.contains(text, pattern);
        boolean regexResult = check(RegexInterpreter.compile(regex), text);

        assertFalse(kmpResult, "KMP не должен найти");
        assertFalse(regexResult, "Regex не должен найти");
    }

    @Test
    void performanceComparison_MediumText() {
        String text = "abc".repeat(10_000);
        String pattern = "abcabc";
        String regex = "(a|b|c)*" + pattern + "(a|b|c)*";

        long startKMP = System.nanoTime();
        boolean kmpResult = KMP.contains(text, pattern);
        long timeKMP = System.nanoTime() - startKMP;

        NFA<String, String> nfa = RegexInterpreter.compile(regex);
        List<String> inputList = Arrays.asList(text.split(""));

        long startNFA = System.nanoTime();
        boolean nfaResult = nfa.accepts(inputList);
        long timeNFA = System.nanoTime() - startNFA;

        System.out.println("\nMedium text (30,000 chars):");
        System.out.println("KMP time: " + TimeUnit.NANOSECONDS.toMillis(timeKMP) + " ms");
        System.out.println("NFA time: " + TimeUnit.NANOSECONDS.toMillis(timeNFA) + " ms");

        assertEquals(kmpResult, nfaResult);
    }
}
