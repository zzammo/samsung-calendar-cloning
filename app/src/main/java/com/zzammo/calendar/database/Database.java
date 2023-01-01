package com.zzammo.calendar.database;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.zzammo.calendar.database.firestore.FireStore;
import com.zzammo.calendar.database.room.ScheduleDatabase;
import com.zzammo.calendar.util.AfterTask;

import java.util.ArrayList;
import java.util.Arrays;

public class Database {
    final String TAG = "database.Database : Dirtfy";

    public final static int LOCAL = 1;
    public final static int SERVER = 2;

    ScheduleDatabase localDB;
    FirebaseFirestore serverDB = null;

    public Database(Context context) {
        localDB = ScheduleDatabase.getInstance(context);
        if(serverDB == null){
            serverDB = FirebaseFirestore.getInstance();
        }
    }

    /**
     *
     * @param type DB 타입 1:LOCAL, 2:SERVER
     * @param afterTask type 에 따라 result 의 클래스가 다를 수 있다는 점<br>
     *                  type 1 : <br>
     *                  {@code None} <br>
     *                  type 2 : <br>
     *                  {@code ifSuccess(DocumentReference result)}<br>
     *                  {@code ifFail(Exception result)}
     *
     * @see com.zzammo.calendar.util.AfterTask
     * @see com.google.firebase.firestore.DocumentReference
     * @see Exception
     */
    public void insert(int type, Schedule schedule, AfterTask afterTask){
        switch (type){
            case LOCAL:
                localDB.scheduleDao().insertAll(schedule);
                break;
            case SERVER:
                new FireStore().insert(serverDB, schedule, afterTask);

        }
    }

    /**
     *
     * @param type DB 타입 1:LOCAL, 2:SERVER
     * @param afterTask type 에 따라 result 의 클래스가 다를 수 있다는 점<br>
     *                  type 1 : <br>
     *                  {@code None} <br>
     *                  type 2 : <br>
     *                  {@code ifSuccess(Void result)}<br>
     *                  {@code ifFail(Exception result)}
     *
     * @see com.zzammo.calendar.util.AfterTask
     * @see Void
     * @see Exception
     */
    public void delete(int type, Schedule schedule, AfterTask afterTask){
        switch (type){
            case LOCAL:
                localDB.scheduleDao().delete(schedule);
                break;
            case SERVER:
                new FireStore().delete(serverDB, schedule, afterTask);

        }
    }

    /**
     *
     * @param type DB 타입 1:LOCAL, 2:SERVER
     * @param afterTask type 에 따라 result 의 클래스가 다를 수 있다는 점<br>
     *                  type 1 : <br>
     *                  {@code None} <br>
     *                  type 2 : <br>
     *                  {@code ifSuccess(Void result)}<br>
     *                  {@code ifFail(Void result)}
     *
     * @see com.zzammo.calendar.util.AfterTask
     * @see Void
     */
    public void update(int type, Schedule schedule, AfterTask afterTask){
        switch (type){
            case LOCAL:
                localDB.scheduleDao().updateSchedules(schedule);
                break;
            case SERVER:
                new FireStore().update(serverDB, schedule, afterTask);
        }
    }


    /**
     *
     * @param type DB 타입 1:LOCAL, 2:SERVER
     * @param afterTask type 에 따라 result 의 클래스가 다를 수 있다는 점<br>
     *                  type 1 : <br>
     *                  {@code None} <br>
     *                  type 2 : <br>
     *                  {@code ifSuccess(Task[QuerySnapshot] result)}<br>
     *                  {@code ifFail(Task[QuerySnapshot] result)}
     *
     * @see com.zzammo.calendar.util.AfterTask
     * @see com.google.firebase.firestore.DocumentReference
     * @see Exception
     */
    public void loadAllScheduleDuring(int type, Long begin, Long end, ArrayList<Schedule> schedules , AfterTask afterTask){
        switch (type){
            case LOCAL:
                schedules.addAll(Arrays.asList(localDB.scheduleDao().loadAllScheduleDuring(begin, end)));
                break;
            case SERVER:
                new FireStore().loadAllScheduleDuring(serverDB, begin, end, schedules, afterTask);
        }
    }
}
