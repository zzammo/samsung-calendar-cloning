package com.zzammo.calendar.custom_calendar.teest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.Button;

import com.zzammo.calendar.R;
import com.zzammo.calendar.custom_calendar.teest.adapter.ViewPagerAdapter;
import com.zzammo.calendar.custom_calendar.teest.data.CalendarDate;
import com.zzammo.calendar.custom_calendar.teest.data.PageData;
import com.zzammo.calendar.custom_calendar.teest.view.CustomCalendar;
import com.zzammo.calendar.custom_calendar.utils.Keys;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ViewPagerActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    ViewPagerAdapter pagerAdapter;
    ArrayList<PageData> pageData;

    Button add_btn;
    Button delete_btn;

    Database DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        DB = new Database(this);

        viewPager = findViewById(R.id.activity_view_pager_viewPager);
        add_btn = findViewById(R.id.activity_view_pager_add_btn);
        delete_btn = findViewById(R.id.activity_view_pager_delete_btn);

        pageData = new ArrayList<>();
        init();
        pagerAdapter = new ViewPagerAdapter(this, pageData);
        viewPager.setAdapter(pagerAdapter);
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

        viewPager.setCurrentItem(300);

        add_btn.setOnClickListener(v -> {
//            pagerAdapter.notifyItemInserted(dataList.size()-1);
        });
        delete_btn.setOnClickListener(v -> {
            pageData.remove(pageData.size()-1);
            pagerAdapter.notifyItemRemoved(pageData.size());
        });
    }

    void init(){
        Calendar cal = Calendar.getInstance();

        for (int i = -300; i < 300; i++) {
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

                pageData.add(page);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}