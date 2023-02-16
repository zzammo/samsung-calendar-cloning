package com.zzammo.calendar.notification_service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.AfterTask;
import com.zzammo.calendar.util.Time;
import com.zzammo.calendar.weather.WeatherApiExplorer;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ServiceThread extends Thread {
    Handler handler;
    boolean isRun = true;
    Context context;
    Database database;
    LocalDate today;
    int message;
    HashMap<LocalDate, HashMap<String, Integer>> weather;
    int hasFineLocationPermission;
    int hasCoarseLocationPermission;
    FusedLocationProviderClient locationclient;
    LocationCallback locationcallback;
    Location curr_loc;
    Looper looper;
    LocationRequest locationRequest;
    GoogleApiClient googleApiClient;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ServiceThread(Handler handler, Context context) {
        this.handler = handler;
        this.context = context;
        database = new Database(context);
        today = LocalDate.of(2022, 1, 1);
        weather = new HashMap<>();
        message = 0;
        hasFineLocationPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        locationclient = LocationServices.getFusedLocationProviderClient(context);
        curr_loc = null; looper = this.handler.getLooper();
        locationcallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null) {
                    locationclient.removeLocationUpdates(locationcallback);
                    return;
                }
                curr_loc = locationResult.getLastLocation();
                Log.d("WeGlonD", "curr_loc : " + curr_loc);
                //현위치 찾으면 날씨 쿼리
                getWeather(weather, curr_loc.getLatitude(), curr_loc.getLongitude(), new AfterTask() {
                    @Override
                    public void ifSuccess(Object result) {
                        if (weather.get(LocalDate.now()).get("PTY") > 0) {
                            message = 1;
                        } else message = 0;
                        handler.sendEmptyMessage(message);//쓰레드에 있는 핸들러에게 메세지를 보냄
                    }

                    @Override
                    public void ifFail(Object result) {
                        handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
                    }
                });
                locationclient.removeLocationUpdates(locationcallback);
            }
        };

    }

    public void stopForever() {
        synchronized (this) {
            this.isRun = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void run() {
        //반복적으로 수행할 작업을 한다.
        while (isRun) {
            alarmCheck();
            try {
                Thread.sleep(60000); //1분씩 쉰다.
            } catch (Exception e) {
            }
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void alarmCheck() {
        ArrayList<Schedule> AfterSchedules = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        int currentSecond = LocalDateTime.now().getSecond();
        if (currentSecond != 0) {
            now = now.plusMinutes(1);
            try {
                Thread.sleep((60 - currentSecond) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("WeGlonD", now.toString());
        if (!today.equals(now.toLocalDate())) {
            today = now.toLocalDate();
        }
        //LocalDateTime now = LocalDateTime.of(2023,2,8,7,0,0,0);
        database.loadAllScheduleStartedAt(Time.LocalDateTimeToMills(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(23, 0, 0, 0))), AfterSchedules);
        for (Schedule s : AfterSchedules) {
            if (s.alarm == null || s.alarm.equals("")) continue;
            String[] alarm_time_substr = s.alarm.substring(0, s.alarm.length() - 2).split(", ");
            LocalDateTime begin = Time.MillsToLocalDateTime(s.begin_ms);
            if (s.departAlarm) {
                begin = begin.minusHours(s.need_hour);
                begin = begin.minusMinutes(s.need_minute);
                begin = begin.minusSeconds(s.need_second);
            }
            begin = begin.withSecond(0).withNano(0);
            for (String str : alarm_time_substr) {
                LocalDateTime time = begin;
                if (str.equals("일정 시작시간")) {

                } else if (str.substring(str.length() - 1).equals("분")) {
                    int val = Integer.parseInt(str.substring(0, str.length() - 1));
                    time = time.minusMinutes(val);
                } else if (str.substring(str.length() - 1).equals("간")) {
                    int val = Integer.parseInt(str.substring(0, str.length() - 2));
                    time = time.minusHours(val);
                } else if (str.substring(str.length() - 1).equals("일")) {
                    int val = Integer.parseInt(str.substring(0, str.length() - 1));
                    time = time.minusDays(val);
                } else if (str.substring(str.length() - 1).equals("주")) {
                    int val = Integer.parseInt(str.substring(0, str.length() - 1));
                    time = time.minusWeeks(val);
                }



                if (time.equals(now)) {
                    Log.d("WeGlonD", "Alarm!! - " + s.title);
                    weather.clear();
                    if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                        //현위치 쿼리
                        locationclient.requestLocationUpdates(LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(60000), locationcallback, looper);

                    }
                    else
                        handler.sendEmptyMessage(0);
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