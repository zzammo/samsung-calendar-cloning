package com.zzammo.calendar.activity;

import static java.lang.Math.log;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.zzammo.calendar.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class schedule extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    EditText memo;
    Switch allday_switch;
    Switch alarm_switch;
    TextView src_time;
    TextView src_date;
    TextView dst_time;
    TextView dst_date;
    LinearLayout path_panel;
    LinearLayout ago_timepicker;
    LinearLayout ago_panel;
    LinearLayout time_src_layout;
    LinearLayout date_src_layout;
    LinearLayout time_dst_layout;
    LinearLayout date_dst_layout;
    LinearLayout alarm_time_layout;
    LinearLayout alarm_time_checkbox_layout;
    LinearLayout iterator_layout;
    TextView src_address;
    TextView dst_address;
    RadioGroup iterator_radiogroup;
    TextView iterator_textview;
    TextView alarm_time_textview;

    CheckBox checkbox_ontime;
    CheckBox checkbox_10_min_ago;
    CheckBox checkbox_hourago;
    CheckBox checkbox_dayago;

    private boolean ago_flag = true;
    private int date_picker_flag = 0;// 0 -> off 1 -> src 2-> dst
    private int time_picker_flag = 0;// 0 -> off 1 -> src 2-> dst
    private boolean alarm_time=true;
    private boolean iterator_time=true;
    private int operator_flag=0;
    private boolean[] ago_checkboxes={false,true,false,false};

    private GoogleMap mMap;
    private Marker srcMarker = null;
    private Marker dstMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초


    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location srcLocation=null;
    LatLng srcPosition=null;
    Location dstLocation=null;
    LatLng dstPosition=null;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)

    private String address_data_src=null;
    private String address_data_dst=null;
    // 인증키 (개인이 받아와야함)
    String key = "l7xxb76eb9ee907444a8b8098322fa488048";

    // 파싱한 데이터를 저장할 변수
    String result1 = "";
    String result2 = "";

    String giourl ="http://apis.openapi.sk.com/tmap/geo/fullAddrGeo?addressFlag=F00&coordType=WGS84GEO&version=1&format=json&fullAddr=";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);
        memo = findViewById(R.id.memo);
        allday_switch = findViewById(R.id.allday_switch);
        src_time = findViewById(R.id.src_time);
        src_date = findViewById(R.id.src_date);
        dst_time = findViewById(R.id.dst_time);
        dst_date = findViewById(R.id.dst_date);
        alarm_switch = findViewById(R.id.alarm_switch);
        path_panel = findViewById(R.id.path_panel);
        ago_panel = findViewById(R.id.ago_panel);
        ago_timepicker = findViewById(R.id.ago_timepicker);

        time_src_layout=findViewById(R.id.time_src_layout);
        date_src_layout=findViewById(R.id.date_src_layout);

        time_dst_layout=findViewById(R.id.time_dst_layout);
        date_dst_layout=findViewById(R.id.date_dst_layout);
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


        mLayout = findViewById(R.id.layout_schedule);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) schedule.this);

        /// 일정 시간 설정 src
        src_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date_picker_flag!=0) {
                    if(date_picker_flag==1) {
                        date_src_layout.setVisibility(View.GONE);
                    }else{
                        date_dst_layout.setVisibility(View.GONE);
                    }
                    date_picker_flag=0;
                }
                if (time_picker_flag == 0) {
                    time_src_layout.setVisibility(View.VISIBLE);
                    time_picker_flag = 1;
                } else if (time_picker_flag == 1) {
                    time_src_layout.setVisibility(View.GONE);
                    time_picker_flag = 0;
                } else {
                    time_dst_layout.setVisibility(View.GONE);
                    time_src_layout.setVisibility(View.VISIBLE);
                    time_picker_flag = 1;
                }
            }
        });
        // 일정 날짜 설정 src
        src_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(time_picker_flag!=0) {
                    if(time_picker_flag==1) {
                        time_src_layout.setVisibility(View.GONE);
                    }else{
                        time_dst_layout.setVisibility(View.GONE);
                    }
                    time_picker_flag=0;
                }
                if (date_picker_flag == 0) {
                    date_src_layout.setVisibility(View.VISIBLE);
                    date_picker_flag = 1;
                } else if (date_picker_flag == 1) {
                    date_src_layout.setVisibility(View.GONE);
                    date_picker_flag = 0;
                } else {
                    date_dst_layout.setVisibility(View.GONE);
                    date_src_layout.setVisibility(View.VISIBLE);
                    date_picker_flag = 1;
                }
            }
        });
        // 일정 시간 설정 dst
        dst_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date_picker_flag!=0) {
                    if(date_picker_flag==1) {
                        date_src_layout.setVisibility(View.GONE);
                    }else{
                        date_dst_layout.setVisibility(View.GONE);
                    }
                    date_picker_flag=0;
                }
                if (time_picker_flag == 0) {
                    time_dst_layout.setVisibility(View.VISIBLE);
                    time_picker_flag = 2;
                } else if (time_picker_flag == 2) {
                    time_dst_layout.setVisibility(View.GONE);
                    time_picker_flag = 0;
                } else {
                    time_src_layout.setVisibility(View.GONE);
                    time_dst_layout.setVisibility(View.VISIBLE);
                    time_picker_flag = 2;
                }
            }
        });
        // 일정 날짜 설정 dst
        dst_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(time_picker_flag!=0) {
                    if(time_picker_flag==1) {
                        time_src_layout.setVisibility(View.GONE);
                    }else{
                        time_dst_layout.setVisibility(View.GONE);
                    }
                    time_picker_flag=0;
                }
                if (date_picker_flag == 0) {
                    date_dst_layout.setVisibility(View.VISIBLE);
                    date_picker_flag = 2;
                } else if (date_picker_flag == 2) {
                    date_dst_layout.setVisibility(View.GONE);
                    date_picker_flag = 0;
                } else {
                    date_src_layout.setVisibility(View.GONE);
                    date_dst_layout.setVisibility(View.VISIBLE);
                    date_picker_flag = 2;
                }
            }
        });
        // 하루종일 스위치
        allday_switch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    src_time.setVisibility(View.GONE);
                    dst_time.setVisibility(View.GONE);
                    date_dst_layout.setVisibility(View.GONE);
                    date_src_layout.setVisibility(View.GONE);
                    time_dst_layout.setVisibility(View.GONE);
                    time_src_layout.setVisibility(View.GONE);
                    time_picker_flag=0;
                    date_picker_flag=0;
                }
                else{
                    src_time.setVisibility(View.VISIBLE);
                    dst_time.setVisibility(View.VISIBLE);
                }
            }
        });
        // 출발지 도착지 를 입력할지 말지 결정하는 스위치
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
        // 몇 분 전 알람 선택
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

        alarm_time_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alarm_time){
                    alarm_time_checkbox_layout.setVisibility(View.VISIBLE);
                }else{
                    alarm_time_checkbox_layout.setVisibility(View.GONE);
                    String text="";
                    String[] temp={"일정 시작시간","10분 전","1시간 전","1일 전"};
                    for(int i=0;i<4;i++){
                        if(ago_checkboxes[i]){
                            text+=temp[i];
                            text+=", ";
                        }
                    }
                    text=text.substring(0,text.length()-2);
                    alarm_time_textview.setText(text);
                }
                alarm_time=!alarm_time;
            }
        });

        iterator_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iterator_time){
                    iterator_radiogroup.setVisibility(View.VISIBLE);
                }else{
                    iterator_radiogroup.setVisibility(View.GONE);
                }
                iterator_time=!iterator_time;
            }
        });
        iterator_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.radiobutton_norepeat){
                    operator_flag=0;
                    iterator_textview.setText("반복 안 함");
                    iterator_radiogroup.setVisibility(View.GONE);
                    iterator_time=!iterator_time;
                }
                else if(i==R.id.radiobutton_everyday){
                    operator_flag=1;
                    iterator_textview.setText("매일");
                    iterator_radiogroup.setVisibility(View.GONE);
                    iterator_time=!iterator_time;
                }
                else if(i==R.id.radiobutton_everyweek) {
                    operator_flag=2;
                    iterator_textview.setText("매주");
                    iterator_radiogroup.setVisibility(View.GONE);
                    iterator_time=!iterator_time;
                }
                else if(i==R.id.radiobutton_everymonth) {
                    operator_flag=3;
                    iterator_textview.setText("매월");
                    iterator_radiogroup.setVisibility(View.GONE);
                    iterator_time=!iterator_time;
                }
                else if(i==R.id.radiobutton_everyyear) {
                    operator_flag=4;
                    iterator_textview.setText("매년");
                    iterator_radiogroup.setVisibility(View.GONE);
                    iterator_time=!iterator_time;
                }
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

        // webview 불러오기
        src_address =  findViewById(R.id.src_address);
        dst_address =  findViewById(R.id.dst_address);

        src_address.setFocusable(false);
        dst_address.setFocusable(false);

        src_address.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i("주소설정페이지", "주소입력창 클릭");
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    Log.i("주소설정페이지", "주소입력창 클릭");
                    Intent i = new Intent(getApplicationContext(), AdressApiActivity.class);
                    //화면전환 애니메이션 없애기
                    // overridePendingTransition(5,5);
                    // 주소결과
                    mStartForResult.launch(i);
                }else {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dst_address.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i("주소설정페이지","주소입력창 클릭");
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    Log.i("주소설정페이지","주소입력창 클릭");
                    Intent i=new Intent(getApplicationContext(), AdressApiActivity.class);
                    //화면전환 애니메이션 없애기
                    // overridePendingTransition(5,5);
                    // 주소결과
                    mStartForResult2.launch(i);
                }else {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();



        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            startLocationUpdates(); // 3. 위치 업데이트 시작


        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( schedule.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }



        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 현재 오동작을 해서 주석처리
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                srcPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(srcPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                setSrcLocation(location, markerTitle, markerSnippet);

                srcLocation = location;
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

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
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

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

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
        markerOptions.title("출발지 :" + markerTitle);
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
        markerOptions.title("도착지 :" + markerTitle);
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


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


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


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
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
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(schedule.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");

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
                            Log.d("지오코딩",address_data_src);
                            String data_ = URLEncoder.encode(address_data_src, "utf-8");
                            Log.d("encode","done");
                            URL url = new URL(giourl + data_ + "&appKey=" + key);
                            Log.d("make",url.toString());
                            Log.d("bfreader","ready");
                            BufferedReader  bf;

                            bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                            Log.d("bfreader","done");
                            String temp = bf.readLine();

                            Log.d("연결완료", temp);

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
                            String markerSnippet = "위도:" + String.valueOf(lat)
                                    + " 경도:" + String.valueOf(lon);

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
                            Log.d("지오코딩",address_data_dst);
                            String data_ = URLEncoder.encode(address_data_dst, "utf-8");
                            Log.d("encode","done");
                            URL url = new URL(giourl + data_ + "&appKey=" + key);
                            Log.d("make",url.toString());
                            Log.d("bfreader","ready");
                            BufferedReader  bf;

                            bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                            Log.d("bfreader","done");
                            String temp = bf.readLine();

                            Log.d("연결완료", temp);

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
                            String markerSnippet = "위도:" + String.valueOf(lat)
                                    + " 경도:" + String.valueOf(lon);

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


}