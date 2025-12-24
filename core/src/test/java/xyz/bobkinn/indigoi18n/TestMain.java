package xyz.bobkinn.indigoi18n;

import xyz.bobkinn.indigoi18n.data.Translation;

import java.util.List;

public class TestMain {
    public static void main(String[] args) {
        var t = "%{:==10n}";
        Indigo.INSTANCE.texts.put("test", "ru", Translation.create(t));

        // Warm-up + correctness check
        var r = Indigo.parse("ru", "test", List.of(1234.46));
        assert r.equals("1 234,46");

        int iterations = 5_000_000;

        List<Object> a = List.of(1234.46);
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            Indigo.parse("ru", "test", a);
        }
        long end = System.nanoTime();

        long totalTimeNs = end - start;
        double avgTimeNs = (double) totalTimeNs / iterations;

        System.out.println("Average Indigo.parse execution time:");
        System.out.println(avgTimeNs + " ns");
        System.out.println((avgTimeNs / 1_000.0) + " µs");
    }
}

