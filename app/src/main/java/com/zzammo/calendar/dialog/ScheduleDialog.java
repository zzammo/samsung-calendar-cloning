package com.zzammo.calendar.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.zzammo.calendar.R;
import com.zzammo.calendar.adapter.ScheduleRVAdapter;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.database.room.ScheduleDatabase;
import com.zzammo.calendar.schedule_event.MakeSchedule;
import com.zzammo.calendar.util.AfterTask;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleDialog extends Dialog {

    ScheduleDialog dialog;
    Context mContext;

    Long dateStartTime;

    ArrayList<Schedule> scheduleArrayList;
    ScheduleRVAdapter scheduleRVAdapter;
    LinearLayoutManager layoutManager;
    RecyclerView scheduleRV;
    ImageView addSchedule_iv;

    Database DB;

    public ScheduleDialog(Context context, Long dateStartTime) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.dateStartTime = dateStartTime;
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

        DB = new Database(mContext);

        scheduleRV = this.findViewById(R.id.schedule_dialog_recyclerView);
        addSchedule_iv = this.findViewById(R.id.schedule_dialog_addImageView);

        scheduleArrayList = new ArrayList<>();
        scheduleRVAdapter = new ScheduleRVAdapter(scheduleArrayList);
        scheduleRVAdapter.setOnItemClickListener(position -> {
            DB.delete(Database.LOCAL, scheduleArrayList.get(position), new non());
            scheduleArrayList.remove(position);
            scheduleRVAdapter.notifyItemRemoved(position);
        });
        layoutManager = new LinearLayoutManager(mContext);
        scheduleRV.setAdapter(scheduleRVAdapter);
        scheduleRV.setLayoutManager(layoutManager);

        addSchedule_iv.setOnClickListener(view -> {
            Date date = new Date(dateStartTime);

            Intent it = new Intent(mContext, MakeSchedule.class);
            it.putExtra("date", dateStartTime);
            it.putExtra("month",date.getMonth());
            it.putExtra("day",date.getDay());

            mContext.startActivity(it);

            this.dismiss();
        });

        Database DB = new Database(mContext);
        DB.loadAllScheduleDuring(Database.LOCAL
                , dateStartTime
                , dateStartTime + Time.ONE_DAY
                , scheduleArrayList
                , new non());
        scheduleRVAdapter.notifyDataSetChanged();
    }

    class non implements AfterTask{
        @Override
        public void ifSuccess(Object result) {

        }

        @Override
        public void ifFail(Object result) {

        }
    }

}
