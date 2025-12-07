package com.scammers;

import com.scammers.lab6.PDA;
import com.scammers.lab6.PDALoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PDATests {
    private static PDA pda;
    private static final String RES_PATH = System.getProperty("user.dir") + "/src/test/resources/";

    @BeforeAll
    static void setup() throws IOException {
        pda = PDALoader.loadFromCSV(
                RES_PATH + "brackets_trans.csv",
                RES_PATH + "pda_accept.csv",
                "q0", "Z"
        );
        pda.setAcceptingState("q1");
    }

    @ParameterizedTest(name = "Test {index}: input='{0}', expected={1}")
    @CsvSource({
            "'', true",
            "(), true",
            "(()), true",
            "()(), true",
            "(()())(), true",
            "((((())))), true",

            "((, false",
            "((), false",
            ")(), false",
            "())(, false",
            "(a), false"
    })
    void testParenthesesPDA(String inputStr, boolean expected) {
        List<String> input;
        if (inputStr == null || inputStr.isEmpty()) {
            input = Collections.emptyList();
        } else {
            input = Arrays.asList(inputStr.split(""));
        }

        boolean actual = pda.accepts(input);
        assertEquals(expected, actual, "Failed for input: " + inputStr);
    }
}