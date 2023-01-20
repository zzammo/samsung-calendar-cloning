package com.zzammo.calendar.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.zzammo.calendar.R;

public class schedule extends AppCompatActivity {

    EditText memo;
    Switch allday_switch;
    TextView start_time;
    TextView end_time;
    private int shortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);
        memo=findViewById(R.id.memo);
        allday_switch=findViewById(R.id.allday_switch);
        start_time=findViewById(R.id.start_time);
        end_time=findViewById(R.id.end_time);


        allday_switch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    start_time.setVisibility(View.GONE);
                    end_time.setVisibility(View.GONE);
                    shortAnimationDuration = getResources().getInteger(
                            android.R.integer.config_shortAnimTime);

                }
                else{
                    Animation animation_start = new AlphaAnimation(0, 1);
                    animation_start.setDuration(1000);
                    start_time.setVisibility(View.VISIBLE);
                    start_time.setAnimation(animation_start);
                    Animation animation_end = new AlphaAnimation(0, 1);
                    animation_end.setDuration(1000);
                    end_time.setVisibility(View.VISIBLE);
                    end_time.setAnimation(animation_end);
                }
            }
        });


    }
}