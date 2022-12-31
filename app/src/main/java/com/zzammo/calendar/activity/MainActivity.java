package com.zzammo.calendar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.zzammo.calendar.R;
import com.zzammo.calendar.adapter.ScheduleRVAdapter;
import com.zzammo.calendar.auth.Auth;
import com.zzammo.calendar.holiday.ApiExplorer;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.database.room.ScheduleDatabase;
import com.zzammo.calendar.schedule_event.MakeSchedule;
import com.zzammo.calendar.util.AfterTask;
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
    Intent it;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialCalendarView materialCalendarView;
        materialCalendarView=findViewById((R.id.calendarView));
        materialCalendarView.addDecorators(
                new SaturdayDecorator(),
                new SundayDecorator(),
                new TodayDecorator(),
                new MySelectorDecorator(this)
        );
        Button button=(Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),MainAddressActivity.class);
                startActivity(intent);
            }

        });

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

        calendarView.setOnDateLongClickListener(new OnDateLongClickListener() {
            @Override
            public void onDateLongClick(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date) {
                /*ScheduleDialog oDialog = new ScheduleDialog(MainActivity.this,
                        Time.CalendarDayToMill(date));
                oDialog.show();*/
                it = new Intent(getApplicationContext(), MakeSchedule.class);
                it.putExtra("date",Time.CalendarDayToMill(date));
                it.putExtra("month",date.getMonth());
                it.putExtra("day",date.getDay());
                startActivity(it);
            }
        });//버전 올려서 살렸음

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if(!selected) return;
            Long dateMills = Time.CalendarDayToMill(date);
            //Toast.makeText(this, dateMills+" "+(dateMills+Time.ONE_DAY), Toast.LENGTH_SHORT).show();

            scheduleArrayList.clear();
            scheduleArrayList.addAll(Arrays.asList(DB.scheduleDao().loadAllScheduleDuring(dateMills, dateMills + Time.ONE_DAY)));
            Collections.sort(scheduleArrayList);
            RVAdapter.notifyDataSetChanged();
        });

        new Thread(){
            public void run(){
                try {
                    ApiExplorer apiExplorer = new ApiExplorer(context);
                    apiExplorer.getHolidays(2023, 1);
                    //2023년 1월의 국경일, 공휴일 정보 불러옴. Month로 0이하의 값을 주면 2023년 전체를 불러옴.
                } catch (IOException | XmlPullParserException e) {
                    Log.e("WeGlonD", "ApiExplorer failed - " + e);
                    e.printStackTrace();
                }
            }
        }.start();

        //로그인 테스트 용
        EditText email_et = findViewById(R.id.activity_main_emailEt);
        EditText password_et = findViewById(R.id.activity_main_passwordEt);
        Button signUp_btn = findViewById(R.id.activity_main_signUpBtn);
        Button signIn_btn = findViewById(R.id.activity_main_signInBtn);
        Button signOut_btn = findViewById(R.id.activity_main_signOutBtn);
        Button delete_btn = findViewById(R.id.activity_main_deleteBtn);
        signUp_btn.setOnClickListener(view -> {
            String email = email_et.getText().toString();
            String password = password_et.getText().toString();
            new Auth().signUp(1, email, password, new AfterTask() {
                @Override
                public void ifSuccess(Object result) {
                    Toast.makeText(context, "가입 성공", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void ifFail(Object result) {
                    Toast.makeText(context, "가입 실패", Toast.LENGTH_SHORT).show();
                }
            });
        });
        signIn_btn.setOnClickListener(view -> {
            String email = email_et.getText().toString();
            String password = password_et.getText().toString();
            new Auth().logIn(Auth.EMAIL, email, password, new AfterTask() {
                @Override
                public void ifSuccess(Object result) {
                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void ifFail(Object result) {
                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show();
                }
            });
        });
        signOut_btn.setOnClickListener(view -> {
            if(new Auth().logOn())
                new Auth().logOut();
        });
        delete_btn.setOnClickListener(view -> {
            if(new Auth().logOn())
                new Auth().delete(Auth.EMAIL, new AfterTask() {
                    @Override
                    public void ifSuccess(Object result) {
                        Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void ifFail(Object result) {
                        Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                });
        });
        //로그인 테스트 용
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