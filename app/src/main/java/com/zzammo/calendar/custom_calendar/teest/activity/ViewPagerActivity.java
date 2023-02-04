package com.zzammo.calendar.custom_calendar.teest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.zzammo.calendar.R;
import com.zzammo.calendar.activity.MainActivity;
import com.zzammo.calendar.custom_calendar.teest.adapter.PageRVAdapter;
import com.zzammo.calendar.custom_calendar.teest.adapter.ViewPagerAdapter;
import com.zzammo.calendar.custom_calendar.teest.data.CalendarDate;
import com.zzammo.calendar.custom_calendar.teest.data.PageData;
import com.zzammo.calendar.custom_calendar.teest.view.CustomCalendar;
import com.zzammo.calendar.custom_calendar.utils.Keys;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.dialog.ScheduleDialog;
import com.zzammo.calendar.schedule_event.MakeSchedule;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

public class ViewPagerActivity extends AppCompatActivity {

    CustomCalendar calendar;
    RecyclerView recyclerViewlist;
    Calendar preSelectedDate;

    float y1,y2,total_h,init_view1_h,init_view2_h;
    Float[] max_h = new Float[3];
    Float[] min_h = new Float[3];
    int mode = 0, changemode;
    ViewGroup.LayoutParams params1,params2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        calendar = findViewById(R.id.activity_view_pager_customCalendar);
        recyclerViewlist = findViewById(R.id.activity_view_pager_list);

        params1 = calendar.getLayoutParams();
        params2 = recyclerViewlist.getLayoutParams();

        calendar.post(new Runnable() {
            @Override
            public void run() {
                total_h = calendar.getHeight();
                max_h[0]=total_h; max_h[1]=(total_h); max_h[2]=(total_h*(float)0.5);
                min_h[0]=(total_h*(float)0.5); min_h[1]=(total_h*(float)0.25); min_h[2]=(total_h*(float)0.25);
                Log.d("minseok",calendar.getHeight() + " run " + recyclerViewlist.getHeight());
            }
        });

        calendar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Log.d("minseok","touch");
                calendar.performClick();
                moveview(event);
                return true;
            }
        });

        recyclerViewlist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("minseok","touch");
                moveview(event);
                return true;
            }
        });

        calendar.setOnDateClickListener(date -> {
            Calendar cal = date.getCalendar();

            if (cal == null) return;

            if (preSelectedDate == null || preSelectedDate.compareTo(cal) != 0) {
                preSelectedDate = cal;
            } else if (date.getSchedules().size() == 0) {
                Intent it = new Intent(this, MakeSchedule.class);
                it.putExtra("date", Time.CalendarToMill(cal));
                it.putExtra("month", cal.get(Calendar.MONTH)+1);
                it.putExtra("day", cal.get(Calendar.DATE));
                startActivity(it);
            } else {
                ScheduleDialog oDialog = new ScheduleDialog(this,
                        Time.CalendarToMill(cal));
                oDialog.show();
            }
        });
        calendar.setActivity(this);

        findViewById(R.id.activity_view_pager_add_btn).setOnClickListener(v -> {
            calendar.setSundayColor(getColor(R.color.text_black));
            calendar.setActivity(this);
        });
    }

    void moveview(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("minseok", "mode" + mode + " " + max_h[mode] + " " + min_h[mode]);
                y1 = event.getY();
                init_view1_h = calendar.getHeight();
                init_view2_h = recyclerViewlist.getHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                y2 = event.getY();
                float delta = y2 - y1;
                if (y1 < y2) {
                    //아래로 슬라이딩
                    Log.d("minseok", mode + "move down " + params1.height + " " + params2.height);
                    params1.height = (int) (init_view1_h + delta);
                    float tmp = max_h[mode] + (float) 0.5;
                    if (params1.height > tmp) params1.height = (int) tmp;
                    params2.height = (int) (total_h - params1.height);
                    calendar.setLayoutParams(params1);
                    recyclerViewlist.setLayoutParams(params2);
                    make_view();
                    changemode = -1;
                } else if (y1 > y2) {
                    //위로 슬라이딩
                    Log.d("minseok", mode + "move up" + params1.height + " " + params2.height);
                    float tmp = min_h[mode] + (float) 0.5;
                    params1.height = (int) (init_view1_h + delta);
                    if (params1.height < tmp) params1.height = (int) tmp;
                    params2.height = (int) (total_h - params1.height);
                    calendar.setLayoutParams(params1);
                    recyclerViewlist.setLayoutParams(params2);
                    make_view();
                    changemode = 1;
                }
                break;
            case MotionEvent.ACTION_UP:
                float tmp;
                if (changemode == 1) {
                    tmp = min_h[mode];
                } else {
                    tmp = max_h[mode];
                }

                ValueAnimator animator = ValueAnimator.ofFloat(params1.height, tmp);
                animator.setDuration(500); // set the duration of the animation to 1 second
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        params1.height = (int) (value + 0.5f);
                        params2.height = (int) (total_h - params1.height);
                        calendar.setLayoutParams(params1);
                        recyclerViewlist.setLayoutParams(params2);
                        make_view();
                    }
                });
                Log.d("minseok","animation start");
                animator.start();
                Log.d("minseok","animation end");
                mode += changemode;
                if (mode > 2) mode = 2;
                else if (mode < 0) mode = 0;
                break;
        }
    }

    void make_view(){
        ViewPager2 viewPager2 = calendar.getViewPager();
        View vview = viewPager2.getChildAt(0);
        RecyclerView recyclerview = vview.findViewById(R.id.fragment_page_recyclerView);
        Log.d("minseok",recyclerview.getHeight()+"");
        /*GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) recyclerview.getLayoutParams();
        lp.height = recyclerview.getMeasuredHeight() / 6;
        recyclerview.setLayoutParams(lp);*/
    }

}