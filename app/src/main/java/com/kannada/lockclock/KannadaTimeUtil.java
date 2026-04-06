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

    public static String toKannadaNumber(int number) {
        StringBuilder sb = new StringBuilder();
        String numStr = String.valueOf(number);
        for (char c : numStr.toCharArray()) {
            int digit = c - '0';
            if (digit >= 0 && digit <= 9) {
                sb.append(KANNADA_DIGITS[digit]);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String getKannadaPeriod(int hour24) {
        if (hour24 >= 4 && hour24 < 12) return "ಬೆಳಿಗ್ಗೆ";
        if (hour24 >= 12 && hour24 < 16) return "ಮಧ್ಯಾಹ್ನ";
        if (hour24 >= 16 && hour24 < 19) return "ಸಂಜೆ";
        return "ರಾತ್ರಿ";
    }

    public static String getKannadaTime(Calendar cal) {
        int hour24 = cal.get(Calendar.HOUR_OF_DAY);
        int hour12 = cal.get(Calendar.HOUR);
        if (hour12 == 0) hour12 = 12;
        int minute = cal.get(Calendar.MINUTE);

        String period = getKannadaPeriod(hour24);
        String hh = toKannadaNumber(hour12);
        String mm = (minute < 10) ? "೦" + toKannadaNumber(minute) : toKannadaNumber(minute);

        return period + " " + hh + ":" + mm;
    }

    public static String getKannadaTimeWithSeconds(Calendar cal) {
        int hour24 = cal.get(Calendar.HOUR_OF_DAY);
        int hour12 = cal.get(Calendar.HOUR);
        if (hour12 == 0) hour12 = 12;
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        String period = getKannadaPeriod(hour24);
        String hh = toKannadaNumber(hour12);
        String mm = (minute < 10) ? "೦" + toKannadaNumber(minute) : toKannadaNumber(minute);
        String ss = (second < 10) ? "೦" + toKannadaNumber(second) : toKannadaNumber(second);

        return period + " " + hh + ":" + mm + ":" + ss;
    }

    public static String getKannadaDate(Calendar cal) {
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        return toKannadaNumber(day) + " " + KANNADA_MONTHS[month] + " " + toKannadaNumber(year);
    }

    public static String getKannadaDay(Calendar cal) {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return KANNADA_DAYS[dayOfWeek];
    }
}
