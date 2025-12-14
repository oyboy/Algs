package com.scammers.lab5;

public class XmlToken {
    public enum Type { OPEN_TAG, CLOSE_TAG, TEXT }

    private final Type type;
    private final String value;

    public XmlToken(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() { return type; }
    public String getValue() { return value; }

    @Override
    public String toString() {
        return String.format("[%s: %s]", type, value);
    }
}