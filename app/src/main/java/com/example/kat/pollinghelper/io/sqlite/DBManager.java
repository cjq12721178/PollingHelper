package com.example.kat.pollinghelper.io.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

/**
 * Created by liujunhui on 2016/5/4.
 */
public class DBManager {
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> Constructor");
        helper = new DatabaseHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> queryTheCursor");
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME,
                null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> closeDB");
        // 释放数据库资源
        db.close();
        db = null;
    }





/////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * add InspProjectCfg
     *
     * @param inspProjectCfg
     */
    public void addInspProjectCfg(InspProjectCfg inspProjectCfg)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspProjectCfg");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("INSERT INTO " + DatabaseHelper.TABLE_INSP_PROJECT_CFG
                    + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)" );

            Object[] obj = new Object[9];

            obj[0] = inspProjectCfg.getName();
            obj[1] = inspProjectCfg.getDesc();
            obj[2] = inspProjectCfg.getInsp_times();

            for (int i=0; i< inspProjectCfg.getTime_insp().size(); i++){
                obj[i+3] = inspProjectCfg.getTime_insp().get(i);
            }

//            db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_INSP_PROJECT_CFG
//                    + " VALUES(?, ?)", new Object[] { inspProjectCfg.getName(),
//                    inspProjectCfg.getDesc()});

            // 执行创建表的SQL语句
            db.execSQL(sBuffer.toString(), obj);

            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    /**
     * delete old InspProjectCfg
     *
     * @param inspProjectName
     */
    public void deletInspProjectCfg(String inspProjectName)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> delete InspProjectCfg");
        db.delete(DatabaseHelper.TABLE_INSP_PROJECT_CFG, "name = ?",
                new String[] { inspProjectName });
    }
    /**
     * update InspProjectCfg's
     *
     * @param inspProjectCfg
     */
    public void updateInspTitleCf(InspProjectCfg inspProjectCfg)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> updateInspTitleCf");
        ContentValues cv = new ContentValues();
        cv.put("desc", inspProjectCfg.getDesc());
        cv.put("insp_times", inspProjectCfg.getInsp_times());

        int count = inspProjectCfg.getInsp_times();

        if (count > 0)
            cv.put("Insp_time1", inspProjectCfg.getTime_insp().get(0));
        if (count > 1)
            cv.put("Insp_time2", inspProjectCfg.getTime_insp().get(1));
        if (count > 2)
            cv.put("Insp_time3", inspProjectCfg.getTime_insp().get(2));
        if (count > 3)
            cv.put("Insp_time4", inspProjectCfg.getTime_insp().get(3));
        if (count > 4)
            cv.put("Insp_time5", inspProjectCfg.getTime_insp().get(4));


        db.update(DatabaseHelper.TABLE_INSP_PROJECT_CFG, cv, "name = ?",
                new String[] { inspProjectCfg.getName() });
    }



/////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * add InspMissionCfg
     *
     * @param inspMissionCfgs
     */
    public void addInspMissionCfg(List<InspMissionCfg> inspMissionCfgs)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspMissionCfg");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (InspMissionCfg data : inspMissionCfgs) {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_INSP_MISSION_CFG
                        + " VALUES(?, ?, ?, ?)", new Object[]{data.getName(),
                        data.getDesc(),  data.getDeviceImageData(),data.getNamePorjece() });
            }

            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    /**
     * delete old InspMissionCfg
     *
     * @param
     */
    public void deleteInspListCfg(String inspMissionName)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> deleteInspListCfg");
        db.delete(DatabaseHelper.TABLE_INSP_MISSION_CFG, "name = ?",
                new String[] { inspMissionName });
    }
    /**
     * update InspMissionCfg
     *
     * @param inspMissionCfg
     */
    public void updateInspMissionCfg(InspMissionCfg inspMissionCfg)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> updateInspTitleCf");
        ContentValues cv = new ContentValues();
        cv.put("desc", inspMissionCfg.getDesc());
        cv.put("image_path", inspMissionCfg.getDeviceImageData());
        cv.put("name_project", inspMissionCfg.getNamePorjece());
        db.update(DatabaseHelper.TABLE_INSP_MISSION_CFG, cv, "name = ?",
                new String[] { inspMissionCfg.getName() });
    }



