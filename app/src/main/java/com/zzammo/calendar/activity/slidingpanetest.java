package com.zzammo.calendar.activity;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.zzammo.calendar.R;

import java.lang.reflect.Array;
import java.util.List;

public class slidingpanetest extends AppCompatActivity {
    View view1;
    View view2;
    float y1,y2,total_h,init_view1_h,init_view2_h;
    Float[] max_h = new Float[3];
    Float[] min_h = new Float[3];
    int mode = 0, changemode;
    ViewGroup.LayoutParams params1,params2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slidingpanel);

        view1 = findViewById(R.id.firstview);
        view2 = findViewById(R.id.secondview);

        params1 = view1.getLayoutParams();
        params2 = view2.getLayoutParams();

        view1.post(new Runnable() {
            @Override
            public void run() {
                total_h = view1.getHeight();
                max_h[0]=total_h; max_h[1]=(total_h); max_h[2]=(total_h*(float)0.5);
                min_h[0]=(total_h*(float)0.5); min_h[1]=(total_h*(float)0.25); min_h[2]=(total_h*(float)0.25);
                Log.d("minseok",view1.getHeight() + " run " + view2.getHeight());
            }
        });

        view1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                moveview(event);
                return true;
            }
        });

        view2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                moveview(event);
                return true;
            }
        });
    }

    void moveview(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("minseok","mode"+mode+" "+max_h[mode]+" "+min_h[mode]);
                y1 = event.getY();
                init_view1_h=view1.getHeight();
                init_view2_h=view2.getHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                y2 = event.getY();
                float delta = y2-y1;
                if (y1 < y2) {
                    //아래로 슬라이딩
                    Log.d("minseok",mode+"move down "+params1.height+" "+params2.height);
                    params1.height = (int)(init_view1_h+delta);
                    float tmp = max_h[mode]+(float)0.5;
                    if(params1.height > tmp)params1.height = (int)tmp;
                    params2.height = (int)(total_h - params1.height);
                    view1.setLayoutParams(params1);
                    view2.setLayoutParams(params2);
                    changemode = -1;
                }
                else if(y1 > y2){
                    //위로 슬라이딩
                    Log.d("minseok",mode+"move up"+params1.height+" "+params2.height);
                    float tmp = min_h[mode]+(float)0.5;
                    params1.height = (int)(init_view1_h+delta);
                    if(params1.height < tmp)params1.height = (int)tmp;
                    params2.height = (int)(total_h-params1.height);
                    view1.setLayoutParams(params1);
                    view2.setLayoutParams(params2);
                    changemode = 1;
                }
                break;
            case MotionEvent.ACTION_UP:
                float tmp;
                if(changemode==1){tmp = min_h[mode];}
                else{tmp = max_h[mode];}

                ValueAnimator animator = ValueAnimator.ofFloat(params1.height, tmp);
                animator.setDuration(500); // set the duration of the animation to 1 second
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        params1.height = (int) (value + 0.5f);
                        params2.height = (int)(total_h-params1.height);
                        view1.setLayoutParams(params1);
                        view2.setLayoutParams(params2);
                    }
                });
                animator.start();

                mode+=changemode;
                if(mode>2)mode=2;
                else if(mode<0)mode=0;
                break;
        }
    }
}