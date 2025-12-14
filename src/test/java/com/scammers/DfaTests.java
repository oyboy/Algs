package com.scammers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

import com.scammers.DFA;
import com.scammers.lab3.DFATools; // Убедись, что импорт правильный
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testMinimizationAndEquivalence(String input, boolean expected) {
        DFA<String, String> redundantDfa = createRedundantDFA();
        DFA<Set<String>, String> minimizedDfa = DFATools.minimizeHopcroft(redundantDfa);

        List<String> chain = Arrays.asList(input.split(""));

        boolean resultRedundant = redundantDfa.processChain(chain);
        boolean resultMinimized = minimizedDfa.processChain(chain);

        assertEquals(expected, resultRedundant,
                String.format("Избыточный автомат ошибся на '%s'", input));

        assertEquals(expected, resultMinimized,
                String.format("Минимизированный автомат ошибся на '%s'", input));

        assertEquals(resultRedundant, resultMinimized, "Результаты автоматов не совпадают");
    }

    private static DFA<String, String> createRedundantDFA() {
        DFA<String, String> dfa = new DFA<>("S0");

        dfa.addSymbol("0");
        dfa.addSymbol("1");

        dfa.addTransition("S0", "0", "S0");
        dfa.addTransition("S0", "1", "S1");

        dfa.addTransition("S1", "0", "S2");
        dfa.addTransition("S1", "1", "S0");

        dfa.addTransition("S2", "0", "S3");
        dfa.addTransition("S2", "1", "S2");

        dfa.addTransition("S3", "0", "S2");
        dfa.addTransition("S3", "1", "S0");

        dfa.addAcceptingState("S0");

        return dfa;
    }
}