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
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        System.out.println("part01: " + part(1));
        System.out.println("part02: " + part(5));
        stopWatch.stop();

        System.out.println("Total time: " + stopWatch.formatTime());
    }

    public static long part(int copyCount) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day12-input.txt");
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();

        List<Pair<String, List<Integer>>> parsedLines = lines.stream()
                .map(s -> s.split(" ")).map(firstSplit -> Pair.with(
                        String.join("?", Collections.nCopies(copyCount, firstSplit[0])).replaceAll("\\.+", "."),
                        Arrays.stream(String.join(",", Collections.nCopies(copyCount, firstSplit[1])).split(","))
                                .map(Integer::parseInt).toList()))
                .toList();

        return parsedLines.parallelStream().map(pl -> posibilities(pl.getValue0(), pl.getValue1())).reduce(Long::sum).orElse(0L);
    }

    static Map<String, Long> possibilityCache = new ConcurrentHashMap<>();

    public static long posibilities(String source, List<Integer> target) {
        String key = source + String.join(",", target.stream().map(Object::toString).toList());
        if (possibilityCache.containsKey(key)) return possibilityCache.get(key);

        int firstQ = source.indexOf('?');
        List<Integer> soFar = getLengths(source.substring(0, firstQ < 0 ? source.length() : firstQ));

        if (firstQ < 0) {
            long val = getLengths(source).equals(target) ? 1 : 0;
            possibilityCache.put(key, val);
            return val;
        }

        if (soFar.size() > target.size()) {
            possibilityCache.put(key, 0L);
            return 0L;
        }

        if (!IntStream.range(0, soFar.size())
                .allMatch(i -> i < soFar.size() - 1 ?
                        Objects.equals(soFar.get(i), target.get(i)) :
                        soFar.get(i) <= target.get(i))) {
            possibilityCache.put(key, 0L);
            return 0;
        }

        long val;
        if (soFar.size() > 1) {
            String newSource = source.substring(StringUtils.ordinalIndexOf("." + source, ".#", soFar.size()));
            List<Integer> newTarget = target.stream().skip(soFar.size() - 1).toList();
            val = posibilities(newSource.replaceFirst("\\?", "#"), newTarget) +
                    posibilities(newSource.replaceFirst("\\?", "."), newTarget);
        } else {
            val = posibilities(source.replaceFirst("\\?", "#"), target) +
                    posibilities(source.replaceFirst("\\?", "."), target);
        }

        possibilityCache.put(key, val);
        return val;
    }

    public static List<Integer> getLengths(String s) {
        return Arrays.stream(s.split("\\.+"))
                .filter(StringUtils::isNoneEmpty)
                .map(String::length)
                .toList();
    }
}