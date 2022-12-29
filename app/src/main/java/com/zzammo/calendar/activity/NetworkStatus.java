package com.zzammo.calendar.activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatus{
    public static final int TYPE_WIFI=1;
    public static final int TYPE_MOBILE=2;
    public static final int TYPE_NOT_CONNECTED=3;

    public static int getConnectivityStatus(Context context){
        ////해당 context의 서비스를 사용하기 위해서 context 객체를 받는다.
        ConnectivityManager manager=(ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        if(networkInfo!=null){
            int type=networkInfo.getType();
            if(type==ConnectivityManager.TYPE_MOBILE){
                return TYPE_MOBILE;
            }
            else if(type==ConnectivityManager.TYPE_WIFI){
                return TYPE_WIFI;
            }
        }
        return TYPE_NOT_CONNECTED;
    }
}