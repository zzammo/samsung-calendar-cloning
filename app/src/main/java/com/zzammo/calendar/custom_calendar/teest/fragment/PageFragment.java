package com.zzammo.calendar.custom_calendar.teest.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibm.icu.util.Calendar;
import com.zzammo.calendar.R;
import com.zzammo.calendar.custom_calendar.teest.adapter.PageRVAdapter;
import com.zzammo.calendar.custom_calendar.teest.data.CalendarDate;
import com.zzammo.calendar.custom_calendar.teest.data.PageData;
import com.zzammo.calendar.custom_calendar.teest.view.CustomCalendar;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;

public class PageFragment extends Fragment {

    TextView title_tv;

    RecyclerView recyclerView;
    PageRVAdapter adapter;
    GridLayoutManager layoutManager;

    PageData pageData;
    CustomCalendar.OnDateClickListener listener;
    int sundayColor, saturdayColor, holidayColor, todayColor, basicColor;

    public PageFragment() {
        // Required empty public constructor
    }

    public PageFragment(PageData pageData, CustomCalendar.OnDateClickListener listener) {
        this.pageData = pageData;
        this.listener = listener;
    }

    public PageFragment(PageData pageData, CustomCalendar.OnDateClickListener listener,
                        int sundayColor, int saturdayColor, int holidayColor, int todayColor, int basicColor) {
        this.pageData = pageData;
        this.listener = listener;
        this.sundayColor = sundayColor;
        this.saturdayColor = saturdayColor;
        this.holidayColor = holidayColor;
        this.todayColor = todayColor;
        this.basicColor = basicColor;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        title_tv = view.findViewById(R.id.fragment_page_title_textView);

        recyclerView = view.findViewById(R.id.fragment_page_recyclerView);
        adapter = new PageRVAdapter(getContext(), pageData.getDays(), listener,
                sundayColor, saturdayColor, holidayColor, todayColor, basicColor);
        layoutManager = new GridLayoutManager(getContext(), 7);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        title_tv.setText(Time.CalendarToYM(pageData.getMonth()));

        return view;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}