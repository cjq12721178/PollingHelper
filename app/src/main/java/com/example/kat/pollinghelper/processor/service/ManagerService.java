package com.example.kat.pollinghelper.processor.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.communicator.Ble;
import com.example.kat.pollinghelper.communicator.Communicator;
import com.example.kat.pollinghelper.communicator.Udp;
import com.example.kat.pollinghelper.data.DataStorage;
import com.example.kat.pollinghelper.io.sqlite.DBData;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.processor.opera.EstablishScoutDatabase;
import com.example.kat.pollinghelper.processor.opera.ExportMissionRecord;
import com.example.kat.pollinghelper.processor.opera.ExportScoutConfig;
import com.example.kat.pollinghelper.processor.opera.ExportProjectRecord;
import com.example.kat.pollinghelper.processor.opera.ExportSensorConfig;
import com.example.kat.pollinghelper.processor.opera.ImportProjectAndSensorConfigs;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.processor.opera.Operation;
import com.example.kat.pollinghelper.processor.opera.OperationInfo;
import com.example.kat.pollinghelper.processor.opera.QueryScoutRecord;
import com.example.kat.pollinghelper.processor.opera.ScanBleSensor;
import com.example.kat.pollinghelper.processor.opera.UpdateSensorData;
import com.example.kat.pollinghelper.protocol.BaseStationUdpProtocol;
import com.example.kat.pollinghelper.protocol.SensorBleProtocol;
import com.example.kat.pollinghelper.ui.toast.BeautyToast;

import java.util.HashMap;
import java.util.Map;

public class ManagerService extends Service {

