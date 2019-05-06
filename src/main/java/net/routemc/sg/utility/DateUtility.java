package net.routemc.sg.utility;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtility {

    public static String dateFormat() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"));
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        df.setTimeZone(cal.getTimeZone());
        return df.format(cal.getTime());
    }

    public static String dateTimeFormatByDate(Date date){
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
    }

    public static String getDateFormatByTimestamp(Timestamp timestamp){
        Date date = new Date(timestamp.getTime());
        return dateTimeFormatByDate(date);
    }

    public static String getDateAsJST() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"));
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        df.setTimeZone(cal.getTimeZone());
        return df.format(cal.getTime());
    }

    public static String getTimeAsJST() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"));
        DateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(cal.getTimeZone());
        return df.format(cal.getTime()) + " JST";
    }
}