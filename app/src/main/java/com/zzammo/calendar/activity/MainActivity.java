package com.zzammo.calendar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.zzammo.calendar.R;
import com.zzammo.calendar.adapter.ScheduleRVAdapter;
import com.zzammo.calendar.custom_calendar.teest.activity.ViewPagerActivity;
import com.zzammo.calendar.custom_calendar.test;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.database.room.ScheduleDatabase;
import com.zzammo.calendar.dialog.ScheduleDialog;
import com.zzammo.calendar.holiday.HolidayApiExplorer;
import com.zzammo.calendar.notification_service.MyService;
import com.zzammo.calendar.schedule_event.MakeSchedule;
import com.zzammo.calendar.test.AuthTestActivity;
import com.zzammo.calendar.test.DBTestActivity;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ScheduleDatabase DB;

    MaterialCalendarView calendarView;
    RecyclerView scheduleRV;
    ScheduleRVAdapter RVAdapter;
    LinearLayoutManager layoutManager;
    ArrayList<Schedule> scheduleArrayList;
    Intent it;
    Context context;
//    public static ArrayList<Long> HolidayDates;
//    public static ArrayList<String> HolidayNames;

    CalendarDay preSelectedDate = null;
    TextView holidayname_textview;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        HolidayDates = new ArrayList<>();
//        HolidayNames = new ArrayList<>();

        /*Log.d("minseok",LunarCalendar.Solar2Lunar("20230105")) ; // 양력을 음력으로 바꾸기
        Log.d("minseok", LunarCalendar.Lunar2Solar("20010527")) ; // 음력을 양력으로 바꾸기*/
        Button button = (Button) findViewById(R.id.btn);

        Intent itt=new Intent(MainActivity.this, MyService.class);
        startService(itt);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainAddressActivity.class);
                startActivity(intent);
            }

        });

        Button btn = (Button) findViewById(R.id.next_xml);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), schedule_main.class);
                startActivity(intent);
            }

        });

        Button btn1 = (Button) findViewById(R.id.activity_main_slidingpanelayout_test);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), slidingpanetest.class);
                startActivity(intent);
            }

        });

        this.context = this;

        DB = ScheduleDatabase.getInstance(context);

        //테스트용
        findViewById(R.id.activity_main_auth_test_btn).setOnClickListener(v -> {
            Intent it = new Intent(context, AuthTestActivity.class);
            startActivity(it);
        });
        findViewById(R.id.activity_main_DB_test_btn).setOnClickListener(v -> {
            Intent it = new Intent(context, DBTestActivity.class);
            startActivity(it);
        });
        findViewById(R.id.activity_main_schedule_make_test_btn).setOnClickListener(v -> {
            Intent it = new Intent(context, ScheduleMakeActivity.class);
            if (calendarView.getSelectedDate() != null){
                Long mills = Time.CalendarDayToMill(calendarView.getSelectedDate());
                it.putExtra("timeInMill", mills);
            }
            startActivity(it);
        });
        findViewById(R.id.activity_main_schedule_make_test_btn).setOnClickListener(v -> {
            Intent it = new Intent(context, ScheduleMakeActivity.class);
            if (calendarView.getSelectedDate() != null){
                Long mills = Time.CalendarDayToMill(calendarView.getSelectedDate());
                it.putExtra("timeInMill", mills);
            }
            startActivity(it);
        });
        findViewById(R.id.activity_main_viewPager_test_btn).setOnClickListener(v -> {
            Intent it = new Intent(context, ViewPagerActivity.class);
            startActivity(it);
        });

        findViewById(R.id.custom_calendar_test_btn).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), test.class);
            startActivity(intent);
        });
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
            Toast.makeText(getApplicationContext(),"한번 더 누르시면 종료됩니다",Toast.LENGTH_SHORT).show();
        }
    }

