package save.santa.day06;

import org.javatuples.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

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

        long result = races.parallelStream().map(pair ->
                        IntStream.range(0, pair.getValue0() + 1)
                                .filter(hold -> pair.getValue0() * hold - hold * hold > pair.getValue1())
                                .count())
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

        long result = LongStream.range(0, time + 1)
                .parallel()
                .filter(hold -> time * hold - hold * hold > distance)
                .count();
        System.out.println("part02: " + result);
    }
}