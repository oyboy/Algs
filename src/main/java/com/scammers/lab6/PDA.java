package com.scammers.lab6;

import java.util.*;

import java.util.*;

public class PDA {
    private String startState;
    private String initialStackSymbol;
    private String acceptingState;

    private final Map<String, List<Transition>> transitions = new HashMap<>();

    public PDA(String startState, String initialStackSymbol) {
        this.startState = startState;
        this.initialStackSymbol = initialStackSymbol;
    }

    public void setAcceptingState(String state) {
        if (acceptingState != null) return;
        this.acceptingState = state;
    }

    public void addTransition(String from, String input, String pop, String to, String push) {
        transitions.computeIfAbsent(from, k -> new ArrayList<>())
                .add(new Transition(input, pop, to, push));
    }

    public boolean accepts(List<String> inputChain) {
        Stack<String> stack = new Stack<>();
        if (initialStackSymbol != null && !initialStackSymbol.equals("eps")) {
            stack.push(initialStackSymbol);
        }
        return step(startState, inputChain, 0, stack, 0);
    }

    private boolean step(String currState, List<String> input, int inputIdx, Stack<String> stack, int depth) {
        if (stack.size() > input.size() + 10) {
            return false;
        }

        String indent = "  ".repeat(depth);

        List<String> remainingInput = input.subList(inputIdx, input.size());
        String inputStr = remainingInput.isEmpty() ? "[EPS]" : remainingInput.toString();

        System.out.printf("%s[%d] Состояние: %s | Вход: %s | Стек: %s%n",
                indent, depth, currState, inputStr, stack);

        if (inputIdx == input.size()) {
            if (acceptingState != null && acceptingState.equals(currState) && stack.isEmpty()) {
                System.out.println("Достигнуто принимающее состояние");
                return true;
            }
        }

        List<Transition> availableTransitions = transitions.getOrDefault(currState, Collections.emptyList());

        for (Transition t : availableTransitions) {
            boolean inputMatches = false;
            boolean isEpsilonInput = t.input.equals("eps");

            if (isEpsilonInput) {
                inputMatches = true;
            } else if (inputIdx < input.size() && t.input.equals(input.get(inputIdx))) {
                inputMatches = true;
            }

            if (!inputMatches) continue;

            boolean stackMatches = false;
            boolean isEpsilonPop = t.pop.equals("eps");
            String stackTop = stack.isEmpty() ? "Пусто" : stack.peek();

            if (isEpsilonPop) {
                stackMatches = true;
            } else if (!stack.isEmpty() && stackTop.equals(t.pop)) {
                stackMatches = true;
            }

            if (!stackMatches) continue;

            System.out.printf("%s-> Переход по символу '%s', pop '%s' -> push '%s', новое состояние '%s'%n",
                    indent, t.input, t.pop, t.push, t.toState);

            Stack<String> nextStack = (Stack<String>) stack.clone();

            if (!isEpsilonPop) {
                nextStack.pop();
            }

            if (!t.push.equals("eps")) {
                String[] symbolsToPush = t.push.split("");
                for (int i = symbolsToPush.length - 1; i >= 0; i--) {
                    nextStack.push(symbolsToPush[i]);
                }
            }

            int nextInputIdx = inputIdx + (isEpsilonInput ? 0 : 1);

            if (step(t.toState, input, nextInputIdx, nextStack, depth + 1)) {
                return true;
            } else {
                System.out.printf("%s<- Выход из %s%n", indent, t.toState);
            }
        }
        return false;
    }

    private static class Transition {
        String input;
        String pop;
        String toState;
        String push;

        public Transition(String i, String p, String t, String pu) {
            this.input = i; this.pop = p; this.toState = t; this.push = pu;
        }
    }
}