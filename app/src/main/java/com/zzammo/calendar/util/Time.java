package com.zzammo.calendar.util;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {
    public final static Long ONE_DAY = (long) 8.64e+7;
    public final static Long ONE_HOUR = (long) 3.6e+6;
    public final static Long ONE_MINUTE = (long) 60000;

    public static String millToHM(Long mills){
        String pattern = "HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String date = formatter.format(new Timestamp(mills));
        return date;
    }

    public static String MillToDate(long mills) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String date = formatter.format(new Timestamp(mills));

        return date;
    }

    public static long DateToMill(String date) {

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        Date trans_date = null;
        try {
            trans_date = formatter.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return trans_date.getTime();
    }

    public static long CalendarDayToMill(CalendarDay date) {

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        String dateString = date.getYear()+"-"+date.getMonth()+"-"+date.getDay();

        Date trans_date = null;
        try {
            trans_date = formatter.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return trans_date.getTime();
    }
}
