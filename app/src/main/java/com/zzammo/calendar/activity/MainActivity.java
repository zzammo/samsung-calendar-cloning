package com.zzammo.calendar.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.zzammo.calendar.R;
import com.zzammo.calendar.adapter.ScheduleRVAdapter;
import com.zzammo.calendar.auth.Auth;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.dialog.ScheduleDialog;
import com.zzammo.calendar.database.Holiday;
import com.zzammo.calendar.database.Metadata;
import com.zzammo.calendar.holiday.ApiExplorer;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.database.room.ScheduleDatabase;
import com.zzammo.calendar.schedule_event.MakeSchedule;
import com.zzammo.calendar.util.AfterTask;
import com.zzammo.calendar.util.Time;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ScheduleDatabase DB;

    MaterialCalendarView calendarView;
    RecyclerView scheduleRV;
    ScheduleRVAdapter RVAdapter;
    LinearLayoutManager layoutManager;
    ArrayList<Schedule> scheduleArrayList;
    Intent it;
    Context context;
    public static ArrayList<CalendarDay> HolidayDates;
    public static ArrayList<String> HolidayNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        calendarView = findViewById(R.id.calendarView);
        calendarView.addDecorators(
                new SaturdayDecorator(),
                new SundayDecorator(),
                new TodayDecorator(),
                new MySelectorDecorator(this)
        );
        Button button=(Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),MainAddressActivity.class);
                startActivity(intent);
            }

        });

        this.context = this;

        DB = ScheduleDatabase.getInstance(context);
        scheduleRV = findViewById(R.id.schedule_recyclerView);
        scheduleArrayList = new ArrayList<>();
        RVAdapter = new ScheduleRVAdapter(scheduleArrayList);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        scheduleRV.setLayoutManager(layoutManager);
        scheduleRV.setAdapter(RVAdapter);
        RVAdapter.setOnItemClickListener(position -> {
            DB.scheduleDao().delete(scheduleArrayList.get(position));
            scheduleArrayList.remove(position);
            RVAdapter.notifyItemRemoved(position);
        });

        calendarView.setOnDateLongClickListener(new OnDateLongClickListener() {
            @Override
            public void onDateLongClick(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date) {
                /*ScheduleDialog oDialog = new ScheduleDialog(MainActivity.this,
                        Time.CalendarDayToMill(date));
                oDialog.show();*/
                it = new Intent(getApplicationContext(), MakeSchedule.class);
                it.putExtra("date",Time.CalendarDayToMill(date));
                it.putExtra("month",date.getMonth());
                it.putExtra("day",date.getDay());
                startActivity(it);
            }
        });//버전 올려서 살렸음

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if(!selected) return;
            Long dateMills = Time.CalendarDayToMill(date);
            //Toast.makeText(this, dateMills+" "+(dateMills+Time.ONE_DAY), Toast.LENGTH_SHORT).show();

            scheduleArrayList.clear();
            scheduleArrayList.addAll(Arrays.asList(DB.scheduleDao().loadAllScheduleDuring(dateMills, dateMills + Time.ONE_DAY)));
            Collections.sort(scheduleArrayList);
            RVAdapter.notifyDataSetChanged();