//    void GetHoliday(int year, AfterTask afterTask) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    HolidayApiExplorer apiExplorer = new HolidayApiExplorer(context);
//                    apiExplorer.getHolidays(year, -1, HolidayNames, HolidayDates);
//                    for (int i = 0; i < HolidayNames.size(); i++) {
//                        Log.d("WeGlonD", HolidayDates.get(i).toString() + ' ' + HolidayNames.get(i));
//                    }
//                    afterTask.ifSuccess(0);
//                    calendarView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            calendarView.invalidateDecorators();
//                        }
//                    });
//                    //2023년 1월의 국경일, 공휴일 정보 불러옴. Month로 0이하의 값을 주면 2023년 전체를 불러옴.
//                } catch (IOException | XmlPullParserException e) {
//                    Log.e("WeGlonD", "ApiExplorer failed - " + e);
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

//    public OnMonthChangedListener onMonthChangedListener = new OnMonthChangedListener() {
//        @RequiresApi(api = Build.VERSION_CODES.O)
//        @Override
//        public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
//            HolidayDates.clear();
//            HolidayNames.clear();
//            Log.d("WeGlonD", "onMonthChanged - date : " + date);
//            int year = date.getYear();
//            int month = date.getMonth();
//            Database database = new Database(context);
//            Metadata low_bound_holiday = database.getMetadata("Holi_Min_Year");
//            Metadata high_bound_holiday = database.getMetadata("Holi_Max_Year");
//            int minY, maxY;
//            if (low_bound_holiday == null && high_bound_holiday == null) {
//                minY = maxY = year;
//                database.insert(new Metadata("Holi_Min_Year", Integer.toString(minY)));
//                database.insert(new Metadata("Holi_Max_Year", Integer.toString(maxY)));
//                //공휴일 API에서 가져오기
//                GetHoliday(year, new AfterTask() {
//                    @Override
//                    public void ifSuccess(Object result) {
//                        //calendarView.invalidateDecorators();
//                        //공휴일 DB에 쓰기
//                        for (int i = 0; i < HolidayNames.size(); i++) {
//                            database.insert(new Holiday(HolidayDates.get(i), HolidayNames.get(i)));
//                        }
//                    }
//
//                    @Override
//                    public void ifFail(Object result) {
//                    }
//                });
//
//            } else {
//                minY = Integer.parseInt(low_bound_holiday.data);
//                maxY = Integer.parseInt(high_bound_holiday.data);
//                if (year < minY || year > maxY) {
//                    //공휴일 API에서 가져오기
//                    GetHoliday(year, new AfterTask() {
//                        @Override
//                        public void ifSuccess(Object result) {
//                            //calendarView.invalidateDecorators();
//                            //공휴일 DB에 쓰기
//                            for (int i = 0; i < HolidayNames.size(); i++) {
//                                database.insert(new Holiday(HolidayDates.get(i), HolidayNames.get(i)));
//                            }
//                            if(!HolidayNames.isEmpty()) {
//                                if (year < minY) {
//                                    low_bound_holiday.data = Integer.toString(year);
//                                    database.update(low_bound_holiday);
//                                } else {
//                                    high_bound_holiday.data = Integer.toString(year);
//                                    database.update(high_bound_holiday);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void ifFail(Object result) {
//                        }
//                    });
//
//                } else {
//                    //공휴일 DB에서 가져오기
////                    String keyword = "%" + year;
////                    if (month < 10) keyword = keyword + "0";
////                    keyword = keyword + month + "%";
//                    LocalDate lo = LocalDate.parse(date.toString().substring(12, date.toString().length() - 1), DateTimeFormatter.ofPattern("yyyy-M-d"));
//                    lo = lo.plusMonths(1); lo = lo.minusDays(1);
//                    CalendarDay endday = CalendarDay.from(lo.getYear(), lo.getMonthValue(),lo.getDayOfMonth());
//                    Long begin = Time.CalendarDayToMill(date),  end = Time.CalendarDayToMill(endday);
//                    Log.d("Dirtfy", "begin : " + begin + " end : " + end);
////                    Log.d("Dirtfy", "begin : " + Time.MillToDate(begin) + " end : " + Time.MillToDate(end));
//                    List<Holiday> Holidays = database.HoliLocalDB.holidayDao().searchHolidayByDate(begin, end);
//                    Log.d("Dirtfy", "Holidays size : "+Holidays.size()+"");
//                    for (Holiday holi : Holidays) {
//                        HolidayDates.add(holi.date);
//                        HolidayNames.add(holi.name);
////                        Log.d("Dirtfy", "Holiday DB Read - " + holi.date + " " + Time.MillToDate(holi.date) + " " + holi.name);
//                        calendarView.invalidateDecorators();
//                    }
//                }
//            }
////            Log.d("Dirtfy", "HolidayDates : " + HolidayDates.toString());
////            Log.d("Dirtfy", "HolidayNames : "  + HolidayNames.toString());
//        }
//    };


