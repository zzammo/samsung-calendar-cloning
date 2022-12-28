package com.zzammo.calendar.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.zzammo.calendar.R;
import com.zzammo.calendar.adapter.ScheduleRVAdapter;
import com.zzammo.calendar.dialog.ScheduleDialog;
import com.zzammo.calendar.room.Schedule;
import com.zzammo.calendar.room.ScheduleDatabase;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    ScheduleDatabase DB;

    MaterialCalendarView calendarView;

    RecyclerView scheduleRV;
    ScheduleRVAdapter RVAdapter;
    LinearLayoutManager layoutManager;
    ArrayList<Schedule> scheduleArrayList;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;

        DB = ScheduleDatabase.getInstance(context);

        calendarView = findViewById(R.id.calendarView);

        scheduleRV = findViewById(R.id.schedule_recyclerView);
        scheduleArrayList = new ArrayList<>();
        RVAdapter = new ScheduleRVAdapter(scheduleArrayList);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        scheduleRV.setLayoutManager(layoutManager);
        scheduleRV.setAdapter(RVAdapter);

        calendarView.setOnDateLongClickListener((widget, date) -> {
                ScheduleDialog oDialog = new ScheduleDialog(MainActivity.this,
                        Time.CalendarDayToMill(date),
                        (AlarmManager) getSystemService(ALARM_SERVICE));
                oDialog.show();
        });

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
                if(!selected) return;

                Long dateMills = Time.CalendarDayToMill(date);
                Toast.makeText(context, dateMills+" "+(dateMills+Time.ONE_DAY), Toast.LENGTH_SHORT).show();

                scheduleArrayList.clear();
                scheduleArrayList.addAll(Arrays.asList(DB.scheduleDao().loadAllScheduleDuring(dateMills, dateMills + Time.ONE_DAY)));
                Collections.sort(scheduleArrayList);
                RVAdapter.notifyDataSetChanged();
        });
    }
}