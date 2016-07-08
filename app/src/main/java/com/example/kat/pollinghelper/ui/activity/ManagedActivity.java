package com.example.kat.pollinghelper.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.record.EvaluationType;
import com.example.kat.pollinghelper.fuction.config.PollingState;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.processor.opera.OperationInfo;
import com.example.kat.pollinghelper.processor.service.ManagerService;
import com.example.kat.pollinghelper.ui.dialog.LoadingDialog;
import com.example.kat.pollinghelper.ui.toast.BeautyToast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by KAT on 2016/5/6.
 */
public class ManagedActivity extends AppCompatActivity {

    private Handler handler;
    private LoadingDialog loadingDialog;
    private boolean initial;
    private OperationInfo operationInfo;
    private ManagerServiceConnection managerServiceConnection;

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
    protected void notifyManager(OperaType newOpera, Runnable success, Runnable failed) {
        operationInfo.putArgument(ArgumentTag.AT_RUNNABLE_SUCCESS, success);
        operationInfo.putArgument(ArgumentTag.AT_RUNNABLE_FAILED, failed);
        operationInfo.notifyExecutor(newOpera);
    }

    protected void notifyManager(OperaType newOpera, Runnable success) {
        notifyManager(newOpera, success, null);
    }

    protected void notifyManager(OperaType newOpera) {
        notifyManager(newOpera, null, null);
    }

    protected void notifyManager(Runnable success, Runnable failed, OperaType... newOperas) {
        operationInfo.putArgument(ArgumentTag.AT_RUNNABLE_SUCCESS, success);
        operationInfo.putArgument(ArgumentTag.AT_RUNNABLE_FAILED, failed);
        operationInfo.notifyExecutor(newOperas);
    }

    protected void notifyManager(Runnable success, OperaType... newOperas) {
        notifyManager(success, null, newOperas);
    }

    protected void notifyManager(OperaType... newOperas) {
        notifyManager(null, null, newOperas);
    }

    protected ManagedActivity putArgument(ArgumentTag tag, Object arg) {
        operationInfo.putArgument(tag, arg);
        return this;
    }

    protected Object getArgument(ArgumentTag tag) {
        return operationInfo.getArgument(tag);
    }

    protected boolean isInitial() {
        return initial;
    }

    protected String getPollingRecordLabel(String eventName, PollingState state) {
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

    protected Bitmap getBitmapFromByteArray(byte[] imageData) {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }

    public InputStream uriToInputStream(Uri uri) {
        InputStream result = null;
        try {
            result = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 将InputStream转换成byte[]
    public byte[] uriToByteArray(Uri uri){
        byte[] result = null;
        InputStream inputStream = uriToInputStream(uri);

        if (inputStream != null) {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            try {
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }
                result = byteBuffer.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
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
}
