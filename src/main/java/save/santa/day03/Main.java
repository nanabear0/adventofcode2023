package save.santa.day03;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        part01();
        part02();
    }

    static class Number {
        long number;
        List<Point> range;

        public Number(long number, List<Point> range) {
            this.range = range;
            this.number = number;
        }
    }

    public static Point[] dists = {
            new Point(-1, -1),
            new Point(-1, 0),
            new Point(-1, 1),
            new Point(0, -1),
            new Point(0, 0),
            new Point(0, 1),
            new Point(1, -1),
            new Point(1, 0),
            new Point(1, 1),
    };

    public static void part02() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day03-input.txt");
        assert resource != null;
        Map<Point, Number> numbers = new HashMap<>();
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();
        int y = 0;
        for (String line : lines) {
            Matcher matcher = Pattern.compile("\\d+").matcher(line);
            while (matcher.find()) {
                long number = Long.parseLong(matcher.group());
                List<Point> range = new ArrayList<>();
                for (int x = matcher.start(); x < matcher.end(); x++) {
                    range.add(new Point(x, y));
                }
                Number n = new Number(number, range);
                for (Point r : range) {
                    numbers.put(r, n);
                }
            }
            y++;
        }

        y = 0;
        long sum = 0;
        for (String line : lines) {
            Matcher matcher = Pattern.compile("[^.\\d]").matcher(line);
            while (matcher.find()) {
                int x = matcher.start();
                int finalY = y;

                Map<Point, Long> selectedPoints = new HashMap<>();
                Arrays.stream(dists)
                        .map(dist -> new Point(dist.x + x, dist.y + finalY))
                        .filter(numbers::containsKey)
                        .map(numbers::get)
                        .forEach(number -> selectedPoints.put(number.range.get(0), number.number));

                if (selectedPoints.size() == 2) {
                    sum += selectedPoints.values().stream().reduce((n, m) -> n * m).orElse(0L);
                }
            }
            y++;
        }
        System.out.println("part02: " + sum);
    }

    public static void part01() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day03-input.txt");
        assert resource != null;
        Map<Point, Number> numbers = new HashMap<>();
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();
        int y = 0;
        for (String line : lines) {
            Matcher matcher = Pattern.compile("\\d+").matcher(line);
            while (matcher.find()) {
                long number = Long.parseLong(matcher.group());
                List<Point> range = new ArrayList<>();
                for (int x = matcher.start(); x < matcher.end(); x++) {
                    range.add(new Point(x, y));
                }
                Number n = new Number(number, range);
                for (Point r : range) {
                    numbers.put(r, n);
                }
            }
            y++;
        }

        y = 0;
        Map<Point, Long> selectedPoints = new HashMap<>();
        for (String line : lines) {
            Matcher matcher = Pattern.compile("[^.\\d]").matcher(line);
            while (matcher.find()) {
                int x = matcher.start();
                int finalY = y;
                Arrays.stream(dists)
                        .map(dist -> new Point(dist.x + x, dist.y + finalY))
                        .filter(numbers::containsKey)
                        .map(numbers::get)
                        .forEach(number -> selectedPoints.put(number.range.get(0), number.number));
            }
            y++;
        }
        long sum = selectedPoints.values().stream().reduce(Long::sum).orElse(0L);
        System.out.println("part01: " + sum);
    }
}