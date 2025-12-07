package com.scammers.lab3;
import com.scammers.DFA;

import java.util.*;

public class DFATools {

    //State a b
    /*q1   q1 q4
      q2   q2 q4
      q3   q1 q0
      q4   q1 q0
        */
    public static <S, E> DFA<Set<S>, E> minimizeHopcroft(DFA<S, E> dfa) {
        Set<S> allStates = dfa.getStates();
        Set<E> alphabet = dfa.getAlphabet();
        Set<S> accepting = dfa.getAcceptingStates();
        Set<S> nonAccepting = new HashSet<>(allStates);
        nonAccepting.removeAll(accepting);

        //  недостижимые состояния
        allStates = removeUnreachable(dfa);
        //  множества после удаления лишнего
        final Set<S> finalStates = new HashSet<>(allStates);
        accepting.retainAll(finalStates);
        nonAccepting.retainAll(finalStates);

        // 2. Инициализация разбиения (P) и множества ожидающих обработки (W)
        Set<Set<S>> partition = new HashSet<>();
        if (!accepting.isEmpty()) partition.add(accepting); //q0,q1,q2,q3
        if (!nonAccepting.isEmpty()) partition.add(nonAccepting); //q4

        Queue<Set<S>> waiting = new LinkedList<>(partition);

        // 3. Основной цикл Хопкрофта
        while (!waiting.isEmpty()) {
            Set<S> A = waiting.poll(); //

            for (E c : alphabet) {
                // X = множество состояний, которые переходят в A по символу c
                Set<S> X = new HashSet<>();
                for (S state : finalStates) {
                    S target = dfa.getTransition(state, c);
                    if (target != null && A.contains(target)) {
                        X.add(state);
                    }
                }

                // Разбиение существующих групп множеством X
                Set<Set<S>> nextPartition = new HashSet<>();
                for (Set<S> Y : partition) {
                    Set<S> intersection = new HashSet<>(Y);
                    intersection.retainAll(X); // Y ∩ X

                    Set<S> difference = new HashSet<>(Y);
                    difference.removeAll(X);   // Y \ X

                    if (!intersection.isEmpty() && !difference.isEmpty()) {
                        nextPartition.add(intersection);
                        nextPartition.add(difference);

                        // Обновление очереди W
                        if (waiting.contains(Y)) {
                            waiting.remove(Y);
                            waiting.add(intersection);
                            waiting.add(difference);
                        } else {
                            if (intersection.size() <= difference.size()) {
                                waiting.add(intersection);
                            } else {
                                waiting.add(difference);
                            }
                        }
                    } else {
                        nextPartition.add(Y);
                    }
                }
                partition = nextPartition;
            }
        }

        // 4. Построение нового минимизированного автомата
        return buildMinimisedDFA(dfa, partition, alphabet);
    }

    public static <S1, S2, E> boolean areEquivalent(DFA<S1, E> dfa1, DFA<S2, E> dfa2) {
        S1 start1 = dfa1.getCurrentState();
        S2 start2 = dfa2.getCurrentState();

        Queue<Pair<S1, S2>> queue = new LinkedList<>();
        queue.add(new Pair<>(start1, start2));

        Set<String> visited = new HashSet<>();
        visited.add(start1.toString() + "," + start2.toString());

        while (!queue.isEmpty()) {
            Pair<S1, S2> curr = queue.poll();
            S1 s1 = curr.first;
            S2 s2 = curr.second;

            boolean acc1 = dfa1.getAcceptingStates().contains(s1);
            boolean acc2 = dfa2.getAcceptingStates().contains(s2);
            if (acc1 != acc2) {
                return false;
            }

            Set<E> jointAlphabet = new HashSet<>(dfa1.getAlphabet());
            jointAlphabet.addAll(dfa2.getAlphabet());

            for (E symbol : jointAlphabet) {
                S1 next1 = dfa1.getTransition(s1, symbol);
                S2 next2 = dfa2.getTransition(s2, symbol);

                if ((next1 == null && next2 != null) || (next1 != null && next2 == null)) {
                    return false;
                }

                if (next1 != null && next2 != null) {
                    String key = next1 + "," + next2;
                    if (!visited.contains(key)) {
                        visited.add(key);
                        queue.add(new Pair<>(next1, next2));
                    }
                }
            }
        }

        return true;
    }

    private static <S, E> Set<S> removeUnreachable(DFA<S, E> dfa) {
        Set<S> reachable = new HashSet<>();
        Queue<S> queue = new LinkedList<>();
        queue.add(dfa.getCurrentState());
        reachable.add(dfa.getCurrentState());

        while(!queue.isEmpty()) {
            S current = queue.poll();
            for (E sym : dfa.getAlphabet()) {
                S next = dfa.getTransition(current, sym);
                if (next != null && !reachable.contains(next)) {
                    reachable.add(next);
                    queue.add(next);
                }
            }
        }
        return reachable;
    }

    private static <S, E> DFA<Set<S>, E> buildMinimisedDFA(DFA<S, E> original, Set<Set<S>> partition, Set<E> alphabet) {
        S oldStart = original.getCurrentState();
        Set<S> newStart = null;
        for (Set<S> group : partition) {
            if (group.contains(oldStart)) {
                newStart = group;
                break;
            }
        }

        DFA<Set<S>, E> minDFA = new DFA<>(newStart);

        for (E sym : alphabet) minDFA.addSymbol(sym);

        // Строим переходы и принимающие состояния
        for (Set<S> groupSource : partition) {
            // Проверяем, является ли группа принимающей (достаточно проверить одного представителя)
            S representative = groupSource.iterator().next();
            if (original.getAcceptingStates().contains(representative)) {
                minDFA.addAcceptingState(groupSource);
            }

            for (E sym : alphabet) {
                S targetOld = original.getTransition(representative, sym);
                if (targetOld != null) {
                    // Ищем группу, к которой принадлежит targetOld
                    for (Set<S> groupTarget : partition) {
                        if (groupTarget.contains(targetOld)) {
                            minDFA.addTransition(groupSource, sym, groupTarget);
                            break;
                        }
                    }
                }
            }
        }
        return minDFA;
    }

    // Простая пара для BFS
    private static class Pair<A, B> {
        A first; B second;
        Pair(A first, B second) { this.first = first; this.second = second; }
    }
}
