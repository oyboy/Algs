package com.scammers.lab6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PDALoader {
    public static PDA loadFromCSV(String transitionsFile, String acceptingFile, String startState, String startStack) throws IOException {
        PDA pda = new PDA(startState, startStack);

        try (BufferedReader br = new BufferedReader(new FileReader(transitionsFile))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    pda.addTransition(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim()
                    );
                }
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(acceptingFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.trim().isEmpty()) {
                    pda.setAcceptingState(line.trim());
                }
            }
        }

        return pda;
    }
}
