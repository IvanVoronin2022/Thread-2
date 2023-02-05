import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                int count = 0;
                String route = generateRoute("RLRFR", 100);
                char[] chars = route.toCharArray();
                for (char ch : chars) {
                    if (ch == 'R') {
                        count++;
                    }
                }
                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(count)) {
                        int value = sizeToFreq.get(count) + 1;
                        sizeToFreq.put(count, value);
                    } else {
                        sizeToFreq.put(count, 1);
                    }
                }
            }).start();
        }

        int maxValue = Collections.max(sizeToFreq.values());

        List<Integer> maxValueKeys = sizeToFreq.entrySet().stream()
                .filter(entry -> entry.getValue() == maxValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        System.out.println("Самое частое количество повторений " + maxValueKeys + " (встретилось " +sizeToFreq.get(maxValueKeys.get(0)) + " раз)");
        System.out.println("Другие размеры:");
        sizeToFreq.entrySet().stream()
                .filter(entry -> !maxValueKeys.contains(entry.getKey()))
                .forEach(entry -> System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)"));


    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
