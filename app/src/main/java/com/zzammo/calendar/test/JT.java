package com.zzammo.calendar.test;

import android.content.Context;
import android.widget.Toast;

import com.zzammo.calendar.util.AfterTask;

public class JT implements AfterTask {

    Context context;
    String s;

    public JT(Context context, String s) {
        this.context = context;
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
