package save.santa.day04;

import org.javatuples.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        part01();
        part02();
    }

    public static void part01() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day04-input.txt");
        assert resource != null;
        int value = Files.lines(Path.of(resource.getFile().substring(1)))
                .map(Main::processLine)
                .map(p -> (int) Math.pow(2, p.getValue1() - 1))
                .reduce(Integer::sum)
                .orElse(0);
        System.out.println("part01: " + value);
    }

    public static void part02() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day04-input.txt");
        assert resource != null;
        List<Pair<Integer, Integer>> processedLines = Files.lines(Path.of(resource.getFile().substring(1)))
                .map(Main::processLine)
                .toList();

        Map<Integer, Integer> cardCount = new HashMap<>();
        for (Pair<Integer, Integer> id : processedLines) {
            cardCount.put(id.getValue0(), 1);
        }

        for (Pair<Integer, Integer> id : processedLines) {
            int myCount = cardCount.get(id.getValue0());
            for (int i = 1; i <= id.getValue1(); i++) {
                cardCount.computeIfPresent(id.getValue0() + i, (x, y) -> y + myCount);
            }
        }

        int value = cardCount.values().stream().reduce(Integer::sum).orElse(0);
        System.out.println("part02: " + value);
    }

    public static Pair<Integer, Integer> processLine(String line) {
        String[] firstSplit = line.split(": ");
        Integer id = Integer.parseInt(firstSplit[0].split(" +")[1]);
        String[] sides = firstSplit[1].split(" \\| ");
        Set<Integer> real = processSide(sides[0]);
        Set<Integer> target = processSide(sides[1]);
        real.retainAll(target);
        return Pair.with(id, real.size());

    }

    public static Set<Integer> processSide(String side) {
        Matcher matcher = Pattern.compile("\\d+").matcher(side);
        Set<Integer> numbers = new HashSet<>();
        while (matcher.find()) {
            numbers.add(Integer.parseInt(matcher.group()));
        }
        return numbers;
    }
}