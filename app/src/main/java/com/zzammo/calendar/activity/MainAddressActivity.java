package com.zzammo.calendar.activity;

import static java.lang.Math.log;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
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
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import android.location.Location;

public class MainAddressActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText edit_addr1;
    EditText edit_addr2;
    TextView textView15;
    private RadioGroup radioGroup;
    private int state=4; // 0 -> bus, 1 -> walk, 2 -> car

    String data1=null,data2=null;

    // 인증키 (개인이 받아와야함)
    String key = "l7xxb76eb9ee907444a8b8098322fa488048";

    // 파싱한 데이터를 저장할 변수
    String result1 = "";
    String result2 = "";

    String giourl ="http://apis.openapi.sk.com/tmap/geo/fullAddrGeo?addressFlag=F02&coordType=WGS84GEO&version=1&format=json&fullAddr=";


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
                Log.d("Onclick","nono");
                if (data1 == null || data2 == null) {
                    Toast.makeText(getApplicationContext(),"출발지와 도착지를 입력해주세요",Toast.LENGTH_SHORT).show();
                }
                else {
                    try {

                        String data1_ = URLEncoder.encode(data1, "utf-8");
                        URL url1 = new URL(giourl + data1_ + "&appKey=" + key);

                        String data2_ =URLEncoder.encode(data2,"utf-8");
                        URL url2=new URL(giourl +data2_+"&appKey=" + key);

                        BufferedReader bf;
                        bf = new BufferedReader(new InputStreamReader(url1.openStream(), "UTF-8"));
                        result1 = bf.readLine();
                        bf = new BufferedReader(new InputStreamReader(url2.openStream(), "UTF-8"));
                        result2 = bf.readLine();

                        Log.i("연결완료1", result1);
                        Log.i("연결완료2", result2);

                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(result1);
                        JSONObject coordinateInfo = (JSONObject) jsonObject.get("coordinateInfo");

                        JSONArray coordinate = (JSONArray) coordinateInfo.get("coordinate");
                        JSONObject pos = (JSONObject) coordinate.get(0);

                        double lat1 = Double.parseDouble((String) pos.get("newLat"));
                        double lon1 = Double.parseDouble((String) pos.get("newLon"));
                        LatLng start = new LatLng(lat1, lon1);

                        jsonObject = (JSONObject) jsonParser.parse(result2);
                        coordinateInfo = (JSONObject) jsonObject.get("coordinateInfo");

                        coordinate = (JSONArray) coordinateInfo.get("coordinate");
                        pos = (JSONObject) coordinate.get(0);

                        double lat2 = Double.parseDouble((String) pos.get("newLat"));
                        double lon2 = Double.parseDouble((String) pos.get("newLon"));
                        LatLng goal = new LatLng(lat2, lon2);

                        //////////////2지점 거리랑 중간위치계산
                        LatLng mid = new LatLng((lat2+lat1)/2, (lon1+lon2)/2);
                        Location location1=new Location("start");
                        Location location2=new Location("goal");

                        location1.setLatitude(lat1);
                        location1.setLongitude(lon1);

                        location2.setLatitude(lat2);
                        location2.setLongitude(lon2);

                        double distance=location1.distanceTo(location2);
                        double scale=21-log(distance);


                        MarkerOptions options1 = new MarkerOptions();
                        options1.position(start)
                                .title("출발지")
                                .snippet("한국");
                        map.addMarker(options1);

                        MarkerOptions options2 = new MarkerOptions();
                        options2.position(goal)
                                .title("도착지")
                                .snippet("한국");
                        map.addMarker(options2);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mid, (float)scale));




                        Log.i("PSt","connected");
                        if(state==0){
                            ContentValues contentValues0 =new ContentValues();
                            contentValues0.put("startX",String.valueOf(lon1));
                            contentValues0.put("startY",String.valueOf(lat1));
                            contentValues0.put("endX",String.valueOf(lon2));
                            contentValues0.put("endY",String.valueOf(lat2));
                            contentValues0.put("format","json");
                            contentValues0.put("count",10);
                            NetworkTask networkTask0=new NetworkTask("http://apis.openapi.sk.com/transit/routes",contentValues0);
                            networkTask0.execute();
                        }
                        else if(state==1){
                            ContentValues contentValues1 =new ContentValues();
                            contentValues1.put("startX",String.valueOf(lon1));
                            contentValues1.put("startY",String.valueOf(lat1));
                            contentValues1.put("endX",String.valueOf(lon2));
                            contentValues1.put("endY",String.valueOf(lat2));
                            contentValues1.put("startName",data1_);
                            contentValues1.put("endName",data2_);
                            NetworkTask networkTask1=new NetworkTask("http://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=json",contentValues1);
                            networkTask1.execute();
                        }
                        else if(state==2){
                            ContentValues contentValues2 =new ContentValues();
                            contentValues2.put("startX",String.valueOf(lon1));
                            contentValues2.put("startY",String.valueOf(lat1));
                            contentValues2.put("endX",String.valueOf(lon2));
                            contentValues2.put("endY",String.valueOf(lat2));
                            NetworkTask networkTask2=new NetworkTask("http://apis.openapi.sk.com/tmap/routes?version=1&format=json",contentValues2);
                            networkTask2.execute();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "교통수단을 선택해 주세요", Toast.LENGTH_SHORT).show();
                        }

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

        radioGroup = (RadioGroup) findViewById(R.id.radio);
        radioGroup.setOnCheckedChangeListener(radioButtonChangeListner);

    }

    RadioGroup.OnCheckedChangeListener radioButtonChangeListner=new RadioGroup.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i==R.id.bus){
                state=0;
                Toast.makeText(getApplicationContext(), "버스 눌렸습니다.", Toast.LENGTH_SHORT).show();
            }
            else if(i==R.id.walk){
                state=1;
                Toast.makeText(getApplicationContext(), "월크 눌렸습니다.", Toast.LENGTH_SHORT).show();
            }
            else if(i==R.id.car) {
                state=2;
                Toast.makeText(getApplicationContext(), "자동차 눌렸습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

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

    public class NetworkTask extends AsyncTask<Void,Void,String>{

        private String url;
        private ContentValues values;

        public NetworkTask(String url,ContentValues values){
            this.url=url;
            this.values=values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result="45";
            RequestHttpConnection requestHttpConnection=new RequestHttpConnection(state);
            result=requestHttpConnection.request(url,values);
            Log.e("과연",result);
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

            Log.d("파싱준비", jsonObject.toString());

            String time;
            if(state==0){
                JSONObject plan = (JSONObject) jsonObject.get("plan");
                JSONArray itineraries = (JSONArray) plan.get("itineraries");
                JSONObject totalTime = (JSONObject) itineraries.get(2);

                time = (String) totalTime.toString();
            }else {

                JSONArray features = (JSONArray) jsonObject.get("features");
                JSONObject sp_features = (JSONObject) features.get(0);

                JSONObject properties = (JSONObject) sp_features.get("properties");
                Log.d("properties", (String) properties.toString());
                time = (String) properties.get("totalTime").toString();
                Log.d("tttttime", time);
            }

            textView15=(TextView)findViewById(R.id.textView15);
            Log.e("초초",time);
            textView15.setText(time+"초 소요");


        }
    }
}