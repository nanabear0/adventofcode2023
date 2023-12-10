package save.santa.day10;

import org.javatuples.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        part01();
    }

    public static void part01() throws IOException {
        URL resource = save.santa.day08.Main.class.getClassLoader().getResource("day10-input.txt");
        assert resource != null;
        var lines = Files.lines(Path.of(resource.getFile()))
                .toList();

        Map<Pair<Integer, Integer>, Character> map = new HashMap<>();
        Pair<Integer, Integer> start = Pair.with(-1, -1);
        for (int y = 0; y < lines.size(); y++) {
            var line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == 'S') start = Pair.with(x, y);
                map.put(Pair.with(x, y), line.charAt(x));
            }
        }

        Set<Pair<Integer, Integer>> loopSet = new HashSet<>();
        loopSet.add(start);
        Pair<Integer, Integer> current = start;
        Pair<Integer, Integer> previous = start;
        if (List.of('F', '|', '7').contains(map.getOrDefault(addPoints(start, 0, -1), '.')))
            current = addPoints(start, 0, -1);
        else if (List.of('L', '|', 'J').contains(map.getOrDefault(addPoints(start, 0, 1), '.')))
            current = addPoints(start, 0, 1);
        else if (List.of('F', '-', 'L').contains(map.getOrDefault(addPoints(start, -1, 0), '.')))
            current = addPoints(start, -1, 0);
        else if (List.of('J', '-', '7').contains(map.getOrDefault(addPoints(start, 1, 0), '.')))
            current = addPoints(start, 1, 0);

        Set<Pair<Integer, Integer>> onMyRight = new HashSet<>();
        Set<Pair<Integer, Integer>> onMyLeft = new HashSet<>();

        do {
            loopSet.add(current);
            var tmp = current;
            int myDirection = findEntranceDirection(current, previous);
            switch (map.get(current)) {
                case '-':
                    onMyRight.add(addPoints(current, 0, myDirection == 1 ? -1 : 1));
                    onMyLeft.add(addPoints(current, 0, myDirection == 1 ? 1 : -1));
                    break;
                case '|':
                    onMyRight.add(addPoints(current, myDirection == 0 ? -1 : 1, 0));
                    onMyLeft.add(addPoints(current, myDirection == 0 ? 1 : -1, 0));
                    break;
                case 'L':
                    if (myDirection == 0) {
                        onMyRight.add(addPoints(current, -1, 0));
                        onMyRight.add(addPoints(current, 0, 1));
                    } else {
                        onMyLeft.add(addPoints(current, -1, 0));
                        onMyLeft.add(addPoints(current, 0, 1));
                    }
                    break;
                case 'J':
                    if (myDirection == 3) {
                        onMyRight.add(addPoints(current, 1, 0));
                        onMyRight.add(addPoints(current, 0, 1));
                    } else {
                        onMyLeft.add(addPoints(current, 1, 0));
                        onMyLeft.add(addPoints(current, 0, 1));
                    }
                    break;
                case 'F':
                    if (myDirection == 1) {
                        onMyRight.add(addPoints(current, -1, 0));
                        onMyRight.add(addPoints(current, 0, -1));
                    } else {
                        onMyLeft.add(addPoints(current, -1, 0));
                        onMyLeft.add(addPoints(current, 0, -1));
                    }
                    break;
                case '7':
                    if (myDirection == 2) {
                        onMyRight.add(addPoints(current, 1, 0));
                        onMyRight.add(addPoints(current, 0, -1));
                    } else {
                        onMyLeft.add(addPoints(current, 1, 0));
                        onMyLeft.add(addPoints(current, 0, -1));
                    }
                    break;
            }

            current = switch (myDirection) {
                case 0 -> // from top
                        switch (map.get(current)) {
                            case '|' -> addPoints(current, 0, 1);
                            case 'J' -> addPoints(current, -1, 0);
                            case 'L' -> addPoints(current, 1, 0);
                            default -> current;
                        };
                case 1 ->  // from right
                        switch (map.get(current)) {
                            case '-' -> addPoints(current, -1, 0);
                            case 'L' -> addPoints(current, 0, -1);
                            case 'F' -> addPoints(current, 0, 1);
                            default -> current;
                        };
                case 2 ->  // from bottom
                        switch (map.get(current)) {
                            case '|' -> addPoints(current, 0, -1);
                            case '7' -> addPoints(current, -1, 0);
                            case 'F' -> addPoints(current, 1, 0);
                            default -> current;
                        };
                case 3 ->  // from left
                        switch (map.get(current)) {
                            case '-' -> addPoints(current, 1, 0);
                            case 'J' -> addPoints(current, 0, -1);
                            case '7' -> addPoints(current, 0, 1);
                            default -> current;
                        };
                default -> current;
            };
            previous = tmp;
        } while (!current.equals(start));

        System.out.println("part01: " + loopSet.size() / 2);

        onMyRight.retainAll(map.keySet());
        onMyRight.removeAll(loopSet);
        onMyRight = addMeNeighbours(onMyRight, map, loopSet);

        onMyLeft.retainAll(map.keySet());
        onMyLeft.removeAll(loopSet);
        onMyLeft = addMeNeighbours(onMyLeft, map, loopSet);
        System.out.println("part02: " + onMyRight.size() + " or " + onMyLeft.size());
    }

    public static Set<Pair<Integer, Integer>> addMeNeighbours(Set<Pair<Integer, Integer>> startSet, Map<Pair<Integer, Integer>, Character> map, Set<Pair<Integer, Integer>> loopSet) {
        var currentSet = new HashSet<>(startSet);
        while (true) {
            Set<Pair<Integer, Integer>> newSet = new HashSet<>();
            for (var elem : currentSet) {
                newSet.add(addPoints(elem, 0,1));
                newSet.add(addPoints(elem, 0,-1));
                newSet.add(addPoints(elem, 0,0));
                newSet.add(addPoints(elem, 1,1));
                newSet.add(addPoints(elem, 1,-1));
                newSet.add(addPoints(elem, 1,0));
                newSet.add(addPoints(elem, -1,1));
                newSet.add(addPoints(elem, -1,-1));
                newSet.add(addPoints(elem, -1,0));
            }
            newSet.retainAll(map.keySet());
            newSet.removeAll(loopSet);
            newSet.removeAll(currentSet);
            if(newSet.isEmpty()) break;
            currentSet.addAll(newSet);
        }
        return currentSet;
    }

    public static int findEntranceDirection(Pair<Integer, Integer> current, Pair<Integer, Integer> previous) {
        int yOffset = current.getValue1() - previous.getValue1();
        int xOffset = current.getValue0() - previous.getValue0();

        if (yOffset == 0) return xOffset > 0 ? 3 : 1;
        return yOffset > 0 ? 0 : 2;
    }

    public static Pair<Integer, Integer> addPoints(Pair<Integer, Integer> point, int x, int y) {
        return Pair.with(point.getValue0() + x, point.getValue1() + y);
    }
}