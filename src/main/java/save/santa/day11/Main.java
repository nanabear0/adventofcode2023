package save.santa.day11;

import org.javatuples.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("part01: " + part01(2));
        System.out.println("part02: " + part01(1_000_000));
    }

    public static long part01(int expansionFactor) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day11-input.txt");
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();

        int boundaryX = lines.get(0).length();
        int boundaryY = lines.size();
        Set<Pair<Integer, Integer>> stars = IntStream.range(0, boundaryY).boxed()
                .flatMap(y -> IntStream.range(0, boundaryX).mapToObj(x -> Pair.with(x, y)))
                .filter(pair -> lines.get(pair.getValue1()).charAt(pair.getValue0()) == '#')
                .collect(Collectors.toSet());

        Set<Integer> emptyXs = IntStream.range(0, boundaryX)
                .filter(x -> IntStream.range(0, boundaryY)
                        .noneMatch(y -> stars.contains(Pair.with(x, y))))
                .boxed().collect(Collectors.toSet());
        Set<Integer> emptyYs = IntStream.range(0, boundaryY)
                .filter(y -> IntStream.range(0, boundaryX)
                        .noneMatch(x -> stars.contains(Pair.with(x, y))))
                .boxed().collect(Collectors.toSet());

        return stars.stream()
                .flatMap(star1 -> stars.stream().map(star2 -> Pair.with(star1, star2)))
                .filter(starPair -> !starPair.getValue0().equals(starPair.getValue1()))
                .map(starPair -> distanceBetweenStars(starPair.getValue0(), starPair.getValue1(), emptyXs, emptyYs, expansionFactor))
                .reduce(Long::sum)
                .orElseThrow() / 2;
    }

    public static long distanceBetweenStars(Pair<Integer, Integer> star1,
                                            Pair<Integer, Integer> star2,
                                            Set<Integer> emptyXs,
                                            Set<Integer> emptyYs,
                                            int expansionFactor) {
        return IntStream.range(Math.min(star1.getValue0(), star2.getValue0()), Math.max(star1.getValue0(), star2.getValue0()))
                .mapToLong(x -> (emptyXs.contains(x) ? expansionFactor : 1))
                .sum()
                +
                IntStream.range(Math.min(star1.getValue1(), star2.getValue1()), Math.max(star1.getValue1(), star2.getValue1()))
                        .mapToLong(y -> (emptyYs.contains(y) ? expansionFactor : 1))
                        .sum();
    }
}