package com.scammers;

import java.util.*;

public class NFA<S, E> {
    private S startState;
    private Set<S> acceptingStates = new HashSet<>();
    private final Map<S, Map<E, Set<S>>> transitions = new HashMap<>();
    private final Map<S, Set<S>> epsilonTransitions = new HashMap<>();

    public NFA(S state) {
        this.startState = state;
    }
    public void addTransition(S from, E event, S to) {
        transitions
                .computeIfAbsent(from, k -> new HashMap<>())
                .computeIfAbsent(event, k -> new HashSet<>())
                .add(to);
    }
    public void addEpsilonTransition(S from, S to) {
        epsilonTransitions.computeIfAbsent(from, k -> new HashSet<>()).add(to);
    }

    public void addAcceptingState(S state) {
        acceptingStates.add(state);
    }
    public boolean isAcceptingState(S state) {
        return acceptingStates.contains(state);
    }
    public void removeAcceptingState(S s) { acceptingStates.remove(s); }

    public void removeState(S state) {
        transitions.remove(state);
        acceptingStates.remove(state);
        transitions.values().forEach(map -> {
            map.values().forEach(set -> set.remove(state));
        });
    }

    public Map<S, Set<E>> getTransitionsTo(S targetState) {
        Map<S, Set<E>> result = new HashMap<>();
        transitions.forEach((from, transMap) -> {
            transMap.forEach((symbol, targets) -> {
                if (targets.contains(targetState)) {
                    result.putIfAbsent(from, new HashSet<>());
                    result.get(from).add(symbol);
                }
            });
        });
        return result;
    }

    private Set<S> epsilonClosure(Set<S> states) {
        Set<S> closure = new HashSet<>(states);
        Stack<S> stack = new Stack<>();
        stack.addAll(states);

        while (!stack.isEmpty()) {
            S state = stack.pop();
            Set<S> eps = epsilonTransitions.get(state);
            if (eps != null) {
                for (S next : eps) {
                    if (!closure.contains(next)) {
                        closure.add(next);
                        stack.push(next);
                    }
                }
            }
        }
        return closure;
    }

    public boolean accepts(List<E> input) {
        Set<S> currentStates = epsilonClosure(Set.of(startState));

        for (E symbol : input) {
            Set<S> nextStates = new HashSet<>();
            for (S state : currentStates) {
                Map<E, Set<S>> stateTransitions = transitions.get(state);
                if (stateTransitions != null && stateTransitions.containsKey(symbol)) {
                    nextStates.addAll(stateTransitions.get(symbol));
                }
            }
            currentStates = epsilonClosure(nextStates);
        }
        for (S state : currentStates) {
            if (acceptingStates.contains(state)) {
                return true;
            }
        }
        return false;
    }

    public int getStateCount() {
        Set<S> all = new HashSet<>();
        all.add(startState);
        all.addAll(transitions.keySet());
        all.addAll(epsilonTransitions.keySet());
        return all.size();
    }

    public void merge(NFA<S, E> other) {
        this.transitions.putAll(other.transitions);
        this.epsilonTransitions.putAll(other.epsilonTransitions);
        this.acceptingStates.addAll(other.acceptingStates);
    }

    public S getStartState() {
        return startState;
    }

    public Set<S> getAcceptingStates() {
        return acceptingStates;
    }

    public Map<S, Map<E, Set<S>>> getTransitions() {
        return transitions;
    }

    public Map<S, Set<S>> getEpsilonTransitions() {
        return epsilonTransitions;
    }

    public Set<S> getAllStates() {
        Set<S> all = new HashSet<>();
        all.add(startState);
        all.addAll(acceptingStates);
        all.addAll(transitions.keySet());
        all.addAll(epsilonTransitions.keySet());

        for (Map<E, Set<S>> map : transitions.values()) {
            for (Set<S> targets : map.values()) {
                all.addAll(targets);
            }
        }
        for (Set<S> targets : epsilonTransitions.values()) {
            all.addAll(targets);
        }
        return all;
    }

    public void fuseStates(S stateToRemove, S stateToKeep) {
        if (transitions.containsKey(stateToRemove)) {
            Map<E, Set<S>> trans = transitions.remove(stateToRemove);
            Map<E, Set<S>> keepTrans = transitions.computeIfAbsent(stateToKeep, k -> new HashMap<>());
            for (var entry : trans.entrySet()) {
                keepTrans.computeIfAbsent(entry.getKey(), k -> new HashSet<>()).addAll(entry.getValue());
            }
        }

        if (epsilonTransitions.containsKey(stateToRemove)) {
            Set<S> eps = epsilonTransitions.remove(stateToRemove);
            epsilonTransitions.computeIfAbsent(stateToKeep, k -> new HashSet<>()).addAll(eps);
        }

        for (var map : transitions.values()) {
            for (var set : map.values()) {
                if (set.remove(stateToRemove)) {
                    set.add(stateToKeep);
                }
            }
        }

        for (var set : epsilonTransitions.values()) {
            if (set.remove(stateToRemove)) {
                set.add(stateToKeep);
            }
        }

        if (acceptingStates.remove(stateToRemove)) {
            acceptingStates.add(stateToKeep);
        }
    }
}
