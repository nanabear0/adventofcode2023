package save.santa.day08;

import org.javatuples.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        part01();
        part02();
    }

    private static long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    private static long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    public static void part02() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day08-input.txt");
        assert resource != null;
        var hands = Files.lines(Path.of(resource.getFile()))
                .toList();
        var moves = hands.get(0);
        Map<String, Pair<String, String>> paths = new HashMap<>();

        hands.stream().skip(2).forEach((line) -> {
            Matcher matcher = Pattern.compile("(\\S+) = \\((\\S+), (\\S+)\\)").matcher(line);
            matcher.find();
            paths.put(matcher.group(1), Pair.with(matcher.group(2), matcher.group(3)));
        });

        List<String> currentSet = paths.keySet().stream().filter(p -> p.endsWith("A")).toList();

        long result = currentSet.stream().map(current -> {
            var steps = 0;
            do {
                var path = paths.get(current);
                current = moves.charAt(steps % moves.length()) == 'L' ? path.getValue0() : path.getValue1();
                steps++;
            } while (!current.endsWith("Z"));
            return (long) steps;
        }).reduce(Main::lcm).orElseThrow();

        System.out.println("part02: " + result);
    }

    public static void part01() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day08-input.txt");
        assert resource != null;
        var hands = Files.lines(Path.of(resource.getFile()))
                .toList();
        var moves = hands.get(0);
        Map<String, Pair<String, String>> paths = new HashMap<>();

        hands.stream().skip(2).forEach((line) -> {
            Matcher matcher = Pattern.compile("(\\S+) = \\((\\S+), (\\S+)\\)").matcher(line);
            matcher.find();
            paths.put(matcher.group(1), Pair.with(matcher.group(2), matcher.group(3)));
        });

        var current = "AAA";
        var target = "ZZZ";
        var steps = 0;
        do {
            var path = paths.get(current);
            current = moves.charAt(steps % moves.length()) == 'L' ? path.getValue0() : path.getValue1();
            steps++;
        } while (!target.equals(current));

        System.out.println("part01: " + steps);
    }
}