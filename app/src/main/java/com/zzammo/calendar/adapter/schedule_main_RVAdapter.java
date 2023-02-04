package com.zzammo.calendar.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.zzammo.calendar.R;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.Time;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    }

    @Override
    public int getItemCount() {
        return scheduleArrayList.size();
    }

    class VH extends RecyclerView.ViewHolder{
        TextView start_time_tv;
        TextView time_duration_tv;
        TextView schedule_name_tv;

        public VH(@NonNull View itemView) {
            super(itemView);
            start_time_tv = itemView.findViewById(R.id.start_time_title);
            time_duration_tv = itemView.findViewById(R.id.schedule_time);
            schedule_name_tv = itemView.findViewById(R.id.schedule_title);
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
                                    return true;
                                case R.id.delete:
                                    Database db = new Database(context);
                                    db.delete(currSchedule);
                                    scheduleArrayList.remove(position);
                                    notify();
                                    return true;
                            }
                            return false;
                        }
                    });
                }
            });
        }
    }
}
