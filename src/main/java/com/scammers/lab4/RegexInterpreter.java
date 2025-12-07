package com.scammers.lab4;

import com.scammers.NFA;

import java.util.*;
import java.util.Map;
import java.util.Stack;

import java.util.Map;
import java.util.Stack;

public class RegexInterpreter {
    private static final char CONCAT = '.';
    private static final char UNION = '|';
    private static final char STAR = '*';
    private static final char OPEN = '(';
    private static final char CLOSE = ')';

    public static NFA<String, String> compile(String regex) {
        String prepared = insertExplicitConcat(regex);
        //System.out.println("1. Явная конкатенация:  " + prepared);

        String postfix = toPostfix(prepared);
        //System.out.println("2. Постфиксная запись:  " + postfix);

        //System.out.println("3. Построение (склейка конкатенаций):");
        return buildNFA(postfix);
    }

    private static String insertExplicitConcat(String regex) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < regex.length(); i++) {
            char c1 = regex.charAt(i);
            res.append(c1);
            if (i + 1 < regex.length()) {
                char c2 = regex.charAt(i + 1);
                boolean isC1Boundary = (Character.isLetterOrDigit(c1) || c1 == STAR || c1 == CLOSE);
                boolean isC2Boundary = (Character.isLetterOrDigit(c2) || c2 == OPEN);
                if (isC1Boundary && isC2Boundary) res.append(CONCAT);
            }
        }
        return res.toString();
    }

    private static String toPostfix(String regex) {
        StringBuilder output = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        Map<Character, Integer> precedence = Map.of(STAR, 3, CONCAT, 2, UNION, 1, OPEN, 0);

        for (char c : regex.toCharArray()) {
            if (Character.isLetterOrDigit(c)) output.append(c);
            else if (c == OPEN) stack.push(c);
            else if (c == CLOSE) {
                while (!stack.isEmpty() && stack.peek() != OPEN) output.append(stack.pop());
                stack.pop();
            } else {
                while (!stack.isEmpty() && precedence.getOrDefault(stack.peek(), -1) >= precedence.get(c)) output.append(stack.pop());
                stack.push(c);
            }
        }
        while (!stack.isEmpty()) output.append(stack.pop());
        return output.toString();
    }

    private static NFA<String, String> buildNFA(String postfix) {
        Stack<NFAFragment> stack = new Stack<>();
        int stateCounter = 0;

        for (char c : postfix.toCharArray()) {
            System.out.println("\n------------------------------------------");

            if (Character.isLetterOrDigit(c)) {
                String start = "S" + (stateCounter++);
                String end = "S" + (stateCounter++);

                System.out.println("ШАГ: Литерал '" + c + "'");
                System.out.println("Схема:  (" + start + ") --" + c + "--> (" + end + ")");

                NFA<String, String> nfa = new NFA<>(start);
                nfa.addTransition(start, String.valueOf(c), end);
                nfa.addAcceptingState(end);
                stack.push(new NFAFragment(nfa, start, end));
            }
            else if (c == CONCAT) {
                NFAFragment b = stack.pop();
                NFAFragment a = stack.pop();

                System.out.println("ШАГ: Конкатенация (.)");
                System.out.printf("         %s и %s%n", a.endState, b.startState);

                a.nfa.merge(b.nfa);
                a.nfa.fuseStates(b.startState, a.endState);
                a.nfa.removeAcceptingState(a.endState);

                stack.push(new NFAFragment(a.nfa, a.startState, b.endState));
            }
            else if (c == UNION) {
                NFAFragment b = stack.pop();
                NFAFragment a = stack.pop();

                String start = "S" + (stateCounter++);
                String end = "S" + (stateCounter++);

                System.out.println("ШАГ: Выбор (|)");
                System.out.printf("        (%s) -> [%s..%s], [%s..%s] -> (%s)%n",
                        start, a.startState, a.endState, b.startState, b.endState, end);

                NFA<String, String> res = new NFA<>(start);
                res.merge(a.nfa);
                res.merge(b.nfa);

                res.addEpsilonTransition(start, a.startState);
                res.addEpsilonTransition(start, b.startState);
                res.addEpsilonTransition(a.endState, end);
                res.addEpsilonTransition(b.endState, end);

                res.removeAcceptingState(a.endState);
                res.removeAcceptingState(b.endState);
                res.addAcceptingState(end);

                stack.push(new NFAFragment(res, start, end));
            }
            else if (c == STAR) {
                NFAFragment a = stack.pop();
                String start = "S" + (stateCounter++);
                String end = "S" + (stateCounter++);

                System.out.println("ШАГ: Звезда Клини (*)");
                System.out.printf("        (%s) -> [%s..%s] -> (%s) + Loop/Skip%n",
                        start, a.startState, a.endState, end);

                NFA<String, String> res = new NFA<>(start);
                res.merge(a.nfa);

                res.addEpsilonTransition(start, a.startState);
                res.addEpsilonTransition(start, end);
                res.addEpsilonTransition(a.endState, a.startState);
                res.addEpsilonTransition(a.endState, end);

                res.removeAcceptingState(a.endState);
                res.addAcceptingState(end);

                stack.push(new NFAFragment(res, start, end));
            }
        }
        return stack.pop().nfa;
    }

    private static class NFAFragment {
        NFA<String, String> nfa;
        String startState;
        String endState;
        public NFAFragment(NFA<String, String> n, String s, String e) {
            this.nfa = n; this.startState = s; this.endState = e;
        }
    }
}