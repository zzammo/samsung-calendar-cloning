package com.zzammo.calendar.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.zzammo.calendar.R;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.Time;

import java.util.Calendar;

public class DBTestActivity extends AppCompatActivity {

    Context context;

    EditText title_et;
    EditText location_et;

    Button local_insert_btn;
    Button server_insert_btn;
    Button server_delete_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest);

        context = this;

        title_et = findViewById(R.id.activity_DB_test_title_editText);
        location_et = findViewById(R.id.activity_DB_test_location_editText);

        local_insert_btn = findViewById(R.id.activity_DB_test_localDB_insertBtn);
        server_insert_btn = findViewById(R.id.activity_DB_test_serverDB_insertBtn);
        server_delete_btn = findViewById(R.id.activity_DB_test_serverDB_deleteBtn);

        local_insert_btn.setOnClickListener(view -> {
            Schedule test_schedule = makeTestSchedule();

            new Database(context).insert(Database.LOCAL, test_schedule,
                    new Q());
        });
        server_insert_btn.setOnClickListener(view -> {
            Schedule test_schedule = makeTestSchedule();

            new Database(context).insert(Database.SERVER, test_schedule,
                    new JT(context, "쓰기"));
        });
        server_delete_btn.setOnClickListener(view -> {
            Schedule test_schedule = makeTestSchedule();

            new Database(context).delete(Database.SERVER, test_schedule
                    , new JT(context, "삭제"));
        });
    }

    Schedule makeTestSchedule(){
        String title = title_et.getText().toString();
        String location = location_et.getText().toString();
        Long begin = System.currentTimeMillis();
        return new Schedule(title, location, begin, begin+Time.ONE_HOUR);
    }
}