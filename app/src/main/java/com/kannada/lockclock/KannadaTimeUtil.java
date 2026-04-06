package com.kannada.lockclock;

import java.util.Calendar;

public class KannadaTimeUtil {

    private static final String[] KANNADA_DIGITS = {
        "೦", "೧", "೨", "೩", "೪", "೫", "೬", "೭", "೮", "೯"
    };

    private static final String[] KANNADA_DAYS = {
        "",
        "ಭಾನುವಾರ",
        "ಸೋಮವಾರ",
        "ಮಂಗಳವಾರ",
        "ಬುಧವಾರ",
        "ಗುರುವಾರ",
        "ಶುಕ್ರವಾರ",
        "ಶನಿವಾರ"
    };

    private static final String[] KANNADA_MONTHS = {
        "ಜನವರಿ", "ಫೆಬ್ರವರಿ", "ಮಾರ್ಚ್", "ಏಪ್ರಿಲ್",
        "ಮೇ", "ಜೂನ್", "ಜುಲೈ", "ಆಗಸ್ಟ್",
        "ಸೆಪ್ಟೆಂಬರ್", "ಅಕ್ಟೋಬರ್", "ನವೆಂಬರ್", "ಡಿಸೆಂಬರ್"
    };

    public static String toKannada(int number) {
        StringBuilder sb = new StringBuilder();
        for (char c : String.valueOf(number).toCharArray()) {
            int d = c - '0';
            sb.append((d >= 0 && d <= 9) ? KANNADA_DIGITS[d] : c);
        }
        return sb.toString();
    }

    public static String pad(int n) {
        return (n < 10) ? "೦" + toKannada(n) : toKannada(n);
    }

    public static String getPeriod(int h) {
        if (h >= 4 && h < 12) return "ಬೆಳಿಗ್ಗೆ";
        if (h >= 12 && h < 16) return "ಮಧ್ಯಾಹ್ನ";
        if (h >= 16 && h < 19) return "ಸಂಜೆ";
        return "ರಾತ್ರಿ";
    }

    public static String getTime(Calendar c) {
        int h24 = c.get(Calendar.HOUR_OF_DAY);
        int h12 = c.get(Calendar.HOUR);
        if (h12 == 0) h12 = 12;
        return getPeriod(h24) + " " + toKannada(h12) + ":" + pad(c.get(Calendar.MINUTE)) + ":" + pad(c.get(Calendar.SECOND));
    }

    public static String getTimeLarge(Calendar c) {
        int h12 = c.get(Calendar.HOUR);
        if (h12 == 0) h12 = 12;
        return toKannada(h12) + ":" + pad(c.get(Calendar.MINUTE));
    }

    public static String getSeconds(Calendar c) {
        return pad(c.get(Calendar.SECOND));
    }

    public static String getDate(Calendar c) {
        return toKannada(c.get(Calendar.DAY_OF_MONTH)) + " " + KANNADA_MONTHS[c.get(Calendar.MONTH)] + " " + toKannada(c.get(Calendar.YEAR));
    }

    public static String getDay(Calendar c) {
        return KANNADA_DAYS[c.get(Calendar.DAY_OF_WEEK)];
    }

    public static String getPeriodOnly(Calendar c) {
        return getPeriod(c.get(Calendar.HOUR_OF_DAY));
    }
}
