package com.zzammo.calendar.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
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
import com.zzammo.calendar.database.Metadata;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.database.room.ScheduleDatabase;
import com.zzammo.calendar.dialog.ScheduleDialog;
import com.zzammo.calendar.holiday.HolidayApiExplorer;
import com.zzammo.calendar.lunar.LunarCalendar;
import com.zzammo.calendar.notification_service.MyService;
import com.zzammo.calendar.util.AfterTask;
import com.zzammo.calendar.util.Time;
import com.zzammo.calendar.weather.WeatherApiExplorer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Long> HolidayDates;
    public static ArrayList<String> HolidayNames;

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

    public static CustomCalendar calendarView;
    ConstraintLayout underview;
    NestedScrollView sv;
    float y1,y2,total_h,init_view1_h,init_view2_h;
    Float[] max_h = new Float[3];
    Float[] min_h = new Float[3];
    int mode = 0, changemode;
    ViewGroup.LayoutParams params1,params2;

    EditText edit_;


    boolean moving = false;
    Switch use_localDB_switch;
    Switch use_fireDB_switch;
    LinearLayout login_layout_btn;

    private int day;
    private int month;
    private int year;

    private int flag=0;

    private boolean isLogin=false;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    View naviHeader;
    NumberPicker alarm_how_picker;

    CustomCalendar.OnMonthChangedListener monthChangedListener;
    MotionEvent mevent;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        weather = new HashMap<>();
        HolidayNames = new ArrayList<>();
        HolidayDates = new ArrayList<>();

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH)+1;
        day = Calendar.getInstance().get(Calendar.DATE);

        calendarView = findViewById(R.id.calendarView);
        underview = findViewById(R.id.schedule_main_underview);
        sv = findViewById(R.id.schedule_sv);

        params1 = calendarView.getLayoutParams();
        params2 = underview.getLayoutParams();


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        naviHeader = navigationView.getHeaderView(0);
        naviHeader.findViewById(R.id.login_layout_btn).setOnClickListener(view -> {
            Intent it = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(it);
        });

        alarm_how_picker=naviHeader.findViewById(R.id.alarm_how_picker);

        alarm_how_picker.setMaxValue(2);
        alarm_how_picker.setMinValue(0);
        alarm_how_picker.setDisplayedValues(new String[]{
                "무음","소리","진동"
        });
        alarm_how_picker.setWrapSelectorWheel(false);

        alarm_how_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {

                MyService.how_flag=i1;
            }
        });

        Intent itt=new Intent(MainActivity.this, MyService.class);
        startForegroundService(itt);

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
                Log.d("minseok","touch calendar");
                calendarView.performClick();
                moveview(event);
                return true;
            }
        });

        edit_=findViewById(R.id.edit_);

        monthChangedListener = new MonthChanged();
        calendarView.setOnDateClickListener(new DateClicked());
        calendarView.setOnDateChangedListener(new DateChanged());
        calendarView.setMonthChangedListener(monthChangedListener);
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
                    Database db = new Database(MainActivity.this);
                    LocalDateTime begin = LocalDateTime.of(year,month,day,0,0,0,0);
                    LocalDateTime end=LocalDateTime.of(year,month,day,23,59,0,0);
                    Schedule schedule = new Schedule(edit_.getText() + "", true, false, Time.LocalDateTimeToMills(begin), Time.LocalDateTimeToMills(end),"","");
                    db.insert(Database.LOCAL, schedule, new AfterTask() {
                        @Override
                        public void ifSuccess(Object result) {}
                        @Override
                        public void ifFail(Object result) {}
                    });

                    calendarView.addScheduleOnSelectedDate(schedule);

                    edit_.setText("");
                    add_schedule.setImageResource(R.drawable.add_circle_svgrepo_com);
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
                add_schedule.setImageResource(R.drawable.add_circle_svgrepo_com);
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

        underview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("minseok","underviewtouch");
                return false;
            }
        });
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




        scheduleRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    Log.d("minseok",mode+"scrollup");

                    // Scrolling Up
                    // Perform your desired action here
                } else {
                    Log.d("minseok",mode+"scrolldown");
                    // Scrolling Down
                    // Perform your desired action here
                }

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });



        scheduleRV.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return true;
            }
        });



        scheduleRV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                moveview(event);
                return true;
            }
        });

        RVAdapter = new schedule_main_RVAdapter(scheduleArrayList, this);
        layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL ,false);
        scheduleRV.setLayoutManager(layoutManager);
        scheduleRV.setLayoutManager(new LinearLayoutManager(context){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        scheduleRV.setAdapter(RVAdapter);
        scheduleRV.setNestedScrollingEnabled(false);
        sv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d("minseok", "nested scroll view Scrolled!");
            }
        });

        LocalDate localDate = LocalDate.parse(LunarCalendar.Solar2Lunar(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))), DateTimeFormatter.ofPattern("yyyyMMdd"));
        lunardate.setText("음력 " + localDate.getMonthValue() + "월 " + localDate.getDayOfMonth() + "일");
        getWeather(weather, 35.887390,128.611629);

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
                calendarView.gotoTodayPage();
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
            if (calendarView.getSelectedView() != null)
                calendarView.getSelectedView().setBackgroundResource(0);
            Log.d("Dirtfy", "date changed");

            if (!Objects.equals(calendarView.getSelectedDate(), date.date)) return;

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date.date);

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH)+1;
            day = cal.get(Calendar.DATE);

            if (date.getSchedules().size() == 0 && date.getHolidays().size() == 0) {
//                preSelectedView.setBackgroundColor(getColor(R.color.bg_white));
                Intent intent = new Intent(getApplicationContext(), schedule.class);
                intent.putExtra("mode", 0);
                intent.putExtra("year",year);
                intent.putExtra("month",month);
                intent.putExtra("day",day);
                startActivityForResult(intent,78);
            } else {
//                preSelectedView.setBackgroundColor(getColor(R.color.bg_white));
                ScheduleDialog oDialog = new ScheduleDialog(context,
                        Time.CalendarToMill(cal), date.getHolidays());
                oDialog.show();
            }
        }
    }
    class DateClicked implements CustomCalendar.OnDateClickListener{
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void dateClickListener(View view, CalendarDate date) {
            //            String str_date = date.toString().substring(12,date.toString().length() - 1);
//            LocalDate localDate = LocalDate.parse(str_date, DateTimeFormatter.ofPattern("yyyy-M-d"));
            if (view != null)
                view.setBackgroundResource(R.drawable.today_box);
            Log.d("Dirtfy", "date clicked");

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date.date);
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

            Long dateMills = date.date;
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
        if (scheduleRV.canScrollVertically(-1)) {
            Log.d("minseok","notrealtop");
            return;
        }
        if(mode==0 || mode==1){
            Log.d("minseok","stopmode01");
            scheduleRV.stopScroll();
        }

        //calendar.getViewPager().invalidate();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("minseok", "mode" + mode + " " + max_h[mode] + " " + min_h[mode]);
                y1 = event.getY();
                init_view1_h = calendarView.getHeight();
                init_view2_h = underview.getHeight(); changemode = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                moving = true;
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
                moving = false;
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
        if(mode==2) scheduleRV.smoothScrollToPosition(1);
    }

    class MonthChanged implements CustomCalendar.OnMonthChangedListener{
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void monthChangedListener(Long firstDate) {
            HolidayDates.clear();
            HolidayNames.clear();
            Log.d("WeGlonD", "onMonthChanged - date : " + firstDate);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(firstDate);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH)+1;
            Database database = new Database(context);
            Metadata low_bound_holiday = database.getMetadata("Holi_Min_Year");
            Metadata high_bound_holiday = database.getMetadata("Holi_Max_Year");
            int minY, maxY;
            if (low_bound_holiday == null && high_bound_holiday == null) {
                minY = maxY = year;
                database.insert(new Metadata("Holi_Min_Year", Integer.toString(minY)));
                database.insert(new Metadata("Holi_Max_Year", Integer.toString(maxY)));
                //공휴일 API에서 가져오기
                GetHoliday(year, new AfterTask() {
                    @Override
                    public void ifSuccess(Object result) {
                        //calendarView.invalidateDecorators();
                        //공휴일 DB에 쓰기
                        for (int i = 0; i < HolidayNames.size(); i++) {
                            database.insert(new Holiday(HolidayDates.get(i), HolidayNames.get(i)));
                        }
                    }

                    @Override
                    public void ifFail(Object result) {
                    }
                });

            } else {
                minY = Integer.parseInt(low_bound_holiday.data);
                maxY = Integer.parseInt(high_bound_holiday.data);
                if (year < minY || year > maxY) {
                    //공휴일 API에서 가져오기
                    GetHoliday(year, new AfterTask() {
                        @Override
                        public void ifSuccess(Object result) {
                            //calendarView.invalidateDecorators();
                            //공휴일 DB에 쓰기
                            for (int i = 0; i < HolidayNames.size(); i++) {
                                database.insert(new Holiday(HolidayDates.get(i), HolidayNames.get(i)));
                            }
                            if(!HolidayNames.isEmpty()) {
                                if (year < minY) {
                                    low_bound_holiday.data = Integer.toString(year);
                                    database.update(low_bound_holiday);
                                } else {
                                    high_bound_holiday.data = Integer.toString(year);
                                    database.update(high_bound_holiday);
                                }
                            }
                        }

                        @Override
                        public void ifFail(Object result) {
                        }
                    });

                }
            }
//            Log.d("Dirtfy", "HolidayDates : " + HolidayDates.toString());
//            Log.d("Dirtfy", "HolidayNames : "  + HolidayNames.toString());
        }
    }

    void GetHoliday(int year, AfterTask afterTask) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HolidayApiExplorer apiExplorer = new HolidayApiExplorer(context);
                    apiExplorer.getHolidays(year, -1, HolidayNames, HolidayDates);
                    for (int i = 0; i < HolidayNames.size(); i++) {
                        Log.d("WeGlonD", HolidayDates.get(i).toString() + ' ' + HolidayNames.get(i));
                    }
                    afterTask.ifSuccess(0);
                    calendarView.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("??", "??");
                            calendarView.invalidatePage(getSupportFragmentManager());
                        }
                    });
                    //2023년 1월의 국경일, 공휴일 정보 불러옴. Month로 0이하의 값을 주면 2023년 전체를 불러옴.
                } catch (IOException | XmlPullParserException e) {
                    Log.e("WeGlonD", "ApiExplorer failed - " + e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private long presstime=0;
    @Override
    public void onBackPressed(){
        long tempTime=System.currentTimeMillis();
        long interval=tempTime-presstime;

        if(interval>=0&&interval<=1000){
            setResult(RESULT_CANCELED);
            finish();
        }else{
            presstime=tempTime;
            Toast.makeText(getApplicationContext(),"한번 더 누르시면 메인화면으로 돌아갑니다",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        MenuItem searchItem = menu.findItem(R.id.name_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        ///요 리스너가 타이핑 칠 때 리스너
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            ///다 검색하고 났을때
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("minseok", "querytextsubmit");
                search(query);
                searchView.setQueryHint("검색");
                return false;
            }

            ///칠 때마다 텍스트를 하나하나 입력받을 때 (검색 도중)
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("minseok", "onquerytextchange");
                search(newText);
                searchView.setQueryHint("검색");
                return false;
            }
        });
        //돋보기 눌럿을 때 실행되는 함수
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                Log.d("minseok", "expand_");
                calendarView.setVisibility(View.GONE);
                searchView.setQueryHint("검색");
                scheduleArrayList.clear();
                scheduleArrayList.addAll(DB.scheduleDao().getAll());
                RVAdapter.notifyDataSetChanged();
                return true;
            }

            ///검색을 실행하던 도중 뒤로가기 하면 콜랍스 함수가 실행되지만 익스팬더블이 뜨려고 했지만 검색에 검색 중이던 함수가 다시 작동이 되서 어댑터에 리사이클류 그게 뜨지 않고 오류가 발생
/// 콜랩스 함수가 시작되면 무조건 입력중이던 함수는 적용안되게 수정함
            //검색이 종료되었을 때
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                calendarView.setVisibility(View.VISIBLE);
                registerForContextMenu(scheduleRV);
                Long date = calendarView.getSelectedDate();
                Log.d("minseok", "collapse_" + date);
                scheduleArrayList.clear();
                if (date != null) {
                    scheduleArrayList.addAll(Arrays.asList(DB.scheduleDao().loadAllScheduleDuring(date, date + Time.ONE_DAY-1)));
                    Collections.sort(scheduleArrayList);
                }
                RVAdapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void search(String keyword) {//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!|
        registerForContextMenu(scheduleRV);
        keyword = "%" + keyword + "%";
        scheduleArrayList.clear();
        scheduleArrayList.addAll(DB.scheduleDao().searchRecords(keyword));
        Log.d("minseok", "search_" + scheduleArrayList.size());
        RVAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        Log.d("minseok", "resume_");
        super.onResume();
        Long date = calendarView.getSelectedDate();
        monthChangedListener.monthChangedListener(calendarView.getCurrentDate());
        if (date == null) return;
        scheduleArrayList.clear();

        Schedule[] schedules = DB.scheduleDao().loadAllScheduleDuring(date, date + Time.ONE_DAY-1);

        scheduleArrayList.addAll(Arrays.asList(schedules));
        Collections.sort(scheduleArrayList);
        RVAdapter.notifyDataSetChanged();

//        for (int i = 0;i < CustomCalendar.DAY_SIZE;i++){
//            Long iDate = calendarView.getDateFromPosition(i);
//            ArrayList<Schedule> schedules =
//                    new ArrayList<>(Arrays.asList(
//                            DB.scheduleDao().loadAllScheduleDuring(iDate, iDate + Time.ONE_DAY - 1)));
//            calendarView.updateScheduleOnPosition(i, schedules);
//        }

        calendarView.updateScheduleOnPosition(
                calendarView.getSelectedDateRealIndex(),
                new ArrayList<>(scheduleArrayList)
        );

    }
}
