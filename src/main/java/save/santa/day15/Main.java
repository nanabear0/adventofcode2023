package save.santa.day15;

import org.javatuples.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day15-input.txt");
        assert resource != null;
        String line = Files.lines(Path.of(resource.getFile().substring(1))).findFirst().orElseThrow();

        System.out.println("part01: " + part01(line));
        System.out.println("part02: " + part02(line));
    }

    public static int part02(String line) throws IOException {
        HashMap<Integer, List<Pair<String, Integer>>> hashmap = new HashMap<>();
        for (String op : line.split(",")) {
            if (op.contains("-")) {
                var label = op.substring(0, op.length() - 1);
                var hash = hashFn(label);
                if (hashmap.containsKey(hash)) {
                    var position = IntStream.range(0, hashmap.get(hash).size()).filter(x -> hashmap.get(hash).get(x).getValue0().equals(label)).findFirst();
                    if (position.isPresent()) hashmap.get(hash).remove(position.getAsInt());
                }
            } else if (op.contains("=")) {
                var sp = op.split("=");
                var label = sp[0];
                var hash = hashFn(label);
                var focalLength = Integer.parseInt(sp[1]);
                var box = hashmap.computeIfAbsent(hashFn(label), (_key) -> new ArrayList<>());
                var position = IntStream.range(0, hashmap.get(hash).size()).filter(x -> hashmap.get(hash).get(x).getValue0().equals(label)).findFirst();
                if (position.isPresent()) box.set(position.getAsInt(), Pair.with(label, focalLength));
                else box.add(Pair.with(label, focalLength));
            }
        }

        return hashmap.entrySet().stream().map((entry) ->
                IntStream.range(0, entry.getValue().size()).map(i -> (entry.getKey() + 1) * (i + 1) * entry.getValue().get(i).getValue1()).sum()
        ).reduce(Integer::sum).orElse(0);
    }

    public static int hashFn(String s) {
        return s.chars().reduce(0, (pv, cv) -> ((pv + cv) * 17) % 256);
    }

    public static int part01(String line) throws IOException {
        return Arrays.stream(line.split(","))
                .map(Main::hashFn)
                .reduce(Integer::sum)
                .orElseThrow();
    }

}
