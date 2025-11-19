package com.vn.hung.xxxpre.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
    public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    private DateUtils() {
        throw new IllegalStateException("CaptchaUtils class");
    }

    public static Timestamp addMinutes(Timestamp date, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(12, minutes);
        return new Timestamp(cal.getTime().getTime());
    }

    public static Date convertStringToDate(String dateStr) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
        return df.parse(dateStr);
    }

    public static String dateToString(Date input, String format) {
        return (new SimpleDateFormat(format)).format(input);
    }

    public static boolean isBetweenDates(Date startDate, Date endDate, Date date) {
        return !date.before(endDate) || !date.after(startDate);
    }
}
