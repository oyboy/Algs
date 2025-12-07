package com.scammers;

import com.scammers.lab3.DFATools;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class DFAMinimizationTests {
    /**
     * Тест: Автомат с заведомо лишними состояниями.
     * A->B(1), A->A(0)
     * B->C(1), B->A(0) [B - accept]
     * C->C(1), C->A(0) [C - accept]
     * Ожидание: B и C склеятся. 3 состояния -> 2 состояния.
     */
    @Test
    void testMinimizationRedundantStates() {
        DFA<String, String> dfa = new DFA<>("A");
        dfa.addSymbol("0");
        dfa.addSymbol("1");

        dfa.addTransition("A", "0", "A");
        dfa.addTransition("A", "1", "B");

        dfa.addTransition("B", "0", "A");
        dfa.addTransition("B", "1", "C");

        dfa.addTransition("C", "0", "A");
        dfa.addTransition("C", "1", "C");

        dfa.addAcceptingState("B");
        dfa.addAcceptingState("C");

        DFA<Set<String>, String> minimized = DFATools.minimizeHopcroft(dfa);

        assertEquals(2, minimized.getStates().size(), "Должно остаться 2 состояния");
        assertTrue(DFATools.areEquivalent(dfa, minimized), "Минимизированный автомат должен быть эквивалентен исходному");
    }

    @Test
    void testAlreadyMinimal() {
        DFA<String, String> dfa = new DFA<>("S0");
        dfa.addSymbol("0");
        dfa.addSymbol("1");

        dfa.addTransition("S0", "0", "S0");
        dfa.addTransition("S0", "1", "S1");

        dfa.addTransition("S1", "0", "S2");
        dfa.addTransition("S1", "1", "S0");

        dfa.addTransition("S2", "0", "S1");
        dfa.addTransition("S2", "1", "S2");

        dfa.addAcceptingState("S0");

        DFA<Set<String>, String> minimized = DFATools.minimizeHopcroft(dfa);

        assertEquals(3, minimized.getStates().size(), "Количество состояний не должно измениться");
        assertTrue(DFATools.areEquivalent(dfa, minimized));
    }

    @Test
    void testNotEquivalent() {
        DFA<String, String> dfa1 = new DFA<>("A");
        dfa1.addTransition("A", "1", "B");
        dfa1.addSymbol("1");
        dfa1.addAcceptingState("B");

        DFA<String, String> dfa2 = new DFA<>("A");
        dfa2.addTransition("A", "1", "B");

        assertFalse(DFATools.areEquivalent(dfa1, dfa2));
    }
}
