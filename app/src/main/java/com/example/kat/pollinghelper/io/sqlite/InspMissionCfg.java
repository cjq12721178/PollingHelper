package com.example.kat.pollinghelper.io.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujunhui on 2016/5/5.
 */
public class InspMissionCfg extends DBData {
    public String name; //名称
    public String desc; //描述


    //public String imagePath; //图形文件路径
    private byte[] deviceImageData;
    public String namePorjece; //

    public String getNamePorjece() {
        return namePorjece;
    }

    public void setNamePorjece(String namePorjece) {
        this.namePorjece = namePorjece;
    }



    public InspMissionCfg() {
        super(DatabaseHelper.TABLE_INSP_MISSION_CFG);
    }


    /**
     * add InspMissionCfg
     *
     * @param data
     */
    public boolean addDB(InspMissionCfg data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspMissionCfg");
        // 采用事务处理，确保数据完整性
        boolean result = true;
        db.beginTransaction(); // 开始事务
        try
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("INSERT INTO " + tableName
                    + " VALUES(?, ?, ?, ?)" );

            Object[] obj = new Object[4];

            obj[0] = data.getName();
            obj[1] = data.getDesc();
            obj[2] = data.getDeviceImageData();
            obj[3] = data.getNamePorjece();

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
     * update InspMissionCfg's
     *
     * @param data
     */
    public void updateDB(InspMissionCfg data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> InspMissionCfg");
        ContentValues cv = new ContentValues();
        cv.put("desc", data.getDesc());
        cv.put("image_path", data.getDeviceImageData());
        cv.put("name_project", data.getNamePorjece());

        db.update(tableName, cv, "name = ?",
                new String[] { data.getName() });
    }


    /**
     * query all , return list
     *
     * @return List<InspMissionCfg>
     */
    public List<InspMissionCfg> query()
    {
        try {
            Log.d(AppConstants.LOG_TAG, "DBManager --> query");
            ArrayList<InspMissionCfg> dataList = new ArrayList<InspMissionCfg>();
            Cursor c = queryTheCursor();
            if (c == null)
                return null;
            while (c.moveToNext())
            {
                InspMissionCfg data = new InspMissionCfg();
                data.setName(c.getString(c.getColumnIndex("name")));
                data.setDesc(c.getString(c.getColumnIndex("desc")));
                data.setDeviceImageData(c.getBlob(c.getColumnIndex("image_path")));
                data.setNamePorjece(c.getString(c.getColumnIndex("name_project")));

                dataList.add(data);
            }
            c.close();
            return dataList;

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public InspMissionCfg query(String name)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        Cursor c = queryTheCursor(name);
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspMissionCfg data = new InspMissionCfg();
            data.setName(c.getString(c.getColumnIndex("name")));
            data.setDesc(c.getString(c.getColumnIndex("desc")));
            data.setDeviceImageData(c.getBlob(c.getColumnIndex("image_path")));
            data.setNamePorjece(c.getString(c.getColumnIndex("name_project")));
            return data;
        }
        c.close();
        return null;
    }

    /**
     * query all , return list
     *
     * @return List<InspMissionCfg>
     */
    public List<InspMissionCfg> queryByProjectName(String nameProject)
    {
        try {
            Log.d(AppConstants.LOG_TAG, "DBManager --> query");
            ArrayList<InspMissionCfg> dataList = new ArrayList<InspMissionCfg>();
            Cursor c = db.rawQuery("SELECT * FROM " + tableName +" WHERE name_project = ?",
                    new String[] { nameProject });
            if (c == null)
                return null;
            while (c.moveToNext())
            {
                InspMissionCfg data = new InspMissionCfg();
                data.setName(c.getString(c.getColumnIndex("name")));
                data.setDesc(c.getString(c.getColumnIndex("desc")));
                data.setDeviceImageData(c.getBlob(c.getColumnIndex("image_path")));
                data.setNamePorjece(c.getString(c.getColumnIndex("name_project")));

                dataList.add(data);
            }
            c.close();
            return dataList;

        } catch(Exception ex) {
            ex.printStackTrace();
        }

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


    public byte[] getDeviceImageData() {
        return deviceImageData;
    }

    public void setDeviceImageData(byte[] deviceImageData) {
        this.deviceImageData = deviceImageData;
    }
}
