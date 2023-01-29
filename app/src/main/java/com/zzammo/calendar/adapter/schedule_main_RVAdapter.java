package com.zzammo.calendar.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.zzammo.calendar.R;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.Time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class schedule_main_RVAdapter extends RecyclerView.Adapter<schedule_main_RVAdapter.VH>{
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    schedule_main_RVAdapter.OnItemClickListener mListener = null;
    ArrayList<Schedule> scheduleArrayList;

    public schedule_main_RVAdapter(ArrayList<Schedule> scheduleArrayList) {
        this.scheduleArrayList = scheduleArrayList;
    }

    public void setOnItemClickListener(schedule_main_RVAdapter.OnItemClickListener listener) {
        this.mListener = listener;
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

        }
    }
}
