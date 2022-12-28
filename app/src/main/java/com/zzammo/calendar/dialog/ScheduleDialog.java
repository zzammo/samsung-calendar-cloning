package com.zzammo.calendar.dialog;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.zzammo.calendar.R;
import com.zzammo.calendar.room.Schedule;
import com.zzammo.calendar.room.ScheduleDatabase;
import com.zzammo.calendar.util.Time;

public class ScheduleDialog extends Dialog {

    ScheduleDialog dialog;
    Context mContext;

    AlarmManager alarm_manager;

    Long dateTime;

    EditText title_et;
    EditText location_et;
    TimePicker timePicker;
    Button save_btn;

    public ScheduleDialog(Context context, Long dateTime, AlarmManager alarmManager) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.dateTime = dateTime;
        this.alarm_manager = alarmManager;
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

        title_et = this.findViewById(R.id.schedule_dialog_title);
        location_et = this.findViewById(R.id.schedule_dialog_location);
        timePicker = this.findViewById(R.id.schedule_dialog_timePicker);
        save_btn = this.findViewById(R.id.schedule_dialog_saveBtn);

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
}
