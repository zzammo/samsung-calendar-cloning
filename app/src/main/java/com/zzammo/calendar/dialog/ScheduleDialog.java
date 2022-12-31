package com.zzammo.calendar.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.zzammo.calendar.R;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.database.room.ScheduleDatabase;
import com.zzammo.calendar.util.Time;

public class ScheduleDialog extends Dialog {

    ScheduleDialog dialog;
    Context mContext;

    Long dateTime;

    EditText title_et;
    EditText location_et;
    TimePicker timePicker;
    Button save_btn;
    TextView depart_date;
    TextView depart_clock;
    TextView arrive_date;
    TextView arrive_clock;
    String visiblemode;

    public ScheduleDialog(Context context, Long dateTime) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.dateTime = dateTime;
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.schedule_dialog);
        dialog = this;

        depart_date = findViewById(R.id.depart_date);
        depart_clock = findViewById(R.id.depart_clock);
        arrive_date = findViewById(R.id.arrive_date);
        arrive_clock = findViewById(R.id.arrive_clock);

        title_et = this.findViewById(R.id.schedule_dialog_title);
        location_et = this.findViewById(R.id.schedule_dialog_location);
        timePicker = this.findViewById(R.id.schedule_dialog_timePicker);
        save_btn = this.findViewById(R.id.schedule_dialog_saveBtn);

        //타임피커 보이게
        depart_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timePicker.getVisibility()==View.GONE){
                    timePicker.setVisibility(View.VISIBLE);
                    visiblemode = "departtime";
                }
                else if(timePicker.getVisibility()==View.VISIBLE){
                    timePicker.setVisibility(View.GONE);
                    visiblemode = "nomode";
                }
            }
        });
        arrive_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timePicker.getVisibility()==View.GONE){
                    timePicker.setVisibility(View.VISIBLE);
                    visiblemode = "arrivetime";
                }
                if(timePicker.getVisibility()==View.VISIBLE){
                    timePicker.setVisibility(View.GONE);
                    visiblemode = "nomode";
                }
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                if(visiblemode.equals("departtime")) puttime(depart_clock,hour,minute);
                else if(visiblemode.equals("departtime"))puttime(depart_clock,hour,minute);
            }
        });

        save_btn.setOnClickListener(view -> {
            String title = title_et.getText().toString();
            String location = location_et.getText().toString();
            Long timeMills;

            int hour, minute;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                hour = timePicker.getHour();
            else
                hour = timePicker.getCurrentHour();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                minute = timePicker.getMinute();
            else
                minute = timePicker.getCurrentMinute();
            timeMills = dateTime + hour * Time.ONE_HOUR + minute * Time.ONE_MINUTE;

            Schedule schedule = new Schedule(title, location, timeMills);

            ScheduleDatabase DB = ScheduleDatabase.getInstance(mContext);
            DB.scheduleDao().insertAll(schedule);

            dialog.dismiss();
        });
    }
    private void puttime(TextView a, int hour, int minute){
        if (hour > 12) {
            hour -= 12;
            a.setText("오후 " + hour + "시 " + minute + "분 선택");
        } else {
            a.setText("오전 " + hour + "시 " + minute + "분 선택");
        }
    }
}
