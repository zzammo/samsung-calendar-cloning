package com.zzammo.calendar.custom_calendar.teest.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zzammo.calendar.R;
import com.zzammo.calendar.custom_calendar.teest.data.CalendarDate;
import com.zzammo.calendar.custom_calendar.teest.data.PageData;
import com.zzammo.calendar.custom_calendar.teest.fragment.PageFragment;
import com.zzammo.calendar.custom_calendar.teest.view.CustomCalendar;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.util.Time;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {

    Database DB;
    Context context;

    ArrayList<PageData> data;
    CustomCalendar.OnDataClickListener listener;
    int sundayColor, saturdayColor, holidayColor, todayColor, basicColor;
    boolean setSchedule;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<PageData> data) {
        super(fragmentActivity);
        this.data = data;

        context = fragmentActivity.getApplicationContext();

        todayColor = context.getResources().getColor(R.color.text_white);
        sundayColor = context.getResources().getColor(R.color.red);
        saturdayColor = context.getResources().getColor(R.color.blue);
        basicColor = context.getResources().getColor(R.color.text_black);
        setSchedule = true;

        DB = new Database(fragmentActivity.getApplicationContext());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        for (CalendarDate cd : data.get(position).getDays()){
            if (cd.getCalendar() == null) continue;
            if (cd.getSchedules() != null) break;

            cd.setSchedules(new ArrayList<>());
            Long begin = Time.CalendarToMill(cd.getCalendar());
            DB.loadAllScheduleDuring(begin, begin+ Time.ONE_DAY, cd.getSchedules());
        }
        return new PageFragment(data.get(position), listener,
                sundayColor, saturdayColor, holidayColor, todayColor, basicColor);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setListener(CustomCalendar.OnDataClickListener listener) {
        this.listener = listener;
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

    public void setSetSchedule(boolean setSchedule) {
        this.setSchedule = setSchedule;
    }
}
