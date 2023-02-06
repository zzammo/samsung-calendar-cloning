package com.zzammo.calendar.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.zzammo.calendar.R;
import com.zzammo.calendar.adapter.schedule_main_RVAdapter;
import com.zzammo.calendar.custom_calendar.teest.adapter.PageRVAdapter;
import com.zzammo.calendar.custom_calendar.teest.data.CalendarDate;
import com.zzammo.calendar.custom_calendar.teest.view.CustomCalendar;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Holiday;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.database.room.ScheduleDatabase;
import com.zzammo.calendar.dialog.ScheduleDialog;
import com.zzammo.calendar.lunar.LunarCalendar;
import com.zzammo.calendar.util.AfterTask;
import com.zzammo.calendar.util.Time;
import com.zzammo.calendar.weather.WeatherApiExplorer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class schedule_main extends AppCompatActivity {

    ImageView add_schedule;
    Context context;
    HashMap<LocalDate, HashMap<String, Integer>> weather;
    TextView temperature, lunardate, or;
    ImageView weatherView;
    ScheduleDatabase DB;
    RecyclerView scheduleRV;
    ArrayList<Schedule> scheduleArrayList;
    schedule_main_RVAdapter RVAdapter;
    LinearLayoutManager layoutManager;

    CustomCalendar calendarView;
    ConstraintLayout underview;
    float y1,y2,total_h,init_view1_h,init_view2_h;
    Float[] max_h = new Float[3];
    Float[] min_h = new Float[3];
    int mode = 0, changemode;
    ViewGroup.LayoutParams params1,params2;

    EditText edit_;
    private int day;
    private int month;
    private int year;

    private int flag=0;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    View preSelectedView;
    Long preSelectedDate;

    DateChanged dateChanged;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_main);
        context = this;
        weather = new HashMap<>();

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH)+1;
        day = Calendar.getInstance().get(Calendar.DATE);

        calendarView = findViewById(R.id.calendarView);
        underview = findViewById(R.id.schedule_main_underview);
        params1 = calendarView.getLayoutParams();
        params2 = underview.getLayoutParams();


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        calendarView.post(new Runnable() {
            @Override
            public void run() {
                total_h = calendarView.getHeight();
                max_h[0]=total_h; max_h[1]=(total_h); max_h[2]=(total_h*(float)0.5);
                min_h[0]=(total_h*(float)0.5); min_h[1]=(total_h*(float)0.25); min_h[2]=(total_h*(float)0.25);
                Log.d("minseok",calendarView.getHeight() + " run " + underview.getHeight());
            }
        });

        calendarView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //true : 그 뒤 리스너까지 이벤트를 전달하지 않고, 터치만 하고 끝낸다.
                //false : 그 뒤 이벤트까지 액션을 전달한다.
                //onTouch --> onClick --> onLongClick
                Log.d("minseok","touch");
                calendarView.performClick();
                moveview(event);
                return true;
            }
        });



        edit_=findViewById(R.id.edit_);

        dateChanged = new DateChanged();
        calendarView.setOnDateClickListener((view, date) -> {
            Calendar cal = date.getCalendar();
            Long mill = cal.getTimeInMillis();

            year = date.calendar.get(Calendar.YEAR);
            month = date.calendar.get(Calendar.MONTH)+1;
            day = date.calendar.get(Calendar.DATE);

            if (preSelectedDate == null || !preSelectedDate.equals(mill)) {
                if (preSelectedView != null){
                    preSelectedView.setBackgroundColor(getColor(R.color.bg_white));
                }
                preSelectedView = view;
                preSelectedDate = mill;
                view.setBackgroundColor(getColor(R.color.text_white));
            } else if (date.getSchedules().size() == 0 && date.getHolidays().size() == 0) {
//                preSelectedView.setBackgroundColor(getColor(R.color.bg_white));
                Intent intent = new Intent(getApplicationContext(), schedule.class);
                intent.putExtra("mode", 0);
                intent.putExtra("year",year);
                intent.putExtra("month",month);
                intent.putExtra("day",day);
                startActivityForResult(intent,78);
            } else {
//                preSelectedView.setBackgroundColor(getColor(R.color.bg_white));
                ScheduleDialog oDialog = new ScheduleDialog(this,
                        Time.CalendarToMill(cal), date.getHolidays());
                oDialog.show();
            }

            dateChanged.dateChangedListener(date);
        });
        calendarView.setOnDateChangedListener(dateChanged);
        calendarView.setActivity(this);

        getSupportActionBar().setElevation(0); // appbar shadow remove
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 툴바 홈버튼 활성화
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_density_medium_24);

        temperature = findViewById(R.id.temperature);
        lunardate = findViewById(R.id.lunardate);
        or = findViewById(R.id.or);
        weatherView = findViewById(R.id.weather);
        add_schedule=findViewById(R.id.add_schedule);
        edit_.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()>0){
                    add_schedule.setImageResource(R.drawable.baseline_check_circle_24_blue);
                    flag=2;
                }else{
                    add_schedule.setImageResource(R.drawable.baseline_check_circle_24_gray);
                    flag=1;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edit_.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE){
                    InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(edit_.getWindowToken(),0);
                    return true;
                }
                return false;
            }
        });

        add_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==2){
                    Database db = new Database(schedule_main.this);
                    LocalDateTime begin = LocalDateTime.of(year,month,day,0,0,0,0);
                    LocalDateTime end=LocalDateTime.of(year,month,day,23,59,0,0);
                    Schedule schedule = new Schedule(edit_.getText() + "", true, false, Time.LocalDateTimeToMills(begin), Time.LocalDateTimeToMills(end),"","");
                    db.insert(Database.LOCAL, schedule, new AfterTask() {
                        @Override
                        public void ifSuccess(Object result) {}
                        @Override
                        public void ifFail(Object result) {}
                    });
                    edit_.setText("");
                    add_schedule.setImageResource(R.drawable.ic_baseline_add_circle_24);
                    flag=0;
                    edit_.clearFocus();
                    drawerLayout.requestFocus();
                    InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(edit_.getWindowToken(),0);
                }else if(flag==0) {
                    Intent intent = new Intent(getApplicationContext(), schedule.class);
                    intent.putExtra("mode", 0);
                    intent.putExtra("year", year);
                    intent.putExtra("month", month);
                    intent.putExtra("day", day);
                    startActivityForResult(intent, 78);
                }
            }
        });

        drawerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                edit_.setText("");
                add_schedule.setImageResource(R.drawable.ic_baseline_add_circle_24);
                flag=0;
                edit_.clearFocus();
                drawerLayout.requestFocus();
                InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(edit_.getWindowToken(),0);
                return false;
            }
        });

        DB = ScheduleDatabase.getInstance(context);
        scheduleRV = findViewById(R.id.schedule_recyclerView);
        scheduleArrayList = new ArrayList<>();

        scheduleRV.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!scheduleRV.canScrollVertically(-1)) {
                    Log.i("minseok", "Top of list");
                } else if (!scheduleRV.canScrollVertically(1)) {
                    Log.i("minseok", "End of list");
                } else {
                    Log.i("minseok", "idle");
                }
            }
        });

        scheduleRV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("minseok","touch");
                scheduleRV.performClick();
                if(mode==0||mode==1) {

                    //df
                    moveview(event);
                    return true;
                }
                else{
                    if(!scheduleRV.canScrollVertically(-1)){
                        Log.d("minseok","최상단");
                        //moveview(event);
                    }
                    return false;
                }
            }
        });

