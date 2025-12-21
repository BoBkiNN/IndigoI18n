package xyz.bobkinn.indigoi18n.template.arg;

import java.time.*;
import java.time.format.*;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.text.*;

/**
 * Supports {@link TemporalAccessor}, {@link Date}, {@link Calendar}
 */
public class DateFormatUtil {

    public static String format(Object dt, char type, Locale locale) {
        if (locale == null) locale = Locale.ROOT;

        return switch (type) {
            // hour/minute/second
            case 'h' -> String.valueOf(getHour(dt));
            case 'H' -> pad2(getHour(dt));
            case 'm' -> String.valueOf(getMinute(dt));
            case 'M' -> pad2(getMinute(dt));
            case 's' -> String.valueOf(getSecond(dt));
            case 'S' -> pad2(getSecond(dt));

            // Locale-aware
            case 'N' -> formatLocale(dt, locale, FormatStyle.FULL);
            case 'B' -> formatLocale(dt, locale, FormatStyle.LONG);
            case 'n' -> formatLocale(dt, locale, FormatStyle.MEDIUM);
            case 'b' -> formatLocale(dt, locale, FormatStyle.SHORT);

            // Timezone
            case 'z' -> getZoneName(dt);
            case 'Z' -> getZoneOffset(dt);

            // default: root locale date+time
            default -> formatLocale(dt, Locale.ROOT, FormatStyle.MEDIUM);
        };
    }

    private static String pad2(int val) {
        return String.format("%02d", val);
    }

    // Numeric fields
    private static int getHour(Object dt) {
        if (dt instanceof TemporalAccessor t) return extractHour(t);
        if (dt instanceof Date d) {
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            return c.get(Calendar.HOUR_OF_DAY);
        }
        if (dt instanceof Calendar c) return c.get(Calendar.HOUR_OF_DAY);
        return 0;
    }

    private static int getMinute(Object dt) {
        if (dt instanceof TemporalAccessor t) return extractMinute(t);
        if (dt instanceof Date d) {
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            return c.get(Calendar.MINUTE);
        }
        if (dt instanceof Calendar c) return c.get(Calendar.MINUTE);
        return 0;
    }

    private static int getSecond(Object dt) {
        if (dt instanceof TemporalAccessor t) return extractSecond(t);
        if (dt instanceof Date d) {
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            return c.get(Calendar.SECOND);
        }
        if (dt instanceof Calendar c) return c.get(Calendar.SECOND);
        return 0;
    }

    private static int extractHour(TemporalAccessor t) {
        if (t.isSupported(ChronoField.HOUR_OF_DAY)) return t.get(ChronoField.HOUR_OF_DAY);
        if (t.isSupported(ChronoField.HOUR_OF_AMPM)) return t.get(ChronoField.HOUR_OF_AMPM);
        return 0;
    }

    private static int extractMinute(TemporalAccessor t) {
        return t.isSupported(ChronoField.MINUTE_OF_HOUR) ? t.get(ChronoField.MINUTE_OF_HOUR) : 0;
    }

    private static int extractSecond(TemporalAccessor t) {
        return t.isSupported(ChronoField.SECOND_OF_MINUTE) ? t.get(ChronoField.SECOND_OF_MINUTE) : 0;
    }

    // Locale-aware formatting
    private static String formatLocale(Object dt, Locale locale, FormatStyle style) {
        try {
            if (dt instanceof TemporalAccessor t) {
                if (t.isSupported(ChronoField.HOUR_OF_DAY) || t.isSupported(ChronoField.MINUTE_OF_HOUR)) {
                    return DateTimeFormatter.ofLocalizedDateTime(style)
                            .withLocale(locale)
                            .format(t);
                } else {
                    return DateTimeFormatter.ofLocalizedDate(style)
                            .withLocale(locale)
                            .format(t);
                }
            } else if (dt instanceof Date d) {
                DateFormat df = DateFormat.getDateTimeInstance(style.ordinal(), style.ordinal(), locale);
                return df.format(d);
            } else if (dt instanceof Calendar c) {
                DateFormat df = DateFormat.getDateTimeInstance(style.ordinal(), style.ordinal(), locale);
                df.setTimeZone(c.getTimeZone());
                return df.format(c.getTime());
            }
        } catch (Exception e) {
            return dt.toString();
        }
        return dt.toString();
    }

    // Timezone
    private static String getZoneName(Object dt) {
        if (dt instanceof ZonedDateTime zdt) return zdt.getZone().toString();
        if (dt instanceof OffsetDateTime odt) return odt.getOffset().toString();
        if (dt instanceof Calendar c) return c.getTimeZone().getID();
        if (dt instanceof Date) return TimeZone.getDefault().getID();
        return "";
    }

    private static String getZoneOffset(Object dt) {
        if (dt instanceof ZonedDateTime zdt) return formatOffset(zdt.getOffset());
        if (dt instanceof OffsetDateTime odt) return formatOffset(odt.getOffset());
        if (dt instanceof Calendar c) {
            TimeZone tz = c.getTimeZone();
            int offsetMillis = tz.getOffset(c.getTimeInMillis());
            return String.format("%+03d%02d",
                    offsetMillis / 3600000,
                    (Math.abs(offsetMillis) / 60000) % 60);
        }
        if (dt instanceof Date) {
            TimeZone tz = TimeZone.getDefault();
            int offsetMillis = tz.getRawOffset();
            return String.format("%+03d%02d",
                    offsetMillis / 3600000,
                    (Math.abs(offsetMillis) / 60000) % 60);
        }
        return "";
    }

    private static String formatOffset(ZoneOffset off) {
        int totalSeconds = off.getTotalSeconds();
        int hours = totalSeconds / 3600;
        int minutes = Math.abs((totalSeconds / 60) % 60);
        return String.format("%+03d%02d", hours, minutes);
    }
}