/////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * add inspItermCfgs
     *
     * @param inspItermCfgs
     */
    public void addInspItermCfg(List<InspItermCfg> inspItermCfgs)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspListCfg");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (InspItermCfg data : inspItermCfgs) {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_INSP_ITERM_CFG
                        + " VALUES(?, ?, ?, ?, ?, ?)", new Object[]{data.getId(),
                        data.getDesc(), data.getName_sensor_measure(),
                        data.getUp_alarm(), data.getDown_alarm(), data.getNameMission()});
            }

            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    /**
     * delete old InspItermCfg
     *
     * @param inspItermName
     */
    public void deleteInspItermCfg(String inspItermName)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> delete InspItermCfg");
        db.delete(DatabaseHelper.TABLE_INSP_ITERM_CFG, "name = ?",
                new String[] {inspItermName});
    }
    /**
     * update InspItermCfg
     *
     * @param data
     */
    public void updateInspItermCfg(InspItermCfg data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> updateInspItermCfg");
        ContentValues cv = new ContentValues();
        cv.put("desc", data.getDesc());
        cv.put("name_sensor_measure", data.getName_sensor_measure());
        cv.put("up_alarm", data.getUp_alarm());
        cv.put("down_alarm", data.getDown_alarm());
        cv.put("name_mission", data.getNameMission());

        db.update(DatabaseHelper.TABLE_INSP_ITERM_CFG, cv, "name = ?",
                new String[] { data.getId().toString() });
    }


/////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * add SensorCfg
     *
     * @param sensorCfgs
     */
    public void addSensorCfg(List<SensorCfg> sensorCfgs)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add addSensorCfg");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (SensorCfg data : sensorCfgs) {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_SENSOR_CFG
                        + " VALUES(?, ?, ?, ?, ?)", new Object[]{data.getName(),
                        data.getDesc(), data.getAddr_text(),data.getIndex(),
                        data.getSensor_name()});
            }

            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    /**
     * delete old SensorCfg
     *
     * @param data
     */
    public void deleteSensorCfg(SensorCfg data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> deleteSensorCfg");
        db.delete(DatabaseHelper.TABLE_SENSOR_CFG, "name = ?",
                new String[] { data.getName() });
    }
    /**
     * update SensorCfg
     *
     * @param data
     */
    public void updateSensorCfg(SensorCfg data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> update SensorCfg");
        ContentValues cv = new ContentValues();
        cv.put("desc", data.getDesc());
        cv.put("addr_text", data.getAddr_text());
        cv.put("index", data.getIndex());
        cv.put("sensor_name", data.getSensor_name());

        db.update(DatabaseHelper.TABLE_SENSOR_CFG, cv, "name = ?",
                new String[] { data.getName() });
    }
////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * add InspProjectMission
     *
     * @param inspProjectMissions
     */
    public void addInspProjectMission(List<InspProjectMission> inspProjectMissions)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspProjectMission");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (InspProjectMission data : inspProjectMissions) {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_INSP_PROJECT_MISSION
                        + " VALUES(?, ?, ?, ?)", new Object[]{null,data.getName_project(),
                        data.getName_mission(), data.getIndex_Mission()});
            }

            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    /**
     * delete old InspProjectMission
     *
     * @param IdInspProjectMission
     */
    public void deleteInspProjectMission(Integer IdInspProjectMission)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> deleteSensorCfg");
        db.delete(DatabaseHelper.TABLE_INSP_PROJECT_MISSION, "id = ?",
                new String[] { String.valueOf(IdInspProjectMission) });
    }
    /**
     * update InspProjectMission
     *
     * @param data
     */
    public void updateInspProjectMission(InspProjectMission data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> update InspProjectMission");
        ContentValues cv = new ContentValues();
        cv.put("name_project", data.getName_project());
        cv.put("name_mission", data.getName_mission());
        cv.put("index_mission", data.getIndex_Mission());

        db.update(DatabaseHelper.TABLE_INSP_PROJECT_MISSION, cv, "id = ?",
                new String[] { String.valueOf(data.getId()) });
    }
/////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * add InspMissionIterm
     *
     * @param inspMissionIterms
     */
    public void addInspMissionIterm(List<InspMissionIterm> inspMissionIterms)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspMissionIterm");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (InspMissionIterm data : inspMissionIterms) {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_INSP_MISSION_ITERM
                        + " VALUES(?, ?, ?, ?)", new Object[]{null,data.getId_mission(),
                        data.getId_iterm(), data.getIndex_Iterm()});
            }

            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    /**
     * delete old InspMissionIterm
     *
     * @param idInspMissionIterm
     */
    public void deleteInspMissionIterm(Integer idInspMissionIterm)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> delete InspMissionIterm");
        db.delete(DatabaseHelper.TABLE_INSP_MISSION_ITERM, "id = ?",
                new String[] { String.valueOf(idInspMissionIterm) });
    }
    /**
     * update InspMissionIterm
     *
     * @param data
     */
    public void updateInspMissionIterm(InspMissionIterm data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> update InspMissionIterm");
        ContentValues cv = new ContentValues();
        cv.put("id_mission", data.getId_mission());
        cv.put("name_iterm", data.getId_iterm());
        cv.put("index_iterm", data.getIndex_Iterm());

        db.update(DatabaseHelper.TABLE_INSP_MISSION_ITERM, cv, "id = ?",
                new String[] { String.valueOf(data.getId()) });
    }

