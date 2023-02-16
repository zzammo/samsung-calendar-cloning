package com.zzammo.calendar.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
public class CustomNestedScrollView extends NestedScrollView {

    private boolean scrollable = true;
    private float previousY;
    private int mode;

    public CustomNestedScrollView(Context context) {
        super(context);
    }

    public CustomNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollingEnabled(boolean enabled) {
        scrollable = enabled;
    }

    public void setMode(int M){
        mode = M;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!scrollable) {
            Log.d("minseok","CustomNested scrollable false");
            return false;
        }
        Log.d("minseok","CustomNested scrollable true");
        if(!canScrollVertically(-1)){
            Log.d("minseok","TOP NESTED");
        }

        return super.onInterceptTouchEvent(ev);
    }



    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mode==0||mode==1){
            return false;
        }
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (y > previousY) {
                    // User is scrolling up, perform your action here
                    Log.d("CustomScrollView", "User is scrolling up");
                    return true; // Consume the touch event, preventing the ScrollView from scrolling
                }
                break;
            default:
                previousY = y;
                break;
        }
        return super.onTouchEvent(event);
    }*/
}