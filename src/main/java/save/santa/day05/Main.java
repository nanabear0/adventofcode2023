package save.santa.day05;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        part01();
        part02();
    }

    public static void part02() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day05-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile().substring(1))).toList();
        List<Long> seedDef = Arrays.stream(lines.get(0).split(": ")[1].split(" +")).map(Long::parseLong).toList();
        List<Pair<Long, Long>> seedRanges = IntStream.range(0, seedDef.size() / 2).mapToObj(start -> Pair.with(seedDef.get(start * 2), seedDef.get(start * 2) + seedDef.get(start * 2 + 1))).toList();
        List<List<Triplet<Long, Long, Long>>> mappings = parseMappings(lines);

        long result = seedRanges.stream().flatMap(seedRange -> seedToLocationRange(seedRange, mappings).stream()).map(Pair::getValue0).min(Long::compareTo).orElse(0L);

        System.out.println("part02: " + result);
    }

    private static List<Pair<Long, Long>> seedToLocationRange(Pair<Long, Long> seedRange, List<List<Triplet<Long, Long, Long>>> mappings) {
        List<Pair<Long, Long>> currentRanges = List.of(seedRange);
        for (List<Triplet<Long, Long, Long>> mapping : mappings) {
            List<Pair<Long, Long>> processedRanges = new ArrayList<>();
            List<Pair<Long, Long>> leftoverRanges = currentRanges.stream().toList();
            for (Triplet<Long, Long, Long> map : mapping) {
                List<Pair<Long, Long>> newLeftoverRanges = new ArrayList<>();
                for (Pair<Long, Long> rawRange : leftoverRanges) {
                    Triplet<Pair<Long, Long>, Pair<Long, Long>, Pair<Long, Long>> splitd = splitRange(rawRange, Pair.with(map.getValue1(), map.getValue1() + map.getValue2()));
                    if (splitd.getValue0() != null) newLeftoverRanges.add(splitd.getValue0());
                    if (splitd.getValue1() != null) processedRanges.add(rangeToMap(splitd.getValue1(), map));
                    if (splitd.getValue2() != null) newLeftoverRanges.add(splitd.getValue2());
                }
                leftoverRanges = newLeftoverRanges;
            }
            processedRanges.addAll(leftoverRanges);
            currentRanges = processedRanges;
        }
        return currentRanges;
    }

    private static Pair<Long, Long> rangeToMap(Pair<Long, Long> range, Triplet<Long, Long, Long> map) {
        return Pair.with(range.getValue0() - map.getValue1() + map.getValue0(), range.getValue1() - map.getValue1() + map.getValue0());
    }

    private static boolean isRangeValid(Pair<Long, Long> range) {
        return range != null && range.getValue1() > range.getValue0();
    }

    private static Triplet<Pair<Long, Long>, Pair<Long, Long>, Pair<Long, Long>> splitRange(Pair<Long, Long> range0, Pair<Long, Long> range1) {
        // They fully overlap first is bigger
        if (range0.getValue0() < range1.getValue0() && range0.getValue1() > range1.getValue1())
            return Triplet.with(Pair.with(range0.getValue0(), range1.getValue0()), range1, Pair.with(range1.getValue1(), range0.getValue1()));
        // They fully overlap second is bigger
        if (range1.getValue0() < range0.getValue0() && range1.getValue1() > range0.getValue1())
            return Triplet.with(null, range0, null);
        // first is to the left
        if (range0.getValue0() < range1.getValue0() && range0.getValue1() > range1.getValue0() && range0.getValue1() < range1.getValue1()) {
            return Triplet.with(Pair.with(range0.getValue0(), range1.getValue1()), Pair.with(range1.getValue0(), range0.getValue1()), null);
        }
        // first is to the right
        if (range0.getValue0() > range1.getValue0() && range0.getValue0() < range1.getValue1() && range0.getValue1() > range1.getValue1()) {
            return Triplet.with(null, Pair.with(range0.getValue0(), range1.getValue1()), Pair.with(range1.getValue0(), range0.getValue1()));
        }

        return Triplet.with(range0, null, null);
    }

    private static Long seedToLocation(Long seed, List<List<Triplet<Long, Long, Long>>> mappings) {
        long current = seed;
        for (List<Triplet<Long, Long, Long>> mapping : mappings) {
            long finalCurrent = current;
            current = mapping.stream()
                    .filter(map -> finalCurrent >= map.getValue1() && finalCurrent - map.getValue1() < map.getValue2())
                    .map(map -> finalCurrent - map.getValue1() + map.getValue0())
                    .findFirst()
                    .orElse(finalCurrent);
        }
        return current;
    }

    private static List<List<Triplet<Long, Long, Long>>> parseMappings(List<String> lines) {
        String[] infos = lines.stream().skip(2).reduce((x, y) -> x + "\n" + y).orElseThrow().split("\\n\\n");
        List<List<Triplet<Long, Long, Long>>> mappings = Arrays.stream(infos)
                .map(info -> info
                        .lines()
                        .skip(1)
                        .map(line -> Triplet.fromIterable(
                                Arrays.stream(line.split(" +"))
                                        .map(Long::parseLong)
                                        .toList()))
                        .toList())
                .toList();
        return mappings;
    }

    public static void part01() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day05-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile().substring(1))).toList();
        List<Long> seeds = Arrays.stream(lines.get(0).split(": ")[1].split(" +")).map(Long::parseLong).toList();
        List<List<Triplet<Long, Long, Long>>> mappings = parseMappings(lines);

        List<Long> locations = seeds.stream().map(seed -> seedToLocation(seed, mappings)).toList();
        System.out.println("part01: " + locations.stream().min(Long::compareTo).orElseThrow());
    }
}