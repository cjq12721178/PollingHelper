package com.example.kat.pollinghelper.processor.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.processor.timertask.NotificationTask;
import com.example.kat.pollinghelper.structure.ProjectTimeInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;

public class NotificationService extends Service {

    public NotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initPollingTimer();
        registerReceiver();
    }

    private void initPollingTimer() {
        pollingTimer = new Timer();
        notificationTasks = new ArrayList<>();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.ba_update_project_time));
        registerReceiver(projectConfigChangeReceiver, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(projectConfigChangeReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void executeNotification() {
        synchronized (notificationTasks) {
            if (!notificationTasks.isEmpty()) {
                for (NotificationTask task :
                        notificationTasks) {
                    pollingTimer.schedule(task, task.when(), task.period());
                }
            }
        }
    }

    private void cancelAllNotification() {
        for (NotificationTask task :
                notificationTasks) {
            task.cancel();
        }
        pollingTimer.purge();
    }

    private BroadcastReceiver projectConfigChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Set<ProjectTimeInfo> projectTimeInfoSet = (HashSet<ProjectTimeInfo>)intent.getSerializableExtra(getString(R.string.tag_project_schedule_times));
                if (isNotificationTaskChanged(projectTimeInfoSet)) {
                    cancelAllNotification();
                    modifyNotificationTask(projectTimeInfoSet);
                    executeNotification();
                }
            }
        }
    };

    private boolean isNotificationTaskChanged(Set<ProjectTimeInfo> projectTimeInfoSet) {
        if (projectTimeInfoSet == null)
            return false;

        if (projectTimeInfoSet.size() != notificationTasks.size())
            return true;

        Iterator<ProjectTimeInfo> timeInfoIterator = projectTimeInfoSet.iterator();
        for (NotificationTask task :
                notificationTasks) {
            if (!task.equals(timeInfoIterator.next()))
                return true;
        }

        return false;
    }

    private void modifyNotificationTask(Set<ProjectTimeInfo> projectTimeInfoSet) {
        synchronized (notificationTasks) {
            notificationTasks.clear();
            for (ProjectTimeInfo projectTimeInfo :
                    projectTimeInfoSet) {
                notificationTasks.add(new NotificationTask(this, projectTimeInfo));
            }
        }
    }

    private List<NotificationTask> notificationTasks;
    private Timer pollingTimer;
}
