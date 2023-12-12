package save.santa.day12;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.javatuples.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        StopWatch watch = new StopWatch();
        watch.start();
//        System.out.println("part01: " + part(1));
        System.out.println("part02: " + part(3));
        watch.stop();

        System.out.println("Time: " + watch.formatTime());
    }

    public static long part(int copyCount) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day12-input.txt");
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();

        List<Pair<String, List<Integer>>> parsedLines = lines.stream().map(line -> {
            var firstSplit = line.split(" ");
            var s1 = String.join("?", Collections.nCopies(copyCount, firstSplit[0]));
            var s2 = String.join(",", Collections.nCopies(copyCount, firstSplit[1]));
            return Pair.with(s1, Arrays.stream(s2.split(",")).map(Integer::parseInt).toList());
        }).toList();
        int biggestLine = parsedLines.stream().map(p -> p.getValue0().length()).reduce(Integer::max).orElseThrow();

        return parsedLines.stream().map(pl -> posibilities(pl.getValue0(), pl.getValue1())).reduce(Long::sum).orElse(0L);
    }

    static AtomicInteger n = new AtomicInteger(0);

    public static long posibilities(String source, List<Integer> target) {
        System.out.println(n.incrementAndGet());
        List<Integer> qIndexes = IntStream.range(0, source.length()).filter(i -> source.charAt(i) == '?').boxed().toList();
        return LongStream.range(0, 1L << qIndexes.size())
                .parallel()
                .map(i -> {
                    Set<Integer> pqi = IntStream.range(0, qIndexes.size()).filter(j -> (1 << j & i) > 0).map(qIndexes::get).boxed().collect(Collectors.toSet());
                    String stringToTest = StringUtils.strip(IntStream.range(0, source.length())
                            .map(c -> pqi.contains(c) || source.charAt(c) == '#' ? '#' : '.')
                            .collect(StringBuilder::new,
                                    StringBuilder::appendCodePoint,
                                    StringBuilder::append)
                            .toString(), ".");
                    return getLengths(stringToTest).equals(target) ? 1 : 0;
                }).sum();
    }

    public static List<Integer> getLengths(String s) {
        return Arrays.stream(s.split("\\.+"))
                .filter(StringUtils::isNoneEmpty)
                .map(String::length)
                .toList();
    }
}