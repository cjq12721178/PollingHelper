package com.example.kat.pollinghelper.processor.opera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.structure.config.ScoutProjectConfig;
import com.example.kat.pollinghelper.io.sqlite.InspItermCfg;
import com.example.kat.pollinghelper.io.sqlite.InspMissionCfg;
import com.example.kat.pollinghelper.io.sqlite.InspProjectCfg;
import com.example.kat.pollinghelper.io.sqlite.SensorCfg;
import com.example.kat.pollinghelper.structure.config.ScoutItemConfig;
import com.example.kat.pollinghelper.structure.config.ScoutMissionConfig;
import com.example.kat.pollinghelper.structure.config.ScoutSensorConfig;
import com.example.kat.pollinghelper.structure.config.SimpleTime;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by KAT on 2016/6/13.
 */
public class ImportProjectAndSensorConfigs extends Operation {

    public ImportProjectAndSensorConfigs(OperationInfo operationInfo, Context context) {
        super(operationInfo);
        this.context = context;
    }

    @Override
    protected boolean onExecute() {
        //实际处理
        impotSensorConfig();
        importProjectConfig();

        //debug
        //simulateImportProjectConfigsAndSensorConfigs();

        return true;
    }

    private void simulateImportProjectConfigsAndSensorConfigs() {
        List<ScoutProjectConfig> projectConfigs = new ArrayList<>();
        List<ScoutSensorConfig> sensorConfigs = new ArrayList<>();
        ScoutProjectConfig projectConfig1 = new ScoutProjectConfig("巡检项目1");
        projectConfig1.setDescription("这是巡检项目1");
        projectConfig1.getScheduledTimes().add(new SimpleTime(9, 0));
        projectConfig1.getScheduledTimes().add(new SimpleTime(14, 0));
        projectConfig1.getScheduledTimes().add(new SimpleTime(20, 0));
        ScoutMissionConfig missionConfig11 = new ScoutMissionConfig("巡检任务11");
        missionConfig11.setDescription("这是巡检项目1中的巡检任务1");
        missionConfig11.setDeviceImageData(getBitmap(R.drawable.ic_actionbar_datamode));
        ScoutSensorConfig sensorConfigUS = new ScoutSensorConfig("电压传感器");
        sensorConfigUS.setDescription("测量电压");
        sensorConfigUS.setAddress("BA28");
        ScoutItemConfig itemConfigU = new ScoutItemConfig(new Date().getTime());
        itemConfigU.setMeasureName("U");
        itemConfigU.setMeasureUnit("V");
        itemConfigU.setDescription("电压");
        itemConfigU.setSensor(sensorConfigUS);
        itemConfigU.setDownAlarm(1.0);
        itemConfigU.setUpAlarm(3.0);
        ScoutSensorConfig sensorConfigIS = new ScoutSensorConfig("电流传感器");
        sensorConfigIS.setDescription("测量电流");
        sensorConfigIS.setAddress("2345");
        ScoutItemConfig itemConfigI = new ScoutItemConfig(new Date().getTime());
        itemConfigI.setMeasureName("I");
        itemConfigI.setMeasureUnit("A");
        itemConfigI.setDescription("电流");
        itemConfigI.setSensor(sensorConfigIS);
        itemConfigI.setDownAlarm(0.3);
        itemConfigI.setUpAlarm(0.6);
        ScoutSensorConfig sensorConfigRS = new ScoutSensorConfig("电阻传感器");
        sensorConfigRS.setDescription("测量电阻");
        sensorConfigRS.setAddress("9324");
        ScoutItemConfig itemConfigR = new ScoutItemConfig(new Date().getTime());
        itemConfigR.setMeasureName("R");
        itemConfigR.setMeasureUnit("O");
        itemConfigR.setDescription("电阻");
        itemConfigR.setSensor(sensorConfigRS);
        itemConfigR.setDownAlarm(20.0);
        itemConfigR.setUpAlarm(50.0);
        missionConfig11.getItems().add(itemConfigU);
        missionConfig11.getItems().add(itemConfigI);
        missionConfig11.getItems().add(itemConfigR);
        ScoutMissionConfig missionConfig12 = new ScoutMissionConfig("实际传感器测试");
        missionConfig12.setDescription("其中的测量参数均由实际传感器通过wifi提供");
        missionConfig12.setDeviceImageData(getBitmap(R.drawable.ic_actionbar_setting));
        ScoutSensorConfig sensorConfigWD = new ScoutSensorConfig("无线温度传感器");
        sensorConfigWD.setDescription("测量温度");
        sensorConfigWD.setAddress("111-05-1066");
        ScoutItemConfig itemConfigWD = new ScoutItemConfig(new Date().getTime());
        itemConfigWD.setMeasureName("温度");
        itemConfigWD.setMeasureUnit("℃");
        itemConfigWD.setDescription("测量温度");
        itemConfigWD.setSensor(sensorConfigWD);
        itemConfigWD.setDownAlarm(35);
        itemConfigWD.setUpAlarm(60);
        ScoutSensorConfig sensorConfigWSD = new ScoutSensorConfig("无线温湿度传感器");
        sensorConfigWSD.setDescription("测量温湿度");
        sensorConfigWSD.setAddress("111-40-8003");
        ScoutItemConfig itemConfigWSD = new ScoutItemConfig(new Date().getTime());
        itemConfigWSD.setMeasureName("湿度");
        itemConfigWSD.setMeasureUnit("%Rh");
        itemConfigWSD.setDescription("测量湿度");
        itemConfigWSD.setSensor(sensorConfigWSD);
        itemConfigWSD.setDownAlarm(10);
        itemConfigWSD.setUpAlarm(20);
        ScoutSensorConfig sensorConfigMC = new ScoutSensorConfig("无线门磁传感器");
        sensorConfigMC.setDescription("测量开关状态");
        sensorConfigMC.setAddress("111-45-B322");
        ScoutItemConfig itemConfigMC = new ScoutItemConfig(new Date().getTime());
        itemConfigMC.setMeasureName("门状态");
        //itemConfigMC.setMeasureUnit("");
        itemConfigMC.setDescription("测量门是否打开");
        itemConfigMC.setSensor(sensorConfigMC);
        itemConfigMC.setDownAlarm(0);
        itemConfigMC.setUpAlarm(1);
        ScoutSensorConfig sensorConfigBleWD = new ScoutSensorConfig("智能避雷器");
        sensorConfigBleWD.setDescription("测量温度");
        sensorConfigBleWD.setAddress("111-05-CAF01000");
        ScoutItemConfig itemConfigBleWD = new ScoutItemConfig(new Date().getTime());
        itemConfigBleWD.setMeasureName("温度BLE");
        itemConfigBleWD.setMeasureUnit("℃");
        itemConfigBleWD.setDescription("测量避雷器温度");
        itemConfigBleWD.setSensor(sensorConfigBleWD);
        itemConfigBleWD.setDownAlarm(0);
        itemConfigBleWD.setUpAlarm(30);
        ScoutSensorConfig sensorConfigBleMC = new ScoutSensorConfig("门磁传感器BLE");
        sensorConfigBleMC.setDescription("测量门状态");
        sensorConfigBleMC.setAddress("111-54-64FF0400");
        ScoutItemConfig itemConfigBleMC = new ScoutItemConfig(new Date().getTime());
        itemConfigBleMC.setMeasureName("门状态BLE");
        //itemConfigBleMC.setMeasureUnit("℃");
        itemConfigBleMC.setDescription("测量门是否打开");
        itemConfigBleMC.setSensor(sensorConfigBleMC);
        itemConfigBleMC.setDownAlarm(0);
        itemConfigBleMC.setUpAlarm(1);
        ScoutSensorConfig sensorConfigBleZljsdX = new ScoutSensorConfig("重力加速度BLE-X");
        sensorConfigBleZljsdX.setDescription("测量重力加速度X轴");
        sensorConfigBleZljsdX.setAddress("111-65-8BFF0801");
        ScoutItemConfig itemConfigBleZljsdX = new ScoutItemConfig(new Date().getTime());
        itemConfigBleZljsdX.setMeasureName("重力加速度X轴");
        itemConfigBleZljsdX.setMeasureUnit("m/s2");
        itemConfigBleZljsdX.setDescription("测量重力加速度X轴");
        itemConfigBleZljsdX.setSensor(sensorConfigBleZljsdX);
        itemConfigBleZljsdX.setDownAlarm(-10);
        itemConfigBleZljsdX.setUpAlarm(10);
        ScoutSensorConfig sensorConfigBleZljsdY = new ScoutSensorConfig("重力加速度BLE-Y");
        sensorConfigBleZljsdY.setDescription("测量重力加速度Y轴");
        sensorConfigBleZljsdY.setAddress("111-65-8BFF0802");
        ScoutItemConfig itemConfigBleZljsdY = new ScoutItemConfig(new Date().getTime());
        itemConfigBleZljsdY.setMeasureName("重力加速度Y轴");
        itemConfigBleZljsdY.setMeasureUnit("m/s2");
        itemConfigBleZljsdY.setDescription("测量重力加速度Y轴");
        itemConfigBleZljsdY.setSensor(sensorConfigBleZljsdY);
        itemConfigBleZljsdY.setDownAlarm(-10);
        itemConfigBleZljsdY.setUpAlarm(10);
        ScoutSensorConfig sensorConfigBleZljsdZ = new ScoutSensorConfig("重力加速度BLE-Z");
        sensorConfigBleZljsdZ.setDescription("测量重力加速度Z轴");
        sensorConfigBleZljsdZ.setAddress("111-65-8BFF0803");
        ScoutItemConfig itemConfigBleZljsdZ = new ScoutItemConfig(new Date().getTime());
        itemConfigBleZljsdZ.setMeasureName("重力加速度Z轴");
        itemConfigBleZljsdZ.setMeasureUnit("m/s2");
        itemConfigBleZljsdZ.setDescription("测量重力加速度Z轴");
        itemConfigBleZljsdZ.setSensor(sensorConfigBleZljsdZ);
        itemConfigBleZljsdZ.setDownAlarm(-10);
        itemConfigBleZljsdZ.setUpAlarm(10);
        missionConfig12.getItems().add(itemConfigWD);
        missionConfig12.getItems().add(itemConfigWSD);
        missionConfig12.getItems().add(itemConfigMC);
        missionConfig12.getItems().add(itemConfigBleWD);
        missionConfig12.getItems().add(itemConfigBleMC);
        missionConfig12.getItems().add(itemConfigBleZljsdX);
        missionConfig12.getItems().add(itemConfigBleZljsdY);
        missionConfig12.getItems().add(itemConfigBleZljsdZ);
        ScoutMissionConfig missionConfig13 = new ScoutMissionConfig("巡检任务13");
        missionConfig13.setDescription("这是巡检项目1中的巡检任务3");
        missionConfig13.setDeviceImageData(getBitmap(R.drawable.ic_device_empty));
        projectConfig1.getMissions().add(missionConfig11);
        projectConfig1.getMissions().add(missionConfig12);
        projectConfig1.getMissions().add(missionConfig13);
        ScoutProjectConfig projectConfig2 = new ScoutProjectConfig("巡检项目2");
        projectConfig2.setDescription("这是巡检项目2");
        projectConfig2.getScheduledTimes().add(new SimpleTime(10, 0));
        projectConfig2.getScheduledTimes().add(new SimpleTime(15, 0));
        ScoutMissionConfig missionConfig21 = new ScoutMissionConfig("巡检任务21");
        missionConfig21.setDescription("这是巡检项目2中的巡检任务1");
        missionConfig21.setDeviceImageData(getBitmap(R.drawable.ic_dialog_loading));
        ScoutMissionConfig missionConfig22 = new ScoutMissionConfig("巡检任务22");
        missionConfig22.setDescription("这是巡检项目2中的巡检任务2");
        missionConfig22.setDeviceImageData(getBitmap(R.drawable.ic_save_config));
        ScoutMissionConfig missionConfig23 = new ScoutMissionConfig("巡检任务23");
        missionConfig23.setDescription("这是巡检项目2中的巡检任务3");
        missionConfig23.setDeviceImageData(getBitmap(R.drawable.ic_time_delete));
        projectConfig2.getMissions().add(missionConfig21);
        projectConfig2.getMissions().add(missionConfig22);
        projectConfig2.getMissions().add(missionConfig23);
        projectConfigs.add(projectConfig1);
        projectConfigs.add(projectConfig2);
        sensorConfigs.add(sensorConfigUS);
        sensorConfigs.add(sensorConfigIS);
        sensorConfigs.add(sensorConfigRS);
        sensorConfigs.add(sensorConfigWD);
        sensorConfigs.add(sensorConfigWSD);
        sensorConfigs.add(sensorConfigMC);
        sensorConfigs.add(sensorConfigBleWD);
        sensorConfigs.add(sensorConfigBleMC);
        sensorConfigs.add(sensorConfigBleZljsdX);
        sensorConfigs.add(sensorConfigBleZljsdY);
        sensorConfigs.add(sensorConfigBleZljsdZ);

        setValue(ArgumentTag.AT_LIST_PROJECT_CONFIG, projectConfigs);
        setValue(ArgumentTag.AT_LIST_SENSOR_CONFIG, sensorConfigs);
    }


