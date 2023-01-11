package com.zzammo.calendar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzammo.calendar.R;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;

public class ScheduleRVAdapter extends RecyclerView.Adapter<ScheduleRVAdapter.VH>{

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    OnItemClickListener mListener = null;
    ArrayList<Schedule> scheduleArrayList;

    public ScheduleRVAdapter(ArrayList<Schedule> scheduleArrayList) {
        this.scheduleArrayList = scheduleArrayList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Schedule schedule = scheduleArrayList.get(position);

        holder.title_tv.setText(schedule.title);
        holder.begin_loc_tv.setText(schedule.begin_loc);
        holder.begin_time_tv.setText(Time.millToHM(schedule.begin_ms));
        holder.end_loc_tv.setText(schedule.end_loc);
        holder.end_time_tv.setText(Time.millToHM(schedule.end_ms));
    }

    @Override
    public int getItemCount() {
        return scheduleArrayList.size();
    }

    class VH extends RecyclerView.ViewHolder{
        TextView title_tv;
        TextView begin_time_tv;
        TextView begin_loc_tv;
        TextView end_time_tv;
        TextView end_loc_tv;
        Button delete_btn;

        public VH(@NonNull View itemView) {
            super(itemView);

            title_tv = itemView.findViewById(R.id.schedule_item_title);
            begin_time_tv = itemView.findViewById(R.id.schedule_item_begin_time);
            begin_loc_tv = itemView.findViewById(R.id.schedule_item_begin_loc);
            end_time_tv = itemView.findViewById(R.id.schedule_item_end_time);
            end_loc_tv = itemView.findViewById(R.id.schedule_item_end_loc);
            delete_btn = itemView.findViewById(R.id.schedule_item_deleteBtn);

            delete_btn.setOnClickListener(view -> {
                int position = getAdapterPosition ();
                if (position!=RecyclerView.NO_POSITION){
                    if (mListener!=null){
                        mListener.onItemClick (position);
                    }
                }
            });
        }
    }
}
