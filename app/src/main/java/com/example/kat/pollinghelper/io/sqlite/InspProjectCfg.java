package com.example.kat.pollinghelper.io.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujunhui on 2016/5/5.
 */
public class InspProjectCfg extends DBData{
    private String name; //项目名称
    private String desc; //项目描述
    private Integer insp_times; //巡检次数（次/每天
    private List<Integer> time_insp = new ArrayList<Integer>();//巡检时间 截取尾数（去掉年月日）

    public InspProjectCfg() {
        super(DatabaseHelper.TABLE_INSP_PROJECT_CFG);
    }


    public InspProjectCfg(String name, String desc) {
        super(DatabaseHelper.TABLE_INSP_PROJECT_CFG);
        this.name = name;
        this.desc = desc;
    }

    /**
     * add InspProjectCfg
     *
     * @param inspProjectCfg
     */
    public boolean addDB(InspProjectCfg inspProjectCfg)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspProjectCfg");
        // 采用事务处理，确保数据完整性
        boolean result = true;
        db.beginTransaction(); // 开始事务
        try
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("INSERT INTO " + tableName
                    + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)" );

            Object[] obj = new Object[9];

            obj[0] = inspProjectCfg.getName();
            obj[1] = inspProjectCfg.getDesc();
            obj[2] = inspProjectCfg.getInsp_times();

            for (int i=0; i< inspProjectCfg.getTime_insp().size(); i++){
                obj[i+3] = inspProjectCfg.getTime_insp().get(i);
            }

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
     * update InspProjectCfg's
     *
     * @param inspProjectCfg
     */
    public void updateDB(InspProjectCfg inspProjectCfg)
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


        db.update(tableName, cv, "name = ?",
                new String[] { inspProjectCfg.getName() });
    }


    /**
     * query all persons, return list
     *
     * @return List<Person>
     */
    public List<InspProjectCfg> query()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<InspProjectCfg> dataList = new ArrayList<InspProjectCfg>();
        Cursor c = queryTheCursor();
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspProjectCfg data = new InspProjectCfg();
            data.setName(c.getString(c.getColumnIndex("name")));
            data.setDesc(c.getString(c.getColumnIndex("desc")));
            data.setInsp_times(c.getInt(c.getColumnIndex("insp_times")));
            int count = data.getInsp_times();

            if (count > 0)
                data.getTime_insp().add(0,c.getInt(c.getColumnIndex("Insp_time1")));
            if (count > 1)
                data.getTime_insp().add(1,c.getInt(c.getColumnIndex("Insp_time2")));
            if (count > 2)
                data.getTime_insp().add(2,c.getInt(c.getColumnIndex("Insp_time3")));
            if (count > 3)
                data.getTime_insp().add(3,c.getInt(c.getColumnIndex("Insp_time4")));
            if (count > 4)
                data.getTime_insp().add(4,c.getInt(c.getColumnIndex("Insp_time5")));

            dataList.add(data);
        }
        c.close();
        return dataList;
    }

    public InspProjectCfg query(String nameProject)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        Cursor c = queryTheCursor(nameProject);
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspProjectCfg data = new InspProjectCfg();
            data.setName(c.getString(c.getColumnIndex("name")));
            data.setDesc(c.getString(c.getColumnIndex("desc")));
            data.setInsp_times(c.getInt(c.getColumnIndex("insp_times")));
            int count = data.getInsp_times();

            if (count > 0)
                data.getTime_insp().add(0,c.getInt(c.getColumnIndex("Insp_time1")));
            if (count > 1)
                data.getTime_insp().add(1,c.getInt(c.getColumnIndex("Insp_time2")));
            if (count > 2)
                data.getTime_insp().add(2,c.getInt(c.getColumnIndex("Insp_time3")));
            if (count > 3)
                data.getTime_insp().add(3,c.getInt(c.getColumnIndex("Insp_time4")));
            if (count > 4)
                data.getTime_insp().add(4,c.getInt(c.getColumnIndex("Insp_time5")));

            return data;
        }
        c.close();
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getInsp_times() {
        return insp_times;
    }

    public void setInsp_times(Integer insp_times) {
        this.insp_times = insp_times;
    }

    public List<Integer> getTime_insp() {
        return time_insp;
    }

    public void setTime_insp(List<Integer> time_insp) {
        this.time_insp = time_insp;
    }
}
