package com.zzammo.calendar.custom_calendar.teest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.zzammo.calendar.R;
import com.zzammo.calendar.custom_calendar.teest.adapter.PageRVAdapter;
import com.zzammo.calendar.custom_calendar.teest.adapter.ViewPagerAdapter;
import com.zzammo.calendar.custom_calendar.teest.data.CalendarDate;
import com.zzammo.calendar.custom_calendar.teest.data.PageData;
import com.zzammo.calendar.custom_calendar.teest.fragment.PageFragment;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class CustomCalendar extends LinearLayout {

    final static int MONTH_SCHEDULE = 1;
    final static int MONTH = 2;
    final static int WEEK = 3;

    public final static int DAY_SIZE = 42;

    public static boolean IS_FIRST;

    public interface OnDateClickListener{
        void dateClickListener(View view, CalendarDate date);
    }
    public interface OnInterceptTouchEvent{
        void interceptTouchEvent(MotionEvent ev);
    }
    public interface OnDateChangedListener{
        void dateChangedListener(CalendarDate date);
    }
    public interface OnMonthChangedListener{
        void monthChangedListener(Long firstDate);
    }

    public class OnDateClick{
        OnDateClickListener dateClickListener;
        OnDateChangedListener dateChangedListener;

        public void dateClick(View view, CalendarDate date){
            selectedView = getSelectedView();
            Log.d("Dirtfy", String.valueOf(selectedView == null));

            if (dateChangedListener != null)
                dateChangedListener.dateChangedListener(date);

            int postPage = selectedDatePage.intValue();
            Log.d("Dirtfy", postPage+" : postPage");
            int nowPage = viewPager.getCurrentItem();
            Log.d("Dirtfy", nowPage+" : nowPage1");
            Calendar cal = Calendar.getInstance();
            if (!date.thisMonth){
                cal.setTimeInMillis(date.date);
                int day = cal.get(Calendar.DATE);
                if(day > DAY_SIZE/2 && nowPage > 0)
                    nowPage--;
                else if(day < DAY_SIZE/2 && nowPage < 2*pageCount-1)
                    nowPage++;
            }
            Log.d("Dirtfy", nowPage+" : nowPage2");

            selectedDatePage = (long) nowPage;

            if(postPage != nowPage){
                selectedDate = null;
                selectedView = null;
                view = null;

                viewPager.setCurrentItem(nowPage);
            }
            else{
                selectedDate = date.date;
                selectedView = getSelectedView();
            }
//            selectedDatePage = viewPager.getCurrentItem();
//            selectedDate = date.date;
//            selectedView = getSelectedView();

            if (dateClickListener != null)
                    dateClickListener.dateClickListener(view, date);
        }

        public void setDateClickListener(OnDateClickListener dateClickListener) {
            this.dateClickListener = dateClickListener;
        }

        public void setDateChangedListener(OnDateChangedListener dateChangedListener) {
            this.dateChangedListener = dateChangedListener;
        }
    }

    LinearLayout background;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;

    FragmentActivity activity;
    ArrayList<PageData> data;
    Long selectedDatePage;
    Long selectedDate;
    View selectedView;

    int viewMode;
    int sundayColor;
    int saturdayColor;
    int holidayColor;
    int todayColor;
    int basicColor;
    boolean showSchedule;
    boolean showHoliday;
    int pageCount;

    OnDateClick dateClick;
    OnMonthChangedListener monthChangedListener;

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
        dateClick = new OnDateClick();
        IS_FIRST = true;
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
        showHoliday = typedArray.getBoolean(R.styleable.CustomCalendar_showHoliday, true);
        pageCount = typedArray.getInt(R.styleable.CustomCalendar_pageCount, 300);

        typedArray.recycle();
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int mode = MeasureSpec.getMode(heightMeasureSpec);
//        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            int height = 0;
//            for (int i = 0; i < getChildCount(); i++) {
//                View child = getChildAt(i);
//                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//                int h = child.getMeasuredHeight();
//                if (h > height) height = h;
//            }
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }

    public void setOnDateClickListener(OnDateClickListener listener) {
        dateClick.setDateClickListener(listener);
    }

    public void setOnDateChangedListener(OnDateChangedListener dateChangedListener) {
        dateClick.setDateChangedListener(dateChangedListener);
    }

    public void setMonthChangedListener(OnMonthChangedListener monthChangedListener) {
        this.monthChangedListener = monthChangedListener;
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

    public void setShowHoliday(boolean showHoliday) {
        this.showHoliday = showHoliday;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public Long getSelectedDate() {
        return selectedDate;
    }

    public Long getCurrentDate(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public int getSelectedDateRealIndex(){
        PageFragment pf = (PageFragment) activity.getSupportFragmentManager().
                findFragmentByTag("f"+selectedDatePage);
        Log.d("Dirtfy", "f"+selectedDatePage);
        if (pf == null) return -1;

        Calendar cal = Calendar.getInstance();

        Long s = viewPagerAdapter.getData().get(selectedDatePage.intValue()).getDays().get(0).date;
        cal.setTimeInMillis(s);
        Log.d("Dirtfy", s+" : s "+Time.CalendarToYM(cal)+" "+cal.get(Calendar.DATE));
        Long e = selectedDate;
        cal.setTimeInMillis(e);
        Log.d("Dirtfy", e+" : e "+Time.CalendarToYM(cal)+" "+cal.get(Calendar.DATE));
        return (int) ((e-s)/ Time.ONE_DAY);
    }

    public View getSelectedView(){
        if (selectedDate == null) return null;
//        Calendar cal = Calendar.getInstance();
//
//        cal.setTimeInMillis(selectedDate);
//        cal.set(Calendar.DATE, 1);
//        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
//
//        cal.setTimeInMillis(selectedDate);
//        int realIndex = dayOfWeek + cal.get(Calendar.DATE) - 1;
//
//        Log.d("Dirtfy", realIndex+"getSelectedView");

        PageFragment pf = (PageFragment) activity.getSupportFragmentManager().
                findFragmentByTag("f"+selectedDatePage);
        Log.d("Dirtfy", "f"+selectedDatePage);
        if (pf == null) return null;

        int realIndex = getSelectedDateRealIndex();

        Log.d("Dirtfy", realIndex+" getSelectedView");

        RecyclerView rv = pf.getRecyclerView();
        PageRVAdapter rva = (PageRVAdapter) rv.getAdapter();
        if (rva == null) return null;

        return rva.getViewHolders(realIndex).itemView;
    }

    public void addScheduleOnPosition(int position, Schedule schedule){
        PageFragment pf = (PageFragment) activity.getSupportFragmentManager().
                findFragmentByTag("f"+selectedDatePage);

        if (pf == null) return;

        pf.getPageData().getDays().get(position).getSchedules().add(schedule);

        PageRVAdapter rva = (PageRVAdapter) pf.getRecyclerView().getAdapter();

        if (rva == null) return;

        PageRVAdapter.VH vh = rva.getViewHolders(position);

        rva.onBindViewHolder(vh, position);
    }

    public void addScheduleOnSelectedDate(Schedule schedule){
        PageFragment pf = (PageFragment) activity.getSupportFragmentManager().
                findFragmentByTag("f"+selectedDatePage);

        if (pf == null) return;

        int realIndex = getSelectedDateRealIndex();

        ArrayList<Schedule> schedules = pf.getPageData().getDays().get(realIndex).getSchedules();
        schedules.add(schedule);
        Collections.sort(schedules);

        PageRVAdapter rva = (PageRVAdapter) pf.getRecyclerView().getAdapter();

        if (rva == null) return;

        PageRVAdapter.VH vh = rva.getViewHolders(realIndex);

        rva.onBindViewHolder(vh, realIndex);
    }

    public void updateScheduleOnPosition(int position, ArrayList<Schedule> schedules){
        PageFragment pf = (PageFragment) activity.getSupportFragmentManager().
                findFragmentByTag("f"+selectedDatePage);

        if (pf == null) return;

        int realIndex = position;

        pf.getPageData().getDays().get(realIndex).setSchedules(schedules);

        PageRVAdapter rva = (PageRVAdapter) pf.getRecyclerView().getAdapter();

        if (rva == null) return;

        PageRVAdapter.VH vh = rva.getViewHolders(realIndex);

        rva.onBindViewHolder(vh, realIndex);
    }

    public void updateScheduleOnSelectedDate(ArrayList<Schedule> schedules){
        PageFragment pf = (PageFragment) activity.getSupportFragmentManager().
                findFragmentByTag("f"+selectedDatePage);

        if (pf == null) return;

        int realIndex = getSelectedDateRealIndex();

        pf.getPageData().getDays().get(realIndex).setSchedules(schedules);

        PageRVAdapter rva = (PageRVAdapter) pf.getRecyclerView().getAdapter();

        if (rva == null) return;

        PageRVAdapter.VH vh = rva.getViewHolders(realIndex);

        rva.onBindViewHolder(vh, realIndex);
    }

    public Long getDateFromPosition(int position){
        PageFragment pf = (PageFragment) activity.getSupportFragmentManager().
                findFragmentByTag("f"+selectedDatePage);
        Log.d("Dirtfy", "f"+selectedDatePage);
        if (pf == null) return -1L;

        Calendar cal = Calendar.getInstance();

        Long s = viewPagerAdapter.getData().get(selectedDatePage.intValue()).getDays().get(0).date;
        cal.setTimeInMillis(s);
        cal.add(Calendar.DATE, position);

        return cal.getTimeInMillis();
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

        Calendar cal = Calendar.getInstance();
        Time.setZero(cal);
        selectedDate = cal.getTimeInMillis();
        selectedDatePage = (long) pageCount;

        viewPagerAdapter = new ViewPagerAdapter(activity, data,
                showSchedule, showHoliday,
                sundayColor, saturdayColor, holidayColor, todayColor, basicColor,
                true);
        viewPagerAdapter.setDateClickListener(dateClick);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
//        viewPager.setOffscreenPageLimit(1);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    viewPager.setCurrentItem(position);
                    //invalidatePage(activity.getSupportFragmentManager());
                    if (monthChangedListener != null) {
                        monthChangedListener.monthChangedListener(
                                ((ViewPagerAdapter) viewPager.getAdapter()).getData().
                                        get(position).getMonth());
                    }
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

    public void invalidatePage(FragmentManager fm){
        PageFragment pf = (PageFragment) fm.
                findFragmentByTag("f"+viewPager.getCurrentItem());
        if (pf == null) {
            Log.d("Dirtfy", "pf is null");
            return;
        }
        RecyclerView rv = pf.getRecyclerView();
        PageRVAdapter rva = (PageRVAdapter) rv.getAdapter();
        GridLayoutManager lm = (GridLayoutManager) rv.getLayoutManager();
        rv.setAdapter(null);
        rv.setLayoutManager(null);
        rv.setAdapter(rva);
        rv.setLayoutManager(lm);
        rva.notifyDataSetChanged();
    }

    public void gotoTodayPage(){
        viewPager.setCurrentItem(pageCount);
//        Calendar cal = Calendar.getInstance();
//        Time.setZero(cal);
//        selectedDate = cal.getTimeInMillis();
    }

    void setPage(Calendar cal, PageData page, int pageIdx, int nowYear, int nowMonth){
        Time.setZero(cal);

        try {
            ArrayList<CalendarDate> days = new ArrayList<>();

            cal.set(nowYear, nowMonth + pageIdx, 1);
            page.setMonth(cal.getTimeInMillis());

            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; //해당 월에 시작하는 요일 -1 을 하면 빈칸을 구할 수 있겠죠 ?
            int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH); // 해당 월에 마지막 요일

            cal.add(Calendar.DATE, -dayOfWeek);
            for (int j = 0; j < dayOfWeek; j++) {
                Long time = cal.getTimeInMillis();
                days.add(new CalendarDate(time, false));
                cal.add(Calendar.DATE, 1);
            }
            for (int j = 1; j <= max; j++) {
                Long time = cal.getTimeInMillis();
                days.add(new CalendarDate(time, true));
                cal.add(Calendar.DATE, 1);
            }
            while(days.size() < DAY_SIZE){
                Long time = cal.getTimeInMillis();
                days.add(new CalendarDate(time, false));
                cal.add(Calendar.DATE, 1);
            }

            page.setDays(days);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setData(){
        Calendar cal = Calendar.getInstance();
        Time.setZero(cal);

        int nowYear = cal.get(Calendar.YEAR);
        int nowMonth = cal.get(Calendar.MONTH);

        for (int i = -pageCount; i < pageCount; i++) {
            PageData page = new PageData();
            setPage(cal, page, i, nowYear, nowMonth);
            data.add(page);
//
//            try {
//                PageData page = new PageData();
//                ArrayList<CalendarDate> days = new ArrayList<>();
//
//                cal.set(nowYear, nowMonth + i, 1);
//                page.setMonth(cal.getTimeInMillis());
//
//                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; //해당 월에 시작하는 요일 -1 을 하면 빈칸을 구할 수 있겠죠 ?
//                int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH); // 해당 월에 마지막 요일
//
//                cal.add(Calendar.DATE, -dayOfWeek);
//                for (int j = 0; j < dayOfWeek; j++) {
//                    Long time = cal.getTimeInMillis();
//                    days.add(new CalendarDate(time, false));
//                    cal.add(Calendar.DATE, 1);
//                }
//                for (int j = 1; j <= max; j++) {
//                    Long time = cal.getTimeInMillis();
//                    days.add(new CalendarDate(time, true));
//                    cal.add(Calendar.DATE, 1);
//                }
//                while(days.size() < DAY_SIZE){
//                    Long time = cal.getTimeInMillis();
//                    days.add(new CalendarDate(time, false));
//                    cal.add(Calendar.DATE, 1);
//                }
//
//                page.setDays(days);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }
}
