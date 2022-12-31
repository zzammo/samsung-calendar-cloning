package com.zzammo.calendar.database.firestore;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.util.AfterTask;

import java.util.ArrayList;
import java.util.Map;

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
                            schedules.add((Schedule) document.getData());
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

}
