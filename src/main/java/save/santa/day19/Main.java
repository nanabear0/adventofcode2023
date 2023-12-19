package save.santa.day19;

import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day19-input.txt");
        assert resource != null;
        Iterator<String> lines = Files.lines(Path.of(resource.getFile())).toList().iterator();
        HashMap<String, List<Quartet<String, String, Long, String>>> operations = new HashMap<>();
        List<HashMap<String, Long>> parts = new ArrayList<>();
        while (lines.hasNext()) {
            String line = lines.next();
            if (line.isBlank()) break;
            String[] lineSplit = line.split("[{}]");
            String opKey = lineSplit[0];
            List<Quartet<String, String, Long, String>> steps = new ArrayList<>();
            for (String step : lineSplit[1].split(",")) {
                if (!step.contains(":")) {
                    steps.add(Quartet.with(null, null, null, step));
                } else {
                    int opIndex = Math.abs(step.indexOf('>') * step.indexOf('>'));
                    int colonIndex = step.indexOf(':');
                    steps.add(Quartet.with(
                            step.substring(0, opIndex),
                            step.charAt(opIndex) + "",
                            Long.parseLong(step.substring(opIndex + 1, colonIndex)),
                            step.substring(colonIndex + 1)
                    ));
                }
            }
            operations.put(opKey, steps);
        }
        while (lines.hasNext()) {
            String line = lines.next();
            HashMap<String, Long> props = new HashMap<>();
            for (String asdf : line.substring(1, line.length() - 1).split(",")) {
                props.put(asdf.substring(0, 1), Long.parseLong(asdf.substring(2)));
            }
            parts.add(props);
        }

        System.out.println("part01: " + part01(operations, parts));
        System.out.println("part02: " + part02(operations));
    }

    public static long part02(HashMap<String, List<Quartet<String, String, Long, String>>> operations) {
        List<Pair<String, HashMap<String, Pair<Long, Long>>>> unfinishedRanges = new ArrayList<>() {{
            add(Pair.with("in", new HashMap<>() {{
                put("x", Pair.with(1L, 4000L));
                put("m", Pair.with(1L, 4000L));
                put("a", Pair.with(1L, 4000L));
                put("s", Pair.with(1L, 4000L));
            }}));
        }};
        List<HashMap<String, Pair<Long, Long>>> acceptedRanges = new ArrayList<>();
        while (!unfinishedRanges.isEmpty()) {
            var currentRanges = unfinishedRanges.removeFirst();
            String currentOperation = currentRanges.getValue0();
            var ranges = currentRanges.getValue1();
            if (currentOperation.equals("A")) {
                acceptedRanges.add(ranges);
                continue;
            } else if (currentOperation.equals("R")) {
                continue;
            }

            for (var step : operations.get(currentOperation)) {
                String prop = step.getValue0();
                String op = step.getValue1();
                Long value = step.getValue2();
                String nextStep = step.getValue3();

                if (prop == null) {
                    unfinishedRanges.add(Pair.with(nextStep, ranges));
                    break;
                }

                var operationRange = ranges.get(prop);
                if (operationRange.getValue1() < value || operationRange.getValue0() > value) continue;

                HashMap<String, Pair<Long, Long>> nextOperationRange = new HashMap<>(ranges);
                if (op.equals(">")) {
                    nextOperationRange.put(prop, Pair.with(value + 1, operationRange.getValue1()));
                    ranges.put(prop, Pair.with(operationRange.getValue0(), value));
                } else {
                    nextOperationRange.put(prop, Pair.with(operationRange.getValue0(), value - 1));
                    ranges.put(prop, Pair.with(value, operationRange.getValue1()));
                }
                unfinishedRanges.add(Pair.with(nextStep, nextOperationRange));
            }
        }

        return acceptedRanges.stream()
                .map(r -> r.values().stream()
                        .map(p -> p.getValue1() - p.getValue0() + 1)
                        .reduce((v1, v2) -> v1 * v2).orElse(0L))
                .reduce(Long::sum).orElse(0L);
    }

    public static long part01(HashMap<String, List<Quartet<String, String, Long, String>>> operations, List<HashMap<String, Long>> parts) {
        int sum = 0;
        for (var part : parts) {
            var currentOpKey = "in";
            Boolean result = null;
            while (result == null) {
                for (var step : operations.get(currentOpKey)) {
                    if (step.getValue0() == null ||
                            (step.getValue1().equals(">") && part.get(step.getValue0()) > step.getValue2()) ||
                            (step.getValue1().equals("<") && part.get(step.getValue0()) < step.getValue2())
                    ) {
                        if (step.getValue3().equals("A")) {
                            result = Boolean.TRUE;
                        } else if (step.getValue3().equals("R")) {
                            result = Boolean.FALSE;
                        } else {
                            currentOpKey = step.getValue3();
                        }
                        break;
                    }
                }
            }
            if (result) {
                sum += part.values().stream().reduce(Long::sum).orElse(0L);
            }
        }

        return sum;
    }
}