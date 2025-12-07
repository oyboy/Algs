package com.scammers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

class NfaTests {
    @ParameterizedTest
    @CsvSource({
            "'',true",
            "0,true",
            "1,true",
            "00,true",
            "11,true",
            "000,true",
            "111,true",
            "1100,true",
            "00000,true",
            "11111,true",
            "01010,true",
            "10101,true",
            "11011,true",
            "00011,true",

            "01,false",
            "10,false",
            "1101,false",
            "1011,false",
            "1110,false",
            "111110,false",
            "011010,false",
            "0110100011,false"
    })
    void testNFADirect(String input, boolean expected) {
        NFA<String, String> nfa = createNFA();
        List<String> chain = input.isEmpty() ?
                Collections.emptyList() :
                Arrays.asList(input.split(""));

        boolean result = nfa.accepts(chain);
        assertEquals(expected, result,
                String.format("Для цепочки '%s'", input));
    }

    private NFA<String, String> createNFA() {
        NFA<String, String> nfa = new NFA<>("S0");

        nfa.addEpsilonTransition("S0", "S1");
        nfa.addEpsilonTransition("S0", "S3");

        nfa.addTransition("S1", "0", "S2");
        nfa.addTransition("S1", "1", "S1");
        nfa.addTransition("S2", "0", "S1");
        nfa.addTransition("S2", "1", "S2");

        nfa.addTransition("S3", "0", "S3");
        nfa.addTransition("S3", "1", "S4");
        nfa.addTransition("S4", "0", "S4");
        nfa.addTransition("S4", "1", "S3");

        nfa.addAcceptingState("S1");
        nfa.addAcceptingState("S3");

        return nfa;
    }
}