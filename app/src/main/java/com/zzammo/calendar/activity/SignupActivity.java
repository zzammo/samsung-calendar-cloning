package com.zzammo.calendar.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.zzammo.calendar.R;
import com.zzammo.calendar.auth.Auth;
import com.zzammo.calendar.util.AfterTask;

public class SignupActivity extends AppCompatActivity {

    EditText name_signup_edittext;
    EditText email_signup_edittext;
    EditText id_signup_edittext;
    EditText password_signup_edittext;
    Button signup_buttton;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        context = this;

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

                if (email.equals("") || password.equals("") || name.equals("")){
                    Toast.makeText(context, "빈 칸은 안돼요~", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Auth().signUp(Auth.EMAIL, email, password, new AfterTask() {
                    @Override
                    public void ifSuccess(Object result) {
                        new Auth().updateProfile(Auth.EMAIL, name, new AfterTask() {
                            @Override
                            public void ifSuccess(Object result) {
                                finish();
                            }

                            @Override
                            public void ifFail(Object result) {
                                Toast.makeText(context, "프로필 등록 실패...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void ifFail(Object result) {
                        Toast.makeText(context, "회원가입 실패...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
