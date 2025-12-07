package com.scammers.lb9;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TuringMachine {
    private String currentState;
    private final Map<Integer, String> tape = new HashMap<>();
    private final Map<String, Transition> transitionTable = new HashMap<>();
    private int headPosition = 0;

    private static final int MAX_STEPS = 100000;

    public TuringMachine(String startState, String initialTapeContent) {
        this.currentState = startState;
        for (int i = 0; i < initialTapeContent.length(); i++) {
            tape.put(i, String.valueOf(initialTapeContent.charAt(i)));
        }
    }

    public void addTransition(String state, String readSym, String writeSym, String moveDir, String nextState) {
        String key = state + "|" + readSym;
        transitionTable.put(key, new Transition(writeSym, moveDir, nextState));
    }

    public void run() {
        int steps = 0;
        System.out.println("Начало работы машины Тьюринга...");
        printTape();

        while (!currentState.equals("HALT") && steps < MAX_STEPS) {
            String currentSymbol = tape.getOrDefault(headPosition, "_");

            String key = currentState + "|" + currentSymbol;
            Transition trans = transitionTable.get(key);

            if (trans == null) {
                System.err.println("Нет перехода для состояния " + currentState + " и символа " + currentSymbol);
                break;
            }

            tape.put(headPosition, trans.writeSymbol);

            if (trans.moveDirection.equalsIgnoreCase("R") || trans.moveDirection.equals(">")) {
                headPosition++;
            } else if (trans.moveDirection.equalsIgnoreCase("L") || trans.moveDirection.equals("<")) {
                headPosition--;
            }

            currentState = trans.nextState;
            steps++;

            System.out.printf("Step %d: %s -> %s%n", steps, key, currentState);
        }

        System.out.println("Машина остановилась.");
        printTape();
    }

    public void saveTapeToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(getTapeAsString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printTape() {
        System.out.println("Tape: " + getTapeAsString() + " (State: " + currentState + ", Head: " + headPosition + ")");
    }

    public String getTapeAsString() {
        if (tape.isEmpty()) return "";
        int min = Collections.min(tape.keySet());
        int max = Collections.max(tape.keySet());
        StringBuilder sb = new StringBuilder();
        for (int i = min; i <= max; i++) {
            sb.append(tape.getOrDefault(i, "_"));
        }
        return sb.toString();
    }

    private static class Transition {
        String writeSymbol;
        String moveDirection;
        String nextState;

        public Transition(String w, String m, String n) {
            this.writeSymbol = w; this.moveDirection = m; this.nextState = n;
        }
    }
}
