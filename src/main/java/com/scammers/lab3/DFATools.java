package com.scammers.lab3;
import com.scammers.DFA;

import java.util.*;

public class DFATools {
    public static <S, E> DFA<Set<S>, E> minimizeHopcroft(DFA<S, E> dfa) {
        Set<S> allStates = dfa.getStates();
        Set<E> alphabet = dfa.getAlphabet();
        Set<S> accepting = dfa.getAcceptingStates();
        Set<S> nonAccepting = new HashSet<>(allStates);
        nonAccepting.removeAll(accepting);

        System.out.println("Все состояния (Q): " + allStates);
        System.out.println("Алфавит (Σ): " + alphabet);
        System.out.println("Принимающие (F): " + accepting);
        System.out.println("Непринимающие (Q \\ F): " + nonAccepting);

        allStates = removeUnreachable(dfa);
        final Set<S> finalStates = new HashSet<>(allStates);
        accepting.retainAll(finalStates);
        nonAccepting.retainAll(finalStates);

        System.out.println("Достижимые состояния: " + finalStates);
        System.out.println("Принимающие (достижимые): " + accepting);
        System.out.println("Непринимающие (достижимые): " + nonAccepting);

        // 2. Инициализация разбиения (P) и множества ожидающих обработки (W)
        Set<Set<S>> partition = new HashSet<>();
        if (!accepting.isEmpty()) partition.add(new HashSet<>(accepting));
        if (!nonAccepting.isEmpty()) partition.add(new HashSet<>(nonAccepting));

        Queue<Set<S>> waiting = new LinkedList<>(partition);

        System.out.println("\n=== Инициализация ===");
        System.out.println("Начальное разбиение P: " + partition);
        System.out.println("Начальная очередь W: " + waiting);

        // 3. Основной цикл Хопкрофта
        int iteration = 0;
        while (!waiting.isEmpty()) {
            iteration++;
            System.out.println("\n=== Итерация #" + iteration + " ===");
            System.out.println("Текущее разбиение P: " + partition);
            System.out.println("Текущая очередь W: " + waiting);

            Set<S> A = waiting.poll(); //
            System.out.println("Взят из W блок A: " + A);

            for (E c : alphabet) {
                System.out.println("\n  --- Символ c = " + c + " ---");

                // X = множество состояний, которые переходят в A по символу c
                Set<S> X = new HashSet<>();
                for (S state : finalStates) {
                    S target = dfa.getTransition(state, c);
                    if (target != null && A.contains(target)) {
                        X.add(state);
                    }
                }
                System.out.println("  X (состояния, которые по '" + c + "' попадают в A): " + X);

                // Разбиение существующих групп множеством X
                Set<Set<S>> nextPartition = new HashSet<>();
                for (Set<S> Y : partition) {
                    System.out.println("   Блок Y: " + Y);

                    Set<S> intersection = new HashSet<>(Y);
                    intersection.retainAll(X); // Y ∩ X

                    Set<S> difference = new HashSet<>(Y);
                    difference.removeAll(X);   // Y \ X

                    System.out.println("      Y ∩ X = " + intersection);
                    System.out.println("      Y \\ X = " + difference);

                    if (!intersection.isEmpty() && !difference.isEmpty()) {
                        System.out.println("      -> Блок Y разбивается на два подблока.");
                        nextPartition.add(intersection);
                        nextPartition.add(difference);

                        // Обновление очереди W
                        if (waiting.contains(Y)) {
                            System.out.println("      Y был в очереди W, заменяем его на оба подблока.");
                            waiting.remove(Y);
                            waiting.add(intersection);
                            waiting.add(difference);
                        } else {
                            // добавляем в W меньший подблок (оптимизация)
                            if (intersection.size() <= difference.size()) {
                                System.out.println("      Y не был в W, добавляем в W меньший подблок: " + intersection);
                                waiting.add(intersection);
                            } else {
                                System.out.println("      Y не был в W, добавляем в W меньший подблок: " + difference);
                                waiting.add(difference);
                            }
                        }
                        System.out.println("      Очередь W теперь: " + waiting);
                    } else {
                        System.out.println("      -> Блок Y НЕ разбивается этим X.");
                        nextPartition.add(Y);
                    }
                }
                partition = nextPartition;
                System.out.println("  Новое разбиение P после символа '" + c + "': " + partition);
            }
        }

        System.out.println("\n=== Конец алгоритма ===");
        System.out.println("Финальное разбиение P (классы эквивалентности): " + partition);

        // 4. Построение нового минимизированного автомата
        DFA<Set<S>, E> result = buildMinimisedDFA(dfa, partition, alphabet);
        System.out.println("Состояния минимального ДКА (как множества старых): " + partition);
        System.out.println("Начальное состояние минимального ДКА: " + result.getCurrentState());
        System.out.println("Принимающие состояния минимального ДКА: " + result.getAcceptingStates());
        return result;
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

    private static <S, E> DFA<Set<S>, E> buildMinimisedDFA(DFA<S, E> original,
                                                           Set<Set<S>> partition,
                                                           Set<E> alphabet) {
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

        for (Set<S> groupSource : partition) {
            S representative = groupSource.iterator().next();
            if (original.getAcceptingStates().contains(representative)) {
                minDFA.addAcceptingState(groupSource);
            }

            for (E sym : alphabet) {
                S targetOld = original.getTransition(representative, sym);
                if (targetOld != null) {
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

    private static class Pair<A, B> {
        A first; B second;
        Pair(A first, B second) { this.first = first; this.second = second; }
    }
}