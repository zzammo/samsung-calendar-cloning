package com.zzammo.calendar.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.zzammo.calendar.R;
import com.zzammo.calendar.activity.schedule;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.Time;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class schedule_main_RVAdapter extends RecyclerView.Adapter<schedule_main_RVAdapter.VH>{
    ArrayList<Schedule> scheduleArrayList;
    Context context;

    public schedule_main_RVAdapter(ArrayList<Schedule> scheduleArrayList, Context context) {
        this.scheduleArrayList = scheduleArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item2, parent, false);
        return new VH(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Schedule schedule = scheduleArrayList.get(position);
        LocalTime beginlocalTime = LocalTime.parse(Time.millToHM(schedule.begin_ms), DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endlocalTime = LocalTime.parse(Time.millToHM(schedule.end_ms), DateTimeFormatter.ofPattern("HH:mm"));

        holder.start_time_tv.setText(beginlocalTime.format(DateTimeFormatter.ofPattern("h:mm")));
        holder.time_duration_tv.setText(beginlocalTime.format(DateTimeFormatter.ofPattern("a h:mm")) + " - " + endlocalTime.format(DateTimeFormatter.ofPattern("a h:mm")));
        holder.schedule_name_tv.setText(schedule.title);
        if(schedule.isHoliday){
            holder.holiday_color_line_iv.setVisibility(View.VISIBLE);
            holder.schedule_color_line_iv.setVisibility(View.GONE);
        }
        else{
            holder.holiday_color_line_iv.setVisibility(View.GONE);
            holder.schedule_color_line_iv.setVisibility(View.VISIBLE);
        }
        if(schedule.isAllDay) {
            holder.start_time_tv.setVisibility(View.GONE);
            holder.start_time_allday_iv.setVisibility(View.VISIBLE);
            holder.time_duration_tv.setText("하루 종일");
        }
        else{
            holder.start_time_tv.setVisibility(View.VISIBLE);
            holder.start_time_allday_iv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return scheduleArrayList.size();
    }

    class VH extends RecyclerView.ViewHolder{
        TextView start_time_tv;
        ImageView start_time_allday_iv;
        ImageView schedule_color_line_iv;
        ImageView holiday_color_line_iv;
        TextView time_duration_tv;
        TextView schedule_name_tv;

        public VH(@NonNull View itemView) {
            super(itemView);
            start_time_tv = itemView.findViewById(R.id.start_time_title);
            time_duration_tv = itemView.findViewById(R.id.schedule_time);
            schedule_name_tv = itemView.findViewById(R.id.schedule_title);
            start_time_allday_iv = itemView.findViewById(R.id.start_time_allday);
            schedule_color_line_iv = itemView.findViewById(R.id.schedule_colorline);
            holiday_color_line_iv = itemView.findViewById(R.id.holiday_colorline);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.inflate(R.menu.menu_recycler);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int position = getAdapterPosition();
                            Schedule currSchedule = scheduleArrayList.get(position);
                            switch(menuItem.getItemId()){
                                case R.id.edit:
                                    //수정하는 코드 추가 예정
                                    Intent it = new Intent(context, schedule.class);
                                    it.putExtra("mode", 1);
                                    it.putExtra("scheduleKey", currSchedule.key);
                                    it.putExtra("scheduleServerId", currSchedule.serverId);
                                    it.putExtra("title", currSchedule.title);
                                    it.putExtra("isAllday", currSchedule.isAllDay);
                                    it.putExtra("departAlarm", currSchedule.departAlarm);
                                    it.putExtra("alarm", currSchedule.alarm);
                                    it.putExtra("memo", currSchedule.memo);
                                    it.putExtra("begin_ms", currSchedule.begin_ms);
                                    it.putExtra("end_ms", currSchedule.end_ms);
                                    if(currSchedule.departAlarm){
                                        it.putExtra("begin_loc", currSchedule.begin_loc);
                                        it.putExtra("begin_lat", currSchedule.begin_lat);
                                        it.putExtra("begin_lng", currSchedule.begin_lng);
                                        it.putExtra("end_loc", currSchedule.end_loc);
                                        it.putExtra("end_lat", currSchedule.end_lat);
                                        it.putExtra("end_lng", currSchedule.end_lng);
                                        it.putExtra("need_hour", currSchedule.need_hour);
                                        it.putExtra("need_minute", currSchedule.need_minute);
                                        it.putExtra("need_second", currSchedule.need_second);
                                        it.putExtra("means", currSchedule.means);
                                    }
                                    context.startActivity(it);
                                    return true;
                                case R.id.delete:
                                    Database db = new Database(context);
                                    db.delete(currSchedule);
                                    scheduleArrayList.remove(position);
                                    schedule_main_RVAdapter.this.notifyItemRemoved(position);
                                    return true;
                            }
                            return false;
                        }
                    });
                    if(!scheduleArrayList.get(getAdapterPosition()).isHoliday)
                        popupMenu.show();
                }
            });
        }
    }
}
