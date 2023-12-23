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
        part01();
        part02();
    }

    public static HashMap<Character, Pair<Integer, Integer>> doDir = new HashMap<>() {{
        put('>', Pair.with(1, 0));
        put('<', Pair.with(-1, 0));
        put('^', Pair.with(0, -1));
        put('v', Pair.with(0, 1));
    }};

    public static void part02() throws IOException {
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
                while (true) {
                    Pair<Integer, Integer> finalCurrent = current;
                    Pair<Integer, Integer> finalLast = last;
                    var option = Stream.of(Pair.with(0, -1), Pair.with(0, 1), Pair.with(-1, 0), Pair.with(1, 0))
                            .map(dir -> addDir(finalCurrent, dir))
                            .filter(target -> isFreeSpace(charMap.getOrDefault(target, 'X')))
                            .filter(target -> !target.equals(finalLast))
                            .findFirst();
                    if (option.isEmpty()) break;
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

        watch.stop();
        System.out.println("part01: " + longestHike + ", in:" + watch.formatTime());
    }

    public static void part01() throws IOException {
        StopWatch watch = new StopWatch();
        watch.start();
        URL resource = Main.class.getClassLoader().getResource("day23-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();

        Map<Pair<Integer, Integer>, Character> map = new HashMap<>();
        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(y).length(); x++) {
                map.put(Pair.with(x, y), lines.get(y).charAt(x));
            }
        }
        Pair<Integer, Integer> start = Pair.with(1, 0);
        Pair<Integer, Integer> end = Pair.with(lines.getLast().length() - 2, lines.size() - 1);

        int longestHike = Integer.MIN_VALUE;
        Stack<Pair<HashSet<Pair<Integer, Integer>>, Pair<Integer, Integer>>> currentHikes = new Stack<>() {{
            push(Pair.with(new HashSet<>() {{
                add(start);
            }}, start));
        }};

        while (!currentHikes.isEmpty()) {
            var currentHike = currentHikes.pop();
            var visitedNodes = currentHike.getValue0();
            var head = currentHike.getValue1();

            if (head.equals(end)) {
                longestHike = Math.max(longestHike, visitedNodes.size());
                continue;
            }

            for (var target : Stream.of(Pair.with(0, -1), Pair.with(0, 1), Pair.with(-1, 0), Pair.with(1, 0))
                    .map(dir -> addDir(head, dir))
                    .filter(target -> List.of('.', 'v', '^', '>', '<').contains(map.getOrDefault(target, 'X')))
                    .filter(target -> !visitedNodes.contains(target))
                    .toList()
            ) {
                if (map.get(target).equals('.')) {
                    currentHikes.add(Pair.with(new HashSet<>() {
                        {
                            addAll(visitedNodes);
                            add(target);
                        }
                    }, target));
                } else {
                    var next = addDir(target, doDir.get(map.get(target)));
                    if (next.equals(target) || map.get(next).equals('#') || visitedNodes.contains(next)) continue;
                    currentHikes.add(Pair.with(new HashSet<>() {
                        {
                            addAll(visitedNodes);
                            add(target);
                            add(next);
                        }
                    }, next));
                }
            }
        }
        watch.stop();
        System.out.println("part01: " + (longestHike - 1) + ", in:" + watch.formatTime());
    }

    public static boolean isFreeSpace(Character c) {
        return List.of('.', 'v', '^', '>', '<').contains(c);
    }

    public static Pair<Integer, Integer> addDir(Pair<Integer, Integer> head, Pair<Integer, Integer> dir) {
        return Pair.with(head.getValue0() + dir.getValue0(), head.getValue1() + dir.getValue1());
    }
}