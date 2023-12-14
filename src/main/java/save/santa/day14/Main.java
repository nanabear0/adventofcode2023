package save.santa.day14;

import org.javatuples.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day14-input.txt");
        assert resource != null;
        var lines = Files.lines(Path.of(resource.getFile())).toList();
        Pair<Integer, Integer> yRange = Pair.with(-1, lines.size());
        Pair<Integer, Integer> xRange = Pair.with(-1, lines.get(0).length());
        Set<Pair<Integer, Integer>> rocks = new HashSet<>();
        Set<Pair<Integer, Integer>> supports = new HashSet<>();

        for (int x = xRange.getValue0(); x <= xRange.getValue1(); x++) {
            supports.add(Pair.with(x, yRange.getValue0()));
            supports.add(Pair.with(x, yRange.getValue1()));
        }
        for (int y = yRange.getValue0() + 1; y < yRange.getValue1(); y++) {
            supports.add(Pair.with(xRange.getValue0(), y));
            supports.add(Pair.with(xRange.getValue1(), y));
            for (int x = xRange.getValue0() + 1; x < xRange.getValue1(); x++) {
                switch (lines.get(y).charAt(x)) {
                    case 'O':
                        rocks.add(Pair.with(x, y));
                        break;
                    case '#':
                        supports.add(Pair.with(x, y));
                        break;
                    case '.':
                    default:
                        break;
                }
            }
        }

        // Part01
        var part1Rocks = moveRocksInDir(Pair.with(0, 1), rocks, supports, xRange, yRange);
        int part1Tension = calculateTension(part1Rocks, yRange);
        System.out.println("part01: " + part1Tension);

        //Part02
        List<Integer> tensionHistory = new ArrayList<>();
        Pair<Integer, Integer> tensionCycle = Pair.with(-1, -1);
        for (int i = 1; i <= 1_000_000_000; i++) {
            rocks = runCycle(rocks, supports, xRange, yRange);
            tensionHistory.add(calculateTension(rocks, yRange));
            tensionCycle = guessSequenceLength(tensionHistory);
            if (tensionCycle.getValue0() > 0 && tensionCycle.getValue1() > 0) break;
        }
        int finalTension = tensionHistory.get(((1_000_000_000 - tensionCycle.getValue0()) % tensionCycle.getValue1()) + tensionCycle.getValue0() - 1);
        System.out.println("part02: " + finalTension);
    }

    public static Pair<Integer, Integer> guessSequenceLength(List<Integer> list) {
        for (int skip = 0; skip < list.size() - 4; skip++) {
            for (int i = 2; i < (list.size() - skip) / 2; i++) {
                if (
                        IntStream.range(skip, skip + i).map(list::get).boxed().toList().equals(
                                IntStream.range(skip + i, skip + 2 * i).map(list::get).boxed().toList())
                ) {
                    return Pair.with(skip, i);
                }
            }
        }
        return Pair.with(-1, -1);
    }

    private static Integer calculateTension(Set<Pair<Integer, Integer>> rocks, Pair<Integer, Integer> yRange) {
        return rocks.stream()
                .map(Pair::getValue1)
                .map(y -> yRange.getValue1() - y)
                .reduce(Integer::sum)
                .orElse(0);
    }

    public static Set<Pair<Integer, Integer>> runCycle(
            Set<Pair<Integer, Integer>> _rocks,
            Set<Pair<Integer, Integer>> supports,
            Pair<Integer, Integer> xRange,
            Pair<Integer, Integer> yRange) {
        Set<Pair<Integer, Integer>> rocks = new HashSet<>(_rocks);
        rocks = moveRocksInDir(Pair.with(0, 1), rocks, supports, xRange, yRange);
        rocks = moveRocksInDir(Pair.with(1, 0), rocks, supports, xRange, yRange);
        rocks = moveRocksInDir(Pair.with(0, -1), rocks, supports, xRange, yRange);
        return moveRocksInDir(Pair.with(-1, 0), rocks, supports, xRange, yRange);
    }

    public static Set<Pair<Integer, Integer>> moveRocksInDir(
            Pair<Integer, Integer> scanDir,
            Set<Pair<Integer, Integer>> rocks,
            Set<Pair<Integer, Integer>> supports,
            Pair<Integer, Integer> xRange,
            Pair<Integer, Integer> yRange) {
        Set<Pair<Integer, Integer>> rocksPrime = new HashSet<>(rocks);
        for (Pair<Integer, Integer> support : supports) {
            Pair<Integer, Integer> nextSettlePoint = move(support, scanDir);
            Pair<Integer, Integer> searchPoint = move(support, scanDir);
            while (!supports.contains(searchPoint) && inBounds(searchPoint, xRange, yRange)) {
                if (rocksPrime.contains(searchPoint)) {
                    rocksPrime.remove(searchPoint);
                    rocksPrime.add(nextSettlePoint);
                    nextSettlePoint = move(nextSettlePoint, scanDir);
                }
                searchPoint = move(searchPoint, scanDir);
            }
        }
        return rocksPrime;
    }

    public static boolean inBounds(Pair<Integer, Integer> p,
                                   Pair<Integer, Integer> xRange,
                                   Pair<Integer, Integer> yRange) {
        return p.getValue0() >= xRange.getValue0() &&
                p.getValue0() <= xRange.getValue1() &&
                p.getValue1() >= yRange.getValue0() &&
                p.getValue1() <= yRange.getValue1();
    }

    public static Pair<Integer, Integer> move(Pair<Integer, Integer> p, Pair<Integer, Integer> dir) {
        return Pair.with(p.getValue0() + dir.getValue0(), p.getValue1() + dir.getValue1());
    }
}
