package com.zzammo.calendar.util;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Time {
    public final static Long ONE_DAY = (long) 8.64e+7;
    public final static Long ONE_HOUR = (long) 3.6e+6;
    public final static Long ONE_MINUTE = (long) 60000;

    public static String millToHM(Long mills){
        String pattern = "HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(new Timestamp(mills));
    }

    public static String MillToDate(long mills) {
        String pattern = "yyyyMMdd";
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


    public static Calendar MillToCalendar(Long mills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mills);

        return calendar;
    }
    public static Long CalendarToMill(Calendar calendar) {
        return calendar.getTimeInMillis();
    }

    public static CalendarDay CalendarToCalendarDay(Calendar calendar){
        CalendarDay date = CalendarDay.from(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.DATE));
        return date;
    }

    public static CalendarDay MillToCalendarDay(Long mills){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        CalendarDay date = CalendarToCalendarDay(calendar);
        return date;
    }
    public static long CalendarDayToMill(CalendarDay date) {

        String pattern = "yyyy-M-d";
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

    public static String CalendarToYM(Calendar calendar){
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;

        return year+"년 "+month+"월";
    }

    public static Long YMDToMills(String ymd){
        Integer int_ymd = Integer.parseInt(ymd);
        int year = int_ymd/10000;
        int month = (int_ymd%10000)/100;
        int date = int_ymd%100;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, date, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long LocalDateToMill(LocalDate localDate){
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long LocalDateTimeToMills(LocalDateTime localDateTime){
        Calendar cal = Calendar.getInstance();
        cal.set(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth(),localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDateTime MillsToLocalDateTime(Long mills){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mills);
        LocalDateTime localDateTime = LocalDateTime.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), 0);
        return localDateTime;
    }

    public static void setZero(Calendar cal){
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

}
