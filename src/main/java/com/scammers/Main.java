package com.scammers;

import com.scammers.lab3.DFATools;
import com.scammers.lab4.KMP;
import com.scammers.lab4.RegexInterpreter;
import com.scammers.lab5.XmlLexer;
import com.scammers.lab5.XmlPDA;
import com.scammers.lab6.PDA;
import com.scammers.lab6.PDALoader;
import com.scammers.lab7.CNFInterpreter;
import com.scammers.lab8.ParseNode;
import com.scammers.lab8.Parser;
import com.scammers.lab8.TreeVisualizer;
import com.scammers.lb9.TMLoader;
import com.scammers.lb9.TuringMachine;
import com.scammers.viz.DFAVisualizer;
import com.scammers.viz.NFAVisualizer;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Main {
    private static final String PATH = "src/main/resources/";
    public static void main(String[] args) {
        //lab1();
        //lab2();
        //lab3();
        //lab4();
        lab5();
        //lab6();
        //lab7();
        //lab8();
        //lab9();
    }

    private static void lab1(){
        DFA<String, String> dfa = new  DFA<>("S1");
        try{
            dfa = DFALoader.loadFromCSV(
                    PATH + "states1.csv",
                    PATH + "acceptingStates1.csv",
                    "S0"
            );
        } catch (IOException io) {
            System.err.println("Error loading state file: "  + io.getMessage());
        }

        String symbols = "1001";
        for (String s : symbols.split("")) {
            dfa.onEvent(s);
        }

        if (dfa.isInAcceptingState()) {
            System.out.println("Цепочка принята. Конечное состояние: " + dfa.getCurrentState());
        } else {
            System.out.println("Цепочка отвергнута. Конечное состояние: " + dfa.getCurrentState());
        }
        System.out.println(DFAVisualizer.generateDot(dfa, ""));
    }

    private static void lab2(){
        NFA<String, String> nfa = new  NFA<>("S0");
        try{
            nfa = NFALoader.loadFromCSV(
                    PATH + "states2.csv",
                    PATH + "acceptingStates2.csv",
                    "S0"
            );
        } catch (IOException io) {
            System.err.println("Error loading state file: "  + io.getMessage());
        }
        nfa.addAcceptingState("S1");
        nfa.addAcceptingState("S3");
        String symbols = "00011";
        System.out.println("Accepted: " + nfa.accepts(Arrays.stream(symbols.split("")).toList()));
        System.out.println(NFAVisualizer.generateDot(nfa, "nfa"));
    }

    private static void lab3() {
        try {
            DFA<String, String> dfa = DFALoader.loadFromCSV(
                    PATH + "statesMinimize.csv",
                    PATH + "acceptingMinimize.csv",
                    "S0"
            );

            System.out.println("Исходный автомат: " + dfa.getStates().size() + " состояний.");

            DFA<Set<String>, String> minimized = DFATools.minimizeHopcroft(dfa);
            System.out.println("Минимизированный DFA: " + minimized.getStates().size() + " состояний.");

            boolean equivalent = DFATools.areEquivalent(dfa, minimized);
            System.out.println("Результат проверки на эквивалентность: " + (equivalent ? "Эквивалентны" : "Не эквивалентны"));

            System.out.println("=============================================");
            System.out.println(DFAVisualizer.generateDot(dfa, "Original DFA (4 states)"));
            System.out.println("\n\n");
            System.out.println(DFAVisualizer.generateDot(minimized, "Minimized DFA (3 states)"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void lab4() {
        int textLength = 50_000;
        System.out.println("Генерация текста длиной " + textLength + " символов...");
        String text = generateRandomText(textLength, "abc");

        String pattern = "abacaba";
        text = text.substring(0, textLength - 1000) + pattern + text.substring(textLength - 1000 + pattern.length());

        System.out.println("Искомый паттерн: " + pattern);
        boolean kmpResult = KMP.contains(text, pattern);
        System.out.println("KMP Result:   " + kmpResult);


        String regex = "(a|b|c)*";
        System.out.println("Regex: " + regex);

        NFA<String, String> nfa = RegexInterpreter.compile(regex);
        System.out.println("NFA построено: " + nfa.getStateCount() + " состояний");

        List<String> inputList = java.util.Arrays.asList(text.split(""));
        boolean regexResult = nfa.accepts(inputList);
        System.out.println("NFA Result:   " + regexResult);

        if (kmpResult == regexResult) {
            System.out.println("SUCCESS: Результаты совпадают.");
        } else {
            System.err.println("FAILURE: Результаты различаются!");
        }

        String vizRegex = "abc|abb";
        NFA<String, String> vizNFA = RegexInterpreter.compile(vizRegex);
        String dotCode = NFAVisualizer.generateDot(vizNFA, "Regex: " + vizRegex);
        System.out.println(dotCode);
    }

    private static String generateRandomText(int length, String alphabet) {
        java.util.Random r = new java.util.Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    private static void lab5(){
        XmlLexer lexer = new XmlLexer();
        XmlPDA pda = new XmlPDA();
        StringBuilder xml_text = new StringBuilder();

        try(BufferedReader br = new BufferedReader(new FileReader( "pom.xml"))) {
            String line;
            while ((line = br.readLine()) != null) {
                xml_text.append(line);
            }
        } catch (IOException ex) {
            System.err.println("Error opening file: " + ex.getMessage());
        }
        pda.accepts(lexer.tokenize(xml_text.toString()));
    }

    private static void lab6() {
        try {
            PDA pda = PDALoader.loadFromCSV(
                    PATH + "pda_transitions.csv",
                    PATH + "pda_accept.csv",
                    "q0", "Z"
            );

            List<String> input = Arrays.asList("(())()".split(""));
            if (input.isEmpty()) input = java.util.Collections.emptyList();

            boolean result = pda.accepts(input);
            System.out.printf("Цепочка '%-6s' -> %s%n", input, result ? "Принята" : "Не принята");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void lab7() {
        List<String> grammar = List.of(
                "S->AB",
                "S->AC",
                "C->SB",
                "A->a",
                "B->b"
        );

        System.out.println("Грамматика (CNF): " + grammar);
        PDA pda = CNFInterpreter.compile(grammar);

        String text = "baaabbbaaabb";
        System.out.println("Текст: " + text);

        System.out.println("\nПоиск подстрок, соответствующих грамматике...");

        for (int i = 0; i < text.length(); i++) {
            for (int j = i + 1; j <= text.length(); j++) {
                String substring = text.substring(i, j);

                if (substring.length() % 2 != 0) continue;

                List<String> input = Arrays.asList(substring.split(""));
                if (pda.accepts(input)) {
                    System.out.printf("Найдена подстрока [%d:%d]: '%s'%n", i, j, substring);
                }
            }
        }
    }
    private static void lab8() {
        // S -> (S)S
        // S -> eps
        List<String> grammar = List.of(
                "S->(S)S",
                "S->eps"
        );

        String input = "()())";

        System.out.println("Грамматика: " + grammar);
        System.out.println("Строка: " + input);

        Parser parser = new Parser(grammar);
        ParseNode tree = parser.parse(input);

        if (tree != null) {
            TreeVisualizer.toCSV(tree, "parse_tree.csv");

            String dotCode = TreeVisualizer.toDOT(tree);
            System.out.println(dotCode);
        } else {
            System.err.println("Ошибка: строка не соответствует грамматике.");
        }
    }

    private static void lab9() {
        // 1. I#100 -> 101
        // 2. Z#1111 -> 0000
        // 3. I#1011 -> 1100
        // 4. D#1100 -> 1011
        // x#101x#0000x#1100x#1011

        runCPU("I#100Z#1111I#1011D#1100", "tape_stream.txt");
    }

    private static void runCPU(String tape, String outFile) {
        TuringMachine tm = new TuringMachine("q0", tape);
        try {
            TMLoader.loadRules(tm, PATH + "cpu_rules.csv");
            tm.run();
            tm.saveTapeToFile(outFile);
        } catch (Exception e) { e.printStackTrace(); }
    }
}