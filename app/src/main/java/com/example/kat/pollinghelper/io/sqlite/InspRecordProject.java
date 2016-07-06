package com.example.kat.pollinghelper.io.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by liujunhui on 2016/5/5.
 * 3.1.	巡检项目记录信息表insp_record _project
 */
public class InspRecordProject extends DBData  {
    private Long id; //Project record ID
    private Long id_project; //Project ID



    private String name_project; //项目名称
    private Date  date;
    private String desc; //巡检任务描述巡检时间描述,如201605041000;年月日时分
    private Integer state; //0：great1：good2：bad
    private Integer state_process; //0：建立 1：执行中 2：完成 3：终止
    private String result; //


    public InspRecordProject()  {
        super(DatabaseHelper.TABLE_INSP_RECORD_PROJECT);
    }



    /**
     * add InspRecordProject
     *
     * @param data
     */
    public boolean addDB(InspRecordProject data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspRecordProject");
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
            obj[1] = data.getId_project();
            obj[2] = data.getDate().getTime();
            obj[3] = data.getDesc();
            obj[4] = data.getState();
            obj[5] = data.getState_process();
            obj[6] = data.getResult();

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
     * update InspRecordProject's
     *
     * @param data
     */
    public void updateDB(InspRecordProject data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> InspRecordProject");
        ContentValues cv = new ContentValues();
        cv.put("id_project", data.getId_project());
        cv.put("date", data.getDate().getTime());
        cv.put("desc", data.getDesc());
        cv.put("state", data.getState());
        cv.put("state_progress", data.getState_process());
        cv.put("result", data.getResult());

        db.update(tableName, cv, "id = ?",
                new String[] { String.valueOf(data.getId()) });
    }


    /**
     * query all , return list
     *
     * @return List<InspRecordProject>
     */
    public List<InspRecordProject> query()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<InspRecordProject> dataList = new ArrayList<InspRecordProject>();
        Cursor c = queryTheCursor();
        if (c == null)
            return null;

        while (c.moveToNext())
        {
            InspRecordProject data = new InspRecordProject();
            data.setId(c.getLong(c.getColumnIndex("id")));
            data.setId_project(c.getLong(c.getColumnIndex("id_project")));

            Date date = new Date(c.getLong(c.getColumnIndex("date")));
            data.setDate(date);

            data.setDesc(c.getString(c.getColumnIndex("desc")));
            data.setState(c.getInt(c.getColumnIndex("state")));
            data.setState_process(c.getInt(c.getColumnIndex("state_progress")));
            data.setResult(c.getString(c.getColumnIndex("result")));

            dataList.add(data);
        }
        c.close();
        return dataList;
    }

    public InspRecordProject query(Integer id)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        Cursor c = queryTheCursor(id);
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspRecordProject data = new InspRecordProject();
            data.setId(c.getLong(c.getColumnIndex("id")));
            data.setId_project(c.getLong(c.getColumnIndex("id_project")));

            Date date = new Date(c.getLong(c.getColumnIndex("date")));
            data.setDate(date);

            data.setDesc(c.getString(c.getColumnIndex("desc")));
            data.setState(c.getInt(c.getColumnIndex("state")));
            data.setState_process(c.getInt(c.getColumnIndex("state_progress")));
            data.setResult(c.getString(c.getColumnIndex("result")));
            return data;
        }
        c.close();
        return null;
    }

    public List<InspRecordProject> query(Date begin, Date end)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + tableName +" WHERE date > ? and date < ?",
                    new String[] { String.valueOf(begin.getTime()), String.valueOf(end.getTime()) });
            if (c == null)
                return null;

            ArrayList<InspRecordProject> dataList = new ArrayList<InspRecordProject>();
            while (c.moveToNext())
            {
                InspRecordProject data = new InspRecordProject();
                data.setId(c.getLong(c.getColumnIndex("id")));
                data.setId_project(c.getLong(c.getColumnIndex("id_project")));

                Date date = new Date(c.getLong(c.getColumnIndex("date")));
                data.setDate(date);

                data.setDesc(c.getString(c.getColumnIndex("desc")));
                data.setState(c.getInt(c.getColumnIndex("state")));
                data.setState_process(c.getInt(c.getColumnIndex("state_progress")));
                data.setResult(c.getString(c.getColumnIndex("result")));

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




    public String getName_project() {
        return name_project;
    }

    public void setName_project(String name_project) {
        this.name_project = name_project;
    }
    public Long getId_project() {
        return id_project;
    }

    public void setId_project(Long id_project) {
        this.id_project = id_project;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
