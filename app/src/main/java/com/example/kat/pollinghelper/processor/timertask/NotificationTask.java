package com.example.kat.pollinghelper.processor.timertask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.SimpleTime;
import com.example.kat.pollinghelper.ui.structure.ProjectTimeInfo;

import java.util.Date;
import java.util.TimerTask;

/**
 * Created by KAT on 2016/6/30.
 */
public class NotificationTask extends TimerTask {

    public NotificationTask(Context context, ProjectTimeInfo projectTimeInfo) {
        if (context == null || projectTimeInfo == null)
            throw new NullPointerException();
        this.context = context;
        this.projectTimeInfo = projectTimeInfo;
    }

    @Override
    public void run() {
        //发布预设巡检时间到达通知
        Notification pollingTimeNotification = createNotification();
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(getTaskId(), pollingTimeNotification);

        //通知主界面重建巡检记录
        Intent intent = new Intent(context.getString(R.string.ba_renew_project_record));
        intent.putExtra(context.getString(R.string.tag_project_name), projectTimeInfo.getProjectName());
        context.sendBroadcast(intent);
    }

    private Notification createNotification() {
        Bitmap btm = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_app_logo);
        String[] contentText = context.getResources().getStringArray(R.array.ui_prompt_current_polling_project);
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_app_logo)
                .setContentTitle(context.getString(R.string.ui_prompt_polling_time_hit))
                .setContentText(contentText[0] + projectTimeInfo.getProjectName() + contentText[1] + projectTimeInfo.getPollingTime().toString() + contentText[2])
                .setTicker(context.getString(R.string.ui_prompt_new_polling_project))
                .setLargeIcon(btm)
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(getAwakeBroadcast())
                .build();
    }

    private PendingIntent getAwakeBroadcast() {
        Intent intent = new Intent(context.getString(R.string.ba_awake_application));
        return PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public Date when() {
        return projectTimeInfo.getPollingTime().toNextScheduleDate();
    }

    public long period() {
        return SimpleTime.DAY_MILLISECONDS;
    }

    private int getTaskId() {
        return ++taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null)
            return false;

        if (getClass() == o.getClass()) {
            NotificationTask other = (NotificationTask)o;
            if (projectTimeInfo == null) {
                if (other.projectTimeInfo != null)
                    return false;
            } else if (!projectTimeInfo.equals(other.projectTimeInfo))
                return false;
        } else if (o instanceof ProjectTimeInfo) {
            ProjectTimeInfo other = (ProjectTimeInfo)o;
            if (projectTimeInfo == null)
                return false;
            else if (!projectTimeInfo.equals(other))
                return false;
        }
        return super.equals(o);
    }

    private static int taskId = 0;
    private final Context context;
    private final ProjectTimeInfo projectTimeInfo;
}
