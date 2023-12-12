package save.santa.day12;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.javatuples.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
//        System.out.println("part01: " + part(1));
        System.out.println("part02: " + part(5));
    }

    public static StopWatch stopwatch = new StopWatch();
    public static AtomicInteger completedOnes = new AtomicInteger();
    public static int totalLines;

    public static long part(int copyCount) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day12-input.txt");
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();
        totalLines = lines.size();

        List<Pair<String, List<Integer>>> parsedLines = lines.stream().map(line -> {
            var firstSplit = line.split(" ");
            var s1 = String.join("?", Collections.nCopies(copyCount, firstSplit[0]));
            var s2 = String.join(",", Collections.nCopies(copyCount, firstSplit[1]));
            return Pair.with(s1, Arrays.stream(s2.split(",")).map(Integer::parseInt).toList());
        }).toList();

        stopwatch.start();
        return parsedLines.parallelStream().map(pl -> loggingPossiblities(pl.getValue0(), pl.getValue1())).reduce(Long::sum).orElse(0L);
    }

    public static long loggingPossiblities(String source, List<Integer> target) {
        long result = posibilities(source, target);
        int myIndex = completedOnes.incrementAndGet();
        Duration dur = DurationUtils.toDuration((long) ((double) stopwatch.getTime() / completedOnes.get() * (totalLines - myIndex)), TimeUnit.MILLISECONDS);
        System.out.printf(
                "%05d / %05d done after %s. Estimated time left: %02d:%02d:%02d.%04d\n",
                myIndex,
                totalLines,
                stopwatch.formatTime(),
                dur.toHoursPart(),
                dur.toMinutesPart(),
                dur.toSecondsPart(),
                dur.toMillisPart());

        return result;
    }

    public static long posibilities(String source, List<Integer> target) {
        int firstQ = source.indexOf('?');
        List<Integer> soFar = getLengths(source.substring(0, firstQ < 0 ? source.length() : firstQ));

        if (firstQ < 0) {
            return getLengths(source).equals(target) ? 1 : 0;
        }

        if (soFar.size() > target.size()) return 0;

        if (!IntStream.range(0, soFar.size())
                .allMatch(i -> i < soFar.size() - 1 ?
                        Objects.equals(soFar.get(i), target.get(i)) :
                        soFar.get(i) <= target.get(i))) {
            return 0;
        }

        return posibilities(source.replaceFirst("\\?", "#"), target) + posibilities(source.replaceFirst("\\?", "."), target);
    }

    public static List<Integer> getLengths(String s) {
        return Arrays.stream(s.split("\\.+"))
                .filter(StringUtils::isNoneEmpty)
                .map(String::length)
                .toList();
    }
}