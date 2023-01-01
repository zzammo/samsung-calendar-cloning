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

import com.google.firebase.firestore.DocumentReference;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.zzammo.calendar.R;
import com.zzammo.calendar.adapter.ScheduleRVAdapter;
import com.zzammo.calendar.auth.Auth;
import com.zzammo.calendar.database.Database;
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
    RecyclerView search_recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialCalendarView materialCalendarView;
        materialCalendarView=findViewById((R.id.calendarView));
        materialCalendarView.addDecorators(
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

        calendarView = findViewById(R.id.calendarView);

        scheduleRV = findViewById(R.id.schedule_recyclerView);
        scheduleArrayList = new ArrayList<>();
        RVAdapter = new ScheduleRVAdapter(scheduleArrayList);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        scheduleRV.setLayoutManager(layoutManager);
        scheduleRV.setAdapter(RVAdapter);
        RVAdapter.setOnItemClickListener((v, position) -> {
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
        });

        new Thread(){
            public void run(){
                try {
                    ApiExplorer apiExplorer = new ApiExplorer(context);
                    apiExplorer.getHolidays(2023, 1);
                    //2023년 1월의 국경일, 공휴일 정보 불러옴. Month로 0이하의 값을 주면 2023년 전체를 불러옴.
                } catch (IOException | XmlPullParserException e) {
                    Log.e("WeGlonD", "ApiExplorer failed - " + e);
                    e.printStackTrace();
                }
            }
        }.start();

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
            new Auth().signUp(1, email, password, new AfterTask() {
                @Override
                public void ifSuccess(Object result) {
                    Toast.makeText(context, "가입 성공", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void ifFail(Object result) {
                    Toast.makeText(context, "가입 실패", Toast.LENGTH_SHORT).show();
                }
            });
        });
        signIn_btn.setOnClickListener(view -> {
            String email = email_et.getText().toString();
            String password = password_et.getText().toString();
            new Auth().logIn(Auth.EMAIL, email, password, new AfterTask() {
                @Override
                public void ifSuccess(Object result) {
                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void ifFail(Object result) {
                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show();
                }
            });
        });
        signOut_btn.setOnClickListener(view -> {
            if(new Auth().logOn())
                new Auth().logOut();
        });
        delete_btn.setOnClickListener(view -> {
            if(new Auth().logOn())
                new Auth().delete(Auth.EMAIL, new AfterTask() {
                    @Override
                    public void ifSuccess(Object result) {
                        Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void ifFail(Object result) {
                        Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                });
        });
        local_insert_btn.setOnClickListener(view -> {
            new Database(context).insert(Database.LOCAL, test_schedule,
                    new AfterTask() {
                        @Override
                        public void ifSuccess(Object result) {

                        }

                        @Override
                        public void ifFail(Object result) {

                        }
                    });
        });
        server_insert_btn.setOnClickListener(view -> {
            new Database(context).insert(Database.SERVER, test_schedule,
                    new AfterTask() {
                        @Override
                        public void ifSuccess(Object result) {
                            Toast.makeText(context, "쓰기 성공 : "+test_schedule.id, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void ifFail(Object result) {
                            Toast.makeText(context, "쓰기 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        server_delete_btn.setOnClickListener(view -> {
            new Database(context).delete(Database.SERVER, test_schedule
                    , new AfterTask() {
                        @Override
                        public void ifSuccess(Object result) {
                            Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void ifFail(Object result) {
                            Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        //테스트 용
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
                searchView.setQueryHint("검색");
                return true;
            }
///검색을 실행하던 도중 뒤로가기 하면 콜랍스 함수가 실행되지만 익스팬더블이 뜨려고 했지만 검색에 검색 중이던 함수가 다시 작동이 되서 어댑터에 리사이클류 그게 뜨지 않고 오류가 발생
/// 콜랩스 함수가 시작되면 무조건 입력중이던 함수는 적용안되게 수정함

            //검색이 종료되었을 때
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                /*q = 1; // 검색하다가 종료했을 때는
                if(tabtype == 0){
                    recyclerview = findViewById(R.id.recyclerview);
                    registerForContextMenu(recyclerview);
                    DBHelper databaseHelper = new DBHelper(getApplicationContext());
                    ArrayList<ExpandableListAdapter.Item> mritems = databaseHelper.getItem();
                    if (mritems != null) {
                        recyclerview.setAdapter(new ExpandableListAdapter(mritems,ExpandableListAdapter.mContext));
                        recyclerview.setHasFixedSize(true);
                    }
                }*/
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void search(String keyword){//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!|
        search_recyclerview = findViewById(R.id.schedule_recyclerView);
        registerForContextMenu(search_recyclerview);
        //if(keyword.equals(""))Log.d("minseok",keyword);
        keyword = "%"+keyword+"%";
        List<Schedule> list = DB.scheduleDao().searchRecords(keyword);

        ArrayList<Schedule> search_scheduleList = new ArrayList<>(list);
        if (search_scheduleList != null) {
            search_recyclerview.setAdapter(new ScheduleRVAdapter(search_scheduleList));
        }
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
}