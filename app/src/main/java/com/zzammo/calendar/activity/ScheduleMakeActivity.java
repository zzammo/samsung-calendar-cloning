package com.zzammo.calendar.activity;

import static java.lang.Math.log;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.zzammo.calendar.R;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.Time;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleMakeActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText title_et;

    SwitchMaterial allDay_sw;
    TextView begin_date_tv;
    TextView begin_time_tv;
    TextView end_date_tv;
    TextView end_time_tv;

    MaterialCalendarView begin_cv;
    MaterialCalendarView end_cv;
    TimePicker begin_tp;
    TimePicker end_tp;

    SwitchMaterial alarm_sw;
    LinearLayout location_lo;
    TextView begin_location_tv;
    TextView begin_location_address_tv;
    TextView end_location_tv;
    TextView end_location_address_tv;

    GoogleMap map;
    ArrayList<MarkerOptions> markers;

    RadioGroup transit_rg;
    TextView time_cost_tv;

    EditText memo_et;

    TextView cancel_tv;
    TextView save_tv;

    Calendar beginDate;
    LatLng beginLoc;
    Calendar endDate;
    LatLng endLoc;

    String begin_address = null;
    String end_address = null;

    // 인증키 (개인이 받아와야함)
    final String key = "l7xxb76eb9ee907444a8b8098322fa488048";
    final String gioUrl ="http://apis.openapi.sk.com/tmap/geo/fullAddrGeo?addressFlag=F00&coordType=WGS84GEO&version=1&format=json&fullAddr=";
    final String busQueryUrl = "http://apis.openapi.sk.com/transit/routes/sub";
    final String walkQueryUrl = "http://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=json";
    final String carQueryUrl = "http://apis.openapi.sk.com/tmap/routes?version=1&format=json";

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_make);

        context = this;

        SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().
                findFragmentById(R.id.activity_schedule_make_googleMap);
        supportMapFragment.getMapAsync(this);

        StrictMode.enableDefaults();

        markers = new ArrayList<>();

        bindViews();
        setViews();
        setListeners();

        transit_rg.getCheckedRadioButtonId();

    }

    void bindViews(){
        title_et = findViewById(R.id.activity_schedule_make_schedule_title_et);

        allDay_sw = findViewById(R.id.activity_schedule_make_all_day_switch);
        begin_date_tv = findViewById(R.id.activity_schedule_make_begin_date_tv);
        begin_time_tv = findViewById(R.id.activity_schedule_make_begin_time_tv);
        end_date_tv = findViewById(R.id.activity_schedule_make_end_date_tv);
        end_time_tv = findViewById(R.id.activity_schedule_make_end_time_tv);

        begin_cv = findViewById(R.id.activity_schedule_make_begin_calendar);
        end_cv = findViewById(R.id.activity_schedule_make_end_calendar);
        begin_tp = findViewById(R.id.activity_schedule_make_begin_timePicker);
        end_tp = findViewById(R.id.activity_schedule_make_end_timePicker);

        location_lo = findViewById(R.id.activity_schedule_make_location_lo);
        begin_location_tv = findViewById(R.id.activity_schedule_make_begin_location_tv);
        begin_location_address_tv = findViewById(R.id.activity_schedule_make_begin_location_address_tv);
        end_location_tv = findViewById(R.id.activity_schedule_make_end_location_tv);
        end_location_address_tv = findViewById(R.id.activity_schedule_make_end_location_address_tv);

        transit_rg = findViewById(R.id.activity_schedule_make_transit_radioGroup);

        alarm_sw = findViewById(R.id.activity_schedule_make_alarm_switch);

        time_cost_tv = findViewById(R.id.activity_schedule_make_time_cost_tv);

        memo_et = findViewById(R.id.activity_schedule_make_memo_et);

        cancel_tv = findViewById(R.id.activity_schedule_make_cancel_tv);
        save_tv = findViewById(R.id.activity_schedule_make_save_tv);
    }
    void setViews(){
        alarm_sw.setChecked(true);

        Long it_mills = getIntent().getLongExtra("timeInMill", System.currentTimeMillis());
        beginDate = Calendar.getInstance();
        beginDate.setTimeInMillis(it_mills);
        endDate = Calendar.getInstance();
        endDate.setTimeInMillis(it_mills+Time.ONE_HOUR);

        putDate(begin_date_tv, beginDate.get(Calendar.MONTH)+1, beginDate.get(Calendar.DATE));
        putTime(begin_time_tv, beginDate.get(Calendar.HOUR), beginDate.get(Calendar.MINUTE));
        putDate(end_date_tv, endDate.get(Calendar.MONTH)+1, endDate.get(Calendar.DATE));
        putTime(end_time_tv, endDate.get(Calendar.HOUR), endDate.get(Calendar.MINUTE));

        begin_cv.setDateSelected(Time.CalendarToCalendarDay(beginDate), true);
        end_cv.setDateSelected(Time.CalendarToCalendarDay(endDate), true);
        begin_tp.setCurrentHour(beginDate.get(Calendar.HOUR_OF_DAY));
        begin_tp.setCurrentMinute(beginDate.get(Calendar.MINUTE));
        end_tp.setCurrentHour(endDate.get(Calendar.HOUR_OF_DAY));
        end_tp.setCurrentMinute(endDate.get(Calendar.MINUTE));
    }
    void setListeners(){
        begin_date_tv.setOnClickListener(v -> {
            visibilityToggle(begin_cv);
        });
        begin_time_tv.setOnClickListener(v -> {
            visibilityToggle(begin_tp);
        });
        end_date_tv.setOnClickListener(v -> {
            visibilityToggle(end_cv);
        });
        end_time_tv.setOnClickListener(v -> {
            visibilityToggle(end_tp);
        });

        begin_cv.setOnDateChangedListener((widget, date, selected) -> {
            if (!selected) return;

            dateChanged(begin_date_tv, beginDate, date);
        });
        begin_tp.setOnTimeChangedListener((timePicker, hour, minute) -> {
            timeChanged(begin_time_tv, beginDate, hour, minute);
        });
        end_cv.setOnDateChangedListener((widget, date, selected) -> {
            if (!selected) return;

            dateChanged(end_date_tv, endDate, date);
        });
        end_tp.setOnTimeChangedListener((timePicker, hour, minute) -> {
            timeChanged(end_time_tv, endDate, hour, minute);
        });

        begin_location_address_tv.setOnClickListener(v -> {
            int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
            if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                Intent it = new Intent(context, AdressApiActivity.class);
                startForBeginLoc.launch(it);
            }else {
                Toast.makeText(context, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        end_location_address_tv.setOnClickListener(v -> {
            int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
            if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                Intent it = new Intent(context, AdressApiActivity.class);
                startForEndLoc.launch(it);
            }else {
                Toast.makeText(context, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        alarm_sw.setOnCheckedChangeListener((compoundButton, bool) -> {
            if (bool)
                location_lo.setVisibility(View.VISIBLE);
            else
                location_lo.setVisibility(View.GONE);
        });
        transit_rg.setOnCheckedChangeListener((radioGroup, i) -> {
            executeCalcTimeCostTask();
        });

        cancel_tv.setOnClickListener(v -> {
            finish();
        });
        save_tv.setOnClickListener(v -> {
            String title = title_et.getText().toString();
            String begin_loc = begin_location_address_tv.getText().toString();
            Long begin_time = Time.CalendarToMill(beginDate);
            String end_loc = end_location_address_tv.getText().toString();
            Long end_time = Time.CalendarToMill(endDate);
            String memo = memo_et.getText().toString();

            Schedule schedule = new Schedule(title,
                    begin_loc, beginLoc.latitude, beginLoc.longitude,
                    end_loc, endLoc.latitude, endLoc.longitude,
                    begin_time, end_time,
                    memo);

            new Database(this).insert(schedule);

            finish();
        });
    }

    void putTime(TextView tv, int hour, int minute){
        if (hour > 12) {
            hour -= 12;
            tv.setText("오후 " + hour + "시 " + minute + "분");
        } else {
            tv.setText("오전 " + hour + "시 " + minute + "분");
        }
    }
    void putDate(TextView tv, int month, int day){
        tv.setText(month + "월 " + day + "일");
    }

    void setDate(Calendar calendar, CalendarDay date){
        calendar.set(Calendar.YEAR, date.getYear());
        calendar.set(Calendar.MONTH, date.getMonth());
        calendar.set(Calendar.DATE, date.getDay());
    }
    void setTime(Calendar calendar, int hour, int minute){
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
    }

    void dateChanged(TextView day, Calendar calendar, CalendarDay date){
        setDate(calendar, date);
        putDate(day, calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
    }
    void timeChanged(TextView time, Calendar calendar, int hour, int minute){
        setTime(calendar, hour, minute);
        putTime(time, hour, minute);
    }

    void visibilityToggle(View v){
        if (v.getVisibility() == View.VISIBLE)
            v.setVisibility(View.GONE);
        else {
            begin_cv.setVisibility(View.GONE);
            end_cv.setVisibility(View.GONE);
            begin_tp.setVisibility(View.GONE);
            end_tp.setVisibility(View.GONE);

            v.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng seoul=new LatLng(37.56,126.97);

        MarkerOptions options = new MarkerOptions();
        options.position(seoul)
                .title("서울")
                .snippet("한국의 수도");
        markers.add(options);
        map.addMarker(options);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,10));
    }

    void executeCalcTimeCostTask(){
        if (begin_address == null || end_address == null) return;

        try {
            String encoded_ba = URLEncoder.encode(begin_address, "utf-8");
            String encoded_ea = URLEncoder.encode(end_address, "utf-8");

            beginLoc = queryLatLng(begin_address);
            endLoc = queryLatLng(end_address);

            LatLng mid_latLng = new LatLng(
                    (beginLoc.latitude+endLoc.latitude)/2,
                    (beginLoc.longitude+endLoc.longitude)/2);
            double scale = calculateScale(beginLoc, endLoc);

            MarkerOptions start = new MarkerOptions();
            start.position(beginLoc)
                    .title("출발지")
                    .snippet("한국");
            markers.add(start);
            map.addMarker(start);

            MarkerOptions goal = new MarkerOptions();
            goal.position(endLoc)
                    .title("도착지")
                    .snippet("한국");
            markers.add(goal);
            map.addMarker(goal);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mid_latLng, (float)scale));

            ContentValues contentValues = new ContentValues();
            contentValues.put("startY",String.valueOf(beginLoc.latitude));
            contentValues.put("startX",String.valueOf(beginLoc.longitude));
            contentValues.put("endY",String.valueOf(endLoc.latitude));
            contentValues.put("endX",String.valueOf(endLoc.longitude));
            switch (transit_rg.getCheckedRadioButtonId()){
                case R.id.activity_schedule_make_transit_bus_radioButton:
                    contentValues.put("format","json");
                    new NetworkTask(busQueryUrl, contentValues).execute();
                    break;
                case R.id.activity_schedule_make_transit_walk_radioButton:
                    contentValues.put("startName", encoded_ba);
                    contentValues.put("endName", encoded_ea);
                    new NetworkTask(walkQueryUrl, contentValues).execute();
                    break;
                case R.id.activity_schedule_make_transit_car_radioButton:
                    new NetworkTask(carQueryUrl, contentValues).execute();
                    break;
                default:
                    Toast.makeText(context, "교통수단을 선택해 주세요", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    LatLng parseLatLng(JSONParser jsonParser, String result) throws ParseException {
        JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
        JSONObject coordinateInfo = (JSONObject) jsonObject.get("coordinateInfo");
        JSONArray coordinate = (JSONArray) coordinateInfo.get("coordinate");
        JSONObject pos = (JSONObject) coordinate.get(0);

        String newMatchFlag = pos.get("newMatchFlag").toString();

        double lat;
        double lng;
        if(!newMatchFlag.isEmpty()){
            lat = Double.parseDouble((String) pos.get("newLat"));
            lng = Double.parseDouble((String) pos.get("newLon"));
        }
        else {
            lat = Double.parseDouble((String) pos.get("lat"));
            lng = Double.parseDouble((String) pos.get("lon"));
        }

        return new LatLng(lat, lng);
    }

    double calculateScale(LatLng begin_latLng, LatLng end_latLng) {
        Location location1 = new Location("start");
        Location location2 = new Location("goal");

        location1.setLatitude(begin_latLng.latitude);
        location1.setLongitude(begin_latLng.longitude);

        location2.setLatitude(end_latLng.latitude);
        location2.setLongitude(end_latLng.longitude);

        double distance = location1.distanceTo(location2);

        return 21 - log(distance);
    }

    ActivityResultLauncher<Intent> startForBeginLoc = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    Intent it = result.getData();

                    if (it == null) return;

                    begin_address = it.getExtras().getString("data");

                    if(begin_address == null) return;

                    begin_location_address_tv.setText(begin_address);
                    beginLoc = queryLatLng(begin_address);
                }
            }
    );
    ActivityResultLauncher<Intent> startForEndLoc = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    Intent it = result.getData();

                    if (it == null) return;

                    end_address = it.getExtras().getString("data");

                    if(end_address == null) return;

                    end_location_address_tv.setText(end_address);
                    endLoc = queryLatLng(end_address);
                }
            }
    );

    LatLng queryLatLng(String address){
        try {
            String encoded = URLEncoder.encode(address, "utf-8");
            URL query = new URL(gioUrl + encoded + "&appKey=" + key);

            BufferedReader bf;
            bf = new BufferedReader(new InputStreamReader(query.openStream(), "UTF-8"));
            String result = bf.readLine();

            return parseLatLng(new JSONParser(), result);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public class NetworkTask extends AsyncTask<Void,Void,String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url,ContentValues values){
            this.url=url;
            this.values=values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result="";

            int state;
            switch (transit_rg.getCheckedRadioButtonId()){
                case R.id.activity_schedule_make_transit_bus_radioButton:
                    state = 0;
                    break;
                case R.id.activity_schedule_make_transit_walk_radioButton:
                    state = 1;
                    break;
                case R.id.activity_schedule_make_transit_car_radioButton:
                    state = 2;
                    break;
                default:
                    state = 3;
            }

            RequestHttpConnection requestHttpConnection = new RequestHttpConnection(state);
            result = requestHttpConnection.request(url,values);

            return result;
        }

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

            Long time;
            if(transit_rg.getCheckedRadioButtonId() == R.id.activity_schedule_make_transit_bus_radioButton){
                JSONObject metaData = (JSONObject) jsonObject.get("metaData");
                JSONObject plan = (JSONObject) metaData.get("plan");
                JSONArray itineraries = (JSONArray) plan.get("itineraries");
                JSONObject index = (JSONObject) itineraries.get(0);
                Long totalTime = (Long) index.get("totalTime");

                time = totalTime;
            }else {
                JSONArray features = (JSONArray) jsonObject.get("features");
                JSONObject sp_features = (JSONObject) features.get(0);

                JSONObject properties = (JSONObject) sp_features.get("properties");
                time = (Long) properties.get("totalTime");
            }

            time_cost_tv.setText(Time.millToHM(time*1000) + " 소요");
        }
    }
}