package com.scammers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DFALoader {
    public static DFA<String, String> loadFromCSV(String transitionsFile,
                                                  String acceptingStatesFile,
                                                  String initialState) throws IOException {
        DFA<String, String> dfa = new DFA<>(initialState);

        try (BufferedReader reader = new BufferedReader(new FileReader(transitionsFile))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String from = parts[0].trim();
                    String event = parts[1].trim();
                    String to = parts[2].trim();

                    dfa.addTransition(from, event, to);
                    dfa.addSymbol(event);
                }
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(acceptingStatesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dfa.addAcceptingState(line.trim());
            }
        }
        return dfa;
    }
}
