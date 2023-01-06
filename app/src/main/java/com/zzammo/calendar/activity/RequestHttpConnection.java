package com.zzammo.calendar.activity;

import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RequestHttpConnection {
    private int state;
    public RequestHttpConnection(int state){
        this.state=state;
    }
    public String request(String _url, ContentValues _params){

        //HttpURLConnection 참조변수
        HttpURLConnection conn=null;
        StringBuffer sbParams=new StringBuffer();

        if(_params==null){
            sbParams.append("");
        }
        else{
            boolean isAnd=false;
            String key = "";
            String value="";

            for(Map.Entry<String,Object> parameter:_params.valueSet()) {
                key = parameter.getKey();
                value = parameter.getValue().toString();

                if (isAnd)
                    sbParams.append("&");

                sbParams.append(key).append("=").append(value);

                if (!isAnd)
                    if (_params.size() >= 2)
                        isAnd = true;
            }
        }
        Log.d("Dirtfy", "?");


        try {
            URL url=new URL(_url);
            conn=(HttpURLConnection) url.openConnection();

            // TimeOut 시간 (서버 접속시 연결 시간)
            conn.setConnectTimeout(1000);

            // TimeOut 시간 (Read시 연결 시간)
            conn.setReadTimeout(1000);

            if(this.state==0) {
                Log.d("여기까지","오냐?");
                conn.setRequestMethod("POST");
                //conn.setRequestProperty("accept", "application/json");
                conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("appKey", "l7xxb76eb9ee907444a8b8098322fa488048");
            }
            //x-www-form-urlencoded
            else {
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Host", "apis.openapi.sk.com");
                conn.setRequestProperty("appKey", "l7xxb76eb9ee907444a8b8098322fa488048");
                conn.setRequestProperty("Accept-Language", "ko");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }

            Log.d("여기까지","오냐?2");

            String strParams=sbParams.toString();
            OutputStream os=conn.getOutputStream();
            os.write(strParams.getBytes("UTF-8"));
            os.flush();
            os.close();

            Log.d("여기까지","오냐?3");

            System.out.println(conn.getResponseCode());
            if(conn.getResponseCode() !=HttpURLConnection.HTTP_OK) {
                BufferedReader br = null;
                if (100 <= conn.getResponseCode() && conn.getResponseCode() <= 399) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                String line = "";
                String page = "";

                while ((line = br.readLine()) != null){
                    page += line;
                }
                Log.d("httpsss",page);
                return null;
            }

            BufferedReader reader=new BufferedReader
                    (new InputStreamReader(conn.getInputStream(),"UTF-8"));


            String line=reader.readLine();
            String page = "";

//            while ((line = reader.readLine()) != null){
//                page += line;
//            }
            return line;


        } catch (MalformedURLException e) {
            Log.e("ㅜㅜ", "연결실패...");
            e.printStackTrace();
        } catch (IOException e) {
        Log.e("ㅜㅜ","연결실패..");
        e.printStackTrace();
    }

        return null;
    }
}
