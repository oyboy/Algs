package com.scammers.lab8;

import java.util.*;

public class Parser {
    private final Map<String, List<String>> grammar = new HashMap<>();
    private String startSymbol;

    public Parser(List<String> rules) {
        for (String rule : rules) {
            String[] parts = rule.split("->");
            String lhs = parts[0].trim();
            String rhs = parts[1].trim();
            grammar.computeIfAbsent(lhs, k -> new ArrayList<>()).add(rhs);
            if (startSymbol == null) startSymbol = lhs;
        }
    }

    public ParseNode parse(String input) {
        ParseResult res = parseNonTerminal(startSymbol, input, 0);
        if (res != null && res.index == input.length()) {
            return res.node;
        }
        return null;
    }

    private ParseResult parseNonTerminal(String nonTerm, String input, int idx) {
        List<String> productions = grammar.get(nonTerm);
        if (productions == null) return null;

        for (String production : productions) {
            int currentIdx = idx;
            List<ParseNode> tempChildren = new ArrayList<>();
            boolean matched = true;

            if (production.equals("eps")) {
                tempChildren.add(new ParseNode("ε"));
            } else {
                String[] symbols = production.split("");
                for (String sym : symbols) {
                    if (isNonTerminal(sym)) {
                        ParseResult childRes = parseNonTerminal(sym, input, currentIdx);
                        if (childRes != null) {
                            tempChildren.add(childRes.node);
                            currentIdx = childRes.index;
                        } else {
                            matched = false;
                            break;
                        }
                    } else {
                        if (currentIdx < input.length() &&
                                String.valueOf(input.charAt(currentIdx)).equals(sym)) {
                            tempChildren.add(new ParseNode(sym));
                            currentIdx++;
                        } else {
                            matched = false;
                            break;
                        }
                    }
                }
            }

            if (matched) {
                ParseNode node = new ParseNode(nonTerm);
                for (ParseNode child : tempChildren) node.addChild(child);
                return new ParseResult(node, currentIdx);
            }
        }
        return null;
    }

    private boolean isNonTerminal(String s) {
        return s.matches("[A-Z]");
    }

    private static class ParseResult {
        ParseNode node;
        int index;
        public ParseResult(ParseNode n, int i) { this.node = n; this.index = i; }
    }
}
