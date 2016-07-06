package com.example.kat.pollinghelper.io.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by liujunhui on 2016/5/5.
 * 巡检记录insp_record _mission
 */
public class InspRecordMission extends DBData {
    private Long id; //mission record ID
    private Integer id_mission; //mission ID
    private Long id_project_record; //Project record ID



    private String name_mission;

    private Date date;
    private String desc; //巡检任务记录描述
    private Integer state; //0：great1：good2：bad
    private Integer state_process; //0：建立 1：执行中 2：完成 3：终止

    public InspRecordMission() {
        super(DatabaseHelper.TABLE_INSP_RECORD_MISSION);
    }




    /**
     * add InspRecordMission
     *
     * @param data
     */
    public boolean addDB(InspRecordMission data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspRecordMission");
        // 采用事务处理，确保数据完整性
        boolean result = true;
        db.beginTransaction(); // 开始事务
        try
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("INSERT INTO " + tableName
                    + " VALUES(?, ?, ?, ?, ?, ?, ?)" );

            Object[] obj = new Object[7];

            obj[0] = data.getId();
            obj[1] = data.getId_mission();
            obj[2] = data.getId_project_record();
            obj[3] = data.getDate().getTime();
            obj[4] = data.getDesc();
            obj[5] = data.getState();
            obj[6] = data.getState_process();

            // 执行创建表的SQL语句
            db.execSQL(sBuffer.toString(), obj);
            db.setTransactionSuccessful(); // 设置事务成功完成
        }catch(Exception ex)
        {
            result = false;
            ex.printStackTrace();
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }

        return  result;
    }

    /**
     * update InspRecordMission's
     *
     * @param data
     */
    public void updateDB(InspRecordMission data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> InspRecordProject");
        ContentValues cv = new ContentValues();
        cv.put("id_mission", data.getId_mission());
        cv.put("id_project_record", data.getId_project_record());
        cv.put("desc", data.getDesc());
        cv.put("state", data.getState());
        cv.put("date", data.getDate().getTime());
        cv.put("state_progress", data.getState_process());

        db.update(tableName, cv, "id = ?",
                new String[] { String.valueOf(data.getId()) });
    }


    /**
     * query all , return list
     *
     * @return List<InspRecordMission>
     */
    public List<InspRecordMission> query()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<InspRecordMission> dataList = new ArrayList<InspRecordMission>();
        Cursor c = queryTheCursor();
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspRecordMission data = new InspRecordMission();
            data.setId(c.getLong(c.getColumnIndex("id")));
            data.setId_mission(c.getInt(c.getColumnIndex("id_mission")));
            data.setId_project_record(c.getLong(c.getColumnIndex("id_project_record")));
            data.setDesc(c.getString(c.getColumnIndex("desc")));
            data.setState(c.getInt(c.getColumnIndex("state")));
            data.setState_process(c.getInt(c.getColumnIndex("state_progress")));

            Date date = new Date(c.getLong(c.getColumnIndex("date")));
            data.setDate(date);

            dataList.add(data);
        }
        c.close();
        return dataList;
    }

    public InspRecordMission query(Long id)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        Cursor c = queryTheCursor(id);
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspRecordMission data = new InspRecordMission();
            data.setId(c.getLong(c.getColumnIndex("id")));
            data.setId_mission(c.getInt(c.getColumnIndex("id_mission")));
            data.setId_project_record(c.getLong(c.getColumnIndex("id_project_record")));
            data.setDesc(c.getString(c.getColumnIndex("desc")));
            data.setState(c.getInt(c.getColumnIndex("state")));
            data.setState_process(c.getInt(c.getColumnIndex("state_progress")));
            Date date = new Date(c.getLong(c.getColumnIndex("date")));
            return data;
        }
        c.close();
        return null;
    }


    public List<InspRecordMission> query(Date begin, Date end)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + tableName +" WHERE date > ? and date < ?",
                    new String[] { String.valueOf(begin.getTime()), String.valueOf(end.getTime()) });
            if (c == null)
                return null;

            ArrayList<InspRecordMission> dataList = new ArrayList<InspRecordMission>();
            while (c.moveToNext())
            {
                InspRecordMission data = new InspRecordMission();
                data.setId(c.getLong(c.getColumnIndex("id")));
                data.setId_mission(c.getInt(c.getColumnIndex("id_mission")));
                data.setId_project_record(c.getLong(c.getColumnIndex("id_project_record")));
                data.setDesc(c.getString(c.getColumnIndex("desc")));
                data.setState(c.getInt(c.getColumnIndex("state")));
                data.setDate(new Date(c.getLong(c.getColumnIndex("date"))));
                data.setState_process(c.getInt(c.getColumnIndex("state_progress")));
                dataList.add(data);
            }
            c.close();
            return dataList;
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public List<InspRecordMission> queryByIdRecordProject(Long id)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + tableName +" WHERE id_project_record = ?",
                    new String[] { String.valueOf(id) });
            if (c == null)
                return null;

            ArrayList<InspRecordMission> dataList = new ArrayList<InspRecordMission>();
            while (c.moveToNext())
            {
                InspRecordMission data = new InspRecordMission();
                data.setId(c.getLong(c.getColumnIndex("id")));
                data.setId_mission(c.getInt(c.getColumnIndex("id_mission")));
                data.setId_project_record(c.getLong(c.getColumnIndex("id_project_record")));
                data.setDesc(c.getString(c.getColumnIndex("desc")));
                data.setState(c.getInt(c.getColumnIndex("state")));
                data.setDate(new Date(c.getLong(c.getColumnIndex("date"))));
                data.setState_process(c.getInt(c.getColumnIndex("state_progress")));

                dataList.add(data);
            }
            c.close();
            return dataList;
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_project_record() {
        return id_project_record;
    }

    public void setId_project_record(Long id_project_record) {
        this.id_project_record = id_project_record;
    }
    public String getName_mission() {
        return name_mission;
    }

    public void setName_mission(String name_mission) {
        this.name_mission = name_mission;
    }
    public Integer getId_mission() {
        return id_mission;
    }

    public void setId_mission(Integer id_mission) {
        this.id_mission = id_mission;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getState_process() {
        return state_process;
    }

    public void setState_process(Integer state_process) {
        this.state_process = state_process;
    }
}