//    @Override
//    public void onBackPressed() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setMessage("캘린더 앱을 종료하시겠습니까?");
//        builder.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//        builder.setNegativeButton("네", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                finish();
//            }
//        });
//        builder.show();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_option, menu);
//        MenuItem searchItem = menu.findItem(R.id.name_search);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        ///요 리스너가 타이핑 칠 때 리스너
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            ///다 검색하고 났을때
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Log.d("minseok", "querytextsubmit");
//                search(query);
//                searchView.setQueryHint("검색");
//                return false;
//            }
//
//            ///칠 때마다 텍스트를 하나하나 입력받을 때 (검색 도중)
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                Log.d("minseok", "onquerytextchange");
//                search(newText);
//                searchView.setQueryHint("검색");
//                return false;
//            }
//        });
//        //돋보기 눌럿을 때 실행되는 함수
//        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem menuItem) {
//                Log.d("minseok", "expand_");
//                calendarView.setVisibility(View.GONE);
//                searchView.setQueryHint("검색");
//                scheduleArrayList.clear();
//                scheduleArrayList.addAll(DB.scheduleDao().getAll());
//                RVAdapter.notifyDataSetChanged();
//                return true;
//            }
//
//            ///검색을 실행하던 도중 뒤로가기 하면 콜랍스 함수가 실행되지만 익스팬더블이 뜨려고 했지만 검색에 검색 중이던 함수가 다시 작동이 되서 어댑터에 리사이클류 그게 뜨지 않고 오류가 발생
///// 콜랩스 함수가 시작되면 무조건 입력중이던 함수는 적용안되게 수정함
//            //검색이 종료되었을 때
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
//                calendarView.setVisibility(View.VISIBLE);
//                registerForContextMenu(scheduleRV);
//                CalendarDay date = calendarView.getSelectedDate();
//                Log.d("minseok", "collapse_" + date);
//                scheduleArrayList.clear();
//                if (date != null) {
//                    Long dateMills = Time.CalendarDayToMill(date);
//                    scheduleArrayList.addAll(Arrays.asList(DB.scheduleDao().loadAllScheduleDuring(dateMills, dateMills + Time.ONE_DAY)));
//                    Collections.sort(scheduleArrayList);
//                }
//                RVAdapter.notifyDataSetChanged();
//                return true;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    private void search(String keyword) {//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!|
//        registerForContextMenu(scheduleRV);
//        keyword = "%" + keyword + "%";
//        scheduleArrayList.clear();
//        scheduleArrayList.addAll(DB.scheduleDao().searchRecords(keyword));
//        Log.d("minseok", "search_" + scheduleArrayList.size());
//        RVAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    protected void onResume() {
//        Log.d("minseok", "resume_");
//        super.onResume();
//        CalendarDay date = calendarView.getSelectedDate();
//        onMonthChangedListener.onMonthChanged(calendarView, calendarView.getCurrentDate());
//        if (date == null) return;
//        Long dateMills = Time.CalendarDayToMill(date);
//        scheduleArrayList.clear();
//        scheduleArrayList.addAll(Arrays.asList(DB.scheduleDao().loadAllScheduleDuring(dateMills, dateMills + Time.ONE_DAY)));
//        Collections.sort(scheduleArrayList);
//        RVAdapter.notifyDataSetChanged();
//    }



}