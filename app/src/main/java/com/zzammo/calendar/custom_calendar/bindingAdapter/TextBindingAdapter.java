package com.zzammo.calendar.custom_calendar.bindingAdapter;
import android.app.Activity;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import androidx.databinding.BindingAdapter;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.zzammo.calendar.R;
import com.zzammo.calendar.activity.HolidayDecorator;
import com.zzammo.calendar.activity.MainActivity;
import com.zzammo.calendar.custom_calendar.utils.DateFormat;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TextBindingAdapter {
    public static Calendar calendar = Calendar.getInstance();

    @BindingAdapter({"setCalendarHeaderText"})
    public static void setCalendarHeaderText(TextView view, Long date) {
        try {
            if (date != null) {
                view.setText(DateFormat.getDate(date, DateFormat.CALENDAR_HEADER_FORMAT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter({"setScheduleText"})
    public static void setScheduleText(TextView view, Schedule x){
        try {
            if (x != null) {
                view.setText(x.title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter({"setDayText"})
    public static void setDayText(TextView view, Calendar calendar) {
        try {
            if (calendar != null) {
                GregorianCalendar gregorianCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                view.setText(DateFormat.getDate(gregorianCalendar.getTimeInMillis(), DateFormat.DAY_FORMAT));
                CalendarDay day = Time.CalendarToCalendarDay(calendar);
                if(DecorateSunday(day)||IsHoliday(day))view.setTextColor(Color.RED);
                //if(DecorateSunday(day))view.setTextColor(Color.RED);
                else if(DecorateSaturday(day))view.setTextColor(Color.BLUE);
                if(DecorateDay(day))view.setTextColor(Color.LTGRAY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean DecorateSaturday(CalendarDay day) {
        Long mill = Time.CalendarDayToMill(day);
        calendar.setTimeInMillis(mill);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == Calendar.SATURDAY;
    }

    public static boolean DecorateSunday(CalendarDay day) {
        Long mill = Time.CalendarDayToMill(day);
        calendar.setTimeInMillis(mill);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == Calendar.SATURDAY;
    }

    public static boolean DecorateDay(CalendarDay day) {
        return day.equals(CalendarDay.today());
    }

    public static boolean IsHoliday(CalendarDay day) {
        return MainActivity.HolidayDates.contains(day);//해당 달꺼만 보이게 해놨음
    }
    /*public MySelectorDecorator(Activity context) {
        drawable=context.getResources().getDrawable(R.drawable.my_selector);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }*/
}