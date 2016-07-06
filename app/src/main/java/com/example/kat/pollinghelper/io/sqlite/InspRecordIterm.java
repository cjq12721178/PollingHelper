package com.example.kat.pollinghelper.io.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujunhui on 2016/5/5.
 */
public class InspRecordIterm  extends DBData {
    private Long id; //Project record iterm ID
    private Long id_mission_record; //Project mission record ID
    private String name_measure; //传感器监测量名称
    private Double value; //实际数值

    public InspRecordIterm() {
        super(DatabaseHelper.TABLE_INSP_RECORD_ITERM);
    }



    /**
     * add InspRecordIterm
     *
     * @param data
     */
    public boolean addDB(InspRecordIterm data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspRecordIterm");
        // 采用事务处理，确保数据完整性
        boolean result = true;
        db.beginTransaction(); // 开始事务
        try
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("INSERT INTO " + tableName
                    + " VALUES(?, ?, ?, ?)" );

            Object[] obj = new Object[4];

            obj[0] = data.getId();
            obj[1] = data.getId_mission_record();
            obj[2] = data.getName_measure();
            obj[3] = data.getValue();

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
     * update InspRecordIterm's
     *
     * @param data
     */
    public void updateDB(InspRecordIterm data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> InspRecordProject");
        ContentValues cv = new ContentValues();
        cv.put("id_mission_record", data.getId_mission_record());
        cv.put("name_measure", data.getName_measure());
        cv.put("value", data.getValue());

        db.update(tableName, cv, "id = ?",
                new String[] { String.valueOf(data.getId()) });
    }


    /**
     * query all , return list
     *
     * @return List<InspRecordIterm>
     */
    public List<InspRecordIterm> query()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<InspRecordIterm> dataList = new ArrayList<InspRecordIterm>();
        Cursor c = queryTheCursor();
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspRecordIterm data = new InspRecordIterm();
            data.setId(c.getLong(c.getColumnIndex("id")));
            data.setId_mission_record(c.getLong(c.getColumnIndex("id_mission_record")));
            data.setName_measure(c.getString(c.getColumnIndex("name_measure")));
            data.setValue(c.getDouble(c.getColumnIndex("value")));

            dataList.add(data);
        }
        c.close();
        return dataList;
    }

    public InspRecordIterm query(Integer id)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        Cursor c = queryTheCursor(id);
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspRecordIterm data = new InspRecordIterm();
            data.setId(c.getLong(c.getColumnIndex("id")));
            data.setId_mission_record(c.getLong(c.getColumnIndex("id_mission_record")));
            data.setName_measure(c.getString(c.getColumnIndex("name_measure")));
            data.setValue(c.getDouble(c.getColumnIndex("value")));

            return data;
        }
        c.close();
        return null;
    }


    public List<InspRecordIterm> queryByIdRecordMission(Long id)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + tableName +" WHERE id_mission_record = ?",
                    new String[] { String.valueOf(id) });
            if (c == null)
                return null;

            ArrayList<InspRecordIterm> dataList = new ArrayList<InspRecordIterm>();

            while (c.moveToNext())
            {
                InspRecordIterm data = new InspRecordIterm();
                data.setId(c.getLong(c.getColumnIndex("id")));
                data.setId_mission_record(c.getLong(c.getColumnIndex("id_mission_record")));
                data.setName_measure(c.getString(c.getColumnIndex("name_measure")));
                data.setValue(c.getDouble(c.getColumnIndex("value")));

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

    public Long getId_mission_record() {
        return id_mission_record;
    }

    public void setId_mission_record(Long id_mission_record) {
        this.id_mission_record = id_mission_record;
    }

    public String getName_measure() {
        return name_measure;
    }

    public void setName_measure(String name_measure) {
        this.name_measure = name_measure;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
