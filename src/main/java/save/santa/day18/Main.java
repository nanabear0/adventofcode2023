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
//        System.out.println("part02: " + doPart(target, lossMap, 4, 10));
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

        long area = 0;
        for (int i = 0; i < vertices.size() - 1; i++) {
            var p1 = vertices.get(i);
            var p2 = vertices.get(i + 1);
            area += p1.getValue0() * p2.getValue1() - p1.getValue1() * p2.getValue0();
        }
        area /= 2;
        long pointsInside = Math.abs(area) + 1 - circumference / 2;
        long totalArea = pointsInside + circumference;

        return totalArea;
    }
}