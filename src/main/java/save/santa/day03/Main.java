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
        int number;
        List<Point> range;

        public Number(int number, List<Point> range) {
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
        List<Number> numbers = new ArrayList<>();
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();
        int y = 0;
        for (String line : lines) {
            Matcher matcher = Pattern.compile("\\d+").matcher(line);
            while (matcher.find()) {
                int number = Integer.parseInt(matcher.group());
                List<Point> range = new ArrayList<>();
                for (int x = matcher.start(); x < matcher.end(); x++) {
                    range.add(new Point(x, y));
                }
                numbers.add(new Number(number, range));
            }
            y++;
        }
        System.out.println("Done processing numbers.");

        y = 0;
        int sum = 0;
        for (String line : lines) {
            Matcher matcher = Pattern.compile("[^.\\d]").matcher(line);
            while (matcher.find()) {
                int x = matcher.start();
                int finalY = y;

                Map<Point, Integer> selectedPoints = new HashMap<>();
                Arrays.stream(dists)
                        .map(dist -> new Point(dist.x + x, dist.y + finalY))
                        .flatMap(point -> numbers.stream().filter(number -> number.range.contains(point)))
                        .forEach(number -> selectedPoints.put(number.range.get(0), number.number));

                if (selectedPoints.size() == 2) {
                    sum += selectedPoints.values().stream().reduce((n, m) -> n * m).orElse(0);
                }
            }
            y++;
        }
        System.out.println("part02: " + sum);
    }

    public static void part01() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day03-input.txt");
        assert resource != null;
        List<Number> numbers = new ArrayList<>();
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();
        int y = 0;
        for (String line : lines) {
            Matcher matcher = Pattern.compile("\\d+").matcher(line);
            while (matcher.find()) {
                int number = Integer.parseInt(matcher.group());
                List<Point> range = new ArrayList<>();
                for (int x = matcher.start(); x < matcher.end(); x++) {
                    range.add(new Point(x, y));
                }
                numbers.add(new Number(number, range));
            }
            y++;
        }
        System.out.println("Done processing numbers.");


        y = 0;
        Map<Point, Integer> selectedPoints = new HashMap<>();
        for (String line : lines) {
            Matcher matcher = Pattern.compile("[^.\\d]").matcher(line);
            while (matcher.find()) {
                int x = matcher.start();
                int finalY = y;
                Arrays.stream(dists)
                        .map(dist -> new Point(dist.x + x, dist.y + finalY))
                        .flatMap(point -> numbers.stream().filter(number -> number.range.contains(point)))
                        .forEach(number -> selectedPoints.put(number.range.get(0), number.number));
            }
            y++;
        }
        int sum = selectedPoints.values().stream().reduce(Integer::sum).orElse(0);
        System.out.println("part01: " + sum);
    }
}