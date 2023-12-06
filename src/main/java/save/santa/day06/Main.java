package save.santa.day06;

import org.javatuples.Pair;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        part01();
        part02();
    }

    public static void part01() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day06-input.txt");
        assert resource != null;
        List<List<BigInteger>> lines = Files.lines(Path.of(resource.getFile().substring(1)))
                .map(line ->
                        Arrays.stream(line.substring(9).trim().split(" +"))
                                .map(BigInteger::new).toList())
                .toList();
        List<Pair<BigInteger, BigInteger>> races = IntStream.range(0, lines.get(0).size())
                .mapToObj(i -> Pair.with(lines.get(0).get(i), lines.get(1).get(i)))
                .toList();

        BigInteger result = races.parallelStream()
                .map(pair -> quadratic(pair.getValue0(), pair.getValue1()))
                .reduce(BigInteger::multiply).orElseThrow();
        System.out.println("part01: " + result);
    }

    public static void part02() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day06-input.txt");
        assert resource != null;
        List<BigInteger> lines = Files.lines(Path.of(resource.getFile().substring(1)))
                .map(line ->
                        new BigInteger(line.substring(9).replaceAll(" ", "")))
                .toList();
        BigInteger time = lines.get(0);
        BigInteger distance = lines.get(1);

        BigInteger result = quadratic(time, distance);
        System.out.println("part02: " + result);
    }

    public static BigInteger quadratic(BigInteger _t, BigInteger _d) {
        BigDecimal t = new BigDecimal(_t);
        BigDecimal d = new BigDecimal(_d);
        BigDecimal q = t.pow(2).subtract(d.multiply(BigDecimal.valueOf(4))).sqrt(new MathContext(1000));
        BigInteger v1 = t.subtract(q).divide(BigDecimal.valueOf(2), RoundingMode.HALF_EVEN).setScale(0, RoundingMode.DOWN).toBigInteger();
        BigInteger v2 = t.add(q).divide(BigDecimal.valueOf(2), RoundingMode.HALF_EVEN).setScale(0, RoundingMode.UP).toBigInteger();
        return v2.subtract(v1).subtract(BigInteger.ONE);
    }
}