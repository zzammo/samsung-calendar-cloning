package com.zzammo.calendar.custom_calendar.teest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zzammo.calendar.R;
import com.zzammo.calendar.activity.MainActivity;
import com.zzammo.calendar.custom_calendar.teest.adapter.ViewPagerAdapter;
import com.zzammo.calendar.custom_calendar.teest.data.CalendarDate;
import com.zzammo.calendar.custom_calendar.teest.data.PageData;
import com.zzammo.calendar.custom_calendar.teest.view.CustomCalendar;
import com.zzammo.calendar.custom_calendar.utils.Keys;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.dialog.ScheduleDialog;
import com.zzammo.calendar.schedule_event.MakeSchedule;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

public class ViewPagerActivity extends AppCompatActivity {

    CustomCalendar calendar;

    Calendar preSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        calendar = findViewById(R.id.activity_view_pager_customCalendar);
        calendar.setOnDateClickListener(date -> {
            Calendar cal = date.getCalendar();

            if (preSelectedDate == null || preSelectedDate.compareTo(cal) != 0) {
                preSelectedDate = cal;
            } else if (date.getSchedules().size() == 0) {
                Intent it = new Intent(this, MakeSchedule.class);
                it.putExtra("date", Time.CalendarToMill(cal));
                it.putExtra("month", cal.get(Calendar.MONTH)+1);
                it.putExtra("day", cal.get(Calendar.DATE));
                startActivity(it);
            } else {
                ScheduleDialog oDialog = new ScheduleDialog(this,
                        Time.CalendarToMill(cal));
                oDialog.show();
            }
        });
        calendar.setActivity(this);

        findViewById(R.id.activity_view_pager_add_btn).setOnClickListener(v -> {
            calendar.setSundayColor(getColor(R.color.text_black));
            calendar.setActivity(this);
        });



    }

}