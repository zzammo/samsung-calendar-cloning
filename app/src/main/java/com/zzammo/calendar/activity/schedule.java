package com.zzammo.calendar.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.zzammo.calendar.R;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.Time;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class schedule extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    EditText memo;
    EditText title;
    Switch allday_switch;
    Switch alarm_switch;
    TextView start_time_textview;
    TextView start_date_textview;
    TextView end_time_textview;
    TextView end_date_textview;
    LinearLayout path_panel;

    LinearLayout time_start_layout;
    LinearLayout date_start_layout;
    LinearLayout time_end_layout;
    LinearLayout date_end_layout;
    LinearLayout alarm_time_layout;
    LinearLayout alarm_time_checkbox_layout;
    LinearLayout iterator_layout;

    TextView src_address;
    TextView dst_address;
    RadioGroup iterator_radiogroup;
    TextView iterator_textview;
    TextView alarm_time_textview;
    TextView time_requiered_textview;
    TextView time_requiered_click;

    TextView pre_src_time_textview;


    CheckBox checkbox_ontime;
    CheckBox checkbox_10_min_ago;
    CheckBox checkbox_hourago;
    CheckBox checkbox_dayago;

    RadioGroup means_radiogroup;

    DatePicker date_start_datepicker;
    DatePicker date_end_datepicker;

    TimePicker time_start_timepicker;
    TimePicker time_end_timepicker;

    LinearLayout custom_alram_btn;
    LinearLayout custom_alram_layout;
    CheckBox checkbox_custom;
    LinearLayout numpicker_layout;
    NumberPicker numpicker;
    NumberPicker charpicker;

    Button save_btn;
    Button cancel_btn;

    private boolean isToday=false;

    private int start_hour=8;
    private int start_minute=0;
    private int start_month;
    private int start_day;
    private int start_year;
    private int start_week;

    private int end_hour=9;
    private int end_minute=0;
    private int end_month;
    private int end_day;
    private int end_year;
    private int end_week;

    private int need_hour;
    private int need_minute;
    private int need_second;

    // ?????? : 0 - ?????? ??????, 1 - ?????? ??????
    private int mode;
    private int scheduleKey;
    private String scheduleServerId;

    // ?????? ?????? ?????? : ??? ??? ??? ?????? ???
    private int pre_start_year;
    private int pre_start_hour;
    private int pre_start_minute;
    private int pre_start_month;
    private int pre_start_day;

    private String alarm;

    private boolean[] clicked={false,false,false,false}; // 0 start_date 1 start_time 2 end_date 3 end_time




    private int date_picker_flag = 0;// 0 -> off 1 -> src 2-> dst
    private int time_picker_flag = 0;// 0 -> off 1 -> src 2-> dst
    private boolean alarm_time=true;
    private boolean iterator_time=true;
    private int operator_flag=0;
    private boolean[] ago_checkboxes={false,true,false,false};
    private int means_flag=-1;

    private GoogleMap mMap;
    private Marker srcMarker = null;
    private Marker dstMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1???
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5???


    // onRequestPermissionsResult?????? ????????? ???????????? ActivityCompat.requestPermissions??? ????????? ????????? ????????? ???????????? ?????? ???????????????.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    private boolean customChecked=false;
    private int customVal=5;
    private int customIndex=0; //0 ??? 1 ?????? 2 ??? 3 ???


    // ?????? ???????????? ?????? ????????? ???????????? ???????????????.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // ?????? ?????????


    Location srcLocation=null;
    LatLng srcPosition=null;
    Location dstLocation=null;
    LatLng dstPosition=null;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    private View mLayout;  // Snackbar ???????????? ???????????? View??? ???????????????.
    // (????????? Toast????????? Context??? ??????????????????.)

    private String address_data_src=null;
    private String address_data_dst=null;
    // ????????? (????????? ???????????????)
    String key = "l7xxb76eb9ee907444a8b8098322fa488048";

    // ????????? ???????????? ????????? ??????
    String result1 = "";
    String result2 = "";

    String giourl ="http://apis.openapi.sk.com/tmap/geo/fullAddrGeo?addressFlag=F00&coordType=WGS84GEO&version=1&format=json&fullAddr=";





    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().hide();

        Intent intent=getIntent();
        mode = intent.getIntExtra("mode", 0);
        start_year=intent.getIntExtra("year",2000);
        start_month=intent.getIntExtra("month",1);
        start_day=intent.getIntExtra("day",1);


        title =findViewById(R.id.title);
        memo = findViewById(R.id.memo);
        allday_switch = findViewById(R.id.allday_switch);
        start_time_textview = findViewById(R.id.start_time_textview);
        start_date_textview = findViewById(R.id.start_date_textview);
        end_time_textview = findViewById(R.id.end_time_textview);
        end_date_textview = findViewById(R.id.end_date_textview);
        alarm_switch = findViewById(R.id.alarm_switch);
        path_panel = findViewById(R.id.path_panel);

        time_start_layout=findViewById(R.id.time_start_layout);
        date_start_layout=findViewById(R.id.date_start_layout);

        time_end_layout=findViewById(R.id.time_end_layout);
        date_end_layout=findViewById(R.id.date_end_layout);
        alarm_time_layout=findViewById(R.id.alarm_time_layout);
        alarm_time_checkbox_layout=findViewById(R.id.alarm_time_checkbox_layout);

        iterator_layout=findViewById(R.id.iterator_layout);
        iterator_radiogroup=findViewById(R.id.iterator_radiogroup);
        iterator_textview=findViewById(R.id.iterator_textview);

        checkbox_ontime=findViewById(R.id.checkbox_ontime);
        checkbox_10_min_ago=findViewById(R.id.checkbox_10_min_ago);
        checkbox_hourago=findViewById(R.id.checkbox_hourago);
        checkbox_dayago=findViewById(R.id.checkbox_dayago);
        alarm_time_textview=findViewById(R.id.alarm_time_textview);
        time_requiered_textview=findViewById(R.id.time_requiered_textview);

        means_radiogroup=findViewById(R.id.means_radiogroup);
        time_requiered_click=findViewById(R.id.time_requiered_click);

        date_start_datepicker=findViewById(R.id.date_start_datepicker);
        date_end_datepicker=findViewById(R.id.date_end_datepicker);

        time_start_timepicker=findViewById(R.id.time_start_timepicker);
        time_end_timepicker=findViewById(R.id.time_end_timepicker);

        custom_alram_btn=findViewById(R.id.custom_alram_btn);
        custom_alram_layout=findViewById(R.id.custom_alram_layout);
        checkbox_custom=findViewById(R.id.checkbox_custom);
        numpicker_layout=findViewById(R.id.numpicker_layout);
        numpicker=findViewById(R.id.numpicker);
        charpicker=findViewById(R.id.charpicker);

        save_btn=findViewById(R.id.save_btn);
        cancel_btn=findViewById(R.id.cancel_btn);

        mLayout = findViewById(R.id.layout_schedule);

        pre_src_time_textview=findViewById(R.id.pre_src_time_textview);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) schedule.this);

        end_year=start_year;
        end_day=start_day;
        end_month=start_month;

        LocalDateTime localDatetime=LocalDateTime.now();
        int cur_week=localDatetime.getDayOfWeek().getValue();
        start_week=end_week=cur_week;
        int cur_day=localDatetime.getDayOfMonth();
        int cur_month=localDatetime.getMonthValue();
        int cur_year=localDatetime.getYear();
        int cur_hour=localDatetime.getHour();
        int cur_minute=localDatetime.getMinute();
        Log.d("cur_year",String.valueOf(cur_year));
        Log.d("cur_month",String.valueOf(cur_month));
        Log.d("cur_day",String.valueOf(cur_day));
        Log.d("cur_hour",String.valueOf(cur_hour));
        Log.d("cur_minute",String.valueOf(cur_minute));
        if(cur_day==start_day&&cur_month==start_month&&cur_year==start_year){
            isToday=true;
            if(cur_minute==0){
                start_hour=cur_hour;
                end_hour=cur_hour+1;
                end_hour%=24;
            }else{
                start_hour=cur_hour+1;
                end_hour=cur_hour+2;
                end_hour%=24;
            }
        }
        time_start_timepicker.setIs24HourView(true);
        time_start_timepicker.setHour(start_hour);
        time_start_timepicker.setMinute(start_minute);

        time_end_timepicker.setIs24HourView(true);
        time_end_timepicker.setHour(end_hour);
        time_end_timepicker.setMinute(end_minute);

        start_date_textview.setText(getDateText(start_month,start_day,start_week));
        end_date_textview.setText(getDateText(end_month,end_day,start_week));
        start_time_textview.setText(getTimeText(start_hour,start_minute));
        end_time_textview.setText(getTimeText(end_hour,end_minute));

        /// ?????? ?????? ?????? start
        start_time_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date_picker_flag!=0) {
                    if(date_picker_flag==1) {
                        date_start_layout.setVisibility(View.GONE);
                        setOffClicked(0);
                    }else{
                        date_end_layout.setVisibility(View.GONE);
                        setOffClicked(2);
                    }
                    date_picker_flag=0;
                }
                if (time_picker_flag == 0) {
                    time_start_layout.setVisibility(View.VISIBLE);
                    setOnClicked(1);
                    time_picker_flag = 1;
                } else if (time_picker_flag == 1) {
                    time_start_layout.setVisibility(View.GONE);
                    setOffClicked(1);
                    time_picker_flag = 0;
                } else {
                    time_end_layout.setVisibility(View.GONE);
                    setOffClicked(3);
                    time_start_layout.setVisibility(View.VISIBLE);
                    setOnClicked(1);
                    time_picker_flag = 1;
                }
            }
        });
        // ?????? ?????? ?????? start
        start_date_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(time_picker_flag!=0) {
                    if(time_picker_flag==1) {
                        time_start_layout.setVisibility(View.GONE);
                        setOffClicked(1);
                    }else{
                        time_end_layout.setVisibility(View.GONE);
                        setOffClicked(3);
                    }
                    time_picker_flag=0;
                }
                if (date_picker_flag == 0) {
                    date_start_layout.setVisibility(View.VISIBLE);
                    setOnClicked(0);
                    date_picker_flag = 1;
                } else if (date_picker_flag == 1) {
                    date_start_layout.setVisibility(View.GONE);
                    setOffClicked(0);
                    date_picker_flag = 0;
                } else {
                    date_end_layout.setVisibility(View.GONE);
                    setOffClicked(2);
                    date_start_layout.setVisibility(View.VISIBLE);
                    setOnClicked(0);
                    date_picker_flag = 1;
                }
            }
        });
        // ?????? ?????? ?????? end
        end_time_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date_picker_flag!=0) {
                    if(date_picker_flag==1) {
                        date_start_layout.setVisibility(View.GONE);
                        setOffClicked(0);
                    }else{
                        date_end_layout.setVisibility(View.GONE);
                        setOffClicked(2);
                    }
                    date_picker_flag=0;
                }
                if (time_picker_flag == 0) {
                    time_end_layout.setVisibility(View.VISIBLE);
                    setOnClicked(3);
                    time_picker_flag = 2;
                } else if (time_picker_flag == 2) {
                    time_end_layout.setVisibility(View.GONE);
                    setOffClicked(3);
                    time_picker_flag = 0;
                } else {
                    time_start_layout.setVisibility(View.GONE);
                    setOffClicked(1);
                    time_end_layout.setVisibility(View.VISIBLE);
                    setOnClicked(3);
                    time_picker_flag = 2;
                }
            }
        });
        // ?????? ?????? ?????? end
        end_date_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(time_picker_flag!=0) {
                    if(time_picker_flag==1) {
                        time_start_layout.setVisibility(View.GONE);
                        setOffClicked(1);
                    }else{
                        time_end_layout.setVisibility(View.GONE);
                        setOffClicked(3);
                    }
                    time_picker_flag=0;
                }
                if (date_picker_flag == 0) {
                    date_end_layout.setVisibility(View.VISIBLE);
                    setOnClicked(2);
                    date_picker_flag = 2;
                } else if (date_picker_flag == 2) {
                    date_end_layout.setVisibility(View.GONE);
                    setOffClicked(2);
                    date_picker_flag = 0;
                } else {
                    date_start_layout.setVisibility(View.GONE);
                    setOffClicked(0);
                    date_end_layout.setVisibility(View.VISIBLE);
                    setOnClicked(2);
                    date_picker_flag = 2;
                }
            }
        });

        time_start_timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                start_hour=i;
                start_minute=i1;
                start_time_textview.setText(getTimeText(start_hour,start_minute));
                if(start_year==end_year&&start_month==end_month&&start_day==end_day){
                    if(start_hour>end_hour){
                        end_hour=start_hour;
                        end_minute=start_minute;
                        end_time_textview.setText(getTimeText(end_hour,end_minute));
                        return;
                    }else if(start_hour==end_hour&&start_minute>end_minute) {
                        end_hour=start_hour;
                        end_minute=start_minute;
                        end_time_textview.setText(getTimeText(end_hour,end_minute));
                        return;
                    }
                }
            }
        });
        date_start_datepicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                    start_year=i;
                    start_month=i1+1;
                    start_day=i2;
                    start_week=LocalDate.of(i,i1+1,i2).getDayOfWeek().getValue();
                start_date_textview.setText(getDateText(start_month,start_day,start_week));
                if(start_year>end_year){
                    end_year=start_year;
                    end_month=start_month;
                    end_day=start_day;
                    end_week=LocalDate.of(end_year,end_month,end_day).getDayOfWeek().getValue();
                    end_date_textview.setText(getDateText(end_month,end_day,end_week));
                    return;
                }else if(start_year==end_year){
                    if(start_month>end_month){
                        end_year=start_year;
                        end_month=start_month;
                        end_day=start_day;
                        end_week=LocalDate.of(end_year,end_month,end_day).getDayOfWeek().getValue();
                        end_date_textview.setText(getDateText(end_month,end_day,end_week));
                        return;
                    }else if(start_month==end_month){
                        if(start_day>end_day){
                            end_year=start_year;
                            end_month=start_month;
                            end_day=start_day;
                            end_week=LocalDate.of(end_year,end_month,end_day).getDayOfWeek().getValue();
                            end_date_textview.setText(getDateText(end_month,end_day,end_week));
                            return;
                        }
                    }
                }
            }
        });
        time_end_timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                end_hour=i;
                end_minute=i1;
                end_time_textview.setText(getTimeText(end_hour,end_minute));
                if(start_year==end_year&&start_month==end_month&&start_day==end_day){
                    if(start_hour>end_hour){
                        start_hour=end_hour;
                        start_minute=end_minute;
                        start_time_textview.setText(getTimeText(start_hour,start_minute));
                        return;
                    }else if(start_hour==end_hour&&start_minute>end_minute) {
                        start_hour=end_hour;
                        start_minute=end_minute;
                        start_time_textview.setText(getTimeText(start_hour,start_minute));
                        return;
                    }
                }
            }
        });
        date_end_datepicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                end_year=i;
                end_month=i1+1;
                end_day=i2;
                end_week=LocalDate.of(i,i1+1,i2).getDayOfWeek().getValue();
                end_date_textview.setText(getDateText(end_month,end_day,end_week));
                if(start_year>end_year){
                    start_year=end_year;
                    start_month=end_month;
                    start_day=end_day;
                    start_week=LocalDate.of(start_year,start_month,start_day).getDayOfWeek().getValue();
                    start_date_textview.setText(getDateText(start_month,start_day,start_week));
                    return;
                }else if(start_year==end_year){
                    if(start_month>end_month){
                        start_year=end_year;
                        start_month=end_month;
                        start_day=end_day;
                        start_week=LocalDate.of(start_year,start_month,start_day).getDayOfWeek().getValue();
                        start_date_textview.setText(getDateText(start_month,start_day,start_week));
                        return;
                    }else if(start_month==end_month){
                        if(start_day>end_day){
                            start_year=end_year;
                            start_month=end_month;
                            start_day=end_day;
                            start_week=LocalDate.of(start_year,start_month,start_day).getDayOfWeek().getValue();
                            start_date_textview.setText(getDateText(start_month,start_day,start_week));
                            return;
                        }
                    }
                }
            }
        });


        // ???????????? ?????????
        allday_switch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    start_time_textview.setVisibility(View.GONE);
                    end_time_textview.setVisibility(View.GONE);
                    date_end_layout.setVisibility(View.GONE);
                    date_start_layout.setVisibility(View.GONE);
                    time_start_layout.setVisibility(View.GONE);
                    time_end_layout.setVisibility(View.GONE);
                    time_picker_flag=0;
                    date_picker_flag=0;
                }
                else{
                    start_time_textview.setVisibility(View.VISIBLE);
                    end_time_textview.setVisibility(View.VISIBLE);
                }
            }
        });
        // ????????? ????????? ??? ???????????? ?????? ???????????? ?????????
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
        // ??? ??? ??? ?????? ??????

        /////////////////////////////////////
        alarm_time_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alarm_time){
                    alarm_time_checkbox_layout.setVisibility(View.VISIBLE);
                    alarm_time_textview.setBackground(ContextCompat.getDrawable(schedule.this,R.drawable.ed_text));
                }else{
                    alarm_time_checkbox_layout.setVisibility(View.GONE);
                    alarm_time_textview.setBackgroundColor(ContextCompat.getColor(schedule.this,R.color.bg_white));
                    String text="";
                    String[] temp={"?????? ????????????","10???","1??????","1???"};
                    for(int i=0;i<4;i++){
                        if(ago_checkboxes[i]){
                            text+=temp[i];
                            text+=", ";
                        }
                    }
                    if(customChecked){
                        text+=getCustomText(customVal,customIndex);
                        text+=", ";
                    }
                    if(text==""){
                        alarm_time_textview.setText("?????? ?????? ??????");
                    }else {
                        text=text.substring(0,text.length()-2) + " ???";
                        alarm_time_textview.setText(text);
                    }
                    alarm = text;
                }
                alarm_time=!alarm_time;
            }
        });

        checkbox_ontime.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    ago_checkboxes[0]=true;
                }else{
                    ago_checkboxes[0]=false;
                }
            }
        });
        checkbox_10_min_ago.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    ago_checkboxes[1]=true;
                }else{
                    ago_checkboxes[1]=false;
                }
            }
        });
        checkbox_hourago.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    ago_checkboxes[2]=true;
                }else{
                    ago_checkboxes[2]=false;
                }
            }
        });
        checkbox_dayago.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    ago_checkboxes[3]=true;
                }else{
                    ago_checkboxes[3]=false;
                }
            }
        });

        charpicker.setMaxValue(3);
        charpicker.setMinValue(0);
        charpicker.setDisplayedValues(new String[]{
                "???","??????","???","???"
        });
        charpicker.setWrapSelectorWheel(false);
        custom_alram_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                custom_alram_layout.setVisibility(View.VISIBLE);
                setNumpicker(customIndex);
                numpicker.setValue(customVal);
                charpicker.setValue(customIndex);
                checkbox_custom.setText(getCustomText(customVal,customIndex));
                numpicker_layout.setVisibility(View.VISIBLE);
                custom_alram_btn.setClickable(false);
                customChecked=true;
            }
        });

        checkbox_custom.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    setNumpicker(customIndex);
                    numpicker.setValue(customVal);
                    charpicker.setValue(customIndex);
                    numpicker_layout.setVisibility(View.VISIBLE);
                    customChecked=true;
                }else{
                    numpicker_layout.setVisibility(View.GONE);
                    customChecked=false;
                }
            }
        });

        numpicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                customVal=i1;
                checkbox_custom.setText(getCustomText(customVal,customIndex));
            }
        });

        charpicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                customIndex=i1;
                setNumpicker(customIndex);
                checkbox_custom.setText(getCustomText(customVal,customIndex));
            }
        });

        /////////////////////////////////////////////////////////////////////

        iterator_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iterator_time){
                    iterator_radiogroup.setVisibility(View.VISIBLE);
                    iterator_textview.setBackground(ContextCompat.getDrawable(schedule.this,R.drawable.ed_text));
                }else{
                    iterator_radiogroup.setVisibility(View.GONE);
                    iterator_textview.setBackgroundColor(ContextCompat.getColor(schedule.this,R.color.bg_white));
                }
                iterator_time=!iterator_time;
            }
        });
        iterator_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.radiobutton_norepeat){
                    operator_flag=0;
                    iterator_textview.setText("?????? ??? ???");
                }
                else if(i==R.id.radiobutton_everyday){
                    operator_flag=1;
                    iterator_textview.setText("??????");
                }
                else if(i==R.id.radiobutton_everyweek) {
                    operator_flag=2;
                    iterator_textview.setText("??????");
                }
                else if(i==R.id.radiobutton_everymonth) {
                    operator_flag=3;
                    iterator_textview.setText("??????");
                }
                else if(i==R.id.radiobutton_everyyear) {
                    operator_flag=4;
                    iterator_textview.setText("??????");
                }
                iterator_radiogroup.setVisibility(View.GONE);
                iterator_time=!iterator_time;
                iterator_textview.setBackgroundColor(ContextCompat.getColor(schedule.this,R.color.bg_white));
            }
        });

        time_requiered_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(means_flag>=0&&address_data_src!=null&&address_data_dst!=null){
                    NetworkTask networkTask=new NetworkTask();
                    networkTask.execute();
                }else{
                    Toast.makeText(getApplicationContext(),"????????? ????????? ?????? ????????? ?????? ??????????????????!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        means_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.radiobutton_walk){
                   means_flag=1;
                }
                else if(i==R.id.radiobutton_public){
                   means_flag=0;
                }
                else if(i==R.id.radiobutton_car) {
                    means_flag=2;
                }
            }
        });

        // webview ????????????
        src_address =  findViewById(R.id.src_address);
        dst_address =  findViewById(R.id.dst_address);

        src_address.setFocusable(false);
        dst_address.setFocusable(false);

        src_address.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i("?????????????????????", "??????????????? ??????");
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    Log.i("?????????????????????", "??????????????? ??????");
                    Intent i = new Intent(getApplicationContext(), AdressApiActivity.class);
                    //???????????? ??????????????? ?????????
                    // overridePendingTransition(5,5);
                    // ????????????
                    mStartForResult.launch(i);
                }else {
                    Toast.makeText(getApplicationContext(), "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dst_address.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i("?????????????????????","??????????????? ??????");
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    Log.i("?????????????????????","??????????????? ??????");
                    Intent i=new Intent(getApplicationContext(), AdressApiActivity.class);
                    //???????????? ??????????????? ?????????
                    // overridePendingTransition(5,5);
                    // ????????????
                    mStartForResult2.launch(i);
                }else {
                    Toast.makeText(getApplicationContext(), "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database db = new Database(schedule.this);
                LocalDateTime begin = LocalDateTime.of(start_year,start_month, start_day,start_hour,start_minute,0,0);
                LocalDateTime end = LocalDateTime.of(end_year,end_month,end_day,end_hour,end_minute,0,0);
                int last;
                switch (operator_flag){
                    case 0:
                        last=1;
                        break;
                    case 1:
                        last=61;
                        break;
                    case 2:
                        last=27;
                        break;
                    case 3:
                        last = 13;
                        break;
                    default:
                        last = 11;
                        break;
                }
                for(int i = 0; i < last; i++){
                    Schedule newschedule;
                    if(allday_switch.isChecked() && alarm_switch.isChecked()) {
                        newschedule = new Schedule(title.getText() + "", true, true, address_data_src, srcPosition.latitude, srcPosition.longitude,
                                address_data_dst, dstPosition.latitude, dstPosition.longitude, need_hour, need_minute, need_second, means_flag, Time.LocalDateTimeToMills(begin), Time.LocalDateTimeToMills(end), alarm, memo.getText() + "");
                    }
                    else if(allday_switch.isChecked()){
                        begin.withHour(0).withMinute(0).withSecond(0).withNano(0); end.withHour(23).withMinute(59).withSecond(59).withNano(0);
                        newschedule = new Schedule(title.getText() + "", true, false, Time.LocalDateTimeToMills(begin), Time.LocalDateTimeToMills(end),alarm,memo.getText()+"");
                    }
                    else if(alarm_switch.isChecked()){
                        newschedule = new Schedule(title.getText() + "", false, true, address_data_src, srcPosition.latitude, srcPosition.longitude,
                                address_data_dst, dstPosition.latitude, dstPosition.longitude, need_hour, need_minute, need_second, means_flag, Time.LocalDateTimeToMills(begin), Time.LocalDateTimeToMills(end), alarm, memo.getText() + "");
                    }
                    else{
                        newschedule = new Schedule(title.getText() + "", false, false, Time.LocalDateTimeToMills(begin), Time.LocalDateTimeToMills(end),alarm,memo.getText()+"");
                    }
                    if(mode == 0)
                        db.insert(newschedule);
                    else{
                        newschedule.setKey(scheduleKey); newschedule.setServerId(scheduleServerId);
                        db.update(newschedule);
                    }

                    begin = getPlusTime(begin); end = getPlusTime(end);
                }

                setResult(RESULT_OK);
                finish();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        if(mode > 0){
            //??????????????? ??? ?????? ?????? ?????? ??? ???????????????
            scheduleKey = intent.getIntExtra("scheduleKey", 0);
            scheduleServerId = intent.getStringExtra("scheduleServerId");
            Long begin_ms = intent.getLongExtra("begin_ms", Time.YMDToMills("20000101"));
            Long end_ms = intent.getLongExtra("end_ms", Time.YMDToMills("20000101"));
            LocalDateTime begin = Time.MillsToLocalDateTime(begin_ms); LocalDateTime end = Time.MillsToLocalDateTime(end_ms);
            start_year = begin.getYear(); start_month = begin.getMonthValue(); start_day = begin.getDayOfMonth(); start_hour = begin.getHour(); start_minute = begin.getMinute(); start_week = begin.getDayOfWeek().getValue();
            end_year = end.getYear(); end_month = end.getMonthValue(); end_day = end.getDayOfMonth(); end_hour = end.getHour(); end_minute = end.getMinute(); end_week = end.getDayOfWeek().getValue();
            start_date_textview.setText(getDateText(start_month,start_day,start_week));
            end_date_textview.setText(getDateText(end_month,end_day,end_week));
            start_time_textview.setText(getTimeText(start_hour,start_minute));
            end_time_textview.setText(getTimeText(end_hour,end_minute));

            title.setText(intent.getStringExtra("title"));
            allday_switch.setChecked(intent.getBooleanExtra("isAllday", false));
            alarm_switch.setChecked(intent.getBooleanExtra("departAlarm", false));
            alarm = intent.getStringExtra("alarm");
            if(alarm == null) alarm = "";
            if(alarm.equals("")) alarm_time_textview.setText("?????? ?????? ??????");
            else alarm_time_textview.setText(alarm);
            memo.setText(intent.getStringExtra("memo"));

            if(!alarm.equals("")) {
                String[] alarm_time_substr = alarm.substring(0, alarm.length() - 2).split(", ");
                for (String s : alarm_time_substr) {
                    if (s.equals("?????? ????????????")) {
                        checkbox_ontime.setChecked(true);
                    } else if (s.equals("10???")) {
                        checkbox_10_min_ago.setChecked(true);
                    } else if (s.equals("1??????")) {
                        checkbox_hourago.setChecked(true);
                    } else if (s.equals("1???")) {
                        checkbox_dayago.setChecked(true);
                    } else {
                        //?????? ???????????? ????????? ??????
                        checkbox_custom.setChecked(true);
                        custom_alram_layout.setVisibility(View.VISIBLE);
                        String unit = s.substring(s.length() - 1);
                        String num = s.substring(0, s.length() - 1);
                        if (unit.equals("???")) {
                            customIndex = 0;
                        } else if (unit.equals("???")) {
                            customIndex = 1;
                            num = num.substring(0, num.length() - 1);
                        } else if (unit.equals("???")) {
                            customIndex = 2;
                        } else if (unit.equals("???")) {
                            customIndex = 3;
                        }
                        customVal = Integer.parseInt(num);
                        setNumpicker(customIndex);
                        numpicker.setValue(customVal);
                        charpicker.setValue(customIndex);
                        checkbox_custom.setText(getCustomText(customVal, customIndex));
                        numpicker_layout.setVisibility(View.VISIBLE);
                        custom_alram_btn.setClickable(false);
                    }
                }
            }

            if(alarm_switch.isChecked()){
                address_data_src = intent.getStringExtra("begin_loc");
                src_address.setText(address_data_src);
                if(mMap != null) mMap.clear();
                srcLocation = new Location("db"); dstLocation = new Location("db");
                srcPosition = new LatLng(intent.getDoubleExtra("begin_lat",35.887390), intent.getDoubleExtra("begin_lng",128.611629));
                srcLocation.setLatitude(srcPosition.latitude);
                srcLocation.setLongitude(srcPosition.longitude);
                address_data_dst = intent.getStringExtra("end_loc");
                dst_address.setText(address_data_dst);
                dstPosition = new LatLng(intent.getDoubleExtra("end_lat",35.887390), intent.getDoubleExtra("end_lng",128.611629));
                dstLocation.setLatitude(dstPosition.latitude);
                dstLocation.setLongitude(dstPosition.longitude);
                //setSrcLocation(srcLocation, address_data_src, "?????? : "+srcLocation.getLatitude()+" ?????? : "+srcLocation.getLongitude());
                //setDstLocation(dstLocation, address_data_dst, "?????? : "+dstLocation.getLatitude()+" ?????? : "+dstLocation.getLongitude());
                need_hour = intent.getIntExtra("need_hour", need_hour);
                need_minute = intent.getIntExtra("need_minute", need_minute);
                need_second = intent.getIntExtra("need_second", need_second);

                String text="";
                if(need_hour>0){
                    text+=String.valueOf(need_hour)+"?????? ";
                }
                if(need_minute>0){
                    text+=String.valueOf(need_minute)+"??? ";
                }
                if(need_second>0){
                    text+=String.valueOf(need_second)+"??? ";
                }
                time_requiered_textview.setText(text.substring(0,text.length()-1));
                LocalDateTime localDateTime=LocalDateTime.of(start_year,start_month,start_day,start_hour,start_minute);
                localDateTime=localDateTime.minusHours(need_hour);
                localDateTime=localDateTime.minusMinutes(need_minute);
                pre_start_year=localDateTime.getYear();
                pre_start_month=localDateTime.getMonthValue();
                pre_start_day=localDateTime.getDayOfMonth();
                pre_start_hour=localDateTime.getHour();
                pre_start_minute=localDateTime.getMinute();
                text=getDateText(pre_start_month,pre_start_day,localDateTime.getDayOfWeek().getValue())+" "+
                        getTimeText(pre_start_hour,pre_start_minute);
                pre_src_time_textview.setText(text);

                means_flag = intent.getIntExtra("means", means_flag);
                if(means_flag == 1){
                    means_radiogroup.check(R.id.radiobutton_walk);
                }
                else if(means_flag == 0){
                    means_radiogroup.check(R.id.radiobutton_public);
                }
                else if(means_flag == 2){
                    means_radiogroup.check(R.id.radiobutton_car);
                }
            }
            //?????? ??????????????? ????????? ????????? ??? ????????? ??????
            iterator_layout.setVisibility(View.GONE);
            operator_flag = 0;
        }
    }

    private long presstime=0;
    @Override
    public void onBackPressed(){
        long tempTime=System.currentTimeMillis();
        long interval=tempTime-presstime;

        if(interval>=0&&interval<=1000){
            setResult(RESULT_CANCELED);
            finish();
        }else{
            presstime=tempTime;
            Toast.makeText(getApplicationContext(),"?????? ??? ???????????? ?????????????????? ???????????????",Toast.LENGTH_SHORT).show();
        }
    }


    public void setOffClicked(int i){
        clicked[i] = false;
        if (i == 0) {
            start_date_textview.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_white));
        } else if (i == 1) {
            start_time_textview.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_white));
        } else if (i == 2) {
            end_date_textview.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_white));
        } else if (i == 3) {
            end_time_textview.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_white));
        }
    }
    public void setOnClicked(int i){
        clicked[i] = true;
        if (i == 0) {
            start_date_textview.setBackground(ContextCompat.getDrawable(this, R.drawable.ed_text));
        } else if (i == 1) {
            start_time_textview.setBackground(ContextCompat.getDrawable(this, R.drawable.ed_text));
        } else if (i == 2) {
            end_date_textview.setBackground(ContextCompat.getDrawable(this, R.drawable.ed_text));
        } else if (i == 3) {
            end_time_textview.setBackground(ContextCompat.getDrawable(this, R.drawable.ed_text));
        }
    }

    public void setNumpicker(int index){
        if(index==0) {
            numpicker.setMaxValue(360);
        }else if(index==1) {
            numpicker.setMaxValue(99);
        }else if(index==2) {
            numpicker.setMaxValue(365);
        }else if(index==3) {
            numpicker.setMaxValue(52);
        }
        numpicker.setMinValue(1);
    }

    public String getCustomText(int val,int index){
        String ret="";
        ret+=String.valueOf(val);
        switch (index){
            case 0:
                ret+="???";
                break;
            case 1:
                ret+="??????";
                break;
            case 2:
                ret+="???";
                break;
            case 3:
                ret+="???";
                break;
        }
        return ret;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime getPlusTime(LocalDateTime pre){
        switch(operator_flag){
            case 0:
            case 1:
                return pre.plusDays(1);
            case 2:
                return pre.plusWeeks(1);
            case 3:
                return pre.plusMonths(1);
            default:
                return pre.plusYears(1);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        //????????? ????????? ?????? ??????????????? GPS ?????? ?????? ???????????? ???????????????
        //????????? ??????????????? ????????? ??????
        setDefaultLocation();

        //????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ???????????????.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. ?????? ???????????? ????????? ?????????
            // ( ??????????????? 6.0 ?????? ????????? ????????? ???????????? ???????????? ????????? ?????? ????????? ?????? ???????????????.)


            startLocationUpdates(); // 3. ?????? ???????????? ??????


        }else {  //2. ????????? ????????? ????????? ?????? ????????? ????????? ????????? ???????????????. 2?????? ??????(3-1, 4-1)??? ????????????.

            // 3-1. ???????????? ????????? ????????? ??? ?????? ?????? ????????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. ????????? ???????????? ?????? ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ????????????.
                Snackbar.make(mLayout, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.",
                        Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult?????? ???????????????.
                        ActivityCompat.requestPermissions( schedule.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????.
                // ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }



        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // ?????? ???????????? ?????? ????????????
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });
    }
    private String getDateText(int month,int day,int dayofWeek){
        String ret="";
        ret+=month+"??? "+day+"??? ("+getWeek(dayofWeek)+")";
        return ret;
    }
    private String getTimeText(int hour,int minute){
        String ret="";
        if(hour<12){
            ret+="?????? "+String.valueOf(hour)+":";
        }else {
            ret += "?????? "+String.valueOf(hour-12)+":";
        }
        if(minute<10){
            ret+="0"+String.valueOf(minute);
        }else{
            ret+=String.valueOf(minute);
        }
        return ret;
    }



    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                if(srcPosition == null)
                    srcPosition = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(srcPosition);
                if(address_data_src == null)
                    address_data_src=markerTitle;
                String markerSnippet = "??????:" + String.valueOf(location.getLatitude())
                        + " ??????:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //?????? ????????? ?????? ???????????? ??????
                if(srcLocation == null) {
                    srcLocation = location;
                }
                setSrcLocation(srcLocation, address_data_src, "??????:" + String.valueOf(srcLocation.getLatitude())
                        + " ??????:" + String.valueOf(srcLocation.getLongitude()));
                if(dstLocation != null)
                    setDstLocation(dstLocation, address_data_dst, "??????:" + String.valueOf(dstLocation.getLatitude())
                            + " ??????:" + String.valueOf(dstLocation.getLongitude()));
            }


        }

    };



    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : ????????? ???????????? ??????");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }
    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }




    public String getCurrentAddress(LatLng latlng) {

        //????????????... GPS??? ????????? ??????
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //???????????? ??????
            Toast.makeText(this, "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            return "???????????? ????????? ????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "????????? GPS ??????", Toast.LENGTH_LONG).show();
            return "????????? GPS ??????";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "?????? ?????????", Toast.LENGTH_LONG).show();
            return "?????? ?????????";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setSrcLocation(Location location, String markerTitle, String markerSnippet) { //flag ->0 src ->1 dst

        if (srcMarker != null) srcMarker.remove();

        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title("????????? :" + markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        srcMarker = mMap.addMarker(markerOptions);
        srcMarker.showInfoWindow();

        if (dstMarker == null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latlng);
            mMap.moveCamera(cameraUpdate);
        } else {
            LatLngBounds.Builder zoomToFitBuilder = new LatLngBounds.Builder();
            zoomToFitBuilder.include(latlng);
            zoomToFitBuilder.include(dstPosition);
            LatLngBounds zoomToFitBound = zoomToFitBuilder.build();
            int padding = 100;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(zoomToFitBound, padding);
            mMap.animateCamera(cameraUpdate);
        }
    }

    public void setDstLocation(Location location, String markerTitle, String markerSnippet) {
        Log.d("make", "dstmarker");
        if (dstMarker != null) dstMarker.remove();

        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title("????????? :" + markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        dstMarker = mMap.addMarker(markerOptions);
        dstMarker.showInfoWindow();

        LatLngBounds.Builder zoomToFitBuilder = new LatLngBounds.Builder();
        zoomToFitBuilder.include(latlng);
        zoomToFitBuilder.include(srcPosition);
        LatLngBounds zoomToFitBound = zoomToFitBuilder.build();
        int padding = 100;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(zoomToFitBound, padding);
        mMap.animateCamera(cameraUpdate);
    }


    public void setDefaultLocation() {


        //????????? ??????, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "???????????? ????????? ??? ??????";
        String markerSnippet = "?????? ???????????? GPS ?????? ?????? ???????????????";


        if (srcMarker != null) srcMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        srcMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }


    //??????????????? ????????? ????????? ????????? ?????? ????????????
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions??? ????????? ????????? ????????? ????????? ???????????? ??????????????????.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????

            boolean check_result = true;


            // ?????? ???????????? ??????????????? ???????????????.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                // ???????????? ??????????????? ?????? ??????????????? ???????????????.
                startLocationUpdates();
            } else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ????????????.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // ???????????? ????????? ????????? ???????????? ?????? ?????? ???????????? ????????? ???????????? ?????? ????????? ??? ????????????.
                    Snackbar.make(mLayout, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {


                    // "?????? ?????? ??????"??? ???????????? ???????????? ????????? ????????? ???????????? ??????(??? ??????)?????? ???????????? ???????????? ?????? ????????? ??? ????????????.
                    Snackbar.make(mLayout, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }

    private String getWeek(int week){
        if(week==1){
            return "???";
        }else if(week==2){
            return "???";
        }else if(week==3){
            return "???";
        }else if(week==4){
            return "???";
        }else if(week==5){
            return "???";
        }else if(week==6){
            return "???";
        }else if(week==7){
            return "???";
        }
        return "";
    }


    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(schedule.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:
                //???????????? GPS ?????? ???????????? ??????
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS ????????? ?????????");

                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }

    ActivityResultLauncher<Intent> mStartForResult=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    address_data_src=result.getData().getExtras().getString("data");
                    if(address_data_src!=null){
                        Log.d("text","data: "+address_data_src);
                        new Thread(()->{try {
                            Log.d("????????????",address_data_src);
                            String data_ = URLEncoder.encode(address_data_src, "utf-8");
                            Log.d("encode","done");
                            URL url = new URL(giourl + data_ + "&appKey=" + key);
                            Log.d("make",url.toString());
                            Log.d("bfreader","ready");
                            BufferedReader  bf;

                            bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                            Log.d("bfreader","done");
                            String temp = bf.readLine();

                            Log.d("????????????", temp);

                            JSONParser jsonParser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(temp);
                            JSONObject coordinateInfo = (JSONObject) jsonObject.get("coordinateInfo");

                            JSONArray coordinate = (JSONArray) coordinateInfo.get("coordinate");
                            JSONObject pos = (JSONObject) coordinate.get(0);
                            String newmatchflag = pos.get("newMatchFlag").toString();
                            double lat;
                            double lon;
                            if (!newmatchflag.isEmpty()) {
                                Log.d("fff", "fff");
                                lat = Double.parseDouble((String) pos.get("newLat"));
                                lon = Double.parseDouble((String) pos.get("newLon"));
                            } else {
                                lat = Double.parseDouble((String) pos.get("lat"));
                                lon = Double.parseDouble((String) pos.get("lon"));
                            }

                            srcPosition
                                    = new LatLng(lat, lon);

                            Log.d("lat",String.valueOf(lat));
                            location.setLatitude(lat);
                            Log.d("latdst","done");
                            location.setLongitude(lon);


                            String markerTitle = getCurrentAddress(srcPosition);
                            String markerSnippet = "??????:" + String.valueOf(lat)
                                    + " ??????:" + String.valueOf(lon);

                            Log.d(TAG, "srconLocationResult : " + markerSnippet);


                            Log.d("lat",String.valueOf(lat));
                            location.setLatitude(lat);
                            Log.d("latdst","done");
                            location.setLongitude(lon);
                            Log.d("srcmarker",markerTitle);
                            srcLocation=location;

                            schedule.this.runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                    src_address.setText(markerTitle);
                                    setSrcLocation(srcLocation, markerTitle, markerSnippet);
                                }
                            });

                        }catch (Exception e) {
                            e.printStackTrace();
                        }}).start();
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> mStartForResult2=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    address_data_dst=result.getData().getExtras().getString("data");
                    if(address_data_dst!=null){
                        Log.i("text","data: "+address_data_dst);
                        new Thread(()->{try {
                            Log.d("????????????",address_data_dst);
                            String data_ = URLEncoder.encode(address_data_dst, "utf-8");
                            Log.d("encode","done");
                            URL url = new URL(giourl + data_ + "&appKey=" + key);
                            Log.d("make",url.toString());
                            Log.d("bfreader","ready");
                            BufferedReader  bf;

                            bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                            Log.d("bfreader","done");
                            String temp = bf.readLine();

                            Log.d("????????????", temp);

                            JSONParser jsonParser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(temp);
                            JSONObject coordinateInfo = (JSONObject) jsonObject.get("coordinateInfo");

                            JSONArray coordinate = (JSONArray) coordinateInfo.get("coordinate");
                            JSONObject pos = (JSONObject) coordinate.get(0);
                            String newmatchflag = pos.get("newMatchFlag").toString();
                            double lat;
                            double lon;
                            if (!newmatchflag.isEmpty()) {
                                Log.d("fff", "fff");
                                lat = Double.parseDouble((String) pos.get("newLat"));
                                lon = Double.parseDouble((String) pos.get("newLon"));
                            } else {
                                lat = Double.parseDouble((String) pos.get("lat"));
                                lon = Double.parseDouble((String) pos.get("lon"));
                            }

                            dstPosition
                                    = new LatLng(lat, lon);

                            Log.d("lat",String.valueOf(lat));
                            location.setLatitude(lat);
                            Log.d("latdst","done");
                            location.setLongitude(lon);


                            String markerTitle = getCurrentAddress(dstPosition);
                            String markerSnippet = "??????:" + String.valueOf(lat)
                                    + " ??????:" + String.valueOf(lon);

                            Log.d(TAG, "dstonLocationResult : " + markerSnippet);


                            Log.d("lat",String.valueOf(lat));
                            location.setLatitude(lat);
                            Log.d("latdst","done");
                            location.setLongitude(lon);
                            Log.d("dstmarker",markerTitle);
                            dstLocation=location;


                            schedule.this.runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                    dst_address.setText(markerTitle);
                                    setDstLocation(dstLocation, markerTitle, markerSnippet);
                                }
                            });

                        }catch (Exception e) {
                            e.printStackTrace();
                        }}).start();
                    }
                }
            }
    );

    public class NetworkTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... params) {
            String result="";
            String url="";
            ContentValues contentValues=new ContentValues();
            if(means_flag==0){
                url="http://apis.openapi.sk.com/transit/routes/sub";
                contentValues.put("format","json");
                contentValues.put("endY",String.valueOf(dstPosition.latitude));
                contentValues.put("endX",String.valueOf(dstPosition.longitude));
                contentValues.put("startY",String.valueOf(srcPosition.latitude));
                contentValues.put("startX",String.valueOf(srcPosition.longitude));
            }else if(means_flag==1){
                url="http://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=json";
                contentValues.put("startX",String.valueOf(srcPosition.longitude));
                contentValues.put("startY",String.valueOf(srcPosition.latitude));
                contentValues.put("endX",String.valueOf(dstPosition.longitude));
                contentValues.put("endY",String.valueOf(dstPosition.latitude));
                contentValues.put("startName",address_data_src);
                contentValues.put("endName",address_data_dst);
            }else if(means_flag==2){
                url="http://apis.openapi.sk.com/tmap/routes?version=1&format=json";
                contentValues.put("startX",String.valueOf(srcPosition.longitude));
                contentValues.put("startY",String.valueOf(srcPosition.latitude));
                contentValues.put("endX",String.valueOf(dstPosition.longitude));
                contentValues.put("endY",String.valueOf(dstPosition.latitude));
            }

            RequestHttpConnection requestHttpConnection=new RequestHttpConnection(means_flag);
            result=requestHttpConnection.request(url,contentValues);
            Log.d("result: ",result);
            return result;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = null;
            try {
                jsonObject = (JSONObject) jsonParser.parse(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.d("????????????", jsonObject.toString());

            String time;
            if(means_flag==0){
                JSONObject metaData = (JSONObject) jsonObject.get("metaData");
                JSONObject plan = (JSONObject) metaData.get("plan");
                Log.d("plan",plan.toString());
                JSONArray itineraries = (JSONArray) plan.get("itineraries");
                Log.d("itineraries",itineraries.toString());
                JSONObject index = (JSONObject) itineraries.get(0);
                Log.d("index",index.toString());
                Long totalTime = (Long) index.get("totalTime");
                Log.d("totalTime",index.toString());

                time = (String) totalTime.toString();
                Log.d("totalTime", time);
            }else {

                JSONArray features = (JSONArray) jsonObject.get("features");
                JSONObject sp_features = (JSONObject) features.get(0);

                JSONObject properties = (JSONObject) sp_features.get("properties");
                Log.d("properties", (String) properties.toString());
                time = (String) properties.get("totalTime").toString();
                Log.d("tttttime", time);
            }
            int required_time=Integer.parseInt(time);
            int hour=required_time/3600;
            required_time%=3600;
            int minute=required_time/60;
            required_time%=60;
            int second=required_time;
            String text="";
            if(hour>0){
                text+=String.valueOf(hour)+"?????? ";
            }
            if(minute>0){
                text+=String.valueOf(minute)+"??? ";
            }
            if(second>0){
                text+=String.valueOf(second)+"??? ";
            }
            time_requiered_textview.setText(text.substring(0,text.length()-1));
            need_hour=hour;
            need_minute=minute;
            need_second=second;
            LocalDateTime localDateTime=LocalDateTime.of(start_year,start_month,start_day,start_hour,start_minute);
            localDateTime=localDateTime.minusHours(hour);
            localDateTime=localDateTime.minusMinutes(minute);
            pre_start_year=localDateTime.getYear();
            pre_start_month=localDateTime.getMonthValue();
            pre_start_day=localDateTime.getDayOfMonth();
            pre_start_hour=localDateTime.getHour();
            pre_start_minute=localDateTime.getMinute();
            text=getDateText(pre_start_month,pre_start_day,localDateTime.getDayOfWeek().getValue())+" "+
            getTimeText(pre_start_hour,pre_start_minute);
            pre_src_time_textview.setText(text);
        }
    }
}