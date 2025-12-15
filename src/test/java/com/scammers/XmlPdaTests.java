package com.scammers;

import com.scammers.lab5.XmlLexer;
import com.scammers.lab5.XmlPDA;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class XmlPdaTests {
    private static XmlPDA pda = new XmlPDA();
    private static XmlLexer lexer = new XmlLexer();

    @Test
    void test_valid() {
        String doc =  "<html><body><div>Привет</div></body></html>";
        assertTrue(pda.accepts(lexer.tokenize(doc)));
    }

    @Test
    void test_invalid(){
        String doc = "<root><i><b>Text</i></b></root>";
        assertFalse(pda.accepts(lexer.tokenize(doc)));
    }

    @Test
    void test_over_close(){
        String doc = "<root></root></root>";
        assertFalse(pda.accepts(lexer.tokenize(doc)));
    }

    @Test
    void test_random_tags(){
        String doc = "<rpg><game>Baldurs Gate 3</game><score>10</score></rpg>";
        assertTrue(pda.accepts(lexer.tokenize(doc)));
    }

    @Test
    void test_uncorrect_synth(){
        String doc = "text</text2";
        assertThrows(IllegalArgumentException.class, () -> pda.accepts(lexer.tokenize(doc)));
    }

    @Test
    void test_just_text(){
        String doc = "text";
        assertFalse(pda.accepts(lexer.tokenize(doc)));
    }
}
