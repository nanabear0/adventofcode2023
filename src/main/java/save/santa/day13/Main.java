package save.santa.day13;

import org.javatuples.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("part01: " + doPart(Main::processBasic));
        System.out.println("part02: " + doPart(Main::processWithSmudge));
    }

    public static int doPart(Function<List<String>, Pair<Set<Integer>, Set<Integer>>> processingFn) throws IOException {
        return Arrays.stream(
                        String.join(
                                "\n",
                                Files.lines(Path.of(Main.class.getClassLoader().getResource("day13-input.txt").getFile())).toList()
                        ).split("\n\n"))
                .map(s -> Arrays.stream(s.split("\n")).collect(Collectors.toCollection(ArrayList::new)))
                .map(processingFn)
                .map(pair -> pair.getValue1().stream().findFirst().orElse(0) * 100 +
                        pair.getValue0().stream().findFirst().orElse(0))
                .reduce(Integer::sum)
                .orElseThrow();
    }

    public static Pair<Set<Integer>, Set<Integer>> processWithSmudge(List<String> note) {
        Pair<Set<Integer>, Set<Integer>> base = processBasic(note);
        for (int i = 0; i < note.size(); i++) {
            for (int j = 0; j < note.get(0).length(); j++) {
                replaceNth(note, i, j);
                Pair<Set<Integer>, Set<Integer>> diff = processBasic(note);
                replaceNth(note, i, j);

                diff.getValue0().removeAll(base.getValue0());
                diff.getValue1().removeAll(base.getValue1());
                if (!diff.getValue0().isEmpty() || !diff.getValue1().isEmpty()) {
                    return diff;
                }
            }
        }

        throw new RuntimeException("You fucked up somewhere");
    }

    public static void replaceNth(List<String> note, int i, int j) {
        char replacement = note.get(i).charAt(j) == '#' ? '.' : '#';
        note.set(i, note.get(i).substring(0, j) + replacement + note.get(i).substring(j + 1));
    }

    public static Pair<Set<Integer>, Set<Integer>> processBasic(List<String> note) {
        int sizeY = note.size();
        int sizeX = note.get(0).length();
        return Pair.with(
                IntStream.range(0, sizeX - 1)
                        .filter(i -> IntStream.range(0, i + 1)
                                .allMatch(j -> i + j + 1 >= sizeX || i - j < 0 ||
                                        note.stream().map(line -> line.charAt(i - j)).toList()
                                                .equals(note.stream().map(line -> line.charAt(i + j + 1)).toList())))
                        .map(v -> v + 1)
                        .boxed().collect(Collectors.toSet()),
                IntStream.range(0, sizeY - 1)
                        .filter(i -> IntStream.range(0, i + 1)
                                .allMatch(j -> i + j + 1 >= sizeY || i - j < 0 ||
                                        note.get(i - j).equals(note.get(i + j + 1))))
                        .map(v -> v + 1)
                        .boxed().collect(Collectors.toSet()));
    }
}