package com.zzammo.calendar.schedule_event;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.zzammo.calendar.R;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.database.room.ScheduleDatabase;
import com.zzammo.calendar.util.Time;

public class MakeSchedule extends AppCompatActivity {
    Context mContext;
    Long depart_dateTime;
    Long arrive_dateTime;
    EditText title_et;
    EditText location_et;
    TimePicker timePicker;
    Button save_btn;
    TextView depart_date;
    TextView depart_clock;
    TextView arrive_date;
    TextView arrive_clock;
    MaterialCalendarView calendar;
    String visiblemode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_schedule);

        depart_date = findViewById(R.id.depart_date);
        depart_clock = findViewById(R.id.depart_clock);
        arrive_date = findViewById(R.id.arrive_date);
        arrive_clock = findViewById(R.id.arrive_clock);
        calendar = findViewById(R.id.makeschedule_calendarView);

        title_et = this.findViewById(R.id.schedule_dialog_title);
        location_et = this.findViewById(R.id.schedule_dialog_location);
        timePicker = this.findViewById(R.id.schedule_dialog_timePicker);
        save_btn = this.findViewById(R.id.schedule_dialog_saveBtn);

        puttime(depart_clock,timePicker.getCurrentHour(),timePicker.getCurrentMinute());
        puttime(arrive_clock,timePicker.getCurrentHour()+1,timePicker.getCurrentMinute());
        int month = getIntent().getIntExtra("month",0);
        int day = getIntent().getIntExtra("day",0);
        putdate(depart_date,month,day);
        putdate(arrive_date,month,day);

        depart_dateTime = getIntent().getLongExtra("date",0);
        arrive_dateTime = depart_dateTime+(1000 * 60 * 60 * 24);

        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if(visiblemode.equals("departdate")){
                    putdate(depart_date,date.getMonth(),date.getDay());
                    depart_dateTime = Time.CalendarDayToMill(date);
                }
                else if(visiblemode.equals("arrivedate")){
                    putdate(arrive_date,date.getMonth(),date.getDay());
                    arrive_dateTime = Time.CalendarDayToMill(date);
                }
            }
        });

        //출발 날짜 보이게
        depart_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                depart_clock.setTextColor(Color.parseColor("black"));
                arrive_clock.setTextColor(Color.parseColor("black"));
                timePicker.setVisibility(View.GONE);
                if(calendar.getVisibility()==View.GONE){
                    depart_date.setTextColor(Color.parseColor("red"));
                    calendar.setVisibility(View.VISIBLE);
                    visiblemode = "departdate";
                }
                else if(calendar.getVisibility()==View.VISIBLE){
                    if(visiblemode.equals("departdate")){
                        depart_date.setTextColor(Color.parseColor("black"));
                        calendar.setVisibility(View.GONE);
                        visiblemode = "nomode";
                    }
                    else{
                        depart_date.setTextColor(Color.parseColor("red"));
                        arrive_date.setTextColor(Color.parseColor("black"));
                        visiblemode = "departdate";
                    }
                }
            }
        });
        //도착 날짜 보이게
        arrive_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                depart_clock.setTextColor(Color.parseColor("black"));
                arrive_clock.setTextColor(Color.parseColor("black"));
                timePicker.setVisibility(View.GONE);
                if(calendar.getVisibility()==View.GONE){
                    arrive_date.setTextColor(Color.parseColor("red"));
                    calendar.setVisibility(View.VISIBLE);
                    visiblemode = "arrivedate";
                }
                else if(calendar.getVisibility()==View.VISIBLE){
                    if(visiblemode.equals("arrivedate")){
                        arrive_date.setTextColor(Color.parseColor("black"));
                        calendar.setVisibility(View.GONE);
                        visiblemode = "nomode";
                    }
                    else{
                        arrive_date.setTextColor(Color.parseColor("red"));
                        depart_date.setTextColor(Color.parseColor("black"));
                        visiblemode = "arrivedate";
                    }
                }
            }
        });

        //출발시간 타임피커 보이게
        depart_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.setVisibility(View.GONE);
                depart_date.setTextColor(Color.parseColor("black"));
                arrive_date.setTextColor(Color.parseColor("black"));
                if(timePicker.getVisibility()==View.GONE){
                    depart_clock.setTextColor(Color.parseColor("red"));
                    timePicker.setVisibility(View.VISIBLE);
                    visiblemode = "departtime";
                }
                else if(timePicker.getVisibility()==View.VISIBLE){
                    if(visiblemode.equals("departtime")){
                        depart_clock.setTextColor(Color.parseColor("black"));
                        timePicker.setVisibility(View.GONE);
                        visiblemode = "nomode";
                    }
                    else{
                        depart_clock.setTextColor(Color.parseColor("red"));
                        arrive_clock.setTextColor(Color.parseColor("black"));
                        visiblemode = "departtime";
                    }
                }
            }
        });

        //도착시간 타임피커 보이게
        arrive_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.setVisibility(View.GONE);
                depart_date.setTextColor(Color.parseColor("black"));
                arrive_date.setTextColor(Color.parseColor("black"));
                if(timePicker.getVisibility()==View.GONE){
                    arrive_clock.setTextColor(Color.parseColor("red"));
                    timePicker.setVisibility(View.VISIBLE);
                    visiblemode = "arrivetime";
                }
                else if(timePicker.getVisibility()==View.VISIBLE){
                    if(visiblemode.equals("arrivetime")){
                        arrive_clock.setTextColor(Color.parseColor("black"));
                        timePicker.setVisibility(View.GONE);
                        visiblemode = "nomode";
                    }
                    else{
                        arrive_clock.setTextColor(Color.parseColor("red"));
                        depart_clock.setTextColor(Color.parseColor("black"));
                        visiblemode = "arrivetime";
                    }
                }
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                if(visiblemode.equals("departtime")) puttime(depart_clock,hour,minute);
                else if(visiblemode.equals("arrivetime"))puttime(arrive_clock,hour,minute);
            }
        });

        save_btn.setOnClickListener(view -> {
            String title = title_et.getText().toString();
            String location = location_et.getText().toString();
            Long depart_timeMills,arrive_timeMills;
            int depart_hour,arrive_hour,depart_minute,arrive_minute;
            String de =depart_clock.getText().toString(), ar = arrive_clock.getText().toString();
            depart_hour = Integer.parseInt(de.substring(3,de.indexOf("시")));
            arrive_hour = Integer.parseInt(ar.substring(3,ar.indexOf("시")));
            depart_minute = Integer.parseInt(de.substring(de.indexOf("시")+2,de.indexOf("분")));
            arrive_minute = Integer.parseInt(ar.substring(ar.indexOf("시")+2,ar.indexOf("분")));
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                hour = timePicker.getHour();
            else
                hour = timePicker.getCurrentHour();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                minute = timePicker.getMinute();
            else
                minute = timePicker.getCurrentMinute();*/
            depart_timeMills = depart_dateTime + depart_hour * Time.ONE_HOUR + depart_minute * Time.ONE_MINUTE;
            arrive_timeMills = depart_dateTime + arrive_hour * Time.ONE_HOUR + arrive_minute * Time.ONE_MINUTE;

            Schedule schedule = new Schedule(title, location, depart_timeMills);

            ScheduleDatabase DB = ScheduleDatabase.getInstance(mContext);
            DB.scheduleDao().insertAll(schedule);

            finish();
        });
    }
    private void puttime(TextView a, int hour, int minute){
        if (hour > 12) {
            hour -= 12;
            a.setText("오후 " + hour + "시 " + minute + "분");
        } else {
            a.setText("오전 " + hour + "시 " + minute + "분");
        }
    }
    private void putdate(TextView a, int month, int day){a.setText(month + "월 " + day + "일");}
}
