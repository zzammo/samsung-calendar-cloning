package com.zzammo.calendar.custom_calendar.teest.adapter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzammo.calendar.R;
import com.zzammo.calendar.custom_calendar.teest.data.CalendarDate;
import com.zzammo.calendar.custom_calendar.teest.view.CustomCalendar;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Holiday;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.databinding.PageDateItemBinding;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class PageRVAdapter extends RecyclerView.Adapter<PageRVAdapter.VH> {

    LifecycleOwner lifecycleOwner;
    Context context;
    Database DB;

    public MutableLiveData<Integer> viewHolderHeight;
    VH[] viewHolders;

    ArrayList<CalendarDate> data;
    CustomCalendar.OnDateClick listener;
    int sundayColor, saturdayColor, holidayColor, todayColor, basicColor;
    boolean setBackGroundFirst;

    public PageRVAdapter(LifecycleOwner lifecycleOwner, Context context, ArrayList<CalendarDate> data,
                         CustomCalendar.OnDateClick listener,
                         int sundayColor, int saturdayColor, int holidayColor, int todayColor, int basicColor,
                         boolean setBackGroundFirst) {
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
        this.data = data;
        this.listener = listener;
        this.sundayColor = sundayColor;
        this.saturdayColor = saturdayColor;
        this.holidayColor = holidayColor;
        this.todayColor = todayColor;
        this.basicColor = basicColor;
        this.setBackGroundFirst = setBackGroundFirst;
        DB = new Database(context);

        viewHolderHeight = new MutableLiveData<>();
        viewHolders = new VH[CustomCalendar.DAY_SIZE];
        viewHolderHeight.observe(lifecycleOwner, integer -> {
//            if (viewHolderBindings == null) return;
//            if (viewHolderHeight == null) return;
//            for (PageDateItemBinding vb : viewHolderBindings){
//                if (vb == null) continue;
//                vb.setHeight(viewHolderHeight.getValue());
//            }
            for (VH vh : viewHolders){
                ViewGroup.LayoutParams lp = vh.itemView.getLayoutParams();
                lp.height = viewHolderHeight.getValue();
                vh.itemView.setLayoutParams(lp);
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

        viewHolders[position] = holder;

        if (!day.thisMonth)
            holder.itemView.setAlpha(0.3f);

        holder.day_tv.setText(String.valueOf(cal.get(Calendar.DATE)));

//        Calendar tmp = Calendar.getInstance();
//        tmp.setTimeInMillis(day.date);
//        Log.d("Dirtfy", Time.CalendarToYM(tmp));
//        Log.d("Dirtfy", String.valueOf(day.date.equals(firstDate)));
//        Log.d("Dirtfy", firstDate+" fd "+day.date);


        setColor(holder, day);

        holder.schedule_lo.removeAllViews();

        if(day.getHolidays().size() + day.getSchedules().size() > 3) {
            TextView tv = makeTv("...");
            holder.schedule_lo.addView(tv);
            return;
        }

        setHolidays(holder, day);
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
            if (setBackGroundFirst){
                holder.itemView.setBackgroundResource(R.drawable.today_box);
                setBackGroundFirst = false;
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
    TextView makeTv(String s){
        TextView tv = new TextView(context);
        tv.setText(s);
        tv.setMaxLines(1);
        tv.setTextSize(Dimension.SP, 10);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0,100);
        tv.setLayoutParams(params);
        return tv;
    }
    void setHolidays(VH holder, CalendarDate day){
        ArrayList<Holiday> holidays = day.getHolidays();

        if (holidays == null) return;

        LinearLayout lo = holder.schedule_lo;
        TextView tv;
        for (int i = 0;i < holidays.size();i++){
            tv = makeTv(holidays.get(i).name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setBackgroundColor(context.getColor(R.color.red));
            }
            lo.addView(tv);
        }
    }
    void setSchedules(VH holder, CalendarDate day){
        ArrayList<Schedule> schedules = day.getSchedules();

        if (schedules == null) return;

        LinearLayout lo = holder.schedule_lo;
        TextView tv;
        for (int i = 0;i < schedules.size();i++){
            tv = makeTv(schedules.get(i).title);
//            tv.setBackgroundColor(context.getColor(R.color.));
            lo.addView(tv);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public VH getViewHolders(int index) {
        return viewHolders[index];
    }

    public class VH extends RecyclerView.ViewHolder{

        TextView day_tv;
        LinearLayout schedule_lo;

        public VH(@NonNull View itemView) {
            super(itemView);

            day_tv = itemView.findViewById(R.id.page_date_item_day_textView);
            schedule_lo = itemView.findViewById(R.id.page_date_item_schedule_layout);

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
