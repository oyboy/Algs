package com.scammers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class NFALoader {
    public static NFA<String, String> loadFromCSV(String filePath,
                                                  String acceptingStatesFile,
                                                  String initialState) throws IOException {
        NFA<String, String> nfa = new NFA<>(initialState);
        var lines = Files.lines(Paths.get(filePath))
                .skip(1)
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length != 3) {
                System.err.println("Некорректная строка CSV: " + line);
                continue;
            }

            String from = parts[0].trim();
            String event = parts[1].trim();
            String to = parts[2].trim();

            if (event.equals("e")) {
                nfa.addEpsilonTransition(from, to);
            } else {
                nfa.addTransition(from, event, to);
            }
        }

        if (acceptingStatesFile != null && Files.exists(Paths.get(acceptingStatesFile))) {
            try (BufferedReader reader = new BufferedReader(new FileReader(acceptingStatesFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        nfa.addAcceptingState(line);
                    }
                }
            }
        }

        return nfa;
    }
}
