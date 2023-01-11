package com.zzammo.calendar.database;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zzammo.calendar.database.firestore.FireStore;
import com.zzammo.calendar.database.room.HolidayDatabase;
import com.zzammo.calendar.database.room.MetadataDatabase;
import com.zzammo.calendar.database.room.ScheduleDatabase;
import com.zzammo.calendar.util.AfterTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Database {
    final String TAG = "database.DB : Dirtfy";

    public final static int LOCAL = 1;
    public final static int SERVER = 2;

    ScheduleDatabase localDB;
    public HolidayDatabase HoliLocalDB;
    MetadataDatabase MetaLocalDB;
    FirebaseFirestore serverDB = null;

    FireStore fireStoreMethod = null;

    class quiet implements AfterTask{
        @Override
        public void ifSuccess(Object result) {

        }

        @Override
        public void ifFail(Object result) {

        }
    }

    public Database(Context context) {
        localDB = ScheduleDatabase.getInstance(context);
        HoliLocalDB = HolidayDatabase.getInstance(context);
        MetaLocalDB = MetadataDatabase.getInstance(context);
        if(serverDB == null){
            serverDB = FirebaseFirestore.getInstance();
        }
        if (fireStoreMethod == null){
            fireStoreMethod = new FireStore();
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
    public void insert(Schedule schedule, AfterTask afterTask){
        fireStoreMethod.insert(serverDB, schedule, new AfterTask() {
            @Override
            public void ifSuccess(Object result) {
                localDB.scheduleDao().insertAll(schedule);
            }

            @Override
            public void ifFail(Object result) {
                localDB.scheduleDao().insertAll(schedule);
            }
        }, afterTask);
    }
    public void insert(Schedule schedule){
        fireStoreMethod.insert(serverDB, schedule, new AfterTask() {
            @Override
            public void ifSuccess(Object result) {
                localDB.scheduleDao().insertAll(schedule);
            }

            @Override
            public void ifFail(Object result) {
                localDB.scheduleDao().insertAll(schedule);
            }
        });
    }
    public void insert(Holiday holiday){
        HoliLocalDB.holidayDao().insertAll(holiday);
    }
    public void insert(Metadata metadata){
        MetaLocalDB.metadataDao().insertAll(metadata);
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
    public void delete(Schedule schedule, AfterTask afterTask){
        fireStoreMethod.delete(serverDB, schedule, afterTask);
        localDB.scheduleDao().delete(schedule);
    }
    public void delete(Schedule schedule){
        fireStoreMethod.delete(serverDB, schedule);
        localDB.scheduleDao().delete(schedule);
    }
    public void delete(Holiday holiday){
        HoliLocalDB.holidayDao().delete(holiday);
    }
    public void delete(Metadata metadata){
        MetaLocalDB.metadataDao().delete(metadata);
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
    public void update(Schedule schedule, AfterTask afterTask){
        fireStoreMethod.update(serverDB, schedule, afterTask);
        localDB.scheduleDao().updateSchedules(schedule);
    }
    public void update(Schedule schedule){
        fireStoreMethod.update(serverDB, schedule);
        localDB.scheduleDao().updateSchedules(schedule);
    }
    public void update(Holiday holiday){
        HoliLocalDB.holidayDao().updateHolidays(holiday);
    }
    public void update(Metadata metadata) { MetaLocalDB.metadataDao().updateMetadata(metadata); }

    public void sync(){
        List<Schedule> notSynced = localDB.scheduleDao().getNotSynced();
        Log.d(TAG, notSynced.size()+"");
        for (Schedule s : notSynced){
            fireStoreMethod.insert(serverDB, s, new AfterTask() {
                @Override
                public void ifSuccess(Object result) {
                    localDB.scheduleDao().updateSchedules(s);
                }

                @Override
                public void ifFail(Object result) {

                }
            });
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


    public Metadata getMetadata(String keyword){
        return MetaLocalDB.metadataDao().getMetadata(keyword);
    }
}
