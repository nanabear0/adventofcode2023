package save.santa.day23;

import org.apache.commons.lang3.time.StopWatch;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("part01: " + doPart(false));
        System.out.println("part02: " + doPart(true));
    }

    public static HashMap<Character, Pair<Integer, Integer>> doDir = new HashMap<>() {{
        put('>', Pair.with(1, 0));
        put('<', Pair.with(-1, 0));
        put('^', Pair.with(0, -1));
        put('v', Pair.with(0, 1));
    }};

    public static long doPart(boolean noSlope) throws IOException {
        StopWatch watch = new StopWatch();
        watch.start();
        URL resource = Main.class.getClassLoader().getResource("day23-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();

        Map<Pair<Integer, Integer>, Character> charMap = new HashMap<>();
        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(y).length(); x++) {
                charMap.put(Pair.with(x, y), lines.get(y).charAt(x));
            }
        }
        Pair<Integer, Integer> start = Pair.with(1, 0);
        Pair<Integer, Integer> end = Pair.with(lines.getLast().length() - 2, lines.size() - 1);

        Set<Pair<Integer, Integer>> intersections = charMap.entrySet().stream()
                .filter(entry -> isFreeSpace(entry.getValue()))
                .filter(entry ->
                        Stream.of(Pair.with(0, -1), Pair.with(0, 1), Pair.with(-1, 0), Pair.with(1, 0))
                                .map(dir ->
                                        addDir(entry.getKey(), dir))
                                .filter(pos ->
                                        isFreeSpace(charMap.getOrDefault(pos, 'X'))).count() > 2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        intersections.add(start);
        intersections.add(end);

        HashMap<Pair<Integer, Integer>, HashMap<Pair<Integer, Integer>, Integer>> distanceToNeighbours = new HashMap<>();
        for (var intersection : intersections) {
            distanceToNeighbours.put(intersection, new HashMap<>());
            for (var probe : Stream.of(Pair.with(0, -1), Pair.with(0, 1), Pair.with(-1, 0), Pair.with(1, 0))
                    .map(dir -> addDir(intersection, dir))
                    .filter(target -> isFreeSpace(charMap.getOrDefault(target, 'X'))).toList()) {
                var current = probe;
                var last = intersection;
                int distance = 1;
                Pair<Integer, Integer> nextDir = null;
                while (true) {
                    Pair<Integer, Integer> finalCurrent = current;
                    Pair<Integer, Integer> finalLast = last;
                    var option = (nextDir != null ? Stream.of(nextDir) : Stream.of(Pair.with(0, -1), Pair.with(0, 1), Pair.with(-1, 0), Pair.with(1, 0)))
                            .map(dir -> addDir(finalCurrent, dir))
                            .filter(target -> isFreeSpace(charMap.getOrDefault(target, 'X')))
                            .filter(target -> !target.equals(finalLast))
                            .findFirst();
                    if (option.isEmpty()) break;
                    nextDir = !noSlope && List.of('.', 'v', '^', '>', '<').contains(charMap.get(option.get())) ?
                            doDir.get(charMap.get(option.get())) : null;
                    distance++;
                    last = current;
                    current = option.get();
                    if (intersections.contains(option.get())) {
                        int finalDistance = distance;
                        distanceToNeighbours.get(intersection)
                                .compute(option.get(), (key, oldValue) -> oldValue == null ? finalDistance : Math.max(oldValue, finalDistance));
                        break;
                    }
                }
            }
        }

        Stack<Triplet<HashSet<Pair<Integer, Integer>>, Pair<Integer, Integer>, Integer>> currentHikes = new Stack<>() {{
            push(Triplet.with(new HashSet<>() {{
                add(start);
            }}, start, 0));
        }};

        var longestHike = 0L;
        while (!currentHikes.isEmpty()) {
            var currentHike = currentHikes.pop();
            var visitedNodes = currentHike.getValue0();
            var head = currentHike.getValue1();
            var currentDistance = currentHike.getValue2();

            if (head.equals(end)) {
                longestHike = Math.max(longestHike, currentDistance);
                continue;
            }

            for (var targetEntry : distanceToNeighbours.get(head).entrySet().stream()
                    .filter(entry -> !visitedNodes.contains(entry.getKey())).toList()) {
                currentHikes.add(Triplet.with(new HashSet<>() {
                                                  {
                                                      addAll(visitedNodes);
                                                      add(targetEntry.getKey());
                                                  }
                                              },
                        targetEntry.getKey(),
                        currentDistance + targetEntry.getValue()));
            }
        }

        return longestHike;
    }

    public static boolean isFreeSpace(Character c) {
        return List.of('.', 'v', '^', '>', '<').contains(c);
    }

    public static Pair<Integer, Integer> addDir(Pair<Integer, Integer> head, Pair<Integer, Integer> dir) {
        return Pair.with(head.getValue0() + dir.getValue0(), head.getValue1() + dir.getValue1());
    }
}