package save.santa.day20;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day20-input.txt");
        assert resource != null;
        List<String> lines = Files.lines(Path.of(resource.getFile())).toList();
        HashMap<String, Pair<String, List<String>>> modules = new HashMap<>();
        for (String line : lines) {
            String[] lineSplit = line.split(" +-> +");
            if ("broadcaster".equals(lineSplit[0]))
                modules.put("broadcaster", Pair.with("", Arrays.stream(lineSplit[1].split(" *, *")).toList()));
            else
                modules.put(lineSplit[0].substring(1), Pair.with(lineSplit[0].substring(0, 1), Arrays.stream(lineSplit[1].split(" *, *")).toList()));
        }


        System.out.println("part01: " + part01(modules));
        System.out.println("part02: " + Stream.of(3943L, 3917L, 4057L, 3931L).reduce(ArithmeticUtils::lcm));
    }

    public static HashMap<String, HashMap<String, Boolean>> fillState(HashMap<String, Pair<String, List<String>>> modules) {
        HashMap<String, HashMap<String, Boolean>> states = new HashMap<>();
        for (var entry : modules.entrySet()) {
            for (var targetModuleKey : entry.getValue().getValue1()) {
                if (!modules.containsKey(targetModuleKey)) continue;
                var targetModule = modules.get(targetModuleKey);
                var history = states.computeIfAbsent(targetModuleKey, (key) -> new HashMap<>());
                if ("%".equals(targetModule.getValue0())) {
                    history.put("any", Boolean.FALSE);
                } else if ("&".equals(targetModule.getValue0())) {
                    history.put(entry.getKey(), Boolean.FALSE);
                }
            }
        }
        return states;
    }

    public static long part01(HashMap<String, Pair<String, List<String>>> modules) {
        HashMap<String, HashMap<String, Boolean>> states = fillState(modules);

        long highPulses = 0L;
        long lowPulses = 0L;
        for (long i = 0; i < 1_000L; i++) {
            List<Triplet<String, String, Boolean>> pulses = List.of(Triplet.with("button", "broadcaster", Boolean.FALSE));
            while (!pulses.isEmpty()) {
                for (var pulse : pulses) {
                    if (pulse.getValue2()) highPulses++;
                    else lowPulses++;
                }

                List<Triplet<String, String, Boolean>> nextPulses = new ArrayList<>();
                for (var pulse : pulses) {
                    String source = pulse.getValue0();
                    String target = pulse.getValue1();
                    Boolean value = pulse.getValue2();

                    if (!modules.containsKey(target)) continue;
                    if ("broadcaster".equals(target)) {
                        modules.get(target).getValue1().forEach(next -> nextPulses.add(Triplet.with(target, next, value)));
                    } else if ("&".equals(modules.get(target).getValue0())) {
                        var history = states.get(target);
                        history.put(source, value);
                        var nextValue = !history.values().stream().allMatch(x -> x);
                        modules.get(target).getValue1().forEach(next -> nextPulses.add(Triplet.with(target, next, nextValue)));
                    } else {
                        var history = states.get(target);
                        if (!value) {
                            var nextValue = history.compute("any", (key, oldValue) -> !oldValue);
                            modules.get(target).getValue1().forEach(next -> nextPulses.add(Triplet.with(target, next, nextValue)));
                        }
                    }
                }
                pulses = nextPulses;
            }
        }
        return lowPulses * highPulses;
    }
}