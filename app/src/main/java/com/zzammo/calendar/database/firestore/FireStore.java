package com.zzammo.calendar.database.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.AfterTask;

import java.util.ArrayList;

public class FireStore {
    static final String TAG = "db.firestore : Dirtfy";

    static final String COL_USERS = "Users";
    static final String DOC_SCHEDULES = "Schedules";



    /**
     *
     * {@code ifSuccess(DocumentReference)}<br>
     * {@code ifFail(Exception)}<br>
     * if {@code user == null} then {@code Exception} is null<br>
     * else {@code Exception} is {@code @NonNull}
     */
    public void insert(FirebaseFirestore db, Schedule schedule, AfterTask afterTask){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            afterTask.ifFail(null);
            return;
        }

        db.collection(COL_USERS).document(DOC_SCHEDULES)
            .collection(user.getUid()).add(schedule)
            .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "insert:success");
                    schedule.id = documentReference.getId();
                    afterTask.ifSuccess(documentReference);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "insert:failure", e);
                    afterTask.ifFail(e);
                });
    }

    /**
     *
     * {@code afterTask(Task[QuerySnapshot])}<br>
     * if {@code user == null} then {@code Task[QuerySnapshot]} is null<br>
     * else {@code Task[QuerySnapshot]} is {@code @NonNull}
     */
    public void loadAllScheduleDuring(FirebaseFirestore db, Long begin, Long end,
                                      ArrayList<Schedule> schedules , AfterTask afterTask){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            afterTask.ifFail(null);
            return;
        }

        db.collection(COL_USERS).document(DOC_SCHEDULES)
                .collection(user.getUid())
                .whereLessThan("timeMillis", end).whereGreaterThan("timeMillis", begin)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Schedule ts = (Schedule) document.getData();
                            ts.id = document.getId();
                            schedules.add(ts);
                        }
                        Log.d(TAG, "loadAllScheduleDuring:success");
                        afterTask.ifSuccess(task);
                    }
                    else{
                        Log.d(TAG, "loadAllScheduleDuring:failure", task.getException());
                        afterTask.ifFail(task);
                    }
                });
    }

    /**
     *
     * {@code afterTask(Void)}<br>
     * if {@code user == null} then {@code Void} is null<br>
     * else {@code Void} is {@code @NonNull}
     */
    public void update(FirebaseFirestore db, Schedule schedule, AfterTask afterTask){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            afterTask.ifFail(null);
            return;
        }

        db.collection(COL_USERS).document(DOC_SCHEDULES)
                .collection(user.getUid())
                .document(schedule.id)
                .set(schedule).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        afterTask.ifSuccess(task);
                    }
                    else{
                        afterTask.ifFail(task);
                    }
                });
    }

    /**
     *
     * {@code ifSuccess(Void)}<br>
     * {@code ifFail(Exception)}<br>
     * if {@code user == null} then {@code Exception} is null<br>
     * else {@code Exception} is {@code @NonNull}
     */
    public void delete(FirebaseFirestore db, Schedule schedule, AfterTask afterTask){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            afterTask.ifFail(null);
            return;
        }

        db.collection(COL_USERS).document(DOC_SCHEDULES)
                .collection(user.getUid())
                .document(schedule.id)
                .delete().addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "delete:success");
                        afterTask.ifSuccess(aVoid);
                })
                .addOnFailureListener(e -> {
                        Log.w(TAG, "delete:failure", e);
                        afterTask.ifFail(e);
                });

    }
}
