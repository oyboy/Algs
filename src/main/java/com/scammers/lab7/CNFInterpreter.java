package com.scammers.lab7;

import com.scammers.lab6.PDA;

import java.util.List;

public class CNFInterpreter {

    public static PDA compile(List<String> rules) {
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("Rules cannot be empty");
        }

        String startSymbol = rules.get(0).split("->")[0].trim();
        PDA pda = new PDA("q_main", startSymbol);

        pda.setAcceptingState("q_main");

        for (String rule : rules) {
            parseRule(pda, rule);
        }

        return pda;
    }

    private static void parseRule(PDA pda, String rule) {
        String[] parts = rule.split("->");
        if (parts.length != 2) return;

        String lhs = parts[0].trim();
        String rhs = parts[1].trim();

        if (isTerminalProduction(rhs)) {
            pda.addTransition("q_main", rhs, lhs, "q_main", "eps");
        }
        else if (isNonTerminalProduction(rhs)) {
            pda.addTransition("q_main", "eps", lhs, "q_main", rhs);
        }
    }

    private static boolean isTerminalProduction(String rhs) {
        return rhs.length() == 1 && !Character.isUpperCase(rhs.charAt(0));
    }

    private static boolean isNonTerminalProduction(String rhs) {
        return rhs.length() >= 2 && rhs.chars().allMatch(Character::isUpperCase);
    }
}
