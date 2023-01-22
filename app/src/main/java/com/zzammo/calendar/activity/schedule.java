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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.zzammo.calendar.R;

public class schedule extends AppCompatActivity implements OnMapReadyCallback {

    EditText memo;
    Switch allday_switch;
    Switch alarm_switch;
    TextView start_time;
    TextView end_time;
    LinearLayout path_panel;
    LinearLayout ago_timepicker;
    LinearLayout ago_panel;

    private boolean ago_flag=true;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);
        memo=findViewById(R.id.memo);
        allday_switch=findViewById(R.id.allday_switch);
        start_time=findViewById(R.id.start_time);
        end_time=findViewById(R.id.end_time);
        alarm_switch=findViewById(R.id.alarm_switch);
        path_panel=findViewById(R.id.path_panel);
        ago_panel=findViewById(R.id.ago_panel);
        ago_timepicker=findViewById(R.id.ago_timepicker);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        allday_switch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    start_time.setVisibility(View.GONE);
                    end_time.setVisibility(View.GONE);
                }
                else{
                    start_time.setVisibility(View.VISIBLE);
                    end_time.setVisibility(View.VISIBLE);
                }
            }
        });

        alarm_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    path_panel.setVisibility(View.VISIBLE);
                }
                else{
                    path_panel.setVisibility(View.GONE);
                }
            }
        });

        ago_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ago_flag){
                    ago_timepicker.setVisibility(View.VISIBLE);
                }
                else{
                    ago_timepicker.setVisibility(View.GONE);
                }
                ago_flag=!ago_flag;
            }
        });

    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();         // 마커 생성
        markerOptions.position(SEOUL);
        markerOptions.title("서울");                         // 마커 제목
        markerOptions.snippet("한국의 수도");         // 마커 설명
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));                 // 초기 위치
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));                         // 줌의 정도
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);                           // 지도 유형 설정

    }
}