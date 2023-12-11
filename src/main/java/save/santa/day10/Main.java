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

        List<Pair<Integer, Integer>> loop = new ArrayList<>();
        loop.add(start);
        if (List.of('F', '|', '7').contains(map.getOrDefault(addPoints(start, 0, -1), '.')))
            loop.add(addPoints(start, 0, -1));
        else if (List.of('L', '|', 'J').contains(map.getOrDefault(addPoints(start, 0, 1), '.')))
            loop.add(addPoints(start, 0, 1));
        else if (List.of('F', '-', 'L').contains(map.getOrDefault(addPoints(start, -1, 0), '.')))
            loop.add(addPoints(start, -1, 0));
        else if (List.of('J', '-', '7').contains(map.getOrDefault(addPoints(start, 1, 0), '.')))
            loop.add(addPoints(start, 1, 0));

        do {
            var current = loop.getLast();
            var previous = loop.get(loop.size() - 2);
            loop.add(switch (findEntranceDirection(current, previous)) {
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
            });
        } while (map.get(loop.getLast()) != 'S');

        System.out.println("part01: " + loop.size() / 2);

        int area = 0;
        for (int i = 0; i < loop.size(); i++) {
            var p1 = loop.get(i);
            var p2 = loop.get((i + 1) % loop.size());
            area += p1.getValue0() * p2.getValue1() - p1.getValue1() * p2.getValue0();
        }
        area /= 2;

        int pointsInside = area + 1 - loop.size() / 2;
        System.out.println("part02: " + pointsInside);
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