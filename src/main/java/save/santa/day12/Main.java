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
                        StringUtils.strip(
                                String.join("?", Collections.nCopies(copyCount, firstSplit[0])).replaceAll("\\.+", "."),
                                "."),
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
        int firstD = source.indexOf('.');
        int firstH = source.indexOf('#');

        long val;
        if (target.isEmpty() && firstH >= 0) {
            val = 0;
        } else if (firstH < 0 && firstQ < 0) {
            val = target.isEmpty() ? 1 : 0;
        } else if ((firstD < firstQ || firstQ < 0) && firstD > firstH) {
            val = target.get(0) == firstD ? posibilities(
                    StringUtils.strip(source.substring(firstD), "."),
                    target.stream().skip(1).toList()
            ) : 0;
        } else if (firstQ >= 0) {
            val = posibilities(StringUtils.strip(source.replaceFirst("\\?", "#"), "."), target) +
                    posibilities(StringUtils.strip(source.replaceFirst("\\?", "."), "."), target);
        } else {
            val = target.size() == 1 && target.get(0) == source.length() ? 1 : 0;
        }

        possibilityCache.put(key, val);
        return val;
    }
}