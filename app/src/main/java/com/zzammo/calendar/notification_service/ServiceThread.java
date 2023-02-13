package com.zzammo.calendar.notification_service;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.AfterTask;
import com.zzammo.calendar.util.Time;
import com.zzammo.calendar.weather.WeatherApiExplorer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class ServiceThread extends Thread{
    Handler handler;
    boolean isRun = true;
    Context context;
    Database database;
    LocalDate today;
    int message;
    HashMap<LocalDate, HashMap<String, Integer>> weather;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ServiceThread(Handler handler, Context context){
        this.handler = handler;
        this.context = context;
        database = new Database(context);
        today = LocalDate.of(2022,1,1);
        weather = new HashMap<>();
        message = 0;
    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void run(){
        //반복적으로 수행할 작업을 한다.
        while(isRun){
            alarmCheck();
            try{
                Thread.sleep(60000); //1분씩 쉰다.
            }catch (Exception e) {}
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void alarmCheck(){
        ArrayList<Schedule> AfterSchedules = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        int currentSecond = LocalDateTime.now().getSecond();
        if(currentSecond != 0){
            now.plusMinutes(1);
            try {
                Thread.sleep((60 - currentSecond) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("WeGlonD", now.toString());
        if(!today.equals(now.toLocalDate())){
            today = now.toLocalDate();
            //날씨 가져오기
            weather.clear();
            //현위치 불러와서 넣기
            getWeather(weather, 35.887390, 128.611629, new AfterTask() {
                @Override
                public void ifSuccess(Object result) {
                    if(weather.get(LocalDate.now()).get("PTY") > 0){
                        message = 1;
                    }
                    else message = 0;
                }

                @Override
                public void ifFail(Object result) {
                    message = 0;
                }
            });
        }
        //LocalDateTime now = LocalDateTime.of(2023,2,8,7,0,0,0);
        database.loadAllScheduleStartedAt(Time.LocalDateTimeToMills(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(23,0,0, 0))), AfterSchedules);
        for(Schedule s : AfterSchedules){
            if(s.alarm == null || s.alarm.equals(""))continue;
            String[] alarm_time_substr = s.alarm.substring(0, s.alarm.length() - 2).split(", ");
            LocalDateTime begin = Time.MillsToLocalDateTime(s.begin_ms);
            if(s.departAlarm) {
                begin=begin.minusHours(s.need_hour); begin=begin.minusMinutes(s.need_minute); begin=begin.minusSeconds(s.need_second);
            }
            begin=begin.withSecond(0).withNano(0);
            for(String str : alarm_time_substr){
                LocalDateTime time = begin;
                if(str.equals("일정 시작시간")){

                }
                else if(str.substring(str.length()-1).equals("분")){
                    int val = Integer.parseInt(str.substring(0,str.length() - 1));
                    time = time.minusMinutes(val);
                }
                else if(str.substring(str.length()-1).equals("간")){
                    int val = Integer.parseInt(str.substring(0,str.length()-2));
                    time = time.minusHours(val);
                }
                else if(str.substring(str.length()-1).equals("일")){
                    int val = Integer.parseInt(str.substring(0,str.length()-1));
                    time = time.minusDays(val);
                }
                else if(str.substring(str.length()-1).equals("주")){
                    int val = Integer.parseInt(str.substring(0,str.length()-1));
                    time = time.minusWeeks(val);
                }

                if(time.equals(now)){
                    Log.d("WeGlonD", "Alarm!! - " + s.title);
                    handler.sendEmptyMessage(message);//쓰레드에 있는 핸들러에게 메세지를 보냄
                }
            }
        }
    }

    public void getWeather(HashMap<LocalDate, HashMap<String, Integer>> weather, double latitude, double longitude, AfterTask task){
        WeatherApiExplorer weatherApiExplorer = new WeatherApiExplorer(context);
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    weatherApiExplorer.getWeather(weather, latitude, longitude);
                    Log.d("WeGlonD", weather.toString());
                    task.ifSuccess(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}