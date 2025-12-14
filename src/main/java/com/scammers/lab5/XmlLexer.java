package com.scammers.lab5;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlLexer {
    private static final Pattern XML_PATTERN = Pattern.compile("</([a-zA-Z0-9_\\-]+)>|<([a-zA-Z0-9_\\-]+)>|([^<]+)");

    public List<XmlToken> tokenize(String input) {
        List<XmlToken> tokens = new ArrayList<>();
        Matcher matcher = XML_PATTERN.matcher(input);

        while (matcher.find()) {
            String closeTag = matcher.group(1);
            String openTag = matcher.group(2);
            String text = matcher.group(3);

            if (closeTag != null) {
                tokens.add(new XmlToken(XmlToken.Type.CLOSE_TAG, closeTag));
            } else if (openTag != null) {
                tokens.add(new XmlToken(XmlToken.Type.OPEN_TAG, openTag));
            } else if (text != null && !text.trim().isEmpty()) {
                tokens.add(new XmlToken(XmlToken.Type.TEXT, text.trim()));
            }
        }
        return tokens;
    }
}