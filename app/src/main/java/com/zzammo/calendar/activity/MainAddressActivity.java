package com.zzammo.calendar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zzammo.calendar.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

public class MainAddressActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText edit_addr1;
    EditText edit_addr2;

    String data1,data2;

    // 인증키 (개인이 받아와야함)
    String key = "l7xxb76eb9ee907444a8b8098322fa488048";

    // 파싱한 데이터를 저장할 변수
    String result = "";
    String result2 = "";

    String urlstring="http://apis.openapi.sk.com/tmap/geo/fullAddrGeo?addressFlag=F00&coordType=WGS84GEO&version=1&format=json&fullAddr=";



    private GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adress_api);
////////////////////////////지도 띄우기
        SupportMapFragment supportMapFragment=(SupportMapFragment)getSupportFragmentManager().
                findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(MainAddressActivity.this);

        StrictMode.enableDefaults();

        Button button=(Button) findViewById(R.id.btn2);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (data1 == null || data2 == null) {
                    Toast.makeText(getApplicationContext(),"출발지와 도착지를 입력해주세요",Toast.LENGTH_LONG);
                }
                else {
                    try {

                        data1 = URLEncoder.encode(data1, "utf-8");
                        URL url1 = new URL(urlstring + data1 + "&appKey=" + key);

                        BufferedReader bf;
                        bf = new BufferedReader(new InputStreamReader(url1.openStream(), "UTF-8"));
                        result = bf.readLine();

                        System.out.println(result);
                        Log.i("연결완료", result);

                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
                        JSONObject coordinateInfo = (JSONObject) jsonObject.get("coordinateInfo");

                        JSONArray coordinate = (JSONArray) coordinateInfo.get("coordinate");
                        JSONObject pos = (JSONObject) coordinate.get(0);

                        double lat = Double.parseDouble((String) pos.get("lat"));
                        double lon = Double.parseDouble((String) pos.get("lon"));

                        System.out.println(lat);

                        LatLng start = new LatLng(lat, lon);

                        MarkerOptions options = new MarkerOptions();
                        options.position(start)
                                .title("출발지")
                                .snippet("한국");
                        map.addMarker(options);

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 15));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        edit_addr1 =  findViewById(R.id.edit_addr1);
        edit_addr2 =  findViewById(R.id.edit_addr2);

        edit_addr1.setFocusable(false);
        edit_addr2.setFocusable(false);

        edit_addr1.setOnClickListener(new View.OnClickListener(){
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
                    mStartForResult.launch(i);

                }else {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_addr2.setOnClickListener(new View.OnClickListener(){
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
    public void onMapReady(GoogleMap googleMap){
        map = googleMap;

        LatLng seoul=new LatLng(37.56,126.97);

        MarkerOptions options=new MarkerOptions();
        options.position(seoul)
                .title("서울")
                .snippet("한국의 수도");
        map.addMarker(options);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,10));
    }

    ActivityResultLauncher<Intent> mStartForResult=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    data1=result.getData().getExtras().getString("data");
                    if(data1!=null){
                        Log.i("text","data: "+data1);
                        edit_addr1.setText(data1);
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> mStartForResult2=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    data2=result.getData().getExtras().getString("data");
                    if(data2!=null){
                        Log.i("text","data: "+data2);
                        edit_addr2.setText(data2);
                    }
                }
            }
    );


}