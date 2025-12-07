package com.scammers.viz;
import com.scammers.NFA;

import java.util.List;
import java.util.Map;

import java.util.Map;
import java.util.Set;

public class NFAVisualizer {
    public static <S, E> String generateDot(NFA<S, E> nfa, String title) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph NFA {\n");
        dot.append("    rankdir=LR;\n");
        dot.append("    label=\"" + title + "\";\n");
        dot.append("    node [shape=circle];\n");

        String startNode = sanitize(nfa.getStartState());
        dot.append("    __start [shape=none, label=\"\"];\n");
        dot.append("    __start -> \"" + startNode + "\";\n");

        for (S state : nfa.getAllStates()) {
            String stateName = sanitize(state);
            if (nfa.getAcceptingStates().contains(state)) {
                dot.append("    \"" + stateName + "\" [shape=doublecircle];\n");
            } else {
                dot.append("    \"" + stateName + "\" [shape=circle];\n");
            }
        }

        for (Map.Entry<S, Map<E, Set<S>>> entryFrom : nfa.getTransitions().entrySet()) {
            S from = entryFrom.getKey();
            for (Map.Entry<E, Set<S>> entryEvent : entryFrom.getValue().entrySet()) {
                E symbol = entryEvent.getKey();
                for (S to : entryEvent.getValue()) {
                    dot.append("    \"" + sanitize(from) + "\" -> \"" + sanitize(to) + "\" [label=\"" + symbol + "\"];\n");
                }
            }
        }

        for (Map.Entry<S, Set<S>> entry : nfa.getEpsilonTransitions().entrySet()) {
            S from = entry.getKey();
            for (S to : entry.getValue()) {
                dot.append("    \"" + sanitize(from) + "\" -> \"" + sanitize(to) + "\" [label=\"ε\", style=dashed];\n");
            }
        }

        dot.append("}");
        return dot.toString();
    }

    private static String sanitize(Object obj) {
        return obj.toString().replace("\"", "'");
    }
}