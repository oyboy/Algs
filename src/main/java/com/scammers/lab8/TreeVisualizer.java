package com.scammers.lab8;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TreeVisualizer {
    public static String toDOT(ParseNode root) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph ParseTree {\n");
        sb.append("    node [shape=circle, fontname=\"Arial\"];\n");
        sb.append("    edge [color=\"#555555\"];\n");

        appendDOTRecursive(root, sb);

        sb.append("}");
        return sb.toString();
    }

    private static void appendDOTRecursive(ParseNode node, StringBuilder sb) {
        sb.append(String.format("    node%d [label=\"%s\"];\n", node.getId(), node.getLabel()));

        for (ParseNode child : node.getChildren()) {
            sb.append(String.format("    node%d -> node%d;\n", node.getId(), child.getId()));
            appendDOTRecursive(child, sb);
        }
    }

    public static void toCSV(ParseNode root, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("id,label,parent_id");
            appendCSVRecursive(root, writer, -1);
            System.out.println("CSV сохранен: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void appendCSVRecursive(ParseNode node, PrintWriter writer, int parentId) {
        String pIdStr = (parentId == -1) ? "" : String.valueOf(parentId);
        writer.printf("%d,%s,%s%n", node.getId(), node.getLabel(), pIdStr);

        for (ParseNode child : node.getChildren()) {
            appendCSVRecursive(child, writer, node.getId());
        }
    }
}
