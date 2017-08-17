package com.example.kat.pollinghelper.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.bean.record.EvaluationType;
import com.example.kat.pollinghelper.bean.record.ScoutRecordState;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.processor.opera.OperationInfo;
import com.example.kat.pollinghelper.processor.service.ManagerService;
import com.example.kat.pollinghelper.ui.dialog.AlternativeDialog;
import com.example.kat.pollinghelper.ui.dialog.LoadingDialog;
import com.example.kat.pollinghelper.ui.toast.BeautyToast;

/**
 * Created by KAT on 2016/5/6.
 */
public class ManagedActivity extends AppCompatActivity {

    protected void onInitializeBusiness() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initial = true;
        loadingDialog = new LoadingDialog();
        handler = new Handler();
        managerServiceConnection = new ManagerServiceConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this, ManagerService.class), managerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(managerServiceConnection);
    }

    //提供操作类型和反馈处理，success和failed可以为null
    public void notifyManager(OperaType newOpera, Runnable success, Runnable failed) {
        operationInfo.putArgument(ArgumentTag.AT_RUNNABLE_SUCCESS, success);
        operationInfo.putArgument(ArgumentTag.AT_RUNNABLE_FAILED, failed);
        operationInfo.notifyExecutor(newOpera);
    }

    public void notifyManager(OperaType newOpera, Runnable success) {
        notifyManager(newOpera, success, null);
    }

    public void notifyManager(OperaType newOpera) {
        notifyManager(newOpera, null, null);
    }

    public void notifyManager(Runnable success, Runnable failed, OperaType... newOperas) {
        operationInfo.putArgument(ArgumentTag.AT_RUNNABLE_SUCCESS, success);
        operationInfo.putArgument(ArgumentTag.AT_RUNNABLE_FAILED, failed);
        operationInfo.notifyExecutor(newOperas);
    }

    public void notifyManager(Runnable success, OperaType... newOperas) {
        notifyManager(success, null, newOperas);
    }

    public void notifyManager(OperaType... newOperas) {
        notifyManager(null, null, newOperas);
    }

    public ManagedActivity putArgument(ArgumentTag tag, Object arg) {
        operationInfo.putArgument(tag, arg);
        return this;
    }

    public Object getArgument(ArgumentTag tag) {
        return operationInfo.getArgument(tag);
    }

    protected boolean isInitial() {
        return initial;
    }

    protected String getPollingRecordLabel(String eventName, ScoutRecordState state) {
        return eventName + "（" + state.toString() + "）";
    }

    protected int getEvaluationRadioButtonId(boolean projectOrMission, EvaluationType type) {
        int id = 0;
        if (projectOrMission)  {
            switch (type) {
                case ET_GOOD: {
                    id = R.id.rdo_project_good;
                } break;
                case ET_NORMAL: {
                    id = R.id.rdo_project_normal;
                } break;
                case ET_BAD: {
                    id = R.id.rdo_project_bad;
                } break;
                default: {
                    id = R.id.rdo_project_normal;
                } break;
            }
        } else {
            switch (type) {
                case ET_GOOD: {
                    id = R.id.rdo_mission_good;
                } break;
                case ET_NORMAL: {
                    id = R.id.rdo_mission_normal;
                } break;
                case ET_BAD: {
                    id = R.id.rdo_mission_bad;
                } break;
                default: {
                    id = R.id.rdo_mission_normal;
                } break;
            }
        }
        return id;
    }

    protected void promptMessage(int stringID) {
        BeautyToast.show(stringID);
    }

    protected void provideMaterial() {

    }

    protected final void showLoadingDialog(int stringID) {
        showLoadingDialog(getString(stringID));
    }

    protected final void showLoadingDialog(final String information) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                loadingDialog.show(getSupportFragmentManager(), information);
            }
        });
    }

    protected final void closeLoadingDialog() {
        loadingDialog.dismiss();
    }

    private class ManagerServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            operationInfo = ((ManagerService.ManagerBinder)service).getOperationInfo();

            if (isInitial()) {
                initial = false;
                provideMaterial();
                onInitializeBusiness();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    protected void showAlternativeDialog(int resId) {
        showAlternativeDialog(resId, null, null);
    }

    protected void showAlternativeDialog(int resId, View.OnClickListener confirm) {
        showAlternativeDialog(resId, confirm, null);
    }

    protected void showAlternativeDialog(int resId, View.OnClickListener confirm, View.OnClickListener cancel) {
        if (alternativeDialog == null) {
            alternativeDialog = new AlternativeDialog();
        }
        alternativeDialog.setOnConfirmClickListener(confirm)
                         .setOnCancelClickListener(cancel)
                         .show(getSupportFragmentManager(), getString(resId));
    }

    protected Handler getHandler() {
        return handler;
    }

    private AlternativeDialog alternativeDialog;
    private Handler handler;
    private LoadingDialog loadingDialog;
    private boolean initial;
    private OperationInfo operationInfo;
    private ManagerServiceConnection managerServiceConnection;
}
