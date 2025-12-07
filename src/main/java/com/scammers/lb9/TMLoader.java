package com.scammers.lb9;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TMLoader {
    public static void loadRules(TuringMachine tm, String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    tm.addTransition(
                            parts[0].trim(), // curr state
                            parts[1].trim(), // read
                            parts[2].trim(), // write
                            parts[3].trim(), // move (L/R)
                            parts[4].trim()  // next state
                    );
                }
            }
        }
    }
}
