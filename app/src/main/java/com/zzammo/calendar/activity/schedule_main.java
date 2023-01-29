package com.zzammo.calendar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.zzammo.calendar.R;
import com.zzammo.calendar.adapter.ScheduleRVAdapter;
import com.zzammo.calendar.adapter.schedule_main_RVAdapter;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.database.room.ScheduleDatabase;
import com.zzammo.calendar.lunar.LunarCalendar;
import com.zzammo.calendar.util.Time;
import com.zzammo.calendar.weather.WeatherApiExplorer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class schedule_main extends AppCompatActivity {

    ImageView add_schedule;
    Context context;
    HashMap<LocalDate, HashMap<String, Integer>> weather;
    TextView temperature, lunardate, or;
    ImageView weatherView;
    MaterialCalendarView calendarView;
    ScheduleDatabase DB;
    RecyclerView scheduleRV;
    ArrayList<Schedule> scheduleArrayList;
    schedule_main_RVAdapter RVAdapter;
    LinearLayoutManager layoutManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_main);
        context = this;
        weather = new HashMap<>();

        calendarView = findViewById(R.id.calendarView);

        getSupportActionBar().setElevation(0); // appbar shadow remove
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 툴바 홈버튼 활성화
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_density_medium_24);

        temperature = findViewById(R.id.temperature);
        lunardate = findViewById(R.id.lunardate);
        or = findViewById(R.id.or);
        weatherView = findViewById(R.id.weather);
        add_schedule=findViewById(R.id.add_schedule);

        add_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), schedule.class);
                startActivity(intent);
            }
        });

        DB = ScheduleDatabase.getInstance(context);
        scheduleRV = findViewById(R.id.schedule_recyclerView);
        scheduleArrayList = new ArrayList<>();

        calendarView.setOnDateChangedListener((widget, date, selected) ->{
            String str_date = date.toString().substring(12,date.toString().length() - 1);
            LocalDate localDate = LocalDate.parse(str_date, DateTimeFormatter.ofPattern("yyyy-M-d"));
            Log.d("WeGlonD", str_date);
            LocalDate lunarDate = LocalDate.parse(LunarCalendar.Solar2Lunar(localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))), DateTimeFormatter.ofPattern("yyyyMMdd"));
            lunardate.setText("음력 " + lunarDate.getMonthValue() + "월 " + lunarDate.getDayOfMonth() + "일");
            ShowWeatherInfo(localDate);
            scheduleArrayList.clear();

            Long dateMills = Time.CalendarDayToMill(date);
            scheduleArrayList.clear();
            scheduleArrayList.addAll(Arrays.asList(DB.scheduleDao().loadAllScheduleDuring(dateMills, dateMills + Time.ONE_DAY)));
            Collections.sort(scheduleArrayList);
            RVAdapter.notifyDataSetChanged();
        });

        RVAdapter = new schedule_main_RVAdapter(scheduleArrayList);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        scheduleRV.setLayoutManager(layoutManager);
        scheduleRV.setAdapter(RVAdapter);
        RVAdapter.setOnItemClickListener(position -> {
//            바꿔야함
//            DB.scheduleDao().delete(scheduleArrayList.get(position));
//            scheduleArrayList.remove(position);
//            RVAdapter.notifyItemRemoved(position);
        });

        LocalDate localDate = LocalDate.parse(LunarCalendar.Solar2Lunar(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))), DateTimeFormatter.ofPattern("yyyyMMdd"));
        lunardate.setText("음력 " + localDate.getMonthValue() + "월 " + localDate.getDayOfMonth() + "일");
        getWeather(weather, 35.887390,128.611629);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_option, menu);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        Toast toast = Toast.makeText(getApplicationContext(),"", Toast.LENGTH_LONG);

        switch(item.getItemId())
        {
            case R.id.today_move:
                toast.setText("Select today_move");
                calendarView.setSelectedDate(CalendarDay.today());
                break;
            case R.id.name_search:
                toast.setText("Select name_search");
                break;
        }

        toast.show();

        return super.onOptionsItemSelected(item);
    }

    public void getWeather(HashMap<LocalDate, HashMap<String, Integer>> weather, double latitude, double longitude){
        WeatherApiExplorer weatherApiExplorer = new WeatherApiExplorer(context);
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    weatherApiExplorer.getWeather(weather, latitude, longitude);
                    Log.d("WeGlonD", weather.toString());
                    temperature.post(new Runnable() {
                        @Override
                        public void run() {
                            ShowWeatherInfo(LocalDate.now());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void ShowWeatherInfo(LocalDate date){
        if(weather.containsKey(date)) {
            or.setVisibility(View.VISIBLE);
            temperature.setVisibility(View.VISIBLE);
            weatherView.setVisibility(View.VISIBLE);

            if(weather.get(date).containsKey("TMP")){
                temperature.setText(weather.get(date).get("TMP").toString() + "ºC");
            }
            else{
                temperature.setText(weather.get(date).get("MAX") + "º/" + weather.get(date).get("MIN") + "ºC");
            }

            int pty = weather.get(date).get("PTY");
            int sky = weather.get(date).get("SKY");
            if (pty > 0) {
                if (pty == 1 || pty == 4) {
                    weatherView.setImageResource(R.drawable.rainy_2_svgrepo_com);
                } else if (pty == 2) {
                    weatherView.setImageResource(R.drawable.cloud_snow_rain_svgrepo_com);
                } else {
                    weatherView.setImageResource(R.drawable.snow_outline_svgrepo_com);
                }
            } else {
                if (sky == 1) {
                    weatherView.setImageResource(R.drawable.ic_baseline_wb_sunny_24);
                } else if (sky == 2) {
                    weatherView.setImageResource(R.drawable.cloudy_svgrepo_com);
                } else {
                    weatherView.setImageResource(R.drawable.ic_twotone_wb_cloudy_24);
                }
            }
        }
        else{
            or.setVisibility(View.GONE);
            temperature.setVisibility(View.GONE);
            weatherView.setVisibility(View.GONE);
        }
    }
}
