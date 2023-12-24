package save.santa.day24;

import org.javatuples.Pair;
import org.javatuples.Sextet;
import org.javatuples.Triplet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("part01: " + part01());
        System.out.println("part02: " + part02());
    }

    public static Long part02() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day24-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();
        List<Pair<Triplet<Long, Long, Long>, Triplet<Long, Long, Long>>> flakes =
                lines.stream().map(line -> {
                    List<Long> ns = Arrays.stream(line.split(" *[@,] *"))
                            .map(Long::parseLong).toList();
                    return Pair.with(Triplet.with(ns.get(0), ns.get(1), ns.get(2)), Triplet.with(ns.get(3), ns.get(4), ns.get(5)));
                }).toList();
        HashSet<Long> possibleXs = new HashSet<>();
        HashSet<Long> possibleYs = new HashSet<>();
        HashSet<Long> possibleZs = new HashSet<>();
        for (int i = 0; i < flakes.size() - 1; i++) {
            for (int j = i + 1; j < flakes.size(); j++) {
                if (flakes.get(i).getValue1().getValue0().equals(flakes.get(j).getValue1().getValue0())) {
                    var distanceBetween = Math.abs(flakes.get(i).getValue0().getValue0() - flakes.get(j).getValue0().getValue0());
                    HashSet<Long> myPossibleXs = new HashSet<>();
                    for (int d = 1; d < 1000; d++) {
                        if (distanceBetween % d == 0) {
                            myPossibleXs.add(flakes.get(i).getValue1().getValue0() + d);
                            myPossibleXs.add(flakes.get(i).getValue1().getValue0() - d);
                        }
                    }
                    if (possibleXs.isEmpty()) possibleXs.addAll(myPossibleXs);
                    else possibleXs.retainAll(myPossibleXs);
                } else if (flakes.get(i).getValue1().getValue1().equals(flakes.get(j).getValue1().getValue1())) {
                    var distanceBetween = Math.abs(flakes.get(i).getValue0().getValue1() - flakes.get(j).getValue0().getValue1());
                    HashSet<Long> myPossibleYs = new HashSet<>();
                    for (int d = 1; d < 1000; d++) {
                        if (distanceBetween % d == 0) {
                            myPossibleYs.add(flakes.get(i).getValue1().getValue1() + d);
                            myPossibleYs.add(flakes.get(i).getValue1().getValue1() - d);
                        }
                    }
                    if (possibleYs.isEmpty()) possibleYs.addAll(myPossibleYs);
                    else possibleYs.retainAll(myPossibleYs);
                } else if (flakes.get(i).getValue1().getValue2().equals(flakes.get(j).getValue1().getValue2())) {
                    var distanceBetween = Math.abs(flakes.get(i).getValue0().getValue2() - flakes.get(j).getValue0().getValue2());
                    HashSet<Long> myPossibleZs = new HashSet<>();
                    for (int d = 1; d < 1000; d++) {
                        if (distanceBetween % d == 0) {
                            myPossibleZs.add(flakes.get(i).getValue1().getValue2() + d);
                            myPossibleZs.add(flakes.get(i).getValue1().getValue2() - d);
                        }
                    }
                    if (possibleZs.isEmpty()) possibleZs.addAll(myPossibleZs);
                    else possibleZs.retainAll(myPossibleZs);
                }
            }
        }

        Triplet<Long, Long, Long> rockVelocity = Triplet.with(
                possibleXs.stream().findFirst().orElseThrow(),
                possibleYs.stream().findFirst().orElseThrow(),
                possibleZs.stream().findFirst().orElseThrow()
        );

        var p1 = flakes.get(0);
        p1 = p1.setAt1(Triplet.with(
                p1.getValue1().getValue0() - rockVelocity.getValue0(),
                p1.getValue1().getValue1() - rockVelocity.getValue1(),
                p1.getValue1().getValue2() - rockVelocity.getValue2()));
        var p2 = flakes.get(1);
        p2 = p2.setAt1(Triplet.with(
                p2.getValue1().getValue0() - rockVelocity.getValue0(),
                p2.getValue1().getValue1() - rockVelocity.getValue1(),
                p2.getValue1().getValue2() - rockVelocity.getValue2()));

        var t1 = (p2.getValue0().getValue0() * p2.getValue1().getValue1() +
                p1.getValue0().getValue1() * p2.getValue1().getValue0() -
                p2.getValue0().getValue1() * p2.getValue1().getValue0() -
                p1.getValue0().getValue0() * p2.getValue1().getValue1()) /
                (p1.getValue1().getValue0() * p2.getValue1().getValue1() -
                        p1.getValue1().getValue1() * p2.getValue1().getValue0());

        Triplet<Long, Long, Long> rockStartingAt = Triplet.with(
                p1.getValue0().getValue0() + p1.getValue1().getValue0() * t1,
                p1.getValue0().getValue1() + p1.getValue1().getValue1() * t1,
                p1.getValue0().getValue2() + p1.getValue1().getValue2() * t1
        );

        return rockStartingAt.getValue0() + rockStartingAt.getValue1() + rockStartingAt.getValue2();
    }

    public static Long part01() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day24-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();
        List<Sextet<BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> flakes =
                lines.stream().map(line -> Sextet.fromCollection(Arrays.stream(line.split(" *[@,] *"))
                        .map(x -> new BigDecimal(x).setScale(100, RoundingMode.HALF_EVEN)).toList())).toList();

        var count = 0L;
        for (int i = 0; i < flakes.size() - 1; i++) {
            for (int j = i + 1; j < flakes.size(); j++) {
                if (doTheseBoisIntersect(flakes.get(i), flakes.get(j))) count++;
            }
        }

        return count;
    }

    public static BigDecimal MIN = BigDecimal.valueOf(200000000000000.0);
    public static BigDecimal MAX = BigDecimal.valueOf(400000000000000.0);

    public static boolean doTheseBoisIntersect(Sextet<BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal> p1, Sextet<BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal> p2) {
        var a = p1.getValue4().divide(p1.getValue3(), RoundingMode.HALF_EVEN);
        var b = p2.getValue4().divide(p2.getValue3(), RoundingMode.HALF_EVEN);
        var c = p1.getValue1().subtract(a.multiply(p1.getValue0()));
        var d = p2.getValue1().subtract(b.multiply(p2.getValue0()));

        if (a.equals(b)) return false;

        var x = d.subtract(c).divide(a.subtract(b), RoundingMode.HALF_EVEN);
        var y = a.multiply(x).add(c);

        return x.compareTo(MIN) >= 0 && x.compareTo(MAX) <= 0 &&
                y.compareTo(MIN) >= 0 && y.compareTo(MAX) <= 0 &&
                x.subtract(p1.getValue0()).multiply(p1.getValue3()).compareTo(BigDecimal.ZERO) >= 0 &&
                x.subtract(p2.getValue0()).multiply(p2.getValue3()).compareTo(BigDecimal.ZERO) >= 0;

    }
}