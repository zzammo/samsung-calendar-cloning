package com.zzammo.calendar.custom_calendar.teest.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzammo.calendar.R;
import com.zzammo.calendar.custom_calendar.teest.adapter.PageRVAdapter;
import com.zzammo.calendar.custom_calendar.teest.data.PageData;
import com.zzammo.calendar.custom_calendar.teest.view.CustomCalendar;
import com.zzammo.calendar.util.Time;

import java.util.Calendar;

public class PageFragment extends Fragment {

    TextView title_tv;

    RecyclerView recyclerView;
    PageRVAdapter adapter;
    GridLayoutManager layoutManager;

    PageData pageData;
    CustomCalendar.OnDateClick listener;
    int sundayColor, saturdayColor, holidayColor, todayColor, basicColor;
    boolean setBackgroundToday;
    Long selectedDate;

    public PageFragment() {
        // Required empty public constructor
    }

    public PageFragment(PageData pageData, CustomCalendar.OnDateClick listener) {
        this.pageData = pageData;
        this.listener = listener;
    }

    public PageFragment(PageData pageData, CustomCalendar.OnDateClick listener,
                        int sundayColor, int saturdayColor, int holidayColor, int todayColor, int basicColor,
                        boolean setBackgroundToday,
                        Long selectedDate) {
        this.pageData = pageData;
        this.listener = listener;
        this.sundayColor = sundayColor;
        this.saturdayColor = saturdayColor;
        this.holidayColor = holidayColor;
        this.todayColor = todayColor;
        this.basicColor = basicColor;
        this.setBackgroundToday = setBackgroundToday;
        this.selectedDate = selectedDate;
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
        adapter = new PageRVAdapter(getActivity(), getContext(), pageData.getDays(), listener,
                sundayColor, saturdayColor, holidayColor, todayColor, basicColor, setBackgroundToday,
                selectedDate);
        layoutManager = new GridLayoutManager(getContext(), 7);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7){
            @Override
            public boolean canScrollVertically() {return false; }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(pageData.getMonth());
        title_tv.setText(Time.CalendarToYM(cal));

        return view;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public PageData getPageData() {
        return pageData;
    }
}