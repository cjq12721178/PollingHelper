package com.example.kat.pollinghelper.io.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by liujunhui on 2016/5/12.
 */
public abstract class DBData {
    protected static DatabaseHelper helper=null;
    protected static  SQLiteDatabase db=null;

    protected String tableName;
    public static void createDatabaseEvn(Context context){
        Log.d(AppConstants.LOG_TAG, "DBData --> createDatabaseEvn");

        if (helper != null)
            return;

        helper = new DatabaseHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    public static void closeDatabaseEvn(){
        Log.d(AppConstants.LOG_TAG, "DBData --> closeDatabaseEvn");
        db.close();
        helper = null;
    }

    private DBData() {
    }

    public DBData(String tableName) {
        this.tableName = tableName;
    }

    /**
     * query all data, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor()
    {
        try {
            Log.d(AppConstants.LOG_TAG, "DBManager --> queryTheCursor");
            Cursor c = db.rawQuery("SELECT * FROM " + tableName,
                    null);
            return c;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    public Cursor queryTheCursor(String name)
    {
        try {
            Log.d(AppConstants.LOG_TAG, "DBManager --> queryTheCursor");
            Cursor c = db.rawQuery("SELECT * FROM " + tableName + " WHERE name = ?",
                    new String[] { name });
            return c;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Cursor queryTheCursor(Integer id)
    {
        try {
            Log.d(AppConstants.LOG_TAG, "DBManager --> queryTheCursor");
            Cursor c = db.rawQuery("SELECT * FROM " + tableName +"  WHERE id = ?",
                    new String[] { String.valueOf(id) });
            return c;
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Cursor queryTheCursor(long id)
    {
        try {
            Log.d(AppConstants.LOG_TAG, "DBManager --> queryTheCursor");
            Cursor c = db.rawQuery("SELECT * FROM " + tableName +"  WHERE  id = ?",
                    new String[] { String.valueOf(id) });
            return c;
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * delete old data
     *
     * @param name
     */
    public boolean deleteDB(String name)
    {
        try {
            Log.d(AppConstants.LOG_TAG, "DBManager --> delete by name");
            db.delete(tableName, "name = ?",
                    new String[]{name});
        }catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }

        return true;

    }



    /**
     * delete old data
     *
     * @param id
     */
    public boolean deleteDB(long id)
    {
        try {
            Log.d(AppConstants.LOG_TAG, "DBManager --> delete by long id");
            db.delete(tableName, "id = ?",
                    new String[] { String.valueOf(id) }   );
        }catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }

        return true;

    }

    /**
     * delete old data
     *
     * @param id
     */
    public boolean deleteDB(Integer id)
    {
        try {
            Log.d(AppConstants.LOG_TAG, "DBManager --> delete by long id");

            Object[] obj = new Object[1];
            obj[0] = id;
            db.delete(tableName, "id = ?",
                    new String[] { String.valueOf(id) });
        }catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    //public abstract void addDB(DBData data);
    //public abstract void deleteDB(DBData data);
   //public abstract void updateDBf(DBData data);
}
