package com.example.kat.pollinghelper.processor.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.kat.pollinghelper.processor.opera.ModifyBaseStationIpOrPort;
import com.example.kat.pollinghelper.processor.opera.ModifyScanBleCycleOrDuration;
import com.example.kat.pollinghelper.processor.opera.ModifyUdpDataRequestCycle;
import com.example.kat.pollinghelper.processor.opera.OperaType;
import com.example.kat.pollinghelper.processor.opera.Operation;
import com.example.kat.pollinghelper.processor.opera.OperationInfo;
import com.example.kat.pollinghelper.processor.opera.QueryScoutRecord;
import com.example.kat.pollinghelper.processor.opera.RequestSensorCollection;
import com.example.kat.pollinghelper.processor.opera.ScanBleSensor;
import com.example.kat.pollinghelper.processor.opera.UpdateSensorData;
import com.example.kat.pollinghelper.protocol.BaseStationUdpProtocol;
import com.example.kat.pollinghelper.protocol.SensorBleInfo;
import com.example.kat.pollinghelper.protocol.SensorBleProtocol;
import com.example.kat.pollinghelper.protocol.SensorDataType;
import com.example.kat.pollinghelper.protocol.SensorUdpInfo;
import com.example.kat.pollinghelper.ui.toast.BeautyToast;
import com.example.kat.pollinghelper.utility.Converter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ManagerService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        //注意不要轻易修改调用顺序
        initProtocol();
        importSensorDataTypeConfig();
        createDataStorage();
        launchCommunicator();
        initOperationInfo();
        createOperationMap();
        launchOperationListener();
    }

    private void importSensorDataTypeConfig() {
        setSensorDataTypeMap(getString(R.string.file_data_type_udp), true);
        setSensorDataTypeMap(getString(R.string.file_data_type_ble), false);
    }

    private void setSensorDataTypeMap(String configFileName, boolean udpOrBle) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            InputStream is = getAssets().open(configFileName);
            SAXParser parser = factory.newSAXParser();
            SensorDataType.Handler dataTypeHandler = SensorDataType.getHandler();
            parser.parse(is, dataTypeHandler);
            if (udpOrBle) {
                SensorUdpInfo.setDataTypeMap(dataTypeHandler.getDataTypeMap());
            } else {
                SensorBleInfo.setDataTypeMap(dataTypeHandler.getDataTypeMap(),
                        dataTypeHandler.getMeasureNameMap());
            }
        } catch (Exception e) {
            promptMessage(e.getMessage());
        }
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
        //从配置文件获取参数
        SharedPreferences configs = getSharedPreferences(getString(R.string.file_function_setting), MODE_PRIVATE);
        String ip = configs.getString(getString(R.string.key_ip), getString(R.string.base_station_ip));
        int port = Converter.stringToInt(configs.getString(getString(R.string.key_port), null),
                getResources().getInteger(R.integer.base_station_port));
        int requestDataCycle = Converter.stringToInt(configs.getString(getString(R.string.key_data_request_cycle), null),
                getResources().getInteger(R.integer.time_interval_request_data));
        int scanBleCycle = Converter.minuteToMillisecond(Converter.stringToInt(configs.getString(getString(R.string.key_scan_cycle), null),
                getResources().getInteger(R.integer.time_interval_scan_ble_communicator)));
        int scanBleDuration = Converter.secondToMillisecond(Converter.stringToInt(configs.getString(getString(R.string.key_scan_duration), null),
                getResources().getInteger(R.integer.time_duration_scan_ble_communicator)));

        Log.d("PollingHelper", "ip = " + ip);
        Log.d("PollingHelper", "port = " + port);
        Log.d("PollingHelper", "requestDataCycle = " + requestDataCycle);
        Log.d("PollingHelper", "scanBleCycle = " + scanBleCycle);
        Log.d("PollingHelper", "scanBleDuration = " + scanBleDuration);
        //wifi
        udp = new Udp();
        udp.setDataReceivedListener(onUdpDataReceivedListener);
        if (udp.launch()) {
            udp.connect(udp.getParameter().setAddress(ip).setPort(port), false);
            udp.startListen(true);
            udp.sendData(generateRequestDataCommand(), requestDataCycle);
            //Log.d("PollingHelper", "udp launched");
        } else {
            promptMessage(R.string.ui_prompt_udp_launch_failed);
        }

        //ble
        ble = new Ble(this);
        if (ble.launch()) {
            ble.setOnBluetoothDeviceScannedListener(onBleScannedListener);
            ble.startScan(scanBleCycle, scanBleDuration);
            //Log.d("PollingHelper", "ble launched");
        } else {
            promptMessage(R.string.ui_prompt_ble_launch_failed);
        }
    }

    private void promptMessage(int stringId) {
        BeautyToast.show(stringId);
    }

    private void promptMessage(String msg) {
        BeautyToast.show(msg);
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
        operationMap.put(OperaType.OT_SCAN_BLE_SENSOR, new ScanBleSensor(operationInfo, ble, this));
        operationMap.put(OperaType.OT_QUERY_RECORD, new QueryScoutRecord(operationInfo));
        operationMap.put(OperaType.OT_REQUEST_SENSOR_COLLECTION, new RequestSensorCollection(operationInfo, dataStorage));
        ModifyBaseStationIpOrPort modifyBaseStationIpOrPort = new ModifyBaseStationIpOrPort(operationInfo, udp, this);
        operationMap.put(OperaType.OT_MODIFY_BASE_STATION_IP, modifyBaseStationIpOrPort);
        operationMap.put(OperaType.OT_MODIFY_BASE_STATION_PORT, modifyBaseStationIpOrPort);
        operationMap.put(OperaType.OT_MODIFY_DATA_REQUEST_CYCLE, new ModifyUdpDataRequestCycle(operationInfo, udp, this));
        ModifyScanBleCycleOrDuration modifyScanBleCycleOrDuration = new ModifyScanBleCycleOrDuration(operationInfo, ble, this);
        operationMap.put(OperaType.OT_MODIFY_SCAN_BLE_CYCLE, modifyScanBleCycleOrDuration);
        operationMap.put(OperaType.OT_MODIFY_SCAN_BLE_DURATION, modifyScanBleCycleOrDuration);
    }

    private void initOperationInfo() {
        operationInfo = new OperationInfo();
        uiEventProcessor = new Handler();
        //operationInfo.putArgument(ArgumentTag.AT_DATA_LISTENER, dataStorage.getSensorList());
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

    private void execute(Operation operation) {
        //执行命令
        boolean result = operation.execute();
        //处理意外错误信息
        if (!result) {
            String accidentErrorInfo = operation.getErrorMessageForOnce();
            if (accidentErrorInfo != null) {
                promptMessage(accidentErrorInfo);
            }
        }
        //处理执行结果
        if (!operationInfo.isRunningContinuousOpera()) {
            Runnable processor = (Runnable)operationInfo.getArgument(result ? ArgumentTag.AT_RUNNABLE_SUCCESS : ArgumentTag.AT_RUNNABLE_FAILED);
            if (processor != null) {
                uiEventProcessor.post(processor);
            }
        }
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

    private BluetoothAdapter.LeScanCallback onBleScannedListener = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            SensorBleProtocol.SensorParameter sensorParameter = bleProtocol.analyze(device.getAddress(), scanRecord);
            if (sensorParameter != null) {
                dataStorage.receiveSensorInfo(sensorParameter.SensorInfos);
            }
        }
    };

    private Communicator.OnDataReceivedListener onUdpDataReceivedListener = new Communicator.OnDataReceivedListener() {
        @Override
        public void onDataReceived(byte[] data) {
            BaseStationUdpProtocol.BaseStationInfo baseStationInfo = udpProtocol.analyze(data);
            if (baseStationInfo != null && baseStationInfo.CommandCode == BaseStationUdpProtocol.COMMAND_CODE_REQUEST_DATA) {
                dataStorage.receiveSensorInfo(baseStationInfo.SensorInfos);
            }
        }
    };

    private Runnable onOperationProcess = new Runnable() {
        @Override
        public void run() {
            while (running) {
                if (operationInfo.hasOpera()) {
                    operationInfo.waitNotifier();
                } else {
                    execute(operationMap.get(operationInfo.popOpera()));
                }
            }
        }
    };

    private SensorBleProtocol bleProtocol;
    private Ble ble;
    private DataStorage dataStorage;
    private BaseStationUdpProtocol udpProtocol;
    private Udp udp;
    private Handler uiEventProcessor;
    private Map<OperaType, Operation> operationMap;
    private boolean running;
    private OperationInfo operationInfo;
}
