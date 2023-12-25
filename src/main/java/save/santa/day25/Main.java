package save.santa.day25;

import org.javatuples.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("part01: " + part01());
    }

    public static Long part01() throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day25-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();
        HashMap<String, HashSet<String>> edges = new HashMap<>();
        for (var line : lines) {
            String[] lineSplit = line.split(" *:? +");
            var std = lineSplit[0];
            var dests = Arrays.stream(lineSplit).skip(1).collect(Collectors.toCollection(HashSet::new));
            edges.compute(std, (key, set) -> {
                if (set == null) set = new HashSet<>();
                set.addAll(dests);
                return set;
            });
            for (var dest : dests) {
                edges.compute(dest, (key, set) -> {
                    if (set == null) set = new HashSet<>();
                    set.add(std);
                    return set;
                });
            }
        }

        List<Pair<String, Integer>> vtd = new ArrayList<>();
        List<String> verticesAsList = edges.keySet().stream().toList();
        for (String start : verticesAsList) {
            var distance = 0;
            HashMap<String, Integer> distances = new HashMap<>() {{
                put(start, 0);
            }};
            Set<String> frontier = new HashSet<>() {{
                add(start);
            }};

            while (!frontier.isEmpty()) {
                Set<String> newFrontier = frontier.stream()
                        .flatMap(front -> edges.get(front).stream())
                        .filter(next -> !distances.containsKey(next))
                        .filter(next -> !frontier.contains(next))
                        .collect(Collectors.toSet());
                distance++;
                for (var nf : newFrontier) {
                    distances.put(nf, distance);
                }
                frontier.clear();
                frontier.addAll(newFrontier);
            }
            vtd.add(Pair.with(start, distances.values().stream().reduce(Integer::sum).orElseThrow()));
        }

        List<Pair<String, String>> edgesToCut = new ArrayList<>();
        List<String> poop = vtd.stream().sorted(Comparator.comparing(Pair::getValue1)).limit(6).map(Pair::getValue0).toList();
        for (int i = 0; i < poop.size() - 1; i++) {
            for (int j = i + 1; j < poop.size(); j++) {
                if (edges.get(poop.get(i)).contains(poop.get(j))) {
                    edgesToCut.add(Pair.with(poop.get(i), poop.get(j)));
                }
            }
        }

        for (var edgeToCut : edgesToCut) {
            edges.get(edgeToCut.getValue0()).remove(edgeToCut.getValue1());
            edges.get(edgeToCut.getValue1()).remove(edgeToCut.getValue0());
        }

        var start = edgesToCut.get(0).getValue0();
        Set<String> visited = new HashSet<>() {{
            add(start);
        }};
        Set<String> frontier = new HashSet<>() {{
            add(start);
        }};

        while (!frontier.isEmpty()) {
            Set<String> newFrontier = frontier.stream()
                    .flatMap(front -> edges.get(front).stream())
                    .filter(next -> !visited.contains(next))
                    .filter(next -> !frontier.contains(next))
                    .collect(Collectors.toSet());
            visited.addAll(newFrontier);
            frontier.clear();
            frontier.addAll(newFrontier);
        }

        return ((long) visited.size() * (edges.size() - visited.size()));
    }
}