package save.santa.day21;

import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        part01();
    }

    public static void part02(List<Long> steps) throws IOException {
        int gap = -1;
        for (int i = 1; i < steps.size(); i++) {
            int finalI = i;
            List<Long> gaps = IntStream.range(0, steps.size() - i).mapToLong(j -> steps.get(finalI + j) - steps.get(j)).boxed().toList();
            if (IntStream.range(0, gaps.size() - 1).allMatch(j -> gaps.get(j + 1) > gaps.get(j))) {
                gap = i;
                break;
            }
        }

        List<Quartet<Long, Long, Long, Long>> gapDelta = new ArrayList<>();
        pu:
        for (int i = 0; i < gap; i++) {
            List<Long> subList = new ArrayList<>();
            for (int j = 1; j < steps.size() / gap; j++) {
                subList.add(steps.get(j * gap + i) - steps.get((j - 1) * gap + i));
                List<Long> subSubList = IntStream.range(0, subList.size() - 1).mapToObj(x -> subList.get(x + 1) - subList.get(x)).toList();
                if (subSubList.size() > 2 && subSubList.get(subSubList.size() - 1).equals(subSubList.get(subSubList.size() - 2))) {
                    gapDelta.add(Quartet.with(
                            (long) (subList.size() - 1) * gap + i,
                            steps.get((subList.size() - 1) * gap + i),
                            subList.get(subList.size() - 2),
                            subSubList.getLast()));
                    continue pu;
                }
            }

        }
        long target = 26501365L;
        var targetGapDelta = gapDelta.get((int) (target % gap));
        long targetGapCount = (target - targetGapDelta.getValue0()) / gap;

        long result = targetGapDelta.getValue1() +
                targetGapDelta.getValue2() * targetGapCount +
                targetGapCount * (targetGapCount + 1) / 2 * targetGapDelta.getValue3();
        System.out.println("part02: " + result);
    }

    public static void part01() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day21-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile().substring(1))).toList();
        HashSet<Pair<Integer, Integer>> plot = new HashSet<>();
        Pair<Integer, Integer> start = null;

        long yLimit = lines.size();
        long xLimit = lines.get(0).length();
        for (int y = 0; y < yLimit; y++) {
            for (int x = 0; x < xLimit; x++) {
                switch (lines.get(y).charAt(x)) {
                    case 'S':
                        start = Pair.with(x, y);
                    case '.':
                        plot.add(Pair.with(x, y));
                        break;
                }
            }
        }

        Map<Pair<Integer, Integer>, Integer> distance = new HashMap<>();
        Set<Pair<Integer, Integer>> frontier = new HashSet<>();
        distance.put(start, 0);
        frontier.add(start);
        int limit = 1000;

        List<Long> results = new ArrayList<>();
        results.add(1L);
        for (int i = 1; i <= limit; i++) {
            int finalI = i;
            frontier = frontier.stream()
                    .flatMap(p -> Stream.of(Pair.with(0, 1), Pair.with(0, -1), Pair.with(1, 0), Pair.with(-1, 0))
                            .map(dir -> move(p, dir)))
                    .filter(np -> plot.contains(normalize(np, xLimit, yLimit)))
                    .filter(x -> !distance.containsKey(x))
                    .peek(x -> distance.put(x, finalI))
                    .collect(Collectors.toSet());
            results.add(distance.values().stream().filter(x -> x <= finalI).filter(x -> (x - finalI) % 2 == 0).count());
        }

        System.out.println("part01: " + results.get(64));

        part02(results);
    }

    public static Pair<Integer, Integer> normalize(Pair<Integer, Integer> point, long xLimit, long yLimit) {
        return Pair.with((int) ((point.getValue0() % xLimit + xLimit) % xLimit), (int) ((point.getValue1() % yLimit + yLimit) % yLimit));
    }

    public static Pair<Integer, Integer> move(Pair<Integer, Integer> point, Pair<Integer, Integer> dir) {
        return Pair.with(point.getValue0() + dir.getValue0(), point.getValue1() + dir.getValue1());
    }
}