package com.example.kat.pollinghelper.io.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by liujunhui on 2016/5/4.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // 数据库版本号
    private static final int DATABASE_VERSION = 20;
    // 数据库名
    private static final String DATABASE_NAME = "WinsInspDB.db";

    // 数据表名，一个数据库中可以有多个表（虽然本例中只建立了一个表）
    public static final String TABLE_NAME = "PersonTable";
    public static final String TABLE_INSP_PROJECT_CFG = "insp_project_cfg";
    public static final String TABLE_INSP_MISSION_CFG = "insp_mission_cfg";
    public static final String TABLE_INSP_ITERM_CFG = "insp_iterm_cfg";
    public static final String TABLE_SENSOR_CFG = "sensor_cfg";
    public static final String TABLE_INSP_PROJECT = "insp_project";
    public static final String TABLE_INSP_PROJECT_MISSION = "insp_project_mission";
    public static final String TABLE_INSP_MISSION_ITERM = "insp_mission_iterm";
    public static final String TABLE_INSP_SCHEDULE = "insp_schedule";

    public static final String TABLE_INSP_RECORD_PROJECT= "insp_record_project";
    public static final String TABLE_INSP_RECORD_MISSION = "insp_record_mission";
    public static final String TABLE_INSP_RECORD_ITERM = "insp_record_iterm";


    // 构造函数，调用父类SQLiteOpenHelper的构造函数
    public DatabaseHelper(Context context, String name, CursorFactory factory,
                          int version, DatabaseErrorHandler errorHandler)
    {
        super(context, name, factory, version, errorHandler);
    }

    public DatabaseHelper(Context context, String name, CursorFactory factory,
                          int version)
    {
        super(context, name, factory, version);
        // SQLiteOpenHelper的构造函数参数：
        // context：上下文环境
        // name：数据库名字
        // factory：游标工厂（可选）
        // version：数据库模型版本号
    }

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // 数据库实际被创建是在getWritableDatabase()或getReadableDatabase()方法调用时
        Log.d(AppConstants.LOG_TAG, "DatabaseHelper Constructor");
        // CursorFactory设置为null,使用系统默认的工厂类
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 调用时间：数据库第一次创建时onCreate()方法会被调用

        // onCreate方法有一个 SQLiteDatabase对象作为参数，根据需要对这个对象填充表和初始化数据
        // 这个方法中主要完成创建数据库后对数据库的操作

        Log.d(AppConstants.LOG_TAG, "DatabaseHelper onCreate");

        // 构建创建表的SQL语句（可以从SQLite Expert工具的DDL粘贴过来加进StringBuffer中）
        StringBuffer sBuffer = new StringBuffer();

        sBuffer.append("CREATE TABLE [" + TABLE_NAME + "] (");
        sBuffer.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sBuffer.append("[name] TEXT,");
        sBuffer.append("[age] INTEGER,");
        sBuffer.append("[info] TEXT)");

        // 执行创建表的SQL语句
        db.execSQL(sBuffer.toString());

        createTable(db);

        // 即便程序修改重新运行，只要数据库已经创建过，就不会再进入这个onCreate方法

    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 调用时间：如果DATABASE_VERSION值被改为别的数,系统发现现有数据库版本不同,即会调用onUpgrade

        // onUpgrade方法的三个参数，一个 SQLiteDatabase对象，一个旧的版本号和一个新的版本号
        // 这样就可以把一个数据库从旧的模型转变到新的模型
        // 这个方法中主要完成更改数据库版本的操作

        Log.d(AppConstants.LOG_TAG, "DatabaseHelper onUpgrade");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        dropTable(db);
        onCreate(db);
        // 上述做法简单来说就是，通过检查常量值来决定如何，升级时删除旧表，然后调用onCreate来创建新表
        // 一般在实际项目中是不能这么做的，正确的做法是在更新数据表结构时，还要考虑用户存放于数据库中的数据不丢失
    }

    private void createTable(SQLiteDatabase db) {

        Log.d(AppConstants.LOG_TAG, "DatabaseHelper onCreate");

        // 构建创建表的SQL语句（可以从SQLite Expert工具的DDL粘贴过来加进StringBuffer中）
        //创建 2.1.	巡检项目配置表insp_project_cfg
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("CREATE TABLE [" + TABLE_INSP_PROJECT_CFG + "] (");
            //sBuffer.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
            sBuffer.append("[name] TEXT NOT NULL PRIMARY KEY,");
            sBuffer.append("[desc] TEXT,");
            sBuffer.append("[insp_times] INTEGER,");
            sBuffer.append("[Insp_time1] TEXT,");
            sBuffer.append("[Insp_time2] TEXT,");
            sBuffer.append("[Insp_time3] TEXT,");
            sBuffer.append("[Insp_time4] TEXT,");
            sBuffer.append("[Insp_time5] TEXT,");
            sBuffer.append("[testInfo] TEXT)");


            // 执行创建表的SQL语句
            db.execSQL(sBuffer.toString());
        }

        //巡检列表配置表insp_list_cfg
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("CREATE TABLE [" + TABLE_INSP_MISSION_CFG + "] (");
            //sBuffer.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
            sBuffer.append("[name] TEXT NOT NULL PRIMARY KEY,");
            sBuffer.append("[desc] TEXT,");
            sBuffer.append("[image_path] BLOB,");
            sBuffer.append("[name_project] TEXT)");


            // 执行创建表的SQL语句
            db.execSQL(sBuffer.toString());
        }

        //巡检量配置表insp_iterm_cfg
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("CREATE TABLE [" + TABLE_INSP_ITERM_CFG + "] (");
            //sBuffer.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
            sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
            sBuffer.append("[measure_name] TEXT,");
            sBuffer.append("[measure_unit] TEXT,");
            sBuffer.append("[desc] TEXT,");
            sBuffer.append("[up_alarm] DOUBLE,");
            sBuffer.append("[down_alarm] DOUBLE,");
            sBuffer.append("[name_sensor_measure] TEXT,");
            sBuffer.append("[name_mission] TEXT)");


            // 执行创建表的SQL语句
            db.execSQL(sBuffer.toString());
        }

        //创建传感器配置表sensor_cfg
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("CREATE TABLE [" + TABLE_SENSOR_CFG + "] (");
            //sBuffer.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
            sBuffer.append("[name] TEXT NOT NULL PRIMARY KEY,");
            sBuffer.append("[desc] TEXT,");
            sBuffer.append("[addr_text] TEXT,");
            sBuffer.append("[index_sensor] INTEGER,");
            sBuffer.append("[sensor_name] TEXT)");

            // 执行创建表的SQL语句
            db.execSQL(sBuffer.toString());
        }


        ////////////////////////////////////////////////////////////////////
        //2.5.	巡检项目配置信息insp_project
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("CREATE TABLE [" + TABLE_INSP_PROJECT + "] (");
            sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY, ");
            sBuffer.append("[name_project] TEXT)");

            // 执行创建表的SQL语句
            db.execSQL(sBuffer.toString());
        }

        //2.6.	巡检项目任务配置信息insp_project_mission
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("CREATE TABLE [" + TABLE_INSP_PROJECT_MISSION + "] (");
            sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY, ");
            sBuffer.append("[name_project] TEXT,");
            sBuffer.append("[name_mission] TEXT,");
            sBuffer.append("[index_mission] INTEGER)");

            // 执行创建表的SQL语句
            db.execSQL(sBuffer.toString());
        }


        //2.7.	巡检任务与条目配置信息关系表insp_mission_iterm
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("CREATE TABLE [" + TABLE_INSP_MISSION_ITERM + "] (");
            sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY, ");
            sBuffer.append("[id_mission] INTEGER,");
            sBuffer.append("[id_iterm] INTEGER,");
            sBuffer.append("[index_iterm] INTEGER)");

            // 执行创建表的SQL语句
            db.execSQL(sBuffer.toString());
        }

        //2.7.	巡检任务与条目配置信息关系表insp_mission_iterm
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("CREATE TABLE [" + TABLE_INSP_SCHEDULE + "] (");
            sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY, ");
            sBuffer.append("[name_project] TEXT,");
            sBuffer.append("[date] DATETIME)");


            // 执行创建表的SQL语句
            db.execSQL(sBuffer.toString());
        }
        /////////////////////////////////////////////////////////////////
        //
        //3.1.	巡检项目记录信息表insp_record _project
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("CREATE TABLE [" + TABLE_INSP_RECORD_PROJECT + "] (");
            sBuffer.append("[id] INT64 NOT NULL PRIMARY KEY,");
            //sBuffer.append("[id_project] INTEGER,");
            sBuffer.append("[name_project] TEXT,");
            sBuffer.append("[date] DATETIME,");
            sBuffer.append("[finish_date] DATETIME,");
            sBuffer.append("[desc] TEXT,");
            sBuffer.append("[state] INTEGER,");
            sBuffer.append("[state_progress] INTEGER,");
            sBuffer.append("[result] TEXT)");

            // 执行创建表的SQL语句
            db.execSQL(sBuffer.toString());
        }


        //创建巡检任务记录insp_record _mission
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("CREATE TABLE [" + TABLE_INSP_RECORD_MISSION + "] (");
            sBuffer.append("[id] INT64 NOT NULL PRIMARY KEY,");
            sBuffer.append("[name_mission] TEXT,");
            sBuffer.append("[id_project_record] INT64,");
            sBuffer.append("[date] DATETIME,");
            sBuffer.append("[desc] TEXT,");
            sBuffer.append("[state] INTEGER,");
            sBuffer.append("[state_progress] INTEGER)");

            // 执行创建表的SQL语句
            db.execSQL(sBuffer.toString());
        }

        //创建巡检传感器数据记录insp_record _iterm
        {
            StringBuffer sBuffer = new StringBuffer();

            sBuffer.append("CREATE TABLE [" + TABLE_INSP_RECORD_ITERM + "] (");
            sBuffer.append("[id] INT64 NOT NULL PRIMARY KEY,");
            sBuffer.append("[id_mission_record] INT64,");
            sBuffer.append("[name_measure] TEXT,");
            sBuffer.append("[value] DOUBLE)");

            // 执行创建表的SQL语句
            db.execSQL(sBuffer.toString());
        }

    }
    private void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSP_PROJECT_CFG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSP_MISSION_CFG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSP_ITERM_CFG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR_CFG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSP_PROJECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSP_PROJECT_MISSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSP_MISSION_ITERM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSP_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSP_RECORD_PROJECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSP_RECORD_MISSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSP_RECORD_ITERM);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }


    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        // 每次打开数据库之后首先被执行

        Log.d("", "DatabaseHelper onOpen");
    }


}
