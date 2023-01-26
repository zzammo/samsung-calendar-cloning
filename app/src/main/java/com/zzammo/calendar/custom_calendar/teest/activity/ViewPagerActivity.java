package com.zzammo.calendar.custom_calendar.teest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.Button;

import com.zzammo.calendar.R;
import com.zzammo.calendar.custom_calendar.teest.adapter.ViewPagerAdapter;

import java.util.ArrayList;

public class ViewPagerActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    ViewPagerAdapter pagerAdapter;
    ArrayList<Integer> dataList;

    Button add_btn;
    Button delete_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        viewPager = findViewById(R.id.activity_view_pager_viewPager);
        add_btn = findViewById(R.id.activity_view_pager_add_btn);
        delete_btn = findViewById(R.id.activity_view_pager_delete_btn);

        dataList = new ArrayList<>();
        dataList.add(0);
        pagerAdapter = new ViewPagerAdapter(this, dataList);
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

        add_btn.setOnClickListener(v -> {
            dataList.add(dataList.size());
            pagerAdapter.notifyItemInserted(dataList.size()-1);
        });
        delete_btn.setOnClickListener(v -> {
            dataList.remove(dataList.size()-1);
            pagerAdapter.notifyItemRemoved(dataList.size());
        });
    }
}