//            if(scheduleArrayList.size() == 0) return;

            ScheduleDialog oDialog = new ScheduleDialog(MainActivity.this,
                        Time.CalendarDayToMill(date));
                oDialog.show();

        });

        HolidayDates = new ArrayList<>(); HolidayNames = new ArrayList<>();
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                HolidayDates.clear(); HolidayNames.clear();
                Log.d("WeGlonD", "onMonthChanged - date : " + date); //calendarday 에서의 month는 1월이 0 ~ 12월이 11 인듯
                int year = date.getYear(); int month = date.getMonth();
                Database database = new Database(context);
                Metadata low_bound_holiday = database.getMetadata("Holi_Min_Year");
                Metadata high_bound_holiday = database.getMetadata("Holi_Max_Year");
                int minY, maxY;
                if(low_bound_holiday == null && high_bound_holiday == null){
                    minY = maxY = year;
                    database.insert(new Metadata("Holi_Min_Year", Integer.toString(minY)));
                    database.insert(new Metadata("Holi_Max_Year", Integer.toString(maxY)));
                    //공휴일 API에서 가져오기
                    GetHoliday(year, new AfterTask(){
                        @Override
                        public void ifSuccess(Object result) {
                            //공휴일 DB에 쓰기
                            for(int i = 0; i < HolidayNames.size(); i++){
                                CalendarDay day = HolidayDates.get(i);
                                String datestr = Integer.toString(day.getYear());
                                if(day.getMonth() < 10) datestr = datestr + "0";
                                datestr = datestr + day.getMonth();
                                if(day.getDay() < 10) datestr = datestr + "0";
                                datestr = datestr + day.getDay();
                                database.insert(new Holiday(datestr, HolidayNames.get(i)));
                            }
                        }
                        @Override
                        public void ifFail(Object result) {
                        }
                    });

                } else {
                    minY = Integer.parseInt(low_bound_holiday.data); maxY = Integer.parseInt(high_bound_holiday.data);
                    if(year < minY || year > maxY){
                        //공휴일 API에서 가져오기
                        GetHoliday(year, new AfterTask(){
                            @Override
                            public void ifSuccess(Object result) {
                                //공휴일 DB에 쓰기
                                for(int i = 0; i < HolidayNames.size(); i++){
                                    CalendarDay day = HolidayDates.get(i);
                                    String datestr = Integer.toString(day.getYear());
                                    if(day.getMonth() < 10) datestr = datestr + "0";
                                    datestr = datestr + day.getMonth();
                                    if(day.getDay() < 10) datestr = datestr + "0";
                                    datestr = datestr + day.getDay();
                                    database.insert(new Holiday(datestr, HolidayNames.get(i)));
                                }
                            }
                            @Override
                            public void ifFail(Object result) {
                            }
                        });

                        if(year < minY) {
                            low_bound_holiday.data = Integer.toString(year);
                            database.update(low_bound_holiday);
                        }
                        else{
                            high_bound_holiday.data = Integer.toString(year);
                            database.update(high_bound_holiday);
                        }
                    }
                    else{
                        //공휴일 DB에서 가져오기
                        String keyword = "%"+year;
                        if(month < 10) keyword = keyword + "0";
                        keyword = keyword + month + "%";
                        List<Holiday> Holidays = database.HoliLocalDB.holidayDao().searchHolidayByDate(keyword);
                        for(Holiday holi : Holidays){
                            int rawdata = Integer.parseInt(holi.date);
                            CalendarDay calendarDay = CalendarDay.from(rawdata/10000,(rawdata%10000)/100,rawdata%100);
                            HolidayDates.add(calendarDay);
                            HolidayNames.add(holi.name);
                            Log.d("WeGlonD", "Holiday DB Read - " + calendarDay + " " + holi.name);
                        }
                    }
                }

            }
        });

        //테스트 용
        EditText email_et = findViewById(R.id.activity_main_emailEt);
        EditText password_et = findViewById(R.id.activity_main_passwordEt);
        Button signUp_btn = findViewById(R.id.activity_main_signUpBtn);
        Button signIn_btn = findViewById(R.id.activity_main_signInBtn);
        Button signOut_btn = findViewById(R.id.activity_main_signOutBtn);
        Button delete_btn = findViewById(R.id.activity_main_deleteBtn);
        Button local_insert_btn = findViewById(R.id.activity_main_localDB_insertBtn);
        Button server_insert_btn = findViewById(R.id.activity_main_serverDB_insertBtn);
        Button server_delete_btn = findViewById(R.id.activity_main_serverDB_deleteBtn);
        Schedule test_schedule = new Schedule("test_title", "test_loc",
                System.currentTimeMillis(),
                System.currentTimeMillis());
        signUp_btn.setOnClickListener(view -> {
            String email = email_et.getText().toString();
            String password = password_et.getText().toString();
            new Auth().signUp(Auth.EMAIL, email, password, new jt("가입"));
        });
        signIn_btn.setOnClickListener(view -> {
            String email = email_et.getText().toString();
            String password = password_et.getText().toString();
            new Auth().logIn(Auth.EMAIL, email, password, new jt("로그인"));
        });
        signOut_btn.setOnClickListener(view -> {
            if(new Auth().logOn())
                new Auth().logOut();
        });
        delete_btn.setOnClickListener(view -> {
            if(new Auth().logOn())
                new Auth().delete(Auth.EMAIL, new jt("삭제"));
        });
        local_insert_btn.setOnClickListener(view -> {
            new Database(context).insert(Database.LOCAL, test_schedule,
                    new q());
        });
        server_insert_btn.setOnClickListener(view -> {
            new Database(context).insert(Database.SERVER, test_schedule,
                    new jt("쓰기"));
        });
        server_delete_btn.setOnClickListener(view -> {
            new Database(context).delete(Database.SERVER, test_schedule
                    , new jt("삭제"));
        });
        //테스트 용
    }

    void GetHoliday(int year, AfterTask afterTask){
        new Thread(){
            public void run(){
                try {
                    ApiExplorer apiExplorer = new ApiExplorer(context);
                    apiExplorer.getHolidays(year, -1, HolidayNames, HolidayDates);
                    for(int i = 0; i < HolidayNames.size(); i++){
                        Log.d("WeGlonD", HolidayDates.get(i).toString() + ' ' + HolidayNames.get(i));
                    }
                    afterTask.ifSuccess(0);
                    //2023년 1월의 국경일, 공휴일 정보 불러옴. Month로 0이하의 값을 주면 2023년 전체를 불러옴.
                } catch (IOException | XmlPullParserException e) {
                    Log.e("WeGlonD", "ApiExplorer failed - " + e);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("캘린더 앱을 종료하시겠습니까?");
        builder.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
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
                search(query);
                searchView.setQueryHint("검색");
                return false;
            }

            ///칠 때마다 텍스트를 하나하나 입력받을 때 (검색 도중)
            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                searchView.setQueryHint("검색");
                return false;
            }
        });
        //돋보기 눌럿을 때 실행되는 함수
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                calendarView.setVisibility(View.GONE);
                searchView.setQueryHint("검색");
                return true;
            }
