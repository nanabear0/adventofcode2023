package save.santa.day16;

import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day17-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();
        HashMap<Pair<Integer, Integer>, Integer> lossMap = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(i).length(); j++) {
                lossMap.put(Pair.with(j, i), lines.get(i).charAt(j) - '0');
            }
        }
        Pair<Integer, Integer> target = Pair.with(lines.get(0).length() - 1, lines.size() - 1);
        Map<Quartet<Integer, Integer, Integer, Integer>, Integer> distance = new HashMap<>();
        distance.put(Quartet.with(0, 0, 0, 0), 0);
        Set<Quartet<Integer, Integer, Integer, Integer>> finishedProcessing = new HashSet<>();
        int result;

        while (true) {
            Quartet<Integer, Integer, Integer, Integer> current = distance.entrySet().stream()
                    .filter(x -> !finishedProcessing.contains(x.getKey()))
                    .filter(x -> distance.containsKey(x.getKey()))
                    .min(Map.Entry.comparingByValue())
                    .orElseThrow().getKey();
            if (Objects.equals(current.getValue0(), target.getValue0()) && current.getValue1().equals(target.getValue1())) {
                result = distance.get(current);
                break;
            }

            List<Quartet<Integer, Integer, Integer, Integer>> nps =
                    Stream.of(Pair.with(0, 1), Pair.with(0, -1), Pair.with(1, 0), Pair.with(-1, 0))
                            .map(dir -> move(current, dir))
                            .filter(Objects::nonNull)
                            .filter(x -> lossMap.containsKey(Pair.with(x.getValue0(), x.getValue1())))
                            .filter(x -> !finishedProcessing.contains(x))
                            .toList();
            nps.forEach(np -> distance.merge(np, distance.get(current) + lossMap.get(Pair.with(np.getValue0(), np.getValue1())), Math::min));
            finishedProcessing.add(current);
        }

        System.out.println("part01: " + result);
    }

    public static Quartet<Integer, Integer, Integer, Integer> move(Quartet<Integer, Integer, Integer, Integer> initial, Pair<Integer, Integer> dir) {
        if (dir.getValue0() != 0 && initial.getValue2() / dir.getValue0() < 0) return null;
        if (dir.getValue1() != 0 && initial.getValue3() / dir.getValue1() < 0) return null;
        if (dir.getValue0() != 0 && initial.getValue2() + dir.getValue0() > 3) return null;
        if (dir.getValue1() != 0 && initial.getValue3() + dir.getValue1() > 3) return null;
        return Quartet.with(
                initial.getValue0() + dir.getValue0(),
                initial.getValue1() + dir.getValue1(),
                dir.getValue0() == 0 ? 0 : initial.getValue2() + dir.getValue0(),
                dir.getValue1() == 0 ? 0 : initial.getValue3() + dir.getValue1());
    }
}