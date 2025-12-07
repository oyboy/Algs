package com.scammers.viz;


import com.scammers.DFA;

public class DFAVisualizer {
    public static <S, E> String generateDot(DFA<S, E> dfa, String title) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph DFA {\n");
        dot.append("    rankdir=LR;\n");
        dot.append("    label=\"" + title + "\";\n");
        dot.append("    labelloc=\"t\";\n");

        dot.append("    __start [shape=none, label=\"\"];\n");
        dot.append("    __start -> \"" + sanitize(dfa.getCurrentState()) + "\";\n");

        for (S state : dfa.getStates()) {
            String shape = dfa.getAcceptingStates().contains(state) ? "doublecircle" : "circle";
            dot.append("    \"" + sanitize(state) + "\" [shape=" + shape + "];\n");
        }

        for (S from : dfa.getStates()) {
            for (E symbol : dfa.getAlphabet()) {
                S to = dfa.getTransition(from, symbol);
                if (to != null) {
                    dot.append("    \"" + sanitize(from) + "\" -> \"" + sanitize(to) + "\" [label=\"" + symbol + "\"];\n");
                }
            }
        }

        dot.append("}");
        return dot.toString();
    }

    private static String sanitize(Object obj) {
        return obj.toString().replace("\"", "'");
    }
}