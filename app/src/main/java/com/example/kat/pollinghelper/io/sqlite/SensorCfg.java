package com.example.kat.pollinghelper.io.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujunhui on 2016/5/5.
 */
public class SensorCfg  extends DBData{
    public String name; //名称
    public String desc; //描述
    public String addr_text; //完整地址
    public Integer index; //序号
    public String sensor_name; //

    public SensorCfg() {
        super(DatabaseHelper.TABLE_SENSOR_CFG);
    }


    /**
     * add SensorCfg
     *
     * @param data
     */
    public boolean addDB(SensorCfg data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add SensorCfg");
        // 采用事务处理，确保数据完整性
        boolean result = true;
        db.beginTransaction(); // 开始事务
        try
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("INSERT INTO " + tableName
                    + " VALUES(?, ?, ?, ?, ?)" );

            Object[] obj = new Object[5];

            obj[0] = data.getName();
            obj[1] = data.getDesc();
            obj[2] = data.getAddr_text();
            obj[3] = data.getIndex();
            obj[4] = data.getSensor_name();

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
     * update SensorCfg's
     *
     * @param data
     */
    public void updateDB(SensorCfg data)
    {
        try {
            ContentValues cv = new ContentValues();
            cv.put("desc", data.getDesc());
            cv.put("addr_text", data.getAddr_text());
            cv.put("index_sensor", data.getIndex());
            cv.put("sensor_name", data.getSensor_name());

            db.update(tableName, cv, "name = ?",
                    new String[]{data.getName()});
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }


    /**
     * query all , return list
     *
     * @return List<SensorCfg>
     */
    public List<SensorCfg> query()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<SensorCfg> dataList = new ArrayList<SensorCfg>();
        Cursor c = queryTheCursor();
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            SensorCfg data = new SensorCfg();
            data.setName(c.getString(c.getColumnIndex("name")));
            data.setDesc(c.getString(c.getColumnIndex("desc")));
            data.setAddr_text(c.getString(c.getColumnIndex("addr_text")));
            data.setIndex(c.getInt(c.getColumnIndex("index_sensor")));
            data.setSensor_name(c.getString(c.getColumnIndex("sensor_name")));
            dataList.add(data);
        }
        c.close();
        return dataList;
    }

    public SensorCfg query(String name)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        Cursor c = queryTheCursor(name);
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            SensorCfg data = new SensorCfg();
            data.setName(c.getString(c.getColumnIndex("name")));
            data.setDesc(c.getString(c.getColumnIndex("desc")));
            data.setAddr_text(c.getString(c.getColumnIndex("addr_text")));
            data.setIndex(c.getInt(c.getColumnIndex("index_sensor")));
            data.setSensor_name(c.getString(c.getColumnIndex("sensor_name")));
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

    public String getAddr_text() {
        return addr_text;
    }

    public void setAddr_text(String addr_text) {
        this.addr_text = addr_text;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getSensor_name() {
        return sensor_name;
    }

    public void setSensor_name(String sensor_name) {
        this.sensor_name = sensor_name;
    }
}
