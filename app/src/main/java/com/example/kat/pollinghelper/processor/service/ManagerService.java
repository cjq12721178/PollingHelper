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
import com.example.kat.pollinghelper.processor.opera.Command;
import com.example.kat.pollinghelper.processor.opera.EstablishPollingDatabase;
import com.example.kat.pollinghelper.processor.opera.ExportMissionRecord;
import com.example.kat.pollinghelper.processor.opera.ExportPollingConfig;
import com.example.kat.pollinghelper.processor.opera.ExportProjectRecord;
import com.example.kat.pollinghelper.processor.opera.ExportSensorConfig;
import com.example.kat.pollinghelper.processor.opera.FinishProcess;
import com.example.kat.pollinghelper.processor.opera.ImportProjectAndSensorConfigs;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.processor.opera.OperationInfo;
import com.example.kat.pollinghelper.processor.opera.ScanBleSensor;
import com.example.kat.pollinghelper.processor.opera.UpdateSensorData;
import com.example.kat.pollinghelper.protocol.BaseStationUdpProtocol;
import com.example.kat.pollinghelper.protocol.SensorBleProtocol;
import com.example.kat.pollinghelper.ui.structure.ElseFunctionListItem;
import com.example.kat.pollinghelper.ui.toast.BeautyToast;

import java.util.HashMap;
import java.util.Map;

public class ManagerService extends Service {

    public class ManagerBinder extends Binder {
        public OperationInfo getOperationInfo() {
            return operationInfo;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //注意不要轻易修改调用顺序
        initProtocol();
        createDataStorage();
        launchCommunicator();
        createOperationMap();
        initOperationInfo();
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
        operationMap.put(OperaType.OT_IMPORT_PROJECT_AND_SENSOR_CONFIGS, new ImportProjectAndSensorConfigs(this));
        operationMap.put(OperaType.OT_FINISH_PROCESS, new FinishProcess());
        operationMap.put(OperaType.OT_EXPORT_POLLING_PROJECT_RECORD, new ExportProjectRecord());
        operationMap.put(OperaType.OT_UPDATE_SENSOR_DATA, new UpdateSensorData(dataStorage));
        operationMap.put(OperaType.OT_EXPORT_POLLING_CONFIGS, new ExportPollingConfig());
        operationMap.put(OperaType.OT_EXPORT_SENSOR_CONFIG, new ExportSensorConfig());
        operationMap.put(OperaType.OT_EXPORT_POLLING_MISSION_RECORD, new ExportMissionRecord());
        operationMap.put(OperaType.OT_CREATE_POLLING_DATABASE, new EstablishPollingDatabase(this));
        operationMap.put(OperaType.OT_SCAN_BLE_SENSOR, new ScanBleSensor(ble, getResources().getInteger(R.integer.time_duration_scan_ble)));
    }

    private void initOperationInfo() {
        operationInfo = new OperationInfo();
        uiEventProcessor = new Handler();
        operationInfo.putArgument(ArgumentTag.AT_HANDLER_UI_FEEDBACK, uiEventProcessor);
        operationInfo.putArgument(ArgumentTag.AT_RUNNABLE_FINISH_PROCESSOR, onFinishProcessor);
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
        operationInfo.notifyExecutor(OperaType.OT_FINISH_PROCESS);
    }

    private Communicator.OnDataReceivedListener onUdpDataReceivedListener = new Communicator.OnDataReceivedListener() {
        @Override
        public void onDataReceived(byte[] data) {
            BaseStationUdpProtocol.BaseStationInfo baseStationInfo = udpProtocol.analyze(data);
            if (baseStationInfo != null && baseStationInfo.CommandCode == BaseStationUdpProtocol.COMMAND_CODE_REQUEST_DATA) {
                dataStorage.receiveSensorInfo(baseStationInfo.SensorInfos);
            }
        }
    };

    private Runnable onFinishProcessor = new Runnable() {
        @Override
        public void run() {
            running = false;
        }
    };

    private Runnable onOperationProcess = new Runnable() {
        @Override
        public void run() {
            while (running) {
                if (operationInfo.getOperaQueue().isEmpty()) {
                    operationInfo.waitNotifier();
                } else {
                    operationMap.get(operationInfo.getOperaQueue().remove()).execute(operationInfo);
                }
            }
        }
    };

    private BluetoothAdapter.LeScanCallback onBleScannedListener = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            SensorBleProtocol.SensorParameter sensorParameter = bleProtocol.analyze(device.getAddress(), scanRecord);
            if (sensorParameter != null) {
                dataStorage.receiveSensorInfo(sensorParameter.SensorInfos);
            }
        }
    };

    private byte[] generateBleOriginData(String address, byte[] scanRecord) {
        byte[] originData = null;
        if (address != null && scanRecord != null) {
            String[] addresses = address.split(":");
            if (address != null) {
                try {
                    originData = new byte[addresses.length + scanRecord.length];
                    for (int i = 0;i < addresses.length;++i) {
                        originData[i] = Byte.valueOf(addresses[i]);
                    }
                    System.arraycopy(scanRecord, 0, originData, addresses.length, scanRecord.length);
                } catch (Exception e) {
                    originData = null;
                }
            }
        }
        return originData;
    }

    private SensorBleProtocol bleProtocol;
    private Ble ble;
    private DataStorage dataStorage;
    private BaseStationUdpProtocol udpProtocol;
    private Udp udp;
    private Handler uiEventProcessor;
    private Map<OperaType, Command> operationMap;
    private boolean running;
    private OperationInfo operationInfo;
}