///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * add InspRecordProject
     *
     * @param inspRecordProjects
     */
    public void addInspRecordProject(List<InspRecordProject> inspRecordProjects)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspRecordProject");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (InspRecordProject data : inspRecordProjects) {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_INSP_RECORD_PROJECT
                        + " VALUES(?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{data.getId(),
                        data.getName_project(), data.getDate().toString(),data.getDesc(),data.getState(),
                        data.getState_process(), data.getResult()});
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    /**
     * delete old InspRecordProject
     *
     * @param idInspRecordProject
     */
    public void deleteInspRecordProject(long idInspRecordProject)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> delete InspRecordProject");
        db.delete(DatabaseHelper.TABLE_INSP_RECORD_PROJECT, "id = ?",
                new String[] { String.valueOf(idInspRecordProject) });
    }
    /**
     * update InspRecordProject
     *
     * @param data
     */
    public void updateInspRecordProject(InspRecordProject data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> update InspRecordProject");
        ContentValues cv = new ContentValues();
        cv.put("name_project", data.getName_project());
        cv.put("date", data.getDate().toString());
        cv.put("desc", data.getDesc());
        cv.put("state", data.getState());
        cv.put("state_progress", data.getState());
        cv.put("result", data.getResult());

        db.update(DatabaseHelper.TABLE_INSP_RECORD_PROJECT, cv, "id = ?",
                new String[] {  String.valueOf(data.getId()) });
    }


/////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * add InspRecordMission
     *
     * @param inspRecordMissions
     */
    public void addInspRecordMission(List<InspRecordMission> inspRecordMissions)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add inspRecordMissions");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (InspRecordMission data : inspRecordMissions) {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_INSP_RECORD_MISSION
                        + " VALUES(?, ?, ?, ?, ?, ?, ?)", new Object[]{data.getId(),
                        data.getName_mission(),data.getId_project_record(),
                        data.getDate().toString(),data.getDesc(), data.getState(),});
            }

            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    /**
     * delete old InspRecordMission
     *
     * @param idInspRecordMission
     */
    public void deleteInspRecordMission(long idInspRecordMission)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> delete InspRecordMission");
        db.delete(DatabaseHelper.TABLE_INSP_RECORD_MISSION, "id = ?",
                new String[] { String.valueOf(idInspRecordMission) });
    }
    /**
     * update InspRecordMission
     *
     * @param data
     */
    public void updateInspRecordMission(InspRecordMission data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> update InspRecordMission");
        ContentValues cv = new ContentValues();
        cv.put("name_mission", data.getName_mission());
        cv.put("id_project_record", data.getId_project_record());
        cv.put("date", data.getDate().toString());
        cv.put("desc", data.getDesc());
        cv.put("state", data.getState());

        db.update(DatabaseHelper.TABLE_INSP_RECORD_MISSION, cv, "id = ?",
                new String[] { String.valueOf(data.getId()) });
    }


/////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * add InspRecordIterm
     *
     * @param inspRecordIterms
     */
    public void addInspRecordIterm(List<InspRecordIterm> inspRecordIterms)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspRecordIterm");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (InspRecordIterm data : inspRecordIterms) {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_INSP_RECORD_ITERM
                        + " VALUES(?, ?, ?, ?)", new Object[]{data.getId(),
                        data.getId_mission_record(), data.getName_measure(),data.getValue()});
            }

            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    /**
     * delete old InspRecordIterm
     *
     * @param idInspRecordIterm
     */
    public void deleteInspRecordIterm(long idInspRecordIterm)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> delete InspRecordIterm");
        db.delete(DatabaseHelper.TABLE_INSP_RECORD_ITERM, "id = ?",
                new String[] { String.valueOf(idInspRecordIterm) });
    }
    /**
     * update InspRecordIterm
     *
     * @param data
     */
    public void updateInspRecordIterm(InspRecordIterm data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> update InspRecordList");
        ContentValues cv = new ContentValues();
         cv.put("id_mission_record", data.getId_mission_record());
        cv.put("name_measure", data.getName_measure());
        cv.put("value", data.getValue());

        db.update(DatabaseHelper.TABLE_INSP_RECORD_ITERM, cv, "id = ?",
                new String[] { String.valueOf(data.getId()) });
    }
}
