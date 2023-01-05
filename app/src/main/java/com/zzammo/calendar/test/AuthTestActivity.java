package com.zzammo.calendar.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.zzammo.calendar.R;
import com.zzammo.calendar.auth.Auth;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;

public class AuthTestActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_test);

        context = this;

        EditText email_et = findViewById(R.id.activity_auth_test_emailEt);
        EditText password_et = findViewById(R.id.activity_auth_test_passwordEt);

        Button signUp_btn = findViewById(R.id.activity_auth_test_signUpBtn);
        Button signIn_btn = findViewById(R.id.activity_auth_test_signInBtn);
        Button signOut_btn = findViewById(R.id.activity_auth_test_signOutBtn);
        Button delete_btn = findViewById(R.id.activity_auth_test_deleteBtn);

        signUp_btn.setOnClickListener(view -> {
            String email = email_et.getText().toString();
            String password = password_et.getText().toString();
            new Auth().signUp(Auth.EMAIL, email, password, new JT(context, "가입"));
        });
        signIn_btn.setOnClickListener(view -> {
            String email = email_et.getText().toString();
            String password = password_et.getText().toString();
            new Auth().logIn(Auth.EMAIL, email, password, new JT(context, "로그인"));
        });
        signOut_btn.setOnClickListener(view -> {
            if(new Auth().logOn())
                new Auth().logOut();
        });
        delete_btn.setOnClickListener(view -> {
            if(new Auth().logOn())
                new Auth().delete(Auth.EMAIL, new JT(context, "삭제"));
        });

    }
}