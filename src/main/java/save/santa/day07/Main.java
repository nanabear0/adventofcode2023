package save.santa.day07;

import org.javatuples.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        doPart("part1", cv1, Main::getHandType);
        doPart("part2", cv2, Main::getHandType2);
    }

    public static void doPart(String outputStr, Map<String, Integer> cardValues, Function<String, Integer> handTypeFunction) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day07-input.txt");
        assert resource != null;
        List<Pair<String, Integer>> hands = Files.lines(Path.of(resource.getFile().substring(1)))
                .map(line -> {
                    String[] sp = line.split(" +");
                    return Pair.with(sp[0], Integer.parseInt(sp[1]));
                })
                .sorted((h1, h2) -> compareTo(h2.getValue0(), h1.getValue0(), cardValues, handTypeFunction))
                .toList();
        int result = IntStream.range(0, hands.size())
                .map(i -> hands.get(i).getValue1() * (hands.size() - i))
                .sum();
        System.out.println(outputStr + ": " + result);
    }

    public static Map<String, Integer> cv2 = new HashMap<>() {{
        put("A", 14);
        put("K", 13);
        put("Q", 12);
        put("T", 10);
        put("9", 9);
        put("8", 8);
        put("7", 7);
        put("6", 6);
        put("5", 5);
        put("4", 4);
        put("3", 3);
        put("2", 2);
        put("J", 0);
    }};

    public static Map<String, Integer> cv1 = new HashMap<>() {{
        put("A", 14);
        put("K", 13);
        put("Q", 12);
        put("J", 11);
        put("T", 10);
        put("9", 9);
        put("8", 8);
        put("7", 7);
        put("6", 6);
        put("5", 5);
        put("4", 4);
        put("3", 3);
        put("2", 2);
    }};

    public static int compareTo(String hand1, String hand2, Map<String, Integer> cardValues, Function<String, Integer> handTypeFunction) {
        int type1 = handTypeFunction.apply(hand1);
        int type2 = handTypeFunction.apply(hand2);
        if (type1 != type2) return Integer.compare(type1, type2);

        return IntStream.range(0, hand1.length())
                .mapToObj(i -> Integer.compare(
                        cardValues.get(hand1.substring(i, i + 1)),
                        cardValues.get(hand2.substring(i, i + 1))))
                .reduce((acc, v) -> acc == 0 ? v : acc).orElseThrow();
    }

    public static int getHandType(String hand) {
        Map<String, Integer> groups = new TreeMap<>();
        Arrays.stream(hand.split("")).forEach(c -> groups.compute(c, (k, v) -> v == null ? 1 : v + 1));
        List<Integer> counts = groups.values().stream().sorted(Collections.reverseOrder()).toList();

        if (counts.get(0) == 5) return 6;
        else if (counts.get(0) == 4) return 5;
        else if (counts.get(0) == 3 && counts.get(1) == 2) return 4;
        else if (counts.get(0) == 3) return 3;
        else if (counts.get(0) == 2 && counts.get(1) == 2) return 2;
        else if (counts.get(0) == 2) return 1;
        else return 0;
    }

    public static int getHandType2(String hand) {
        Map<String, Integer> groups = new TreeMap<>();
        Arrays.stream(hand.split("")).forEach(c -> groups.compute(c, (k, v) -> v == null ? 1 : v + 1));
        Integer _j = groups.remove("J");
        int j = _j == null ? 0 : _j;

        List<Integer> counts = groups.values().stream().sorted(Collections.reverseOrder()).toList();
        if (counts.isEmpty() || counts.get(0) + j == 5) return 6;
        else if (counts.get(0) + j == 4) return 5;
        else if (counts.get(0) + counts.get(1) + j == 5) return 4;
        else if (counts.get(0) + j == 3) return 3;
        else if (counts.get(0) + counts.get(1) + j == 4) return 2;
        else if (counts.get(0) + j == 2) return 1;
        else return 0;
    }
}