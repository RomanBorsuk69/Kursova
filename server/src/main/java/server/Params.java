package server;

import javax.servlet.http.HttpServletRequest;

/** Технічне читання параметрів HTTP-запиту з зрозумілими повідомленнями про помилки (без предметних правил). */
final class Params {
    private Params() {
    }

    static long longValue(HttpServletRequest req, String name) {
        String v = required(req, name);
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Параметр «" + name + "» має бути цілим числом");
        }
    }

    static int intValue(HttpServletRequest req, String name) {
        String v = required(req, name);
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Параметр «" + name + "» має бути цілим числом");
        }
    }

    static double doubleValue(HttpServletRequest req, String name) {
        String v = required(req, name);
        try {
            return Double.parseDouble(v.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Параметр «" + name + "» має бути числом");
        }
    }

    static RequestState state(String value) {
        try {
            return RequestState.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Невідомий стан заявки: " + value);
        }
    }

    private static String required(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException("Не задано параметр «" + name + "»");
        }
        return v.trim();
    }
}
