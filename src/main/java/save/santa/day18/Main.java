package save.santa.day18;

import org.javatuples.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day18-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile().substring(1))).toList();

        System.out.println("part01: " + part01(lines));
        System.out.println("part02: " + part02(lines));
    }

    private static long part01(List<String> lines) {
        List<Pair<Long, Long>> vertices = new ArrayList<>();
        long circumference = 0;

        Pair<Long, Long> current = Pair.with(0L, 0L);
        vertices.add(current);
        for (String line : lines) {
            String[] _sl = line.split(" +");
            long distance = Long.parseLong(_sl[1]);
            circumference += distance;

            current = switch (_sl[0]) {
                case "U" -> current.setAt1(current.getValue1() - distance);
                case "R" -> current.setAt0(current.getValue0() + distance);
                case "D" -> current.setAt1(current.getValue1() + distance);
                case "L" -> current.setAt0(current.getValue0() - distance);
                default -> current;
            };
            vertices.add(current);
        }

        return findArea(vertices, circumference);
    }

    private static long part02(List<String> lines) {
        List<Pair<Long, Long>> vertices = new ArrayList<>();
        long circumference = 0;

        Pair<Long, Long> current = Pair.with(0L, 0L);
        vertices.add(current);
        for (String line : lines) {
            String f = line.split(" +")[2].replaceAll("[#()]", "");
            long distance = Long.parseLong(f.substring(0, 5), 16);
            circumference += distance;

            current = switch (f.charAt(5)) {
                case '3' -> current.setAt1(current.getValue1() - distance);
                case '0' -> current.setAt0(current.getValue0() + distance);
                case '1' -> current.setAt1(current.getValue1() + distance);
                case '2' -> current.setAt0(current.getValue0() - distance);
                default -> current;
            };
            vertices.add(current);
        }

        return findArea(vertices, circumference);
    }

    private static long findArea(List<Pair<Long, Long>> vertices, long circumference) {
        long area = 0;
        for (int i = 0; i < vertices.size() - 1; i++) {
            var p1 = vertices.get(i);
            var p2 = vertices.get(i + 1);
            area += p1.getValue0() * p2.getValue1() - p1.getValue1() * p2.getValue0();
        }
        area /= 2;
        long pointsInside = Math.abs(area) + 1 - circumference / 2;
        return pointsInside + circumference;
    }
}