package com.zzammo.calendar.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.zzammo.calendar.R;
import com.zzammo.calendar.adapter.ScheduleRVAdapter;
import com.zzammo.calendar.dialog.ScheduleDialog;
import com.zzammo.calendar.holiday.ApiExplorer;
import com.zzammo.calendar.room.Schedule;
import com.zzammo.calendar.room.ScheduleDatabase;
import com.zzammo.calendar.util.Time;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
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
        RVAdapter.setOnItemClickListener((v, position) -> {
            DB.scheduleDao().delete(scheduleArrayList.get(position));
            scheduleArrayList.remove(position);
            RVAdapter.notifyItemRemoved(position);
        });

        calendarView.setOnDateLongClickListener((widget, date) -> {
                ScheduleDialog oDialog = new ScheduleDialog(MainActivity.this,
                        Time.CalendarDayToMill(date));
                oDialog.show();
        });

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
                if(!selected) return;

                Long dateMills = Time.CalendarDayToMill(date);
//                Toast.makeText(context, dateMills+" "+(dateMills+Time.ONE_DAY), Toast.LENGTH_SHORT).show();

                scheduleArrayList.clear();
                scheduleArrayList.addAll(Arrays.asList(DB.scheduleDao().loadAllScheduleDuring(dateMills, dateMills + Time.ONE_DAY)));
                Collections.sort(scheduleArrayList);
                RVAdapter.notifyDataSetChanged();
        });

        new Thread(){
            public void run(){
                try {
                    ApiExplorer apiExplorer = new ApiExplorer(context);
                    apiExplorer.getHolidays(2023,1);
                } catch (IOException | XmlPullParserException e) {
                    Log.d("WeGlonD", "Holiday fail "+e);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CalendarDay date = calendarView.getSelectedDate();

        if(date == null) return;

        Long dateMills = Time.CalendarDayToMill(date);

        scheduleArrayList.clear();
        scheduleArrayList.addAll(Arrays.asList(DB.scheduleDao().loadAllScheduleDuring(dateMills, dateMills + Time.ONE_DAY)));
        Collections.sort(scheduleArrayList);
        RVAdapter.notifyDataSetChanged();
    }
}