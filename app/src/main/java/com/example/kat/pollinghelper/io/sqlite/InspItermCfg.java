package com.example.kat.pollinghelper.io.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujunhui on 2016/5/5.
 */
public class InspItermCfg extends DBData {
    private Long id;
    private String measureName;
    private String measureUnit;
    private String desc; //描述
    private Double up_alarm; //告警上限
    private Double down_alarm; //告警下限
    private String name_sensor_measure; //传感器测量名称
    private String nameMission; //描述



    public String getNameMission() {
        return nameMission;
    }

    public void setNameMission(String nameMission) {
        this.nameMission = nameMission;
    }



    public InspItermCfg() {
        super(DatabaseHelper.TABLE_INSP_ITERM_CFG);
    }


    /**
     * add InspItermCfg
     *
     * @param data
     */
    public boolean addDB(InspItermCfg data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add InspItermCfg");
        // 采用事务处理，确保数据完整性
        boolean result = true;
        db.beginTransaction(); // 开始事务
        try
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("INSERT INTO " + tableName
                    + " VALUES(?, ?, ?, ?, ?, ?, ?, ?)" );

            Object[] obj = new Object[8];

            obj[0] = data.getId();
            obj[1] = data.getMeasureName();
            obj[2] = data.getMeasureUnit();
            obj[3] = data.getDesc();
            obj[4] = data.getUp_alarm();
            obj[5] = data.getDown_alarm();
            obj[6] = data.getName_sensor_measure();
            obj[7] = data.getNameMission();

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
    public void updateDB(InspItermCfg data)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> InspMissionCfg");
        ContentValues cv = new ContentValues();
        cv.put("desc", data.getDesc());
        cv.put("name_sensor_measure", data.getName_sensor_measure());
        cv.put("up_alarm", data.getUp_alarm());
        cv.put("down_alarm", data.getDown_alarm());
        cv.put("name_mission", data.getNameMission());
        cv.put("measure_name", data.getMeasureName());
        cv.put("measure_unit", data.getMeasureUnit());

        db.update(tableName, cv, "id = ?",
                new String[] { String.valueOf(data.getId()) });
    }


    /**
     * query all , return list
     *
     * @return List<InspMissionCfg>
     */
    public List<InspItermCfg> query()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<InspItermCfg> dataList = new ArrayList<InspItermCfg>();
        Cursor c = queryTheCursor();
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspItermCfg data = new InspItermCfg();
            data.setId(c.getLong(c.getColumnIndex("id")));
            data.setDesc(c.getString(c.getColumnIndex("desc")));
            data.setName_sensor_measure(c.getString(c.getColumnIndex("name_sensor_measure")));
            data.setUp_alarm(c.getDouble(c.getColumnIndex("up_alarm")));
            data.setDown_alarm(c.getDouble(c.getColumnIndex("down_alarm")));
            data.setNameMission(c.getString(c.getColumnIndex("name_mission")));
            data.setMeasureName(c.getString(c.getColumnIndex("measure_name")));
            data.setMeasureUnit(c.getString(c.getColumnIndex("measure_unit")));
            dataList.add(data);
        }
        c.close();
        return dataList;
    }

    public InspItermCfg query(Integer id)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        Cursor c = queryTheCursor(id);
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspItermCfg data = new InspItermCfg();
            data.setId(c.getLong(c.getColumnIndex("id")));
            data.setDesc(c.getString(c.getColumnIndex("desc")));
            data.setName_sensor_measure(c.getString(c.getColumnIndex("name_sensor_measure")));
            data.setUp_alarm(c.getDouble(c.getColumnIndex("up_alarm")));
            data.setDown_alarm(c.getDouble(c.getColumnIndex("down_alarm")));
            data.setNameMission(c.getString(c.getColumnIndex("name_mission")));
            data.setMeasureName(c.getString(c.getColumnIndex("measure_name")));
            data.setMeasureUnit(c.getString(c.getColumnIndex("measure_unit")));
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
    public List<InspItermCfg> queryByMission(String nameMission)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<InspItermCfg> dataList = new ArrayList<InspItermCfg>();
        Cursor c = db.rawQuery("SELECT * FROM " + tableName +" WHERE name_mission = ?",
                new String[] { nameMission });
        if (c == null)
            return null;
        while (c.moveToNext())
        {
            InspItermCfg data = new InspItermCfg();
            data.setId(c.getLong(c.getColumnIndex("id")));
            data.setDesc(c.getString(c.getColumnIndex("desc")));
            data.setName_sensor_measure(c.getString(c.getColumnIndex("name_sensor_measure")));
            data.setUp_alarm(c.getDouble(c.getColumnIndex("up_alarm")));
            data.setDown_alarm(c.getDouble(c.getColumnIndex("down_alarm")));
            data.setNameMission(c.getString(c.getColumnIndex("name_mission")));
            data.setMeasureName(c.getString(c.getColumnIndex("measure_name")));
            data.setMeasureUnit(c.getString(c.getColumnIndex("measure_unit")));
            dataList.add(data);
        }
        c.close();
        return dataList;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName_sensor_measure() {
        return name_sensor_measure;
    }

    public void setName_sensor_measure(String name_sensor_measure) {
        this.name_sensor_measure = name_sensor_measure;
    }

    public Double getUp_alarm() {
        return up_alarm;
    }

    public void setUp_alarm(Double up_alarm) {
        this.up_alarm = up_alarm;
    }

    public Double getDown_alarm() {
        return down_alarm;
    }

    public void setDown_alarm(Double down_alarm) {
        this.down_alarm = down_alarm;
    }

    public String getMeasureName() {
        return measureName;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }
}
