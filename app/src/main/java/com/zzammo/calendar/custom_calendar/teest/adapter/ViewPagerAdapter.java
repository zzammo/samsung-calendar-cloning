package com.zzammo.calendar.custom_calendar.teest.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zzammo.calendar.custom_calendar.teest.fragment.PageFragment;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {

    ArrayList<Integer> data;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Integer> data) {
        super(fragmentActivity);
        this.data = data;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new PageFragment(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
