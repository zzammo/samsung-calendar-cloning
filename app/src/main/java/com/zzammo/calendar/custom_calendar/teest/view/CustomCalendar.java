package com.zzammo.calendar.custom_calendar.teest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.zzammo.calendar.R;
import com.zzammo.calendar.custom_calendar.teest.adapter.ViewPagerAdapter;
import com.zzammo.calendar.custom_calendar.teest.data.CalendarDate;
import com.zzammo.calendar.custom_calendar.teest.data.PageData;

import java.util.ArrayList;
import java.util.Calendar;

public class CustomCalendar extends LinearLayout {

    public interface OnDateClickListener{
        void dateClickListener(View view, CalendarDate date);
    }
    public interface OnInterceptTouchEvent{
        void interceptTouchEvent(MotionEvent ev);
    }
    public interface OnDateChangedListener{
        void dateChangedListener(CalendarDate date);
    }


    final static int MONTH_SCHEDULE = 1;
    final static int MONTH = 2;
    final static int WEEK = 3;

    LinearLayout background;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;

    FragmentActivity activity;
    ArrayList<PageData> data;
    Long selectedDate;

    int viewMode;
    int sundayColor;
    int saturdayColor;
    int holidayColor;
    int todayColor;
    int basicColor;
    boolean showSchedule;
    int pageCount;

    OnDateClickListener dateClickListener;
    OnDateChangedListener dateChangedListener;
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int height = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if (h > height) height = h;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setOnDateClickListener(OnDateClickListener listener) {
        this.dateClickListener = listener;
    }

    public void setOnDateChangedListener(OnDateChangedListener dateChangedListener) {
        this.dateChangedListener = dateChangedListener;
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
        //true : 상위 뷰페이저 이벤트 전달
        //false : 하위 뷰페이저 이벤트 전달
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

//    public boolean performClick(){
//        return super.performClick();
//    }

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

        viewPager.setCurrentItem(pageCount, false);

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
                calendar.set(Calendar.MILLISECOND, 0);
                page.setMonth(calendar);

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; //해당 월에 시작하는 요일 -1 을 하면 빈칸을 구할 수 있겠죠 ?
                int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 해당 월에 마지막 요일

                for (int j = 0; j < dayOfWeek; j++) {
                    days.add(new CalendarDate());
                }
                for (int j = 1; j <= max; j++) {
                    Calendar day = Calendar.getInstance();
                    day.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + i, j, 0, 0, 0);
                    day.set(Calendar.MILLISECOND, 0);
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
