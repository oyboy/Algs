package com.scammers;

import lombok.Getter;

import java.util.*;

@Getter
public class DFA<S, E> {
    private S currentState;
    private final Map<S, Map<E, S>> transitions = new HashMap<>();
    private final Set<S> acceptingStates = new HashSet<>();
    private final Set<E> alphabet = new  HashSet<>();

    public DFA(S state) {
        this.currentState = state;
    }
    public void addTransition(S from, E event, S to) {
        transitions.computeIfAbsent(from, k -> new HashMap<>()).put(event, to);
    }
    public void addAcceptingState(S state) {
        acceptingStates.add(state);
    }
    public void addSymbol(E symbol) {
        alphabet.add(symbol);
    }

    public void onEvent(E event) {
        Map<E, S> stateTransitions = transitions.get(currentState);
        if (stateTransitions != null && stateTransitions.containsKey(event)) {
            currentState = stateTransitions.get(event);
            System.out.println("Переход в состояние: " + currentState);
        } else {
            System.out.println("Нет перехода из состояния " + currentState + " по событию " + event);
        }
    }

    public Set<S> getStates() {
        Set<S> allStates = new HashSet<>();
        allStates.addAll(transitions.keySet());
        for (Map<E, S> trans : transitions.values()) {
            allStates.addAll(trans.values());
        }
        return allStates;
    }

    public S getTransition(S from, E symbol) {
        Map<E, S> stateTransitions = transitions.get(from);
        return stateTransitions != null ? stateTransitions.get(symbol) : null;
    }

    public boolean isInAcceptingState() {
        return acceptingStates.contains(currentState);
    }

    public boolean processChain(List<E> chain) {
        S initialState = currentState;
        for (E event : chain) {
            onEvent(event);
        }
        boolean result = isInAcceptingState();
        currentState = initialState;
        return result;
    }
}