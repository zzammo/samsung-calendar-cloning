package com.zzammo.calendar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.zzammo.calendar.R;

public class LoginActivity extends AppCompatActivity {

    EditText id_edittext;
    EditText password_edittext;
    Button login_buttton;
    Button make_membership_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        id_edittext=findViewById(R.id.id_edittext);
        password_edittext=findViewById(R.id.password_edittext);
        login_buttton=findViewById(R.id.login_buttton);
        make_membership_btn=findViewById(R.id.make_membership_btn);

        login_buttton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id= String.valueOf(id_edittext.getText());
                String password= String.valueOf(password_edittext.getText());
            }
        });

        make_membership_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
    }
}
