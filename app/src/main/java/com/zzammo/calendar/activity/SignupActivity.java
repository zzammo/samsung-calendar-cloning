package com.zzammo.calendar.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.zzammo.calendar.R;

public class SignupActivity extends AppCompatActivity {

    EditText name_signup_edittext;
    EditText email_signup_edittext;
    EditText id_signup_edittext;
    EditText password_signup_edittext;
    Button signup_buttton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        name_signup_edittext=findViewById(R.id.name_signup_edittext);
        email_signup_edittext=findViewById(R.id.email_signup_edittext);
        id_signup_edittext=findViewById(R.id.id_signup_edittext);
        password_signup_edittext=findViewById(R.id.password_signup_edittext);
        signup_buttton=findViewById(R.id.signup_buttton);


        signup_buttton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name= String.valueOf(name_signup_edittext.getText());
                String email= String.valueOf(email_signup_edittext.getText());
                String id= String.valueOf(id_signup_edittext.getText());
                String password= String.valueOf(password_signup_edittext.getText());
            }
        });
    }
}
