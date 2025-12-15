package com.scammers.lab5;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlLexer {
    private static final Pattern XML_PATTERN = Pattern.compile(
            "(<!--.*?-->)|(<\\?.*?\\?>)" +
                    "|</([a-zA-Z0-9_\\-\\.:]+)>" +
                    "|<([a-zA-Z0-9_\\-\\.:]+)[^>]*>" +
                    "|([^<]+)" +
                    "|(<)",
            Pattern.DOTALL
    );

    public List<XmlToken> tokenize(String input) {
        List<XmlToken> tokens = new ArrayList<>();
        Matcher matcher = XML_PATTERN.matcher(input);

        while (matcher.find()) {
            if (matcher.group(1) != null || matcher.group(2) != null) {
                continue;
            }

            if (matcher.group(3) != null) {
                tokens.add(new XmlToken(XmlToken.Type.CLOSE_TAG, matcher.group(3)));
            } else if (matcher.group(4) != null) {
                tokens.add(new XmlToken(XmlToken.Type.OPEN_TAG, matcher.group(4)));
            } else if (matcher.group(5) != null) {
                String text = matcher.group(5).trim();
                if (!text.isEmpty()) {
                    tokens.add(new XmlToken(XmlToken.Type.TEXT, text));
                }
            } else if (matcher.group(6) != null) {
                throw new IllegalArgumentException("Обнаружен сломанный тег или лишний символ '<' на позиции " + matcher.start());
            }
        }
        return tokens;
    }
}