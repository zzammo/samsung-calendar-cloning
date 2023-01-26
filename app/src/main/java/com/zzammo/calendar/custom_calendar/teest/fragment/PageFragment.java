package com.zzammo.calendar.custom_calendar.teest.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zzammo.calendar.R;

public class PageFragment extends Fragment {

    TextView idx_tv;

    Integer idx;

    public PageFragment() {
        // Required empty public constructor
    }

    public PageFragment(int idx) {
        this.idx = idx;
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

        idx_tv = view.findViewById(R.id.fragment_page_idx_tv);
        idx_tv.setText(String.valueOf(idx));
        return view;
    }
}