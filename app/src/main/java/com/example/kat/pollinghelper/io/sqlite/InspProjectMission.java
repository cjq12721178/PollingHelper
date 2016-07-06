package com.example.kat.pollinghelper.io.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujunhui on 2016/5/11.
 * 2.6.	巡检项目与任务配置信息insp_project_mission
 */
public class InspProjectMission extends DBData {
    private Integer id; //Project mission ID
    private String name_project; //Project mission ID
    private String name_mission; //巡检任务名称
    private Integer index_Mission; //巡检任务序号
    private String desc_mission; //

    public String getDesc_mission() {
        return desc_mission;
    }

    public void setDesc_mission(String desc_mission) {
        this.desc_mission = desc_mission;
    }



    public InspProjectMission() {
        super(DatabaseHelper.TABLE_INSP_PROJECT_MISSION);
    }



    /**
     * add InspProjectMission
     *
     * @param data
     */
    public boolean addDB(InspProjectMission data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspProjectMission");
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
            obj[1] = data.getName_project();
            obj[2] = data.getName_mission();
            obj[3] = data.getIndex_Mission();
            //obj[3] = data.getIndex_Mission();

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
    public void updateDB(InspProjectMission data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> InspProjectMission");
        ContentValues cv = new ContentValues();
        cv.put("id", data.getId());
        cv.put("name_project", data.getName_project());
        cv.put("name_mission", data.getName_mission());
        cv.put("index_mission", data.getIndex_Mission());

        db.update(tableName, cv, "id = ?",
                new String[] { String.valueOf(data.getId()) });
    }


    /**
     * query all , return list
     *
     * @return List<InspProjectMission>
     */
    public List<InspProjectMission> query()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<InspProjectMission> dataList = new ArrayList<InspProjectMission>();
        Cursor c = queryTheCursor();
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspProjectMission data = new InspProjectMission();
            data.setId(c.getInt(c.getColumnIndex("id")));
            data.setName_project(c.getString(c.getColumnIndex("name_project")));
            data.setName_mission(c.getString(c.getColumnIndex("name_mission")));
            data.setIndex_Mission(c.getInt(c.getColumnIndex("index_mission")));
            dataList.add(data);
        }
        c.close();
        return dataList;
    }

    public InspProjectMission query(Integer id)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        Cursor c = queryTheCursor(id);
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspProjectMission data = new InspProjectMission();
            data.setId(c.getInt(c.getColumnIndex("id")));
            data.setName_project(c.getString(c.getColumnIndex("name_project")));
            data.setName_mission(c.getString(c.getColumnIndex("name_mission")));
            data.setIndex_Mission(c.getInt(c.getColumnIndex("index_mission")));
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

    public String getName_mission() {
        return name_mission;
    }

    public void setName_mission(String name_mission) {
        this.name_mission = name_mission;
    }

    public Integer getIndex_Mission() {
        return index_Mission;
    }

    public void setIndex_Mission(Integer index_Mission) {
        this.index_Mission = index_Mission;
    }
}
