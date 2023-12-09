package save.santa.day09;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("part01: " + doPart(false));
        System.out.println("part02: " + doPart(true));
    }

    public static int doPart(boolean reversed) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day09-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();

        return lines.stream().map(line -> Arrays.stream(line.split(" +")).map(Integer::parseInt).toList()).map(startingNumbers -> {
            List<List<Integer>> stages = new ArrayList<>() {{
                add(reversed ? startingNumbers.reversed() : startingNumbers);
            }};
            do {
                stages.add(IntStream.range(0, stages.getLast().size() - 1).mapToObj(i -> stages.getLast().get(i + 1) - stages.getLast().get(i)).toList());
            } while (!stages.getLast().stream().allMatch(n -> n == 0));
            return stages.stream().map(List::getLast).reduce(Integer::sum).orElse(0);
        }).reduce(Integer::sum).orElseThrow();
    }
}