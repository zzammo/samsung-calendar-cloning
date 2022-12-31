package com.zzammo.calendar.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.zzammo.calendar.auth.email.Email;
import com.zzammo.calendar.util.AfterTask;

public class Auth {
    final String TAG = "auth.Auth : Dirtfy";

    public final static int EMAIL = 1;
    public final static int GOOGLE = 2;

    FirebaseAuth mAuth = null;

    public Auth() {
        if(mAuth == null){
            mAuth = FirebaseAuth.getInstance();
        }
    }

    /**
     *
     * @param type 계정 타입 1:EMAIL
     * @param afterTask type 에 따라 result 의 클래스가 다를 수 있다는 점<br>
     *                  type 1 : <br>
     *                  {@code ifSuccess(AuthResult result)}<br>
     *                  {@code ifFail(AuthResult result)}
     *
     * @see com.zzammo.calendar.util.AfterTask
     * @see com.google.firebase.auth.AuthResult
     */
    public void signUp(int type, String email, String password, AfterTask afterTask){
        switch (type){
            case EMAIL:
                new Email().singUp(mAuth, email, password, afterTask);
        }
    }

    /**
     *
     * @param type 계정 타입 1:EMAIL
     * @param afterTask type 에 따라 result 의 클래스가 다를 수 있다는 점<br>
     *                  type 1 : <br>
     *                  {@code ifSuccess(AuthResult result)}<br>
     *                  {@code ifFail(AuthResult result)}
     *
     * @see com.zzammo.calendar.util.AfterTask
     * @see com.google.firebase.auth.AuthResult
     */
    public void logIn(int type, String email, String password, AfterTask afterTask){
        switch (type){
            case EMAIL:
                new Email().signIn(mAuth, email, password, afterTask);
        }
    }

    /**
     *
     * @return 로그인 중이면 {@code true} 아니면 {@code false}
     */
    public boolean logOn(){
        return mAuth.getCurrentUser() != null;
    }

    public void logOut(){
        mAuth.signOut();
    }

    /**
     *
     * @param type 계정 타입 1:EMAIL
     * @param afterTask type 에 따라 result 의 클래스가 다를 수 있다는 점<br>
     *                  type 1 : <br>
     *                  {@code ifSuccess(AuthResult result)}<br>
     *                  {@code ifFail(AuthResult result)}
     *
     * @see com.zzammo.calendar.util.AfterTask
     * @see com.google.firebase.auth.AuthResult
     */
    public void delete(int type, AfterTask afterTask){
        switch (type){
            case EMAIL:
                new Email().delete(mAuth.getCurrentUser(), afterTask);
        }
    }
}
