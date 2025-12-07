package com.scammers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

class DfaTests {
    @ParameterizedTest
    @CsvSource({
            "110,     true",
            "1001,    true",
            "1010,    false",
            "1111,    true",
            "101,     false",
            "0,       true",
            "1,       false",
            "11,      true",
            "100,     false"
    })

    void testDivisibleByThree(String input, boolean expected) {
        DFA<String, String> testDfa = createDFA();

        List<String> chain = Arrays.asList(input.split(""));
        boolean result = testDfa.processChain(chain);

        assertEquals(expected, result,
                String.format("Для цепочки '%s'", input));
    }

    private static DFA<String, String> createDFA() {
        DFA<String, String> newDfa = new DFA<>("S0");

        newDfa.addTransition("S0", "0", "S0");
        newDfa.addTransition("S0", "1", "S1");
        newDfa.addTransition("S1", "0", "S2");
        newDfa.addTransition("S1", "1", "S0");
        newDfa.addTransition("S2", "0", "S1");
        newDfa.addTransition("S2", "1", "S2");

        newDfa.addAcceptingState("S0");

        return newDfa;
    }
}