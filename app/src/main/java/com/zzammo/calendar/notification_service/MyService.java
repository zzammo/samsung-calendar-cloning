package com.zzammo.calendar.notification_service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.zzammo.calendar.R;
import com.zzammo.calendar.activity.MainActivity;

public class MyService extends Service {
    NotificationManager Notifi_M;
    ServiceThread thread;
    Notification notification ;

    public static int how_flag=0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notifi_M.createNotificationChannel(new NotificationChannel("id","name", NotificationManager.IMPORTANCE_HIGH));
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification not = new NotificationCompat.Builder(this, "id")
                .setContentTitle("캘린더 실행중")
                .setContentText("알람 실행 대기")
                .setSmallIcon(R.drawable.ic_baseline_calendar_today_24)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, not);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler, getApplicationContext());
        thread.start();
        return START_STICKY;
    }

    //서비스가 종료될 때 할 작업

    public void onDestroy() {
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(MyService.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent,PendingIntent.FLAG_IMMUTABLE);

            if(msg.what==0) {
                notification = new NotificationCompat.Builder(getApplicationContext(), "id")
                        .setContentTitle("캘린더 알람")
                        .setContentText("출발시간입니다.")
                        .setSmallIcon(R.drawable.ic_baseline_calendar_today_24)
                        .setContentIntent(pendingIntent)
                        .build();

                //토스트 띄우기
                Toast.makeText(MyService.this, "출발시간입니다.", Toast.LENGTH_LONG).show();
            }
            else{
                notification = new NotificationCompat.Builder(getApplicationContext(), "id")
                        .setContentTitle("캘린더 알람")
                        .setContentText("출발시간입니다.\n오늘 비/눈이 예보되어있습니다. 우산을 챙겨주세요.")
                        .setSmallIcon(R.drawable.ic_baseline_calendar_today_24)
                        .setContentIntent(pendingIntent)
                        .build();

                //토스트 띄우기
                Toast.makeText(MyService.this, "출발시간입니다.\n비가 오니 우산을 챙겨주세요.", Toast.LENGTH_LONG).show();
            }

            //0 > 무음  1 > 소리  2 > 진동
            if(how_flag==0){
                notification.defaults=Notification.DEFAULT_ALL;
            }else if(how_flag==1){
                notification.defaults = Notification.DEFAULT_SOUND;
            }else{
                notification.defaults=Notification.DEFAULT_VIBRATE;
            }

            //알림 소리를 한번만 내도록
            //Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

            //확인하면 자동으로 알림이 제거 되도록
            notification.flags = Notification.FLAG_AUTO_CANCEL;

            Notifi_M.notify(0,notification);

        }
    };
}