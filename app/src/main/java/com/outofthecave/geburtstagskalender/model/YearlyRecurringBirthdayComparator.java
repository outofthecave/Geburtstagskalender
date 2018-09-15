package com.outofthecave.geburtstagskalender.model;

import java.util.Calendar;
import java.util.Comparator;

/**
 * {@link Comparator} for {@link Birthday}'s that takes into account that birthdays recur yearly
 * and therefore form a cycle rather than a linear scale. They can still be compared by cutting the
 * cycle at a given reference date and flattening it out into a scale.
 */
public final class YearlyRecurringBirthdayComparator implements Comparator<Birthday> {
    private final int referenceMonth;
    private final int referenceDay;

    private YearlyRecurringBirthdayComparator(int referenceMonth, int referenceDay) {
        this.referenceMonth = referenceMonth;
        this.referenceDay = referenceDay;
    }

    /**
     * Get a {@link YearlyRecurringBirthdayComparator} with today's date as the reference date.
     *
     * @return A new {@link YearlyRecurringBirthdayComparator} for today.
     */
    public static YearlyRecurringBirthdayComparator forReferenceDateToday() {
        Calendar now = Calendar.getInstance();
        int currentMonth = CalendarUtil.getOneBasedMonth(now);
        int currentDay = now.get(Calendar.DAY_OF_MONTH);
        return YearlyRecurringBirthdayComparator.forReferenceDate(currentMonth, currentDay);
    }

    /**
     * Get a {@link YearlyRecurringBirthdayComparator} that considers birthdays that coincide with
     * the reference date to be the smallest and birthdays that coincide with the day before the
     * reference date to be the greatest (because they would only recur the next year).
     * <p>
     * February 29 is treated as if it existed every year.
     *
     * @param month The one-based month of the reference date.
     * @param day   The one-based day of the reference date.
     * @return A new {@link YearlyRecurringBirthdayComparator} for the given reference date.
     */
    public static YearlyRecurringBirthdayComparator forReferenceDate(int month, int day) {
        return new YearlyRecurringBirthdayComparator(month, day);
    }

    @Override
    public int compare(Birthday lhs, Birthday rhs) {
        int cmp = getMonthOffset(lhs) - getMonthOffset(rhs);
        if (cmp == 0) {
            cmp = lhs.day - rhs.day;
        }
        return cmp;
    }

    private int getMonthOffset(Birthday birthday) {
        int monthOffset = birthday.month - referenceMonth;
        if (monthOffset < 0 || (monthOffset == 0 && birthday.day < referenceDay)) {
            monthOffset += 12;
        }
        return monthOffset;
    }
}