///검색을 실행하던 도중 뒤로가기 하면 콜랍스 함수가 실행되지만 익스팬더블이 뜨려고 했지만 검색에 검색 중이던 함수가 다시 작동이 되서 어댑터에 리사이클류 그게 뜨지 않고 오류가 발생
/// 콜랩스 함수가 시작되면 무조건 입력중이던 함수는 적용안되게 수정함
            //검색이 종료되었을 때
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                calendarView.setVisibility(View.VISIBLE);
                registerForContextMenu(scheduleRV);
                CalendarDay date = calendarView.getSelectedDate();
                scheduleArrayList.clear();
                if(date != null){
                    Long dateMills = Time.CalendarDayToMill(date);
                    scheduleArrayList.addAll(Arrays.asList(DB.scheduleDao().loadAllScheduleDuring(dateMills, dateMills + Time.ONE_DAY)));
                    Collections.sort(scheduleArrayList);
                }
                else{
                    scheduleArrayList = new ArrayList<>(DB.scheduleDao().getAll());
                }
                RVAdapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void search(String keyword){//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!|
        registerForContextMenu(scheduleRV);
        keyword = "%"+keyword+"%";
        scheduleArrayList.clear();
        scheduleArrayList.addAll(DB.scheduleDao().searchRecords(keyword));
        RVAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onResume() {
        super.onResume();
        CalendarDay date = calendarView.getSelectedDate();
        if(date == null) return;
        Long dateMills = Time.CalendarDayToMill(date);
        scheduleArrayList.clear();
        scheduleArrayList.addAll(Arrays.asList(DB.scheduleDao().loadAllScheduleDuring(dateMills, dateMills + Time.ONE_DAY)));
        Collections.sort(scheduleArrayList);
        RVAdapter.notifyDataSetChanged();
    }

    class q implements AfterTask{
        @Override
        public void ifSuccess(Object result) {

        }

        @Override
        public void ifFail(Object result) {

        }
    }
    class jt implements AfterTask{

        String s;

        public jt(String s) {
            this.s = s;
        }

        @Override
        public void ifSuccess(Object result) {
            Toast.makeText(context, s+" 성공", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void ifFail(Object result) {
            Toast.makeText(context, s+" 실패", Toast.LENGTH_SHORT).show();
        }
    }
}