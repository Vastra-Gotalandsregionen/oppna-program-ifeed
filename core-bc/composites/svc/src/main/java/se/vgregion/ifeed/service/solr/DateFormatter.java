package se.vgregion.ifeed.service.solr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * @author anders
 * 
 */
public class DateFormatter {

    public enum DateFormat {
        SOLR_DATE_FORMAT(ISODateTimeFormat.dateTime()), W3CDTF(ISODateTimeFormat.dateTimeNoMillis()), TIME_ONLY(
                ISODateTimeFormat.hourMinuteSecond()), DATE_ONLY(ISODateTimeFormat.yearMonthDay());
        DateTimeFormatter formatter;

        DateFormat(DateTimeFormatter formatter) {
            this.formatter = formatter;
        }

        /*public DateTimeFormatter formatter() {
            return formatter;
        }*/
    }

    public void formatTextDate(String withThatKey, Map<String, Object> insideHere) {
        String issued = (String) insideHere.get(withThatKey);
        if (issued != null && issued.contains("T")) {
            String[] parts = issued.split(Pattern.quote("T"));
            issued = parts[0];
        }
        insideHere.put(withThatKey, issued);
    }

    public static String formatTextDate(String that) {
        if (that.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z")) {
            String[] parts = that.split(Pattern.quote("T"));
            String[] subDayParts = parts[1].split(Pattern.quote(":"));
            return parts[0] + " " + subDayParts[0] + ":" + subDayParts[1];
        }
        return that;
    }

    public static String formatTextToDateOnly(String that) {
        if (that.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z")) {
            String[] parts = that.split(Pattern.quote("T"));
            String[] subDayParts = parts[1].split(Pattern.quote(":"));
            return parts[0] + " " + subDayParts[0] + ":" + subDayParts[1];
        }
        return that;
    }

    public void formatDates(Map<String, Object> within) {
        formatTextDate("dc.date.validfrom", within);
        formatTextDate("dc.date.issued", within);
        formatTextDate("dc.date.validto", within);
    }

    public static String format(int year, final int month, final int date, final DateFormat dateFormat) {
        DateTime dt = new DateTime(year, month, date, 0, 0, 0, 0, DateTimeZone.UTC);
        return dt.toString(dateFormat.formatter);
    }

    /*public static Date parse(final String dateString, final DateFormat format) {
        return format.formatter.parseDateTime(dateString).toDate();
    }*/

    public static String format(final Date date, final DateFormat dateFormat) {
        DateTime dt = new DateTime(date.getTime(), DateTimeZone.UTC);
        return dt.toString(dateFormat.formatter);
    }

    public static String format(final Date date) {
        return format(date, DateFormat.W3CDTF);
    }

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static Date parse(final String thatTextDate) {
        try {
            return sdf.parse(thatTextDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isSomeDate(Object object) {
        if (object instanceof Date) {
            return true;
        }
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        // Add other parsing formats to try as you like:
        String[] dateFormats = {"yyyy-MM-dd", "MMM dd, yyyy hh:mm:ss Z"};
        for (String dateFormat : dateFormats) {
            try {
                out.format(new SimpleDateFormat(dateFormat).parse(object.toString()));
                return true;
            } catch (Exception ignore) {

            }
        }
        return false;
    }

    public static String toUtcDateIfPossible(String dateStr) {
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        // Add other parsing formats to try as you like:
        String[] dateFormats = {"yyyy-MM-dd", "MMM dd, yyyy hh:mm:ss Z"};
        for (String dateFormat : dateFormats) {
            try {
                return out.format(new SimpleDateFormat(dateFormat).parse(dateStr));
            } catch (ParseException ignore) { }
        }
        return dateStr;
    }

    /*public static boolean validate(final String solrDateString, final DateFormat dateFormat) {
        try {
            dateFormat.formatter.parseDateTime(solrDateString);
        } catch (UnsupportedOperationException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }*/

}
