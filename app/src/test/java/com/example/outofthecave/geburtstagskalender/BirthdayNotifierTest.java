package com.example.outofthecave.geburtstagskalender;

import com.example.outofthecave.geburtstagskalender.model.Birthday;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link BirthdayNotifier}, which will execute on the development
 * machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BirthdayNotifierTest {
    @Test
    public void testJoinNames() throws Exception {
        Birthday lisasBirthday = new Birthday();
        lisasBirthday.name = "Lisa";

        Birthday annesBirthday = new Birthday();
        annesBirthday.name = "Anne";

        Birthday paulsBirthday = new Birthday();
        paulsBirthday.name = "Paul";

        String conjunction = "and";

        List<Birthday> birthdays = Arrays.asList(lisasBirthday);
        String actual = BirthdayNotifier.joinNames(birthdays, conjunction, false);
        String expected = "Lisa";
        assertEquals(expected, actual);

        birthdays = Arrays.asList(lisasBirthday, annesBirthday);
        actual = BirthdayNotifier.joinNames(birthdays, conjunction, false);
        expected = "Lisa and Anne";
        assertEquals(expected, actual);

        birthdays = Arrays.asList(lisasBirthday, annesBirthday, paulsBirthday);
        actual = BirthdayNotifier.joinNames(birthdays, conjunction, false);
        expected = "Lisa, Anne and Paul";
        assertEquals(expected, actual);

        birthdays = Arrays.asList(lisasBirthday);
        actual = BirthdayNotifier.joinNames(birthdays, conjunction, true);
        expected = "Lisa";
        assertEquals(expected, actual);

        birthdays = Arrays.asList(lisasBirthday, annesBirthday);
        actual = BirthdayNotifier.joinNames(birthdays, conjunction, true);
        expected = "Lisa and Anne";
        assertEquals(expected, actual);

        birthdays = Arrays.asList(lisasBirthday, annesBirthday, paulsBirthday);
        actual = BirthdayNotifier.joinNames(birthdays, conjunction, true);
        expected = "Lisa, Anne, and Paul";
        assertEquals(expected, actual);
    }
}