    //从数据库载入项目巡检配置
    private void importProjectConfig() {
        List<ScoutProjectConfig> projectConfigs = new ArrayList<>();
        InspProjectCfg project = new  InspProjectCfg();
        //载入项目信息
        List<InspProjectCfg> dataList = project.query();
        for (InspProjectCfg itermProject : dataList)
        {
            ScoutProjectConfig projectConfig = new ScoutProjectConfig(itermProject.getName());
            projectConfig.setDescription(itermProject.getDesc());

            for (int time : itermProject.getTime_insp()){
                SimpleTime simpleTime = new SimpleTime(time);
                projectConfig.getScheduledTimes().add(simpleTime);
            }

            //载入任务信息
            InspMissionCfg missionDB = new InspMissionCfg();
            List<InspMissionCfg> missionList = missionDB.queryByProjectName(itermProject.getName());
            for (InspMissionCfg missionIterm : missionList) {
                ScoutMissionConfig missionConfig = new ScoutMissionConfig(missionIterm.getName());
                missionConfig.setDescription(missionIterm.getDesc());
                missionConfig.setDeviceImageData(missionIterm.getDeviceImageData());

                //载入条目信息
                InspItermCfg InspIterm = new InspItermCfg();
                List<InspItermCfg> itermList = InspIterm.queryByMission(missionIterm.getName());
                for (InspItermCfg iterm : itermList) {
                    ScoutItemConfig itemConfig = new ScoutItemConfig(iterm.getId());
                    itemConfig.setDescription(iterm.getDesc());
                    itemConfig.setMeasureName(iterm.getMeasureName());
                    itemConfig.setMeasureUnit(iterm.getMeasureUnit());
                    itemConfig.setDownAlarm(iterm.getDown_alarm());
                    itemConfig.setUpAlarm(iterm.getUp_alarm());
                    itemConfig.setSensor(getSensor(iterm.getName_sensor_measure()));

                    missionConfig.getItems().add(itemConfig);
                }
                projectConfig.getMissions().add(missionConfig);
            }
            projectConfigs.add(projectConfig);
        }

        setValue(ArgumentTag.AT_LIST_PROJECT_CONFIG, projectConfigs);
    }

    private ScoutSensorConfig getSensor(String name) {
        SensorCfg sensorCfg = new SensorCfg();
        SensorCfg iterm = sensorCfg.query(name);
        if (iterm != null)
        {
            ScoutSensorConfig sensorConfig= new ScoutSensorConfig(iterm.getName());
            sensorConfig.setDescription(iterm.getDesc());
            sensorConfig.setAddress(iterm.getAddr_text());
            return  sensorConfig;
        }

        return  new ScoutSensorConfig("");
    }

    private void impotSensorConfig() {
        List<ScoutSensorConfig> sensorConfigs = new ArrayList<>();

        SensorCfg data1 = new SensorCfg();

        List<SensorCfg> dataList = data1.query();
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (SensorCfg iterm : dataList)
        {
            ScoutSensorConfig sensorConfig= new ScoutSensorConfig(iterm.getName());
            sensorConfig.setDescription(iterm.getDesc());
            sensorConfig.setAddress(iterm.getAddr_text());
            sensorConfigs.add(sensorConfig);
        }

        setValue(ArgumentTag.AT_LIST_SENSOR_CONFIG, sensorConfigs);
    }


    private byte[] getBitmap(int resID) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitmapFactory.decodeResource(context.getResources(), resID).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private Context context;
}
