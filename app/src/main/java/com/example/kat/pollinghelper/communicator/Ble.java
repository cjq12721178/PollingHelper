package com.example.kat.pollinghelper.communicator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by KAT on 2016/6/27.
 */
public class Ble {

    public Ble(Context context) {
        this.context = context;
        handler = new Handler();
        scanTimer = new Timer();
        intervalTime = -1;
        durationTime = 10000;
    }

    public boolean launch() {
        if (bluetoothManager == null) {
            if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                return false;
            }

            bluetoothManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                return false;
            }

            bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                bluetoothManager = null;
                return false;
            }

            bluetoothAdapter.enable();
        }

        return true;
    }

    public void startScan(int intervalTime, int durationTime) {
        if (bluetoothAdapter != null && !bluetoothAdapter.isDiscovering() && durationTime > 0) {
            this.durationTime = durationTime;
            if (intervalTime > 0) {
                if (this.intervalTime > 0) {
                    scanTimer.cancel();
                }
                this.intervalTime = intervalTime;
                scanTimer.schedule(onStartScan, 0, intervalTime);
            } else {
                onStartScan.run();
            }
        }
    }

    public void stopScan() {
        if (bluetoothAdapter != null) {
            if (intervalTime > 0) {
                scanTimer.cancel();
            }
            if (bluetoothAdapter.isDiscovering()) {
                onStopScan.run();
            }
        }
    }

    private Runnable onStopScan = new Runnable() {
        @Override
        public void run() {
            if (bluetoothAdapter != null) {
                bluetoothAdapter.stopLeScan(onBluetoothDeviceScannedListener);
            }
        }
    };

    public void setOnBluetoothDeviceScannedListener(BluetoothAdapter.LeScanCallback l) {
        onBluetoothDeviceScannedListener = l;
    }

    private TimerTask onStartScan = new TimerTask() {
        @Override
        public void run() {
            if (!bluetoothAdapter.isDiscovering()) {
                handler.postDelayed(onStopScan, durationTime);
                bluetoothAdapter.startLeScan(onBluetoothDeviceScannedListener);
            }
        }
    };

    private int intervalTime;
    private int durationTime;
    private Timer scanTimer;
    private BluetoothAdapter.LeScanCallback onBluetoothDeviceScannedListener;
    private Handler handler;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private Context context;
}
