package save.santa.day02;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        part01();
        part02();
    }

    public static void part02() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day02-input.txt");
        assert resource != null;
        try (Stream<String> lines = Files.lines(Path.of(resource.getFile()))) {
            int result = lines.map(Main::lineMin).reduce(Integer::sum).orElse(-1);
            System.out.println("part02: " + result);
        }
    }

    public static int lineMin(String line) {
        Matcher matcher = Pattern.compile("(\\d+) (red|blue|green)").matcher(line);
        Map<String, Integer> neededColors = new HashMap<>();
        while (matcher.find()) {
            String color = matcher.group(2);
            int count = Integer.parseInt(matcher.group(1));
            neededColors.compute(color, (x, y) -> Math.max(y == null ? 0 : y, count));
        }
        return neededColors.values().stream().reduce((x, y) -> x * y).orElse(0);
    }

    public static void part01() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day02-input.txt");
        assert resource != null;
        try (Stream<String> lines = Files.lines(Path.of(resource.getFile()))) {
            int result = lines.filter(Main::linePossible).map(Main::readGameId).reduce(Integer::sum).orElse(-1);
            System.out.println("part01: " + result);
        }
    }

    public static Map<String, Integer> colorAllocation = new HashMap<>() {{
        put("red", 12);
        put("blue", 14);
        put("green", 13);
    }};

    public static int readGameId(String line) {
        Matcher matcher = Pattern.compile("Game (\\d+):").matcher(line);
        matcher.find();
        System.out.println(matcher.group(1));
        return Integer.parseInt(matcher.group(1));
    }

    public static boolean linePossible(String line) {
        Matcher matcher = Pattern.compile("(\\d+) (red|blue|green)").matcher(line);
        Map<String, Integer> neededColors = new HashMap<>();
        while (matcher.find()) {
            String color = matcher.group(2);
            int count = Integer.parseInt(matcher.group(1));
            neededColors.compute(color, (x, y) -> Math.max(y == null ? 0 : y, count));
        }
        return colorAllocation.keySet().stream().allMatch(color -> colorAllocation.get(color) >= neededColors.get(color));
    }
}