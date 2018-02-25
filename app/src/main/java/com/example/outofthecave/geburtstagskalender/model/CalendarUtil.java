package com.example.outofthecave.geburtstagskalender.model;

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
}
