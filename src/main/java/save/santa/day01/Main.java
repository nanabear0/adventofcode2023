package save.santa.day01;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        part01();
        part02();
    }

    public static void part01() throws IOException {
        System.out.printf("part01: %d\n", process(Pattern.compile("[123456789]")));
    }

    public static void part02() throws IOException {
        System.out.printf("part02: %d\n", process(Pattern.compile("one|two|three|four|five|six|seven|eight|nine|1|2|3|4|5|6|7|8|9")));
    }

    public static int process(Pattern pattern) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("day01-input.txt");
        try (Stream<String> lines = Files.lines(Path.of(resource.getFile().substring(1)))) {
            return lines.map(line -> doThing(pattern, line)).reduce(Integer::sum).orElse(0);
        }
    }

    public static Map<String, String> replaces = new HashMap<>() {{
        put("one", "1");
        put("two", "2");
        put("three", "3");
        put("four", "4");
        put("five", "5");
        put("six", "6");
        put("seven", "7");
        put("eight", "8");
        put("nine", "9");
    }};

    public static int doThing(Pattern pattern, String input) {
        Matcher regexMatcher = pattern.matcher(input);
        List<String> l = new LinkedList<>();
        if (regexMatcher.find()) {
            do {
                String match = regexMatcher.group();
                if (Character.isDigit(match.charAt(0))) l.add(match);
                else l.add(replaces.get(match));
            } while (regexMatcher.find(regexMatcher.start() + 1));
        }
        return Integer.parseInt(l.getFirst() + l.getLast());
    }
}