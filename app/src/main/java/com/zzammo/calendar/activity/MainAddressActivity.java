package com.zzammo.calendar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.zzammo.calendar.R;


public class MainAddressActivity extends AppCompatActivity {

    EditText edit_addr1;
    EditText edit_addr2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adress_api);

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

    ActivityResultLauncher<Intent> mStartForResult=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    String data=result.getData().getExtras().getString("data");
                    if(data!=null){
                        Log.i("text","data: "+data);
                        edit_addr1.setText(data);
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> mStartForResult2=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    String data=result.getData().getExtras().getString("data");
                    if(data!=null){
                        Log.i("text","data: "+data);
                        edit_addr2.setText(data);
                    }
                }
            }
    );

}