//        calendarView.setOnDateChangedListener((widget, date, selected) ->{
//            year= date.getYear();
//            month=date.getMonth();
//            day=date.getDay();
//            String str_date = date.toString().substring(12,date.toString().length() - 1);
//            LocalDate localDate = LocalDate.parse(str_date, DateTimeFormatter.ofPattern("yyyy-M-d"));
//            Log.d("WeGlonD", str_date);
//            LocalDate lunarDate = LocalDate.parse(LunarCalendar.Solar2Lunar(localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))), DateTimeFormatter.ofPattern("yyyyMMdd"));
//            lunardate.setText("음력 " + lunarDate.getMonthValue() + "월 " + lunarDate.getDayOfMonth() + "일");
//            ShowWeatherInfo(localDate);
//            scheduleArrayList.clear();

//            Long dateMills = Time.CalendarDayToMill(date);
//            scheduleArrayList.clear();
//            scheduleArrayList.addAll(Arrays.asList(DB.scheduleDao().loadAllScheduleDuring(dateMills, dateMills + Time.ONE_DAY)));
//            Collections.sort(scheduleArrayList);
//            RVAdapter.notifyDataSetChanged();
//        });

        RVAdapter = new schedule_main_RVAdapter(scheduleArrayList, this);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        scheduleRV.setLayoutManager(layoutManager);
        scheduleRV.setAdapter(RVAdapter);

        LocalDate localDate = LocalDate.parse(LunarCalendar.Solar2Lunar(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))), DateTimeFormatter.ofPattern("yyyyMMdd"));
        lunardate.setText("음력 " + localDate.getMonthValue() + "월 " + localDate.getDayOfMonth() + "일");
        getWeather(weather, 35.887390,128.611629);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        Toast toast = Toast.makeText(getApplicationContext(),"", Toast.LENGTH_LONG);

        switch(item.getItemId())
        {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.today_move:
                toast.setText("Select today_move");
//                calendarView.setSelectedDate(CalendarDay.today());
//                calendarView.setSelectedDate(Time.LocalDateToMill(LocalDate.now()));
                break;
            case R.id.name_search:
                toast.setText("Select name_search");
                break;
        }

        toast.show();
        return super.onOptionsItemSelected(item);
    }

    public void getWeather(HashMap<LocalDate, HashMap<String, Integer>> weather, double latitude, double longitude){
        WeatherApiExplorer weatherApiExplorer = new WeatherApiExplorer(context);
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    weatherApiExplorer.getWeather(weather, latitude, longitude);
                    Log.d("WeGlonD", weather.toString());
                    temperature.post(new Runnable() {
                        @Override
                        public void run() {
                            ShowWeatherInfo(LocalDate.now());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void ShowWeatherInfo(LocalDate date){
        if(weather.containsKey(date)) {
            or.setVisibility(View.VISIBLE);
            temperature.setVisibility(View.VISIBLE);
            weatherView.setVisibility(View.VISIBLE);

            if(weather.get(date).containsKey("TMP")){
                temperature.setText(weather.get(date).get("TMP").toString() + "ºC");
            }
            else{
                temperature.setText(weather.get(date).get("MAX") + "º/" + weather.get(date).get("MIN") + "ºC");
            }

            int pty = weather.get(date).get("PTY");
            int sky = weather.get(date).get("SKY");
            if (pty > 0) {
                if (pty == 1 || pty == 4) {
                    weatherView.setImageResource(R.drawable.rainy_2_svgrepo_com);
                } else if (pty == 2) {
                    weatherView.setImageResource(R.drawable.cloud_snow_rain_svgrepo_com);
                } else {
                    weatherView.setImageResource(R.drawable.snow_outline_svgrepo_com);
                }
            } else {
                if (sky == 1) {
                    weatherView.setImageResource(R.drawable.ic_baseline_wb_sunny_24);
                } else if (sky == 2) {
                    weatherView.setImageResource(R.drawable.cloudy_svgrepo_com);
                } else {
                    weatherView.setImageResource(R.drawable.ic_twotone_wb_cloudy_24);
                }
            }
        }
        else{
            or.setVisibility(View.GONE);
            temperature.setVisibility(View.GONE);
            weatherView.setVisibility(View.GONE);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    class DateChanged implements CustomCalendar.OnDateChangedListener{

        @Override
        public void dateChangedListener(CalendarDate date) {
            //            String str_date = date.toString().substring(12,date.toString().length() - 1);
//            LocalDate localDate = LocalDate.parse(str_date, DateTimeFormatter.ofPattern("yyyy-M-d"));
            Calendar cal = date.getCalendar();
            Long mill = cal.getTimeInMillis();
            String str = Time.MillToDate(mill);
//            Log.d("WeGlonD", str_date);
            LocalDate lunarDate = LocalDate.parse(LunarCalendar.Solar2Lunar(str), DateTimeFormatter.ofPattern("yyyyMMdd"));
//            LocalDate lunarDate = LocalDate.parse(LunarCalendar.Solar2Lunar(localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))), DateTimeFormatter.ofPattern("yyyyMMdd"));
            lunardate.setText("음력 " + lunarDate.getMonthValue() + "월 " + lunarDate.getDayOfMonth() + "일");
            LocalDate localDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
            year=localDate.getYear();
            month=localDate.getMonth().getValue();
            day=localDate.getDayOfMonth();
            edit_.setHint(makeHint());
            ShowWeatherInfo(localDate);
            scheduleArrayList.clear();

            Long dateMills = Time.CalendarToMill(date.getCalendar());
            scheduleArrayList.clear();

            ArrayList<Holiday> holi = date.getHolidays();
            for(Holiday h : holi){
                Long start = h.date;
                Calendar endcalendar = Calendar.getInstance();
                endcalendar.setTimeInMillis(start);
                endcalendar.set(Calendar.HOUR_OF_DAY, 23); endcalendar.set(Calendar.MINUTE, 59); endcalendar.set(Calendar.SECOND, 59);
                Long end = Time.CalendarToMill(endcalendar);
                scheduleArrayList.add(new Schedule(h.name, true, start, end, true));
            }
            scheduleArrayList.addAll(Arrays.asList(DB.scheduleDao().loadAllScheduleDuring(dateMills, dateMills + Time.ONE_DAY)));
            Collections.sort(scheduleArrayList);
            RVAdapter.notifyDataSetChanged();
        }

        public String makeHint(){
            return String.valueOf(month)+"월 "+String.valueOf(day)+"일에 일정 추가";
        }
    }

    void moveview(MotionEvent event) {
        //calendar.getViewPager().invalidate();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("minseok", "mode" + mode + " " + max_h[mode] + " " + min_h[mode]);
                y1 = event.getY();
                init_view1_h = calendarView.getHeight();
                init_view2_h = underview.getHeight(); changemode = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                y2 = event.getY();
                float delta = y2 - y1;
                float absdelta = delta>0?delta:-delta;
                if(absdelta<100){
                    break;
                }
                calendarView.setClickable(false);
                calendarView.getViewPager().setUserInputEnabled(false);
                if (y1 < y2) {
                    //아래로 슬라이딩
                    Log.d("minseok", mode + "move down " + params1.height + " " + params2.height);
                    params1.height = (int) (init_view1_h + delta);
                    float tmp = max_h[mode] + (float) 0.5;
                    if (params1.height > tmp) params1.height = (int) tmp;
                    params2.height = (int) (total_h - params1.height);
                    calendarView.setLayoutParams(params1);
                    underview.setLayoutParams(params2);
                    changemode = -1;
                } else if (y1 > y2) {
                    //위로 슬라이딩
                    Log.d("minseok", mode + "move up" + params1.height + " " + params2.height);
                    float tmp = min_h[mode] + (float) 0.5;
                    params1.height = (int) (init_view1_h + delta);
                    if (params1.height < tmp) params1.height = (int) tmp;
                    params2.height = (int) (total_h - params1.height);
                    calendarView.setLayoutParams(params1);
                    underview.setLayoutParams(params2);
                    changemode = 1;
                }

                break;
            case MotionEvent.ACTION_UP:
                if(changemode==0)break;
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
                        calendarView.setClickable(false);
                        calendarView.getViewPager().setUserInputEnabled(false);
                        float value = (float) animation.getAnimatedValue();
                        params1.height = (int) (value + 0.5f);
                        params2.height = (int) (total_h - params1.height);
                        calendarView.setLayoutParams(params1);
                        underview.setLayoutParams(params2);

                        RecyclerView rc = (calendarView.getViewPager().getChildAt(0)).getRootView().findViewById(R.id.fragment_page_recyclerView);
                        TextView tv = (calendarView.getViewPager().getChildAt(0)).getRootView().findViewById(R.id.fragment_page_title_textView);
                        LinearLayout lo = (calendarView.getViewPager().getChildAt(0)).getRootView().findViewById(R.id.fragment_page_linearLayout);

                        ((PageRVAdapter) rc.getAdapter()).viewHolderHeight.setValue(
                                (calendarView.getHeight()-tv.getHeight()-lo.getHeight())/6
                        );

//                        rc.getAdapter().notifyItemRangeRemoved(0, rc.getAdapter().getItemCount());
//                        rc.getAdapter().notifyDataSetChanged();

                        rc.requestLayout();
                        calendarView.requestLayout();
                        calendarView.setClickable(true);
                        calendarView.getViewPager().setUserInputEnabled(true);
                    }
                });
                Log.d("minseok","animation start");
                animator.start();
                Log.d("minseok","animation end");
                mode += changemode;
                if (mode > 2) mode = 2;
                else if (mode < 0) mode = 0;
                calendarView.setClickable(true);
                calendarView.getViewPager().setUserInputEnabled(true);
                break;
        }

        RecyclerView rc = (calendarView.getViewPager().getChildAt(0)).getRootView().findViewById(R.id.fragment_page_recyclerView);
        TextView tv = (calendarView.getViewPager().getChildAt(0)).getRootView().findViewById(R.id.fragment_page_title_textView);
        LinearLayout lo = (calendarView.getViewPager().getChildAt(0)).getRootView().findViewById(R.id.fragment_page_linearLayout);

        ((PageRVAdapter) rc.getAdapter()).viewHolderHeight.setValue(
                (calendarView.getHeight()-tv.getHeight()-lo.getHeight())/6
        );

//        rc.getAdapter().notifyItemRangeRemoved(0, rc.getAdapter().getItemCount());
//        rc.getAdapter().notifyDataSetChanged();

        rc.requestLayout();
        calendarView.requestLayout();
    }
}
