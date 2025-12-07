package com.scammers;

import com.scammers.lb9.TMLoader;
import com.scammers.lb9.TuringMachine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TMTests {
    private static final String RULES_PATH = "src/main/resources/cpu_rules.csv";

    private String run(String inputTape) {
        TuringMachine tm = new TuringMachine("q0", inputTape);
        try {
            TMLoader.loadRules(tm, RULES_PATH);
            tm.run();
            return tm.getTapeAsString().replace("_", "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testINC() {
        assertEquals("x#1100", run("I#1011"));
        assertEquals("x#01", run("I#00"));
        assertEquals("x#000", run("I#111"));
    }

    @Test
    void testDEC() {
        assertEquals("x#1011", run("D#1100"));
        assertEquals("x#11", run("D#00"));
    }

    @Test
    void testZERO() {
        assertEquals("x#0000", run("Z#1111"));
        assertEquals("x#00", run("Z#10"));
    }

    @Test
    void testSequentialINC() {
        assertEquals("x#11x#11", run("I#10I#10"));
    }

    @Test
    void testIncThenDec() {
        assertEquals("x#11x#10", run("I#10D#11"));
    }

    @Test
    void testComplexStream() {
        // 1. I#100  -> x#101  (4->5)
        // 2. Z#1111 -> x#0000 (Reset)
        // 3. I#1011 -> x#1100 (11->12)
        // 4. D#1100 -> x#1011 (12->11)
        String input = "I#100Z#1111I#1011D#1100";
        String expected = "x#101x#0000x#1100x#1011";
        assertEquals(expected, run(input));
    }

    @Test
    void testMixedLength() {
        // Z#1 -> x#0
        // I#1111 -> x#0000 (overflow)
        assertEquals("x#0x#0000", run("Z#1I#1111"));
    }

    @Test
    void testNoSeparatorsBetweenCommands() {
        // I#0 -> x#1
        // I#0 -> x#1
        assertEquals("x#1x#1", run("I#0I#0"));
    }
}
