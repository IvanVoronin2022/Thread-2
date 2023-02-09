import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {


        List<Thread> threads1 = new ArrayList<>();
        threads1.add(new Thread(() -> {
            int maxKey = 0;
            int maxValue = 0;
            int number = 1;
            synchronized (sizeToFreq) {
                while (!Thread.interrupted()) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        if (Thread.interrupted()) {
                            e.printStackTrace();
                        } else {
                            break;
                        }
                    }
                    for (Map.Entry<Integer, Integer> map : sizeToFreq.entrySet()) {
                        if (map.getValue() > maxValue) {
                            maxKey = map.getKey();
                            maxValue = map.getValue();
                        }
                    }
                    System.out.println("На " + number + " итерации: максимум повторений " + maxKey + " (встретилось " + maxValue + " раз)");
                    number++;
                }
            }
        }));
        threads1.get(threads1.size() - 1).start();

        List<Thread> threads2 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            threads2.add(new Thread(() -> {
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
                    sizeToFreq.notify();
                }
            }));
            threads2.get(threads2.size() - 1).start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //threads2.get(threads2.size() - 1).join();
        }
        //Thread.sleep(100000);
        for (Thread thread : threads2) {
            thread.join(); //
        }
        threads1.get(threads1.size() - 1).interrupt();

        int maxValue = Collections.max(sizeToFreq.values());

        List<Integer> maxValueKeys = sizeToFreq.entrySet().stream()
                .filter(entry -> entry.getValue() == maxValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        System.out.println("Самое частое количество повторений " + maxValueKeys + " (встретилось " + sizeToFreq.get(maxValueKeys.get(0)) + " раз)");

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
