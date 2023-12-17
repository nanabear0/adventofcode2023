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
//        System.out.println("part01: " + doPart(target, lossMap, 0, 3));
        System.out.println("part02: " + doPart(target, lossMap, 4, 10));
    }

    private static long doPart(Pair<Integer, Integer> target, HashMap<Pair<Integer, Integer>, Integer> lossMap, int minTurn, int maxStraight) {
        Map<Quartet<Integer, Integer, Integer, Integer>, Integer> distance = new HashMap<>();
        Set<Quartet<Integer, Integer, Integer, Integer>> finished = new HashSet<>();
        PriorityQueue<Quartet<Integer, Integer, Integer, Integer>> frontier = new PriorityQueue<>(Comparator.comparing(distance::get));
        distance.put(Quartet.with(0, 0, 0, 0), 0);
        frontier.add(Quartet.with(0, 0, 0, 0));
        int result;

        while (true) {
            Quartet<Integer, Integer, Integer, Integer> current = frontier.remove();
            if (Objects.equals(current.getValue0(), target.getValue0()) && current.getValue1().equals(target.getValue1())) {
                result = distance.get(current);
                break;
            }

            List<Pair<Quartet<Integer, Integer, Integer, Integer>, Integer>> nps =
                    Stream.of(Pair.with(0, 1), Pair.with(0, -1), Pair.with(1, 0), Pair.with(-1, 0))
                            .map(dir -> move(current, dir, minTurn, maxStraight))
                            .filter(Objects::nonNull)
                            .filter(x -> lossMap.containsKey(Pair.with(x.getValue0(), x.getValue1())))
                            .filter(x -> !finished.contains(x))
                            .map(np -> Pair.with(np, distance.get(current) + lossMap.get(Pair.with(np.getValue0(), np.getValue1()))))
                            .toList();
            var nfa = nps.stream().filter(np -> distance.getOrDefault(np.getValue0(), Integer.MAX_VALUE) > np.getValue1()).map(Pair::getValue0).toList();
            nps.forEach(np -> distance.merge(np.getValue0(), np.getValue1(), Math::min));
            finished.add(current);
            frontier.addAll(nfa);
        }

        return result;
    }

    public static Quartet<Integer, Integer, Integer, Integer> move(Quartet<Integer, Integer, Integer, Integer> initial, Pair<Integer, Integer> dir, int minTurn, int maxStraight) {
        if (!(initial.getValue2() == 0 && initial.getValue3() == 0)) {
            if (dir.getValue0() != 0 && initial.getValue2() * dir.getValue0() < 0) return null;
            if (dir.getValue1() != 0 && initial.getValue3() * dir.getValue1() < 0) return null;
            boolean isTurn = (dir.getValue0() != 0 ^ initial.getValue2() != 0) || (dir.getValue1() != 0 ^ initial.getValue3() != 0);
            if (isTurn && Math.abs(initial.getValue2() + initial.getValue3()) < minTurn) return null;
            if (!isTurn && Math.abs(initial.getValue2() + initial.getValue3()) >= maxStraight) return null;
        }

        return Quartet.with(
                initial.getValue0() + dir.getValue0(),
                initial.getValue1() + dir.getValue1(),
                dir.getValue0() == 0 ? 0 : initial.getValue2() + dir.getValue0(),
                dir.getValue1() == 0 ? 0 : initial.getValue3() + dir.getValue1());
    }
}