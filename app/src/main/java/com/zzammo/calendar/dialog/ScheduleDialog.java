package com.zzammo.calendar.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzammo.calendar.R;
import com.zzammo.calendar.activity.schedule;
import com.zzammo.calendar.adapter.schedule_main_RVAdapter;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Holiday;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.AfterTask;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleDialog extends Dialog {

    ScheduleDialog dialog;
    Context mContext;

    Long dateStartTime;

    ArrayList<Holiday> holidayArrayList;
    ArrayList<Schedule> scheduleArrayList;
    schedule_main_RVAdapter scheduleRVAdapter;
    LinearLayoutManager layoutManager;
    RecyclerView scheduleRV;
    ImageView addSchedule_iv;

    TextView date_num;
    TextView date_week;

    Database DB;

    public ScheduleDialog(Context context, Long dateStartTime) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.dateStartTime = dateStartTime;
        mContext = context;
    }
    public ScheduleDialog(Context context, Long dateStartTime, ArrayList<Holiday> holidayArrayList) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.dateStartTime = dateStartTime;
        mContext = context;
        this.holidayArrayList = holidayArrayList;
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

        DB = new Database(mContext);

        scheduleRV = this.findViewById(R.id.schedule_dialog_recyclerView);
        addSchedule_iv = this.findViewById(R.id.schedule_dialog_addImageView);
        date_num=this.findViewById(R.id.date_num);
        date_week=this.findViewById(R.id.date_week);
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(dateStartTime);
        date_num.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        date_week.setText(makeWeekText(calendar.get(Calendar.DAY_OF_WEEK)));

        scheduleArrayList = new ArrayList<>();
        scheduleRVAdapter = new schedule_main_RVAdapter(scheduleArrayList, mContext);
        layoutManager = new LinearLayoutManager(mContext);
        scheduleRV.setAdapter(scheduleRVAdapter);
        scheduleRV.setLayoutManager(layoutManager);

        addSchedule_iv.setOnClickListener(view -> {
            Date date = new Date(dateStartTime);

            Intent it = new Intent(mContext, schedule.class);
            it.putExtra("date", dateStartTime);
            it.putExtra("month",date.getMonth());
            it.putExtra("day",date.getDay());

            mContext.startActivity(it);

            this.dismiss();
        });

        Database DB = new Database(mContext);
        if(holidayArrayList != null && !holidayArrayList.isEmpty()){
            for(Holiday h : holidayArrayList){
                Long start = h.date;
                Calendar endcalendar = Calendar.getInstance();
                endcalendar.setTimeInMillis(start);
                endcalendar.set(Calendar.HOUR_OF_DAY, 23); endcalendar.set(Calendar.MINUTE, 59); endcalendar.set(Calendar.SECOND, 59);
                Long end = Time.CalendarToMill(endcalendar);
                scheduleArrayList.add(new Schedule(h.name, true, start, end, true));
            }
        }
        DB.loadAllScheduleDuring(Database.LOCAL
                , dateStartTime
                , dateStartTime + Time.ONE_DAY
                , scheduleArrayList
                , new non());
        scheduleRVAdapter.notifyDataSetChanged();
    }

    public String makeWeekText(int week){
        switch (week){
            case 1:
                return "일요일";
            case 2:
                return "월요일";
            case 3:
                return "화요일";
            case 4:
                return "수요일";
            case 5:
                return "목요일";
            case 6:
                return "금요일";
            default:
                return "토요일";
        }
    }

    class non implements AfterTask{
        @Override
        public void ifSuccess(Object result) {

        }

        @Override
        public void ifFail(Object result) {

        }
    }

    class adapterNotify implements AfterTask{
        @Override
        public void ifSuccess(Object result) {
            scheduleRVAdapter.notifyDataSetChanged();
        }

        @Override
        public void ifFail(Object result) {

        }
    }
}
