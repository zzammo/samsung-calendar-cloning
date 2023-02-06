package com.zzammo.calendar.custom_calendar.teest.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzammo.calendar.R;
import com.zzammo.calendar.custom_calendar.teest.data.CalendarDate;
import com.zzammo.calendar.custom_calendar.teest.view.CustomCalendar;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.databinding.PageDateItemBinding;
import com.zzammo.calendar.util.Time;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

public class PageRVAdapter extends RecyclerView.Adapter<PageRVAdapter.VH> {

    LifecycleOwner lifecycleOwner;
    Context context;
    Database DB;

    public MutableLiveData<Integer> viewHolderHeight;
    ArrayList<PageDateItemBinding> viewHolderBindings;
    View[] viewHolders;

    ArrayList<CalendarDate> data;
    CustomCalendar.OnDateClick listener;
    int sundayColor, saturdayColor, holidayColor, todayColor, basicColor;
    boolean setBackGroundToday;

    public PageRVAdapter(LifecycleOwner lifecycleOwner, Context context, ArrayList<CalendarDate> data,
                         CustomCalendar.OnDateClick listener,
                         int sundayColor, int saturdayColor, int holidayColor, int todayColor, int basicColor,
                         boolean setBackGroundToday) {
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
        this.data = data;
        this.listener = listener;
        this.sundayColor = sundayColor;
        this.saturdayColor = saturdayColor;
        this.holidayColor = holidayColor;
        this.todayColor = todayColor;
        this.basicColor = basicColor;
        this.setBackGroundToday = setBackGroundToday;
        DB = new Database(context);

        viewHolderHeight = new MutableLiveData<>();
        viewHolders = new View[CustomCalendar.DAY_SIZE];
        viewHolderHeight.observe(lifecycleOwner, integer -> {
//            if (viewHolderBindings == null) return;
//            if (viewHolderHeight == null) return;
//            for (PageDateItemBinding vb : viewHolderBindings){
//                if (vb == null) continue;
//                vb.setHeight(viewHolderHeight.getValue());
//            }
            for (View vh : viewHolders){
                ViewGroup.LayoutParams lp = vh.getLayoutParams();
                lp.height = viewHolderHeight.getValue();
                vh.setLayoutParams(lp);
            }
        });
//        viewHolderBindings = new ArrayList<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.page_date_item, parent, false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = parent.getMeasuredHeight() / 6;
        view.setLayoutParams(lp);
//        view.setLayoutParams(lp);
//        ((PageDateItemBinding) DataBindingUtil.bind(view)).setHeight(lp.height);
        return new PageRVAdapter.VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CalendarDate day = data.get(position);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(day.date);

        viewHolders[position] = holder.itemView;

        if (!day.thisMonth)
            holder.day_tv.setAlpha(0.3f);

        holder.day_tv.setText(String.valueOf(cal.get(Calendar.DATE)));

        setColor(holder, day);

        setSchedules(holder, day);
    }

    void setColor(VH holder, CalendarDate day){
        Calendar dayCal = Calendar.getInstance();
        dayCal.setTimeInMillis(day.date);
        int color;

        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
        Time.setZero(today);
        if (dayCal.compareTo(today) == 0) {
            color = todayColor;
            if (setBackGroundToday) {
                holder.itemView.setBackgroundResource(R.drawable.today_box);
                setBackGroundToday = false;
                Log.d("Dirtfy", "RVA");
            }
        }
        else if (day.getHolidays().size() > 0)
            color = holidayColor;
        else if (dayCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            color = sundayColor;
        else if (dayCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            color = saturdayColor;
        else
            color = basicColor;

        holder.day_tv.setTextColor(color);
    }
    void setSchedules(VH holder, CalendarDate day){
        ArrayList<Schedule> schedules = day.getSchedules();

        if (schedules == null) return;

        if (schedules.size() > 2){
            holder.schedule_more.setVisibility(View.VISIBLE);
        }
        else if (schedules.size() == 2){
            holder.schedule_lo.setVisibility(View.VISIBLE);
            holder.schedule_1_tv.setText(schedules.get(0).getTitle());
            holder.schedule_2_tv.setText(schedules.get(1).getTitle());
        }
        else if (schedules.size() == 1){
            holder.schedule_lo.setVisibility(View.VISIBLE);
            holder.schedule_1_tv.setText(schedules.get(0).getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public View getViewHolder(int index) {
        return viewHolders[index];
    }

    class VH extends RecyclerView.ViewHolder{

        TextView day_tv;
        LinearLayout schedule_lo;
        TextView schedule_1_tv;
        TextView schedule_2_tv;
        TextView schedule_more;

        public VH(@NonNull View itemView) {
            super(itemView);

            day_tv = itemView.findViewById(R.id.page_date_item_day_textView);
            schedule_lo = itemView.findViewById(R.id.page_date_item_schedule_layout);
            schedule_1_tv = itemView.findViewById(R.id.page_date_item_schedule_1);
            schedule_2_tv = itemView.findViewById(R.id.page_date_item_schedule_2);
            schedule_more = itemView.findViewById(R.id.page_date_item_schedule_more);

            itemView.setOnClickListener(v -> {
                if (listener == null) return;
                int p = getAdapterPosition();
                if (p == RecyclerView.NO_POSITION) return;
                CalendarDate calendarDate = data.get(p);
                listener.dateClick(v, calendarDate);
            });
            day_tv.setOnClickListener(v -> {
                if (listener == null) return;
                int p = getAdapterPosition();
                if (p == RecyclerView.NO_POSITION) return;
                CalendarDate calendarDate = data.get(p);
                listener.dateClick(itemView, calendarDate);
            });
        }
    }
}
