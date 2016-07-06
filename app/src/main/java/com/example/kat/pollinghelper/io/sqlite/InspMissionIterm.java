package com.example.kat.pollinghelper.io.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujunhui on 2016/5/11.
 * 2.7.	巡检任务与条目配置信息关系表insp_mission_iterm
 */
public class InspMissionIterm  extends DBData{
    private Integer id; //Project mission Iterm ID
    private Integer id_mission; //Project mission ID
    private Integer id_iterm; //巡检任务条目名称
    private Integer index_Iterm; //巡检任务条目序号

    public InspMissionIterm() {
        super(DatabaseHelper.TABLE_INSP_MISSION_ITERM);
    }

    public InspMissionIterm(Integer id, Integer id_mission, Integer id_iterm, Integer index_Iterm) {
        super(DatabaseHelper.TABLE_INSP_MISSION_ITERM);
        this.id = id;
        this.id_mission = id_mission;
        this.id_iterm = id_iterm;
        this.index_Iterm = index_Iterm;
    }


    /**
     * add InspMissionIterm
     *
     * @param data
     */
    public boolean addDB(InspMissionIterm data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspMissionIterm");
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
            obj[1] = data.getId_mission();
            obj[2] = data.getId_iterm();
            obj[3] = data.getIndex_Iterm();

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
     * update InspProjectMission's
     *
     * @param data
     */
    public void updateDB(InspMissionIterm data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> InspMissionIterm");
        ContentValues cv = new ContentValues();
        //cv.put("id", data.getId());
        cv.put("id_mission", data.getId_mission());
        cv.put("id_iterm", data.getId_iterm());
        cv.put("index_iterm", data.getIndex_Iterm());

        db.update(tableName, cv, "id = ?",
                new String[] { String.valueOf(data.getId()) });
    }


    /**
     * query all , return list
     *
     * @return List<InspMissionIterm>
     */
    public List<InspMissionIterm> query()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<InspMissionIterm> dataList = new ArrayList<InspMissionIterm>();
        Cursor c = queryTheCursor();
        while (c.moveToNext())
        {
            InspMissionIterm data = new InspMissionIterm();
            data.setId(c.getInt(c.getColumnIndex("id")));
            data.setId_mission(c.getInt(c.getColumnIndex("id_mission")));
            data.setId_iterm(c.getInt(c.getColumnIndex("id_iterm")));
            data.setIndex_Iterm(c.getInt(c.getColumnIndex("index_iterm")));
            dataList.add(data);
        }
        c.close();
        return dataList;
    }

    public InspMissionIterm query(Integer id)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        Cursor c = queryTheCursor(id);
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspMissionIterm data = new InspMissionIterm();
            data.setId(c.getInt(c.getColumnIndex("id")));
            data.setId_mission(c.getInt(c.getColumnIndex("id_mission")));
            data.setId_iterm(c.getInt(c.getColumnIndex("id_iterm")));
            data.setIndex_Iterm(c.getInt(c.getColumnIndex("index_iterm")));
            return data;
        }
        c.close();
        return null;
    }

    /**
     * query all , return list
     *
     * @return List<InspMissionIterm>
     */
    public List<InspMissionIterm> queryMissionId(Integer id)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<InspMissionIterm> dataList = new ArrayList<InspMissionIterm>();

        Cursor c = db.rawQuery("SELECT * FROM " + tableName +" WHERE id_mission = ?",
            new String[] { String.valueOf(id) });
        if (c == null)
            return null;

        while (c.moveToNext())
        {
            InspMissionIterm data = new InspMissionIterm();
            data.setId(c.getInt(c.getColumnIndex("id")));
            data.setId_mission(c.getInt(c.getColumnIndex("id_mission")));
            data.setId_iterm(c.getInt(c.getColumnIndex("id_iterm")));
            data.setIndex_Iterm(c.getInt(c.getColumnIndex("index_iterm")));
            dataList.add(data);
        }
        c.close();
        return dataList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_mission() {
        return id_mission;
    }

    public void setId_mission(Integer id_mission) {
        this.id_mission = id_mission;
    }

    public Integer getId_iterm() {
        return id_iterm;
    }

    public void setId_iterm(Integer id_iterm) {
        this.id_iterm = id_iterm;
    }

    public Integer getIndex_Iterm() {
        return index_Iterm;
    }

    public void setIndex_Iterm(Integer index_Iterm) {
        this.index_Iterm = index_Iterm;
    }
}
