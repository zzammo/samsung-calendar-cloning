package com.zzammo.calendar.auth.email;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.zzammo.calendar.util.AfterTask;

public class Email {
    static final String TAG = "auth.email : Dirtfy";

    /**
     * {@code afterTask(AuthResult)}<br>
     * {@code task} is {@code @NonNull}
     */
    public void singUp(FirebaseAuth mAuth, String email, String password, AfterTask afterTask){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task ->  {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signUp:success");
                        afterTask.ifSuccess(task);

                    } else {
                        Log.w(TAG, "signUp:failure", task.getException());
                        afterTask.ifFail(task);
                    }
                });
    }

    /**
     * {@code afterTask(AuthResult)}<br>
     * {@code task} is {@code @NonNull}
     */
    public void signIn(FirebaseAuth mAuth, String email, String password, AfterTask afterTask){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signIn:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            afterTask.ifSuccess(task);
                        } else {
                            Log.w(TAG, "signIn:failure", task.getException());
                            afterTask.ifFail(task);
                        }
                });
    }

    /**
     * {@code afterTask(AuthResult)}<br>
     * {@code task} is {@code @NonNull}
     */
    public void delete(FirebaseUser user, AfterTask afterTask){
        user.delete()
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "user delete:success");
                            afterTask.ifSuccess(task);
                        }
                        else{
                            Log.d(TAG, "user delete:failure", task.getException());
                            afterTask.ifFail(task);
                        }
                });
    }

    public void updateProfile(FirebaseUser user, String name, AfterTask afterTask){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            afterTask.ifSuccess(task);
                        }
                        else{
                            afterTask.ifFail(task);
                        }
                    }
                });
    }

    public String getName(FirebaseUser user, AfterTask afterTask){
        return user.getDisplayName();
    }
}
