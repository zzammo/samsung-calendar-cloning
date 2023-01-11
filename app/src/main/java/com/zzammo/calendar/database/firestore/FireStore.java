package com.zzammo.calendar.database.firestore;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
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
    public void insert(FirebaseFirestore db, Schedule schedule, AfterTask... afterTasks){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            for (AfterTask at : afterTasks){
                at.ifFail(null);
            }
            return;
        }

        db.collection(COL_USERS).document(DOC_SCHEDULES)
            .collection(user.getUid()).add(schedule)
            .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "insert:success");
                    schedule.serverId = documentReference.getId();
                    for (AfterTask at : afterTasks){
                        at.ifSuccess(documentReference);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "insert:failure", e);
                    for (AfterTask at : afterTasks){
                        at.ifFail(e);
                    }
                });
    }

    /**
     *
     * {@code afterTask(Task[QuerySnapshot])}<br>
     * if {@code user == null} then {@code Task[QuerySnapshot]} is null<br>
     * else {@code Task[QuerySnapshot]} is {@code @NonNull}
     */
    public void loadAllScheduleDuring(FirebaseFirestore db, Long begin, Long end,
                                      ArrayList<Schedule> schedules , AfterTask... afterTasks){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            for (AfterTask at : afterTasks){
                at.ifFail(null);
            }
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
                            ts.serverId = document.getId();
                            schedules.add(ts);
                        }
                        Log.d(TAG, "loadAllScheduleDuring:success");
                        for (AfterTask at : afterTasks){
                            at.ifSuccess(task);
                        }
                    }
                    else{
                        Log.d(TAG, "loadAllScheduleDuring:failure", task.getException());
                        for (AfterTask at : afterTasks){
                            at.ifFail(task);
                        }
                    }
                });
    }

    /**
     *
     * {@code afterTask(Void)}<br>
     * if {@code user == null} then {@code Void} is null<br>
     * else {@code Void} is {@code @NonNull}
     */
    public void update(FirebaseFirestore db, Schedule schedule, AfterTask... afterTasks){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            for (AfterTask at : afterTasks){
                at.ifFail(null);
            }
            return;
        }

        db.collection(COL_USERS).document(DOC_SCHEDULES)
                .collection(user.getUid())
                .document(schedule.serverId)
                .set(schedule).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (AfterTask at : afterTasks){
                            at.ifSuccess(task);
                        }
                    }
                    else{
                        for (AfterTask at : afterTasks){
                            at.ifFail(task);
                        }
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
    public void delete(FirebaseFirestore db, Schedule schedule, AfterTask... afterTasks){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            for (AfterTask at : afterTasks){
                at.ifFail(null);
            }
            return;
        }

        db.collection(COL_USERS).document(DOC_SCHEDULES)
                .collection(user.getUid())
                .document(schedule.serverId)
                .delete().addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "delete:success");
                    for (AfterTask at : afterTasks){
                        at.ifSuccess(aVoid);
                    }
                })
                .addOnFailureListener(e -> {
                        Log.w(TAG, "delete:failure", e);
                    for (AfterTask at : afterTasks){
                        at.ifFail(e);
                    }
                });

    }
}
