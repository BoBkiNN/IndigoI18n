package xyz.bobkinn.indigoi18n.template;

import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.template.arg.DateFormatUtil;

import java.time.*;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class TestDateFormatUtil {

    @Test
    void testNumericFields() {
        LocalDateTime dt = LocalDateTime.of(2025, 12, 21, 8, 5, 9);

        // unpadded
        assertEquals("8", DateFormatUtil.format(dt, 'h', Locale.US));
        assertEquals("5", DateFormatUtil.format(dt, 'm', Locale.US));
        assertEquals("9", DateFormatUtil.format(dt, 's', Locale.US));

        // padded
        assertEquals("08", DateFormatUtil.format(dt, 'H', Locale.US));
        assertEquals("05", DateFormatUtil.format(dt, 'M', Locale.US));
        assertEquals("09", DateFormatUtil.format(dt, 'S', Locale.US));
    }

    @Test
    void testLocaleDependentFormatting() {
        ZonedDateTime dt = ZonedDateTime.of(
                2025, 12, 21, 21, 30, 0, 0,
                ZoneId.of("UTC")
        );

        String usLong = DateFormatUtil.format(dt, 'N', Locale.US);
        String frLong = DateFormatUtil.format(dt, 'N', Locale.FRANCE);

        assertNotNull(usLong);
        assertNotNull(frLong);

        // Check that the formatted strings differ (month names or day names should appear)
        assertNotEquals(usLong, frLong);
    }


    @Test
    void testDefaultLocaleFormatting() {
        LocalDateTime dt = LocalDateTime.of(2025, 12, 21, 21, 30);
        String rootFormat = DateFormatUtil.format(dt, '?', null);
        assertNotNull(rootFormat);
    }

    @Test
    void testTimezoneFields() {
        ZonedDateTime zdt = ZonedDateTime.of(
                2025, 12, 21, 21, 30, 0, 0,
                ZoneId.of("Europe/Moscow")
        );

        String zoneName = DateFormatUtil.format(zdt, 'z', null);
        String zoneOffset = DateFormatUtil.format(zdt, 'Z', null);

        assertEquals("Europe/Moscow", zoneName);
        assertEquals("+0300", zoneOffset);
    }

    @Test
    void testLegacyDate() {
        Date date = new Date(1735001400000L); // corresponds to 21 Dec 2025 21:30:00 UTC+3

        String h = DateFormatUtil.format(date, 'h', Locale.US);
        String H = DateFormatUtil.format(date, 'H', Locale.US);
        String m = DateFormatUtil.format(date, 'm', Locale.US);
        String M = DateFormatUtil.format(date, 'M', Locale.US);
        String s = DateFormatUtil.format(date, 's', Locale.US);
        String S = DateFormatUtil.format(date, 'S', Locale.US);

        assertNotNull(h);
        assertNotNull(H);
        assertNotNull(m);
        assertNotNull(M);
        assertNotNull(s);
        assertNotNull(S);

        String n = DateFormatUtil.format(date, 'n', Locale.US);
        String N = DateFormatUtil.format(date, 'N', Locale.US);

        assertNotNull(n);
        assertNotNull(N);
    }
}