    private SensorBleProtocol bleProtocol;
    private Ble ble;
    private DataStorage dataStorage;
    private BluetoothAdapter.LeScanCallback onBleScannedListener = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            SensorBleProtocol.SensorParameter sensorParameter = bleProtocol.analyze(device.getAddress(), scanRecord);
            if (sensorParameter != null) {
                dataStorage.receiveSensorInfo(sensorParameter.SensorInfos);
            }
        }
    };
    private BaseStationUdpProtocol udpProtocol;
    private Communicator.OnDataReceivedListener onUdpDataReceivedListener = new Communicator.OnDataReceivedListener() {
        @Override
        public void onDataReceived(byte[] data) {
            BaseStationUdpProtocol.BaseStationInfo baseStationInfo = udpProtocol.analyze(data);
            if (baseStationInfo != null && baseStationInfo.CommandCode == BaseStationUdpProtocol.COMMAND_CODE_REQUEST_DATA) {
                dataStorage.receiveSensorInfo(baseStationInfo.SensorInfos);
            }
        }
    };
    private Udp udp;
    private Handler uiEventProcessor;
    private Map<OperaType, Operation> operationMap;
    private boolean running;
    private OperationInfo operationInfo;
    private Runnable onOperationProcess = new Runnable() {
        @Override
        public void run() {
            while (running) {
                if (operationInfo.hasOpera()) {
                    operationInfo.waitNotifier();
                } else {
                    feedbackUI(operationMap.get(operationInfo.popOpera()).execute());
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        //注意不要轻易修改调用顺序
        initProtocol();
        createDataStorage();
        launchCommunicator();
        initOperationInfo();
        createOperationMap();
        launchOperationListener();
    }

    private void createDataStorage() {
        dataStorage = new DataStorage();
    }

    private void initProtocol() {
        udpProtocol = new BaseStationUdpProtocol();
        bleProtocol = new SensorBleProtocol();
    }

    private void launchOperationListener() {
        running = true;
        Thread operationProcessor = new Thread(onOperationProcess);
        operationProcessor.start();
    }

    private void launchCommunicator() {
        //wifi
        udp = new Udp();
        udp.setDataReceivedListener(onUdpDataReceivedListener);
        if (udp.launch()) {
            udp.connect(udp.getParameter().setAddress(getString(R.string.base_station_ip)).setPort(getResources().getInteger(R.integer.base_station_port)), false);
            udp.startListen(true);
            udp.sendData(generateRequestDataCommand(), getResources().getInteger(R.integer.time_interval_request_data));
            Log.d("PollingHelper", "udp launched");
        } else {
            promptMessage(R.string.ui_prompt_udp_launch_failed);
        }

        //ble
        ble = new Ble(this);
        if (ble.launch()) {
            ble.setOnBluetoothDeviceScannedListener(onBleScannedListener);
            ble.startScan(getResources().getInteger(R.integer.time_interval_scan_ble),
                    getResources().getInteger(R.integer.time_duration_scan_ble));
            Log.d("PollingHelper", "ble launched");
        } else {
            promptMessage(R.string.ui_prompt_ble_launch_failed);
        }
    }

    private void promptMessage(int stringId) {
        BeautyToast.show(stringId);
    }

    private byte[] generateRequestDataCommand() {
        BaseStationUdpProtocol.BaseStationInfo baseStationInfo = udpProtocol.getPackageInfo();
        baseStationInfo.CommandCode = BaseStationUdpProtocol.COMMAND_CODE_REQUEST_DATA;
        return udpProtocol.assemble(baseStationInfo);
    }

    private void createOperationMap() {
        operationMap = new HashMap<>();
        operationMap.put(OperaType.OT_IMPORT_PROJECT_AND_SENSOR_CONFIGS, new ImportProjectAndSensorConfigs(operationInfo, this));
        operationMap.put(OperaType.OT_EXPORT_POLLING_PROJECT_RECORD, new ExportProjectRecord(operationInfo));
        operationMap.put(OperaType.OT_UPDATE_SENSOR_DATA, new UpdateSensorData(operationInfo, dataStorage));
        operationMap.put(OperaType.OT_EXPORT_POLLING_CONFIGS, new ExportScoutConfig(operationInfo));
        operationMap.put(OperaType.OT_EXPORT_SENSOR_CONFIG, new ExportSensorConfig(operationInfo));
        operationMap.put(OperaType.OT_EXPORT_POLLING_MISSION_RECORD, new ExportMissionRecord(operationInfo));
        operationMap.put(OperaType.OT_CREATE_POLLING_DATABASE, new EstablishScoutDatabase(operationInfo, this));
        operationMap.put(OperaType.OT_SCAN_BLE_SENSOR, new ScanBleSensor(operationInfo, ble, getResources().getInteger(R.integer.time_duration_scan_ble)));
        operationMap.put(OperaType.OT_QUERY_RECORD, new QueryScoutRecord(operationInfo));
    }

    private void initOperationInfo() {
        operationInfo = new OperationInfo();
        uiEventProcessor = new Handler();
        //operationInfo.putArgument(ArgumentTag.AT_HANDLER_UI_FEEDBACK, uiEventProcessor);
        //operationInfo.putArgument(ArgumentTag.AT_RUNNABLE_FINISH_PROCESSOR, onFinishProcessor);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ManagerBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        closeOperationListener();
        closeCommunicator();
        closeDatabase();
        super.onDestroy();
    }

    private void closeDatabase() {
        DBData.closeDatabaseEvn();
    }

    private void closeCommunicator() {
        udp.close();
        ble.stopScan();
    }

    private void closeOperationListener() {
        running = false;
        operationInfo.notifyExecutor();
    }

    private void feedbackUI(boolean executeResult) {
        Runnable processor = (Runnable)operationInfo.getArgument(executeResult ? ArgumentTag.AT_RUNNABLE_SUCCESS : ArgumentTag.AT_RUNNABLE_FAILED);
        if (processor != null && !operationInfo.isRunningContinuousOpera()) {
            uiEventProcessor.post(processor);
        }
    }

    public class ManagerBinder extends Binder {
        public OperationInfo getOperationInfo() {
            return operationInfo;
        }
    }
}
