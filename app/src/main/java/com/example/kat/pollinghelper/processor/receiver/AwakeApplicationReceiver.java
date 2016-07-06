package com.example.kat.pollinghelper.processor.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.kat.pollinghelper.ui.activity.MainActivity;

import java.lang.reflect.Field;
import java.util.List;

public class AwakeApplicationReceiver extends BroadcastReceiver {
    public AwakeApplicationReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        //getCurrentApplicationRunningState需在另一个线程中才能正常工作
        //目前先这样简单处理，以后有需要换成线程池
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                switch (getCurrentApplicationRunningState(context)) {
                    case APP_FOREGROUND: {

                    } break;
                    case APP_BACKGROUND: {
                        moveTaskToFront(context);
                    } break;
                    case APP_TERMINATE: {
                        moveTaskToFront(context);
                    } break;
                    default: {
                    } break;
                }
            }
        });
        thread.start();
    }

    private void moveTaskToFront(Context context){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    private int getCurrentApplicationRunningState(Context context) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
        if (activityManager == null)
            return APP_UNKNOWN;

        Field processFlagGetter = processFlagGetter();
        if (processFlagGetter == null)
            return APP_UNKNOWN;

        String packageName = context.getApplicationInfo().packageName;
        List<ActivityManager.RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processList) {
            if (process.processName.equals(packageName)) {
                if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Integer flag = getProcessFlag(processFlagGetter, process);
                    if (flag != null && ((flag & FLAG_HAS_ACTIVITIES) != 0)) {
                        return APP_BACKGROUND;
                    } else {
                        return APP_TERMINATE;
                    }
                } else if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                        process.importanceReasonCode == ActivityManager.RunningAppProcessInfo.REASON_UNKNOWN) {
                    return APP_FOREGROUND;
                } else {
                    return APP_UNKNOWN;
                }
            }
        }
        return APP_UNKNOWN;
    }

    @Nullable
    private Integer getProcessFlag(Field processFlagGetter, ActivityManager.RunningAppProcessInfo process) {
        Integer flag = null;
        try {
            flag = processFlagGetter.getInt(process);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Nullable
    private Field processFlagGetter() {
        Field processFlag = null;
        try {
            processFlag = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("flags");
        } catch (NoSuchFieldException ignored) {
        }
        return processFlag;
    }

    private static final int FLAG_HAS_ACTIVITIES = 1<<2;
    private static final int APP_UNKNOWN = 0;
    private static final int APP_FOREGROUND = 1;
    private static final int APP_BACKGROUND = 2;
    private static final int APP_TERMINATE = 3;
}
