package save.santa.day06;

import org.javatuples.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        part01();
        part02();
    }

    public static void part01() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day06-input.txt");
        assert resource != null;
        List<List<Integer>> lines = Files.lines(Path.of(resource.getFile().substring(1)))
                .map(line ->
                        Arrays.stream(line.substring(9).trim().split(" +"))
                                .map(Integer::parseInt).toList())
                .toList();
        List<Pair<Integer, Integer>> races = IntStream.range(0, lines.get(0).size())
                .mapToObj(i -> Pair.with(lines.get(0).get(i), lines.get(1).get(i)))
                .toList();

        long result = races.parallelStream()
                .map(pair -> quadratic(pair.getValue0(), pair.getValue1()))
                .reduce((x, y) -> x * y).orElseThrow();
        System.out.println("part01: " + result);
    }

    public static void part02() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day06-input.txt");
        assert resource != null;
        List<Long> lines = Files.lines(Path.of(resource.getFile().substring(1)))
                .map(line ->
                        Long.parseLong(line.substring(9).replaceAll(" ", "")))
                .toList();
        long time = lines.get(0);
        long distance = lines.get(1);

        long result = quadratic(time, distance);
        System.out.println("part02: " + result);
    }

    public static long quadratic(long t, long d) {
        double q = Math.pow(t * t - 4 * d, 0.5);
        double v1 = Math.floor((t - q) / 2);
        double v2 = Math.ceil((t + q) / 2);
        return (long) (v2 - v1 - 1);
    }
}