package com.scammers;

import com.scammers.lab6.PDA;
import com.scammers.lab7.CNFInterpreter;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CNFTests {
    private boolean check(PDA pda, String str) {
        List<String> input = str.isEmpty() ? Collections.emptyList() : Arrays.asList(str.split(""));
        return pda.accepts(input);
    }

    @Test
    void testSimpleAB() {
        // Язык {ab}
        // S -> AB
        // A -> a
        // B -> b
        List<String> rules = List.of("S->AB", "A->a", "B->b");
        PDA pda = CNFInterpreter.compile(rules);

        assertTrue(check(pda, "ab"));
        assertFalse(check(pda, "a"));
        assertFalse(check(pda, "b"));
        assertFalse(check(pda, "ba"));
        assertFalse(check(pda, "aa"));
    }

    @Test
    void testAnBn() {
        // Язык a^n b^n, n >= 1
        // S -> AB | AC
        // C -> SB
        // A -> a
        // B -> b
        List<String> rules = List.of(
                "S->AB",
                "S->AC",
                "C->SB",
                "A->a",
                "B->b"
        );
        PDA pda = CNFInterpreter.compile(rules);

        assertTrue(check(pda, "ab"));
        assertTrue(check(pda, "aabb"));
        assertTrue(check(pda, "aaabbb"));

        assertFalse(check(pda, "aab"));
        assertFalse(check(pda, "abb"));
        assertFalse(check(pda, "ba"));
        assertFalse(check(pda, ""));
    }

    @Test
    void testBracketsCNF() {
        // S -> SS | AB
        // A -> a
        // B -> b
        List<String> rules = List.of(
                "S->SS",
                "S->AB",
                "A->a",
                "B->b"
        );
        PDA pda = CNFInterpreter.compile(rules);

        assertTrue(check(pda, "ab"));      // ()
        assertTrue(check(pda, "abab"));    // ()()
        assertTrue(check(pda, "ababab"));  // ()()()
    }
}
