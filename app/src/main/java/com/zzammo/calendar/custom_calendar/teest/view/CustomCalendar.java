package com.zzammo.calendar.custom_calendar.teest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.zzammo.calendar.R;
import com.zzammo.calendar.custom_calendar.teest.adapter.ViewPagerAdapter;
import com.zzammo.calendar.custom_calendar.teest.data.CalendarDate;
import com.zzammo.calendar.custom_calendar.teest.data.PageData;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Holiday;
import com.zzammo.calendar.database.Metadata;
import com.zzammo.calendar.holiday.HolidayApiExplorer;
import com.zzammo.calendar.util.AfterTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomCalendar extends LinearLayout {

    public interface OnDateClickListener{
        void dateClickListener(View view, CalendarDate date);
    }
    public interface OnInterceptTouchEvent{
        void interceptTouchEvent(MotionEvent ev);
    }

    final static int MONTH_SCHEDULE = 1;
    final static int MONTH = 2;
    final static int WEEK = 3;

    LinearLayout background;
    ViewPager2 viewPager;

    ViewPagerAdapter viewPagerAdapter;

    FragmentActivity activity;
    ArrayList<PageData> data;

    int viewMode;
    int sundayColor;
    int saturdayColor;
    int holidayColor;
    int todayColor;
    int basicColor;
    boolean showSchedule;
    int pageCount;

    OnDateClickListener dateClickListener;
    private OnTouchListener mOnTouchListener;

    public CustomCalendar(Context context) {
        super(context);
        initView();
    }

    public CustomCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomCalendar);
        setTypeArray(typedArray);
    }

    private void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.custom_calendar, this, false);
        addView(v);

        background = findViewById(R.id.custom_calendar_background);
        viewPager = findViewById(R.id.custom_calendar_viewpager);
    }

    private void setTypeArray(TypedArray typedArray) {
        viewMode = typedArray.getInt(R.styleable.CustomCalendar_viewMode, MONTH_SCHEDULE);
        sundayColor = typedArray.getColor(R.styleable.CustomCalendar_sundayColor,
                getResources().getColor(R.color.red));
        saturdayColor = typedArray.getColor(R.styleable.CustomCalendar_saturdayColor,
                getResources().getColor(R.color.blue));
        holidayColor = typedArray.getColor(R.styleable.CustomCalendar_holidayColor,
                getResources().getColor(R.color.red));
        todayColor = typedArray.getColor(R.styleable.CustomCalendar_holidayColor,
                getResources().getColor(R.color.text_white));
        basicColor = typedArray.getColor(R.styleable.CustomCalendar_holidayColor,
                getResources().getColor(R.color.text_black));
        showSchedule = typedArray.getBoolean(R.styleable.CustomCalendar_showSchedule, true);
        pageCount = typedArray.getInt(R.styleable.CustomCalendar_pageCount, 300);

        typedArray.recycle();
    }


    public void setOnDateClickListener(OnDateClickListener listener) {
        this.dateClickListener = listener;
    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }

    public void setViewMode(int viewMode) {
        this.viewMode = viewMode;
    }

    public void setSundayColor(int sundayColor) {
        this.sundayColor = sundayColor;
    }

    public void setSaturdayColor(int saturdayColor) {
        this.saturdayColor = saturdayColor;
    }

    public void setHolidayColor(int holidayColor) {
        this.holidayColor = holidayColor;
    }

    public void setTodayColor(int todayColor) {
        this.todayColor = todayColor;
    }

    public void setBasicColor(int basicColor) {
        this.basicColor = basicColor;
    }

    public void setShowSchedule(boolean showSchedule) {
        this.showSchedule = showSchedule;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mOnTouchListener != null) {
            mOnTouchListener.onTouch(this, ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;

        data = new ArrayList<>();
        setData();

        viewPagerAdapter = new ViewPagerAdapter(activity, data,
                showSchedule,
                sundayColor, saturdayColor, holidayColor, todayColor, basicColor);
        viewPagerAdapter.setDateClickListener(dateClickListener);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);



        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    viewPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        viewPager.setCurrentItem(pageCount);

        invalidate();
        requestLayout();
    }

    void setData(){
        Calendar cal = Calendar.getInstance();

        for (int i = -pageCount; i < pageCount; i++) {
            try {
                PageData page = new PageData();
                ArrayList<CalendarDate> days = new ArrayList<>();

                Calendar calendar = Calendar.getInstance();
                calendar.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + i, 1, 0, 0, 0);
                page.setMonth(calendar);

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; //해당 월에 시작하는 요일 -1 을 하면 빈칸을 구할 수 있겠죠 ?
                int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 해당 월에 마지막 요일

                for (int j = 0; j < dayOfWeek; j++) {
                    days.add(new CalendarDate());
                }
                for (int j = 1; j <= max; j++) {
                    Calendar day = Calendar.getInstance();
                    day.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + i, j, 0, 0, 0);
                    days.add(new CalendarDate(day));
                }

                page.setDays(days);

                data.add(page);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
