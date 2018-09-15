package com.outofthecave.geburtstagskalender.model;

import java.util.Calendar;

public final class CalendarUtil {
    private CalendarUtil() {
    }

    public static int getOneBasedMonth(Calendar calendar) {
        return 1 + calendar.get(Calendar.MONTH) - Calendar.JANUARY;
    }

    public static int getMonthForCalendar(Birthday birthday) {
        return birthday.month - 1 + Calendar.JANUARY;
    }

    public static boolean isSameDay(Calendar lhs, Calendar rhs) {
        return lhs.get(Calendar.DAY_OF_YEAR) == rhs.get(Calendar.DAY_OF_YEAR)
                && lhs.get(Calendar.YEAR) == rhs.get(Calendar.YEAR)
                && lhs.get(Calendar.ERA) == rhs.get(Calendar.ERA);
    }
}
