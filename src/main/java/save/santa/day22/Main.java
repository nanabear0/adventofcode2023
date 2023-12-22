package save.santa.day22;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day22-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();
        HashMap<Triplet<Integer, Integer, Integer>, Integer> blocks = new HashMap<>();
        HashMap<Integer, Pair<Set<Triplet<Integer, Integer, Integer>>, Set<Triplet<Integer, Integer, Integer>>>> shapes = new HashMap<>();

        int lineIndex = 0;
        for (var line : lines) {
            List<Integer> nums = Arrays.stream(line.split("[,~]")).map(Integer::parseInt).toList();
            Set<Triplet<Integer, Integer, Integer>> myBlocks = IntStream.range(nums.get(0), nums.get(3) + 1).boxed()
                    .flatMap(x -> IntStream.range(nums.get(1), nums.get(4) + 1).boxed()
                            .flatMap(y -> IntStream.range(nums.get(2), nums.get(5) + 1).boxed()
                                    .map(z -> Triplet.with(x, y, z)))).collect(Collectors.toSet());
            var bottomZ = myBlocks.stream().min(Comparator.comparing(Triplet::getValue2)).orElseThrow().getValue2();
            var bottoms = myBlocks.stream().filter(b -> Objects.equals(b.getValue2(), bottomZ)).collect(Collectors.toSet());
            shapes.put(lineIndex, Pair.with(myBlocks, bottoms));
            for (var b : myBlocks) {
                blocks.put(b, lineIndex);
            }
            lineIndex++;
        }

        boolean movedAnything;
        do {
            movedAnything = false;
            for (var shape : shapes.entrySet()) {
                var bottomZ = shape.getValue().getValue1().stream().findAny().orElseThrow().getValue2();
                int canFall = 0;
                for (int j = 1; j < bottomZ; j++) {
                    int finalJ = j;
                    if (shape.getValue().getValue1().stream().map(b -> b.setAt2(b.getValue2() - finalJ)).anyMatch(blocks::containsKey)) {
                        break;
                    }
                    canFall++;
                }
                if (canFall > 0) {
                    movedAnything = true;
                    shape.getValue().getValue0().forEach(blocks::remove);
                    int finalCanFall = canFall;
                    shape.setValue(Pair.with(
                            shape.getValue().getValue0().stream().map(b -> b.setAt2(b.getValue2() - finalCanFall)).collect(Collectors.toSet()),
                            shape.getValue().getValue1().stream().map(b -> b.setAt2(b.getValue2() - finalCanFall)).collect(Collectors.toSet())
                    ));
                    shape.getValue().getValue0().forEach(x -> blocks.put(x, shape.getKey()));
                }
            }
        } while (movedAnything);

        List<ArrayList<Integer>> imCriticalSupportOf = IntStream.range(0, shapes.size()).mapToObj(x -> new ArrayList<Integer>()).toList();
        List<HashSet<Integer>> imSupporting = IntStream.range(0, shapes.size()).mapToObj(x -> new HashSet<Integer>()).toList();
        List<HashSet<Integer>> imSupportedBy = IntStream.range(0, shapes.size()).mapToObj(x -> new HashSet<Integer>()).toList();
        for (var shape : shapes.entrySet()) {
            imSupportedBy.get(shape.getKey())
                    .addAll(shape.getValue().getValue1().stream()
                            .map(b -> b.setAt2(b.getValue2() - 1))
                            .map(blocks::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toCollection(HashSet::new)));
            if (imSupportedBy.get(shape.getKey()).size() == 1) {
                imCriticalSupportOf.get(imSupportedBy.get(shape.getKey()).stream().findFirst().get()).add(shape.getKey());
            }
            for (int mySupport : imSupportedBy.get(shape.getKey())) {
                imSupporting.get(mySupport).add(shape.getKey());
            }
        }
        System.out.println("part01: " + (shapes.size() - imCriticalSupportOf.stream().filter(x -> !x.isEmpty()).count()));

        int fallCount = 0;
        for (int i = 0; i < shapes.size(); i++) {
            Set<Integer> removedBlocks = new HashSet<>();
            removedBlocks.add(i);

            while (true) {
                Set<Integer> newRemovals = removedBlocks.stream().flatMap(removedBlock -> imSupporting.get(removedBlock).stream()).distinct()
                        .filter(removalTarget -> removedBlocks.containsAll(imSupportedBy.get(removalTarget)))
                        .collect(Collectors.toSet());
                if (!removedBlocks.addAll(newRemovals)) break;
            }
            fallCount += removedBlocks.size() - 1;
        }

        System.out.println("part02: " + fallCount);
    }
}