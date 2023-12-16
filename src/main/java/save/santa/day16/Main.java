package save.santa.day16;

import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day16-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();
        Map<Pair<Integer, Integer>, Character> obstacles = new HashMap<>();
        Quartet<Integer, Integer, Integer, Integer> range = Quartet.with(0, lines.size(), 0, lines.get(0).length());
        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(i).length(); j++) {
                obstacles.put(Pair.with(j, i), lines.get(i).charAt(j));
            }
        }

        System.out.println("part01: " + findEnergizedTilesStartingFrom(Quartet.with(-1, 0, 1, 0), range, obstacles));

        long max = 0;
        for (int y = 0; y < range.getValue3(); y++) {
            max = Math.max(max, findEnergizedTilesStartingFrom(Quartet.with(-1, y, 1, 0), range, obstacles));
            max = Math.max(max, findEnergizedTilesStartingFrom(Quartet.with(range.getValue1(), y, -1, 0), range, obstacles));
        }
        for (int x = 0; x < range.getValue3(); x++) {
            max = Math.max(max, findEnergizedTilesStartingFrom(Quartet.with(x, -1, 0, 1), range, obstacles));
            max = Math.max(max, findEnergizedTilesStartingFrom(Quartet.with(x, range.getValue3(), 0, -1), range, obstacles));
        }
        System.out.println("part02: " + max);
    }

    private static long findEnergizedTilesStartingFrom(Quartet<Integer, Integer, Integer, Integer> startingBeam, Quartet<Integer, Integer, Integer, Integer> range, Map<Pair<Integer, Integer>, Character> obstacles) {
        Set<Quartet<Integer, Integer, Integer, Integer>> processedBeams = new HashSet<>();
        List<Quartet<Integer, Integer, Integer, Integer>> beams = new ArrayList<>();
        beams.add(startingBeam);
        while (!beams.isEmpty()) {
            List<Quartet<Integer, Integer, Integer, Integer>> nextSetOfBeams = new ArrayList<>();

            for (var beam : beams) {
                var nextBeam = addDirToBeam(beam);
                if (!isBeamInRange(nextBeam, range)) continue;

                switch (obstacles.get(Pair.with(nextBeam.getValue0(), nextBeam.getValue1()))) {
                    case '.':
                        nextSetOfBeams.add(nextBeam);
                        break;
                    case '/':
                        if (nextBeam.getValue3() == 1) nextSetOfBeams.add(nextBeam.setAt2(-1).setAt3(0));
                        else if (nextBeam.getValue3() == -1) nextSetOfBeams.add(nextBeam.setAt2(1).setAt3(0));
                        else if (nextBeam.getValue2() == 1) nextSetOfBeams.add(nextBeam.setAt2(0).setAt3(-1));
                        else if (nextBeam.getValue2() == -1) nextSetOfBeams.add(nextBeam.setAt2(0).setAt3(1));
                        break;
                    case '\\':
                        if (nextBeam.getValue3() == 1) nextSetOfBeams.add(nextBeam.setAt2(1).setAt3(0));
                        else if (nextBeam.getValue3() == -1) nextSetOfBeams.add(nextBeam.setAt2(-1).setAt3(0));
                        else if (nextBeam.getValue2() == 1) nextSetOfBeams.add(nextBeam.setAt2(0).setAt3(1));
                        else if (nextBeam.getValue2() == -1) nextSetOfBeams.add(nextBeam.setAt2(0).setAt3(-1));
                        break;
                    case '-':
                        if (nextBeam.getValue3() != 0) {
                            nextSetOfBeams.add(nextBeam.setAt2(-1).setAt3(0));
                            nextSetOfBeams.add(nextBeam.setAt2(1).setAt3(0));
                        } else {
                            nextSetOfBeams.add(nextBeam);
                        }
                        break;
                    case '|':
                        if (nextBeam.getValue2() != 0) {
                            nextSetOfBeams.add(nextBeam.setAt2(0).setAt3(-1));
                            nextSetOfBeams.add(nextBeam.setAt2(0).setAt3(1));
                        } else {
                            nextSetOfBeams.add(nextBeam);
                        }
                        break;
                }
            }
            beams = nextSetOfBeams.stream()
                    .filter(beam -> !processedBeams.contains(beam))
                    .toList();
            processedBeams.addAll(beams);
        }

        return processedBeams.stream().map(pb -> Pair.with(pb.getValue0(), pb.getValue1())).distinct().count();
    }

    public static boolean isBeamInRange(Quartet<Integer, Integer, Integer, Integer> beam, Quartet<Integer, Integer, Integer, Integer> range) {
        return beam.getValue0() >= range.getValue0() &&
                beam.getValue0() < range.getValue1() &&
                beam.getValue1() >= range.getValue2() &&
                beam.getValue1() < range.getValue3();
    }

    public static Quartet<Integer, Integer, Integer, Integer> addDirToBeam(Quartet<Integer, Integer, Integer, Integer> beam) {
        return Quartet.with(beam.getValue0() + beam.getValue2(), beam.getValue1() + beam.getValue3(), beam.getValue2(), beam.getValue3());
    }

}
