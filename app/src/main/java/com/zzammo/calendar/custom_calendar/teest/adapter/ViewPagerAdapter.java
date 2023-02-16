package com.zzammo.calendar.custom_calendar.teest.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zzammo.calendar.custom_calendar.teest.data.CalendarDate;
import com.zzammo.calendar.custom_calendar.teest.data.PageData;
import com.zzammo.calendar.custom_calendar.teest.fragment.PageFragment;
import com.zzammo.calendar.custom_calendar.teest.view.CustomCalendar;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ViewPagerAdapter extends FragmentStateAdapter {

    Database DB;

    ArrayList<PageData> data;
    CustomCalendar.OnDateClick dateClick;
    int sundayColor, saturdayColor, holidayColor, todayColor, basicColor;
    boolean showSchedule, showHoliday;
    boolean setBackgroundFirst, isFirst;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<PageData> data) {
        super(fragmentActivity);
        this.data = data;

        DB = new Database(fragmentActivity.getApplicationContext());
    }

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<PageData> data,
                            boolean showSchedule, boolean showHoliday,
                            int sundayColor, int saturdayColor, int holidayColor, int todayColor, int basicColor,
                            boolean setBackgroundFirst) {
        super(fragmentActivity);
        this.data = data;
        this.showSchedule = showSchedule;
        this.showHoliday = showHoliday;
        this.sundayColor = sundayColor;
        this.saturdayColor = saturdayColor;
        this.holidayColor = holidayColor;
        this.todayColor = todayColor;
        this.basicColor = basicColor;
        this.setBackgroundFirst = setBackgroundFirst;
        isFirst = true;

        DB = new Database(fragmentActivity.getApplicationContext());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (showSchedule){
            for (CalendarDate cd : data.get(position).getDays()){
                if (cd.getSchedules() != null) break;

                cd.setSchedules(new ArrayList<>());
                Long begin = cd.date;
                DB.loadAllScheduleDuring(begin, begin+Time.ONE_DAY-1, cd.getSchedules());
                Collections.sort(cd.getSchedules());
            }
        }
        if (showHoliday){
            for (CalendarDate cd : data.get(position).getDays()){
                if (cd.getHolidays() != null) break;

                cd.setHolidays(new ArrayList<>());
                Long begin = cd.date;
                cd.getHolidays().addAll(DB.HoliLocalDB.holidayDao().searchHolidayByDate(begin, begin+Time.ONE_DAY-1));
                Collections.sort(cd.getHolidays());
            }
        }

//        boolean bf = setBackgroundFirst && isFirstTime && position == firstDatePage;
//        Log.d("Dirtfy", bf +" bf "+isFirstTime+" ft "+(position == firstDatePage)+" ba "+setBackgroundFirst);
//        Log.d("Dirtfy", position+" p "+firstDatePage);
//        if (bf) {
//            isFirstTime = false;
//            Log.d("Dirtfy", "VPA");
//        }

        boolean tb = setBackgroundFirst && isFirst && position == data.size()/2;
        if(tb)
            isFirst = false;

        return new PageFragment(data.get(position), dateClick,
                sundayColor, saturdayColor, holidayColor, todayColor, basicColor, tb);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setDateClickListener(CustomCalendar.OnDateClick dateClick) {
        this.dateClick = dateClick;
    }

    public void setSundayColor(int sundayColor) {
        this.sundayColor = sundayColor;
    }

    public void setSaturdayColor(int saturdayColor) {
        this.saturdayColor = saturdayColor;
    }

    public void setHolidayColor(int holidayColor) {
        this.holidayColor = holidayColor;
    }

    public void setTodayColor(int todayColor) {
        this.todayColor = todayColor;
    }

    public void setBasicColor(int basicColor) {
        this.basicColor = basicColor;
    }

    public void setShowSchedule(boolean showSchedule) {
        this.showSchedule = showSchedule;
    }

    public ArrayList<PageData> getData() {
        return data;
    }
}
