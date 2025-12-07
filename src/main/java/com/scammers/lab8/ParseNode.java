package com.scammers.lab8;

import java.util.ArrayList;
import java.util.List;

public class ParseNode {
    private static int GLOBAL_ID = 0;

    private final int id;
    private final String label;
    private final List<ParseNode> children;

    public ParseNode(String label) {
        this.id = GLOBAL_ID++;
        this.label = label;
        this.children = new ArrayList<>();
    }

    public void addChild(ParseNode child) {
        children.add(child);
    }

    public int getId() { return id; }
    public String getLabel() { return label; }
    public List<ParseNode> getChildren() { return children; }
}
