package com.example.outofthecave.geburtstagskalender.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link YearlyRecurringBirthdayComparator}, which will execute on the development
 * machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class YearlyRecurringBirthdayComparatorTest {
    @Test
    public void testForReferenceJanuary1() throws Exception {
        Calendar referenceCalendar = new Calendar.Builder()
                // A leap year.
                .set(Calendar.YEAR, 2000)
                .set(Calendar.MONTH, Calendar.JANUARY)
                .set(Calendar.DAY_OF_MONTH, 1)
                .build();

        Calendar referenceEndCalendar = copyDate(referenceCalendar);
        referenceEndCalendar.add(Calendar.YEAR, 1);

        while (referenceCalendar.compareTo(referenceEndCalendar) < 0) {
            List<Birthday> expectedBirthdays = new ArrayList<>();
            Calendar calendar = copyDate(referenceCalendar);

            Calendar endCalendar = copyDate(calendar);
            endCalendar.add(Calendar.YEAR, 1);

            while (calendar.compareTo(endCalendar) < 0) {
                Birthday birthday = new Birthday();
                birthday.month = CalendarUtil.getOneBasedMonth(calendar);
                birthday.day = calendar.get(Calendar.DAY_OF_MONTH);
                expectedBirthdays.add(birthday);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            List<Birthday> birthdays = new ArrayList<>(expectedBirthdays);
            Collections.shuffle(birthdays);
            int referenceMonth = CalendarUtil.getOneBasedMonth(referenceCalendar);
            int referenceDay = referenceCalendar.get(Calendar.DAY_OF_MONTH);
            YearlyRecurringBirthdayComparator comparator = YearlyRecurringBirthdayComparator.forReferenceDate(referenceMonth, referenceDay);
            Collections.sort(birthdays, comparator);

            assertEquals(
                    String.format("Reference date: %s\nExpected: %s\nActual:   %s\n",
                            referenceCalendar.getTime(), expectedBirthdays, birthdays),
                    expectedBirthdays,
                    birthdays);

            referenceCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private static Calendar copyDate(Calendar calendar) {
        return new Calendar.Builder()
                .set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                .set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                .set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
                .build();
    }
}
