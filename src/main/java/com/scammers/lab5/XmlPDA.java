package com.scammers.lab5;

import java.util.List;
import java.util.Stack;

public class XmlPDA {
    private enum State {
        Q0,
        Q_ERROR
    }

    private State currentState = State.Q0;
    private final Stack<String> stack = new Stack<>();

    public boolean accepts(List<XmlToken> inputChain) {
        currentState = State.Q0;
        stack.clear();

        System.out.println("Запуск PDA...");

        for (XmlToken token : inputChain) {
            if (currentState == State.Q_ERROR) break;
            step(token);
        }

        boolean result = (currentState == State.Q0) && stack.isEmpty();

        if (!stack.isEmpty()) System.out.println("  [Result]: Стек не пуст: " + stack);
        if (currentState == State.Q_ERROR) System.out.println("  [Result]: Автомат в состоянии ошибки.");

        if (result) {
            System.out.println("[Result]: цепочка принята, стек: " + stack);
        }
        return result;
    }

    private void step(XmlToken token) {
        System.out.printf("  State: %-7s | Token: %-15s | Stack: %s%n", currentState, token, stack);

        switch (currentState) {
            case Q0:
                if (token.getType() == XmlToken.Type.OPEN_TAG) {
                    // Правило: q0, <TAG>, Z -> q0, TAG Z (Push)
                    stack.push(token.getValue());
                    currentState = State.Q0;
                }
                else if (token.getType() == XmlToken.Type.CLOSE_TAG) {
                    // Правило: q0, </TAG>, TAG -> q0, eps (Pop)
                    if (stack.isEmpty()) {
                        System.out.println("    -> Ошибка: Стек пуст, нечего закрывать.");
                        currentState = State.Q_ERROR;
                    } else {
                        String top = stack.peek();
                        if (top.equals(token.getValue())) {
                            stack.pop();
                            currentState = State.Q0;
                        } else {
                            System.out.println("    -> Ошибка: Несовпадение тегов. Ждали </" + top + ">");
                            currentState = State.Q_ERROR;
                        }
                    }
                }
                else if (token.getType() == XmlToken.Type.TEXT) {
                    // Правило: q0, text, Z -> q0, Z (игнор)
                    currentState = State.Q0;
                }
                break;

            case Q_ERROR:
                break;
        }
    }
}