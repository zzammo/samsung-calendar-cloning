package com.zzammo.calendar.custom_calendar.teest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    View preSelectedView;
    Calendar preSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        calendar = findViewById(R.id.activity_view_pager_customCalendar);
        calendar.setOnDateClickListener((view, date) -> {
            Calendar cal = date.getCalendar();



            if (preSelectedDate == null || preSelectedDate.compareTo(cal) != 0) {
                if (preSelectedView != null){
                    preSelectedView.setBackgroundColor(getColor(R.color.white));
                }
                preSelectedView = view;
                preSelectedDate = cal;
                view.setBackgroundColor(getColor(R.color.text_white));
            } else if (date.getSchedules().size() == 0) {
                preSelectedView.setBackgroundColor(getColor(R.color.white));
                Intent it = new Intent(this, MakeSchedule.class);
                it.putExtra("date", Time.CalendarToMill(cal));
                it.putExtra("month", cal.get(Calendar.MONTH)+1);
                it.putExtra("day", cal.get(Calendar.DATE));
                startActivity(it);
            } else {
                preSelectedView.setBackgroundColor(getColor(R.color.white));
                ScheduleDialog oDialog = new ScheduleDialog(this,
                        Time.CalendarToMill(cal));
                oDialog.show();
            }
        });
        calendar.setInterceptTouchEvent(new CustomCalendar.OnInterceptTouchEvent() {
            @Override
            public void interceptTouchEvent(MotionEvent ev) {
                Toast.makeText(getApplicationContext(), "!", Toast.LENGTH_SHORT).show();
            }
        });
        calendar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Toast.makeText(getApplicationContext(), "?", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        calendar.setActivity(this);

        findViewById(R.id.activity_view_pager_add_btn).setOnClickListener(v -> {
            calendar.setSundayColor(getColor(R.color.text_black));
            calendar.setActivity(this);
        });



    }

}