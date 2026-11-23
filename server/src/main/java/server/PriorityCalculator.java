package server;

public class PriorityCalculator {
    private static final double MAX_DOWNTIME_HOURS = 72.0;

    /** F1 — критичність обладнання: рівень 1..5 нормується до 0..100. */
    public static double factor1(int criticalityLevel) {
        return normalizeLevel(criticalityLevel);
    }

    /** F2 — тяжкість несправності: рівень 1..5 нормується до 0..100. */
    public static double factor2(int faultSeverity) {
        return normalizeLevel(faultSeverity);
    }

    /** F3 — вплив простою: 72 години і більше відповідають значенню 100. */
    public static double factor3(double downtimeHours) {
        if (!Double.isFinite(downtimeHours) || downtimeHours < 0) throw new IllegalArgumentException("Некоректна тривалість простою");
        return round(Math.min(100.0, downtimeHours / MAX_DOWNTIME_HOURS * 100.0));
    }

    public static double calculate(double f1, double f2, double f3) {
        check(f1); check(f2); check(f3);
        return round(0.40 * f1 + 0.35 * f2 + 0.25 * f3);
    }

    private static double normalizeLevel(int level) {
        if (level < 1 || level > 5) throw new IllegalArgumentException("Рівень має бути в діапазоні 1..5");
        return (level - 1) * 100.0 / 4;
    }

    private static void check(double value) {
        if (!Double.isFinite(value) || value < 0 || value > 100) throw new IllegalArgumentException("Фактор має бути в діапазоні 0..100");
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
