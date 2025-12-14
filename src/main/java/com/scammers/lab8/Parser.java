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
        System.out.println("Разбор строки: \"" + input + "\"");
        ParseResult res = parseNonTerminal(startSymbol, input, 0, 0);

        if (res != null && res.index == input.length()) {
            System.out.println("\nРезультат: строка успешно разобрана.");
            return res.node;
        }
        System.out.println("\nРезультат: ошибка разбора.");
        return null;
    }

    private ParseResult parseNonTerminal(String nonTerm, String input, int idx, int depth) {
        String indent = "  ".repeat(depth);
        String remainder = idx < input.length() ? input.substring(idx) : "[конец строки]";
        List<String> productions = grammar.get(nonTerm);

        if (productions == null) return null;

        for (String production : productions) {
            System.out.println(indent + "> Поиск " + nonTerm + " (правило: " + production + "). Остаток: \"" + remainder + "\"");

            int currentIdx = idx;
            List<ParseNode> tempChildren = new ArrayList<>();
            boolean matched = true;

            if (production.equals("eps")) {
                System.out.println(indent + "  . Эпсилон (пусто)");
                tempChildren.add(new ParseNode("ε"));
            } else {
                String[] symbols = production.split("");

                for (String sym : symbols) {
                    if (isNonTerminal(sym)) {
                        ParseResult childRes = parseNonTerminal(sym, input, currentIdx, depth + 1);
                        if (childRes != null) {
                            tempChildren.add(childRes.node);
                            currentIdx = childRes.index;
                        } else {
                            matched = false;
                            break;
                        }
                    } else {
                        if (currentIdx < input.length()) {
                            String charInInput = String.valueOf(input.charAt(currentIdx));
                            if (charInInput.equals(sym)) {
                                System.out.println(indent + "  . Терминал '" + sym + "' совпал");
                                tempChildren.add(new ParseNode(sym));
                                currentIdx++;
                            } else {
                                System.out.println(indent + "  ! Ошибка: ожидалось '" + sym + "', найдено '" + charInInput + "'");
                                matched = false;
                                break;
                            }
                        } else {
                            System.out.println(indent + "  ! Ошибка: ожидалось '" + sym + "', но строка кончилась");
                            matched = false;
                            break;
                        }
                    }
                }
            }

            if (matched) {
                System.out.println(indent + "< [OK] Узел " + nonTerm + " построен по правилу " + production);
                ParseNode node = new ParseNode(nonTerm);
                for (ParseNode child : tempChildren) node.addChild(child);
                return new ParseResult(node, currentIdx);
            } else {
                System.out.println(indent + "! Откат. Выбор следующего правила");
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