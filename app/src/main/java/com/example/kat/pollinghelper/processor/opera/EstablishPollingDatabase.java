package com.example.kat.pollinghelper.processor.opera;

import android.content.Context;

import com.example.kat.pollinghelper.io.sqlite.DBData;

/**
 * Created by KAT on 2016/6/27.
 */
public class EstablishPollingDatabase extends Operation {

    public EstablishPollingDatabase(Context context) {
        this.context = context;
    }

    @Override
    protected void onExecute() {
        DBData.createDatabaseEvn(context);
    }

    private final Context context;
}
