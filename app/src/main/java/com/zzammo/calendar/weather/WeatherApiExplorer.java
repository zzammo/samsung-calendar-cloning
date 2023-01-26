package com.zzammo.calendar.weather;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class WeatherApiExplorer {
    Context context;
    private final String key = "Iw10j3Acrg3WrDmYl0%2FXMM08AJStZ0qjmSPPaS2Lb7oazhsvcADHN2v%2BkkSotAJWWHqErRHUUK9no0Z8o6hLrA%3D%3D";
    private LocalTime[] times = new LocalTime[8];
    public WeatherApiExplorer(Context context){ this.context = context; }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getWeather() throws IOException {
        // 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300
        for(int i = 0; i < 8; i++) {
            times[i] = LocalTime.of(2 + 3 * i, 10);
        }
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        if(now.isBefore(times[0])) {
            today = today.minusDays(1);
            now = LocalTime.of(23,30);
        }
        String date = ""; String time = "";
        for(int i = 6; i > -1; i--){
            if(now.isAfter(times[i])){
                date = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                time = times[i].minusMinutes(10).format(DateTimeFormatter.ofPattern("HHmm"));
                break;
            }
        }
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + key); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); /*‘21년 6월 28일 발표*/
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(time, "UTF-8")); /*05시 발표(정시단위) */
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("55", "UTF-8")); /*예보지점의 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("127", "UTF-8")); /*예보지점의 Y 좌표값*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        Log.d("WeGlonD", "Weather - Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        HashMap<LocalDate, HashMap<String, Integer>> weather = new HashMap<>();
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(rd);
            //Log.d("WeGlonD", jsonObject.toJSONString());
            JSONObject items = (JSONObject) ((JSONObject)((JSONObject)jsonObject.get("response")).get("body")).get("items");
            //Log.d("WeGlonD", items.toJSONString());
            JSONArray item = (JSONArray) items.get("item");

            int todaymin = 987654321, todaymax = -987654321;
            HashMap<LocalDate, ArrayList<Integer>> sky = new HashMap<>();

            for(Object o : item){
                if( o instanceof JSONObject ) {
                    JSONObject json = (JSONObject) o;
                    //Log.d("WeGlonD", json.toJSONString());
                    LocalDate weatherdate = LocalDate.parse(json.get("fcstDate").toString(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                    String type = (String) json.get("category");

                    if(((String)json.get("fcstTime")).equals("0000")) continue;

                    if(!weather.containsKey(weatherdate)){
                        weather.put(weatherdate, new HashMap<String, Integer>());
                        sky.put(weatherdate, new ArrayList<>());
                    }

                    HashMap<String, Integer> data = weather.get(weatherdate);
                    if(type.equals("SKY")){
                        sky.get(weatherdate).add(Integer.parseInt((String)json.get("fcstValue")));
                    }
                    else if(type.equals("PTY")){
                        if(!data.containsKey("PTY")){
                            data.put("PTY", Integer.parseInt((String)json.get("fcstValue")));
                        }
                        else{
                            data.replace("PTY", Integer.max(data.get("PTY"), Integer.parseInt((String)json.get("fcstValue"))));
                        }
                    }
                    else if(type.equals("TMP") && weatherdate.equals(today)){
                        int temperature = Integer.parseInt((String)json.get("fcstValue"));
                        if(temperature > todaymax) todaymax = temperature;
                        if(temperature < todaymin) todaymin = temperature;
                    }
                    else if(type.equals("TMX")){
                        if(weatherdate.equals(today)){
                            todaymax = (int)Double.parseDouble((String)json.get("fcstValue"));
                        }
                        else{
                            data.put("MAX", (int)Double.parseDouble((String)json.get("fcstValue")));
                        }
                    }
                    else if(type.equals("TMN")){
                        if(weatherdate.equals(today)){
                            todaymin = (int)Double.parseDouble((String)json.get("fcstValue"));
                        }
                        else{
                            data.put("MIN", (int)Double.parseDouble((String)json.get("fcstValue")));
                        }
                    }
                }
            }

            weather.get(today).put("MAX", todaymax);
            weather.get(today).put("MIN", todaymin);
            for (LocalDate key : sky.keySet()) {
                ArrayList<Integer> skies = sky.get(key);
                int sum = 0, size = skies.size();
                for(Integer s : skies){
                    sum += s;
                }
                double avg = (double) sum / size;
                int res;
                if(avg > 3) res = 3;
                else if(avg > 2) res = 2;
                else res = 1;
                //res가 1이면 맑음, 2면 구름, 3이면 흐림
                weather.get(key).put("SKY", res);
            }

            Log.d("WeGlonD", weather.toString());

        }catch(ParseException e){
            e.printStackTrace();
            Log.d("WeGlonD", "Weather Parse Failed");
        }
//        catch (JSONException e) {
//            e.printStackTrace();
//            Log.d("WeGlonD", "Weather get item Failed");
//        }
    }
}
