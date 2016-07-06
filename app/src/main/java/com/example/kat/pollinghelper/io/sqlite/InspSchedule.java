package com.example.kat.pollinghelper.io.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liujunhui on 2016/5/11.
 * 2.7.	巡检任务与预定时间配置信息关系表insp_ schedule
 */
public class InspSchedule extends DBData {
    private Integer id; //
    private String name_project; //Project name
    private Date date; //巡检任务名称


    public InspSchedule() {
        super(DatabaseHelper.TABLE_INSP_SCHEDULE);
    }



    /**
     * add InspSchedule
     *
     * @param data
     */
    public boolean addDB(InspSchedule data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspSchedule");
        // 采用事务处理，确保数据完整性
        boolean result = true;
        db.beginTransaction(); // 开始事务
        try
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("INSERT INTO " + tableName
                    + " VALUES(?, ?, ?)" );

            Object[] obj = new Object[3];

            obj[0] = data.getId();
            obj[1] = data.getName_project();
            obj[2] = data.getDate().getTime();

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
     * update InspSchedule's
     *
     * @param data
     */
    public void updateDB(InspSchedule data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> InspProjectMission");
        ContentValues cv = new ContentValues();
        cv.put("name_project", data.getName_project());
        cv.put("date", data.getDate().getTime());

        db.update(tableName, cv, "id = ?",
                new String[] { String.valueOf(data.getId()) });
    }


    /**
     * query all , return list
     *
     * @return List<InspSchedule>
     */
    public List<InspSchedule> query()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<InspSchedule> dataList = new ArrayList<InspSchedule>();
        Cursor c = queryTheCursor();
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspSchedule data = new InspSchedule();
            data.setId(c.getInt(c.getColumnIndex("id")));
            data.setName_project(c.getString(c.getColumnIndex("name_project")));
            String str = c.getString(c.getColumnIndex("date"));
            Date date = new Date(c.getLong(c.getColumnIndex("date")));
            data.setDate(date);
            dataList.add(data);
        }
        c.close();
        return dataList;
    }

    public InspSchedule query(Integer id)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        Cursor c = queryTheCursor(id);
        if (c == null)
            return null;

        while (c.moveToNext())
        {
            InspSchedule data = new InspSchedule();
            data.setId(c.getInt(c.getColumnIndex("id")));
            data.setName_project(c.getString(c.getColumnIndex("name_project")));
            String str = c.getString(c.getColumnIndex("date"));
            Date date = new Date(c.getLong(c.getColumnIndex("date")));
            data.setDate(date);
            return data;
        }
        c.close();
        return null;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName_project() {
        return name_project;
    }

    public void setName_project(String name_project) {
        this.name_project = name_project;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }




}
