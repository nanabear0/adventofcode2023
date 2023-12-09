package save.santa.day09;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("part01: " + doPart(false));
        System.out.println("part02: " + doPart(true));
    }

    public static int doPart(boolean reversed) throws IOException {
        return Files.lines(Path.of(Main.class.getClassLoader().getResource("day09-input.txt").getFile()))
                .map(line -> Arrays.stream(line.split(" +"))
                        .map(Integer::parseInt).toList())
                .map(startingNumbers ->
                        Stream.iterate(reversed ? startingNumbers.reversed() : startingNumbers,
                                        last -> !last.stream().allMatch(n -> n == 0),
                                        previous -> IntStream.range(0, previous.size() - 1).mapToObj(i -> previous.get(i + 1) - previous.get(i)).toList())
                                .map(List::getLast).reduce(Integer::sum).orElse(0))
                .reduce(Integer::sum)
                .orElse(0);
    }
}