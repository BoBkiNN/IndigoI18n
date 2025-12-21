package xyz.bobkinn.indigoi18n.template.arg;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.text.*;

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
            case 'n' -> formatLocale(dt, locale, FormatStyle.MEDIUM);

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
        if (dt instanceof LocalTime) return ((LocalTime) dt).getHour();
        if (dt instanceof LocalDateTime) return ((LocalDateTime) dt).getHour();
        if (dt instanceof ZonedDateTime) return ((ZonedDateTime) dt).getHour();
        if (dt instanceof OffsetDateTime) return ((OffsetDateTime) dt).getHour();
        if (dt instanceof Date) {
            Calendar c = Calendar.getInstance();
            c.setTime((Date) dt);
            return c.get(Calendar.HOUR_OF_DAY);
        }
        return 0;
    }

    private static int getMinute(Object dt) {
        if (dt instanceof LocalTime) return ((LocalTime) dt).getMinute();
        if (dt instanceof LocalDateTime) return ((LocalDateTime) dt).getMinute();
        if (dt instanceof ZonedDateTime) return ((ZonedDateTime) dt).getMinute();
        if (dt instanceof OffsetDateTime) return ((OffsetDateTime) dt).getMinute();
        if (dt instanceof Date) {
            Calendar c = Calendar.getInstance();
            c.setTime((Date) dt);
            return c.get(Calendar.MINUTE);
        }
        return 0;
    }

    private static int getSecond(Object dt) {
        if (dt instanceof LocalTime) return ((LocalTime) dt).getSecond();
        if (dt instanceof LocalDateTime) return ((LocalDateTime) dt).getSecond();
        if (dt instanceof ZonedDateTime) return ((ZonedDateTime) dt).getSecond();
        if (dt instanceof OffsetDateTime) return ((OffsetDateTime) dt).getSecond();
        if (dt instanceof Date) {
            Calendar c = Calendar.getInstance();
            c.setTime((Date) dt);
            return c.get(Calendar.SECOND);
        }
        return 0;
    }

    // Locale-aware formatting
    private static String formatLocale(Object dt, Locale locale, FormatStyle style) {
        try {
            if (dt instanceof LocalDateTime) {
                return DateTimeFormatter.ofLocalizedDateTime(style)
                        .withLocale(locale)
                        .format((LocalDateTime) dt);
            } else if (dt instanceof LocalDate) {
                return DateTimeFormatter.ofLocalizedDate(style)
                        .withLocale(locale)
                        .format((LocalDate) dt);
            } else if (dt instanceof Date) {
                DateFormat df = DateFormat.getDateTimeInstance(style.ordinal(), style.ordinal(), locale);
                return df.format(dt);
            } else if (dt instanceof ZonedDateTime) {
                return DateTimeFormatter.ofLocalizedDateTime(style)
                        .withLocale(locale)
                        .format((ZonedDateTime) dt);
            } else if (dt instanceof OffsetDateTime) {
                return DateTimeFormatter.ofLocalizedDateTime(style)
                        .withLocale(locale)
                        .format((OffsetDateTime) dt);
            }
        } catch (Exception e) {
            return dt.toString();
        }
        return dt.toString();
    }

    // Timezone
    private static String getZoneName(Object dt) {
        if (dt instanceof ZonedDateTime) return ((ZonedDateTime) dt).getZone().toString();
        if (dt instanceof OffsetDateTime) return ((OffsetDateTime) dt).getOffset().toString();
        if (dt instanceof Date) {
            TimeZone tz = TimeZone.getDefault();
            return tz.getID();
        }
        return "";
    }

    private static String getZoneOffset(Object dt) {
        if (dt instanceof ZonedDateTime) {
            ZoneOffset off = ((ZonedDateTime) dt).getOffset();
            return formatOffset(off);
        }
        if (dt instanceof OffsetDateTime) {
            ZoneOffset off = ((OffsetDateTime) dt).getOffset();
            return formatOffset(off);
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
