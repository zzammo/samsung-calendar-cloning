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

        try {
            URL url=new URL(_url);
            conn=(HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Host","apis.openapi.sk.com");
            conn.setRequestProperty("appKey","l7xxb76eb9ee907444a8b8098322fa488048");
            conn.setRequestProperty("Accept-Language","ko");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            String strParams=sbParams.toString();
            OutputStream os=conn.getOutputStream();
            os.write(strParams.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            if(conn.getResponseCode() !=HttpURLConnection.HTTP_OK)
                return null;

            BufferedReader reader=new BufferedReader
                    (new InputStreamReader(conn.getInputStream(),"UTF-8"));


            String line;
            String page = "";

            while ((line = reader.readLine()) != null){
                page += line;
            }
            Log.e("ㅜㅜ",page);
            return page;


        } catch (MalformedURLException e) {
            Log.e("ㅜㅜ","연결실패...");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("ㅜㅜ","연결실패..");
            e.printStackTrace();
        }
        return null;
    }
}
