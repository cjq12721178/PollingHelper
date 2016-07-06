package com.example.kat.pollinghelper.processor.opera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.io.sqlite.InspItermCfg;
import com.example.kat.pollinghelper.io.sqlite.InspMissionCfg;
import com.example.kat.pollinghelper.io.sqlite.InspProjectCfg;
import com.example.kat.pollinghelper.io.sqlite.SensorCfg;
import com.example.kat.pollinghelper.fuction.config.PollingItemConfig;
import com.example.kat.pollinghelper.fuction.config.PollingMissionConfig;
import com.example.kat.pollinghelper.fuction.config.PollingProjectConfig;
import com.example.kat.pollinghelper.fuction.config.PollingSensorConfig;
import com.example.kat.pollinghelper.fuction.config.SimpleTime;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by KAT on 2016/6/13.
 */
public class ImportProjectAndSensorConfigs extends Operation {

    public ImportProjectAndSensorConfigs(Context context) {
        this.context = context;
    }

    @Override
    protected boolean onPreExecute(OperationInfo operationInfo) {
        uiProcessor = (Runnable)operationInfo.getArgument(ArgumentTag.AT_RUNNABLE_IMPORT_PROJECT_AND_SENSOR_CONFIGS);
        return super.onPreExecute(operationInfo);
    }

    @Override
    protected void onExecute() {
        //实际处理
        impotSensorConfig();
        importProjectConfig();
        //TODO 搜索所有现有巡检项目的最新一次的巡检记录
        //注意，不是最近一次巡检时间的不要
        //如巡检项目有10:00,14:00,18:00三个巡检时间，当前时间为15:00，则只要14:00那一次巡检记录，
        //若搜索出来没有14:00的巡检记录，则不用添加到结果中
        //可以通过projectConfig.getCurrentScheduledTime(System.currentTimeMillis())
        //方式来获取预设巡检时间，将其作为搜索条件即可


        //debug
//        List<PollingProjectConfig> projectConfigs = new ArrayList<>();
//        List<PollingSensorConfig> sensorConfigs = new ArrayList<>();
//        PollingProjectConfig projectConfig1 = new PollingProjectConfig("巡检项目1");
//        projectConfig1.setDescription("这是巡检项目1");
//        projectConfig1.getScheduledTimes().add(new SimpleTime(9, 0));
//        projectConfig1.getScheduledTimes().add(new SimpleTime(14, 0));
//        projectConfig1.getScheduledTimes().add(new SimpleTime(20, 0));
//        PollingMissionConfig missionConfig11 = new PollingMissionConfig("巡检任务11");
//        missionConfig11.setDescription("这是巡检项目1中的巡检任务1");
//        missionConfig11.setDeviceImageData(getBitmap(R.drawable.i1));
//        PollingSensorConfig sensorConfigUS = new PollingSensorConfig("电压传感器");
//        sensorConfigUS.setDescription("测量电压");
//        sensorConfigUS.setAddress("BA28");
//        PollingItemConfig itemConfigU = new PollingItemConfig(new Date().getTime());
//        itemConfigU.setMeasureName("U");
//        itemConfigU.setMeasureUnit("V");
//        itemConfigU.setDescription("电压");
//        itemConfigU.setSensor(sensorConfigUS);
//        itemConfigU.setDownAlarm(1.0);
//        itemConfigU.setUpAlarm(3.0);
//        PollingSensorConfig sensorConfigIS = new PollingSensorConfig("电流传感器");
//        sensorConfigIS.setDescription("测量电流");
//        sensorConfigIS.setAddress("2345");
//        PollingItemConfig itemConfigI = new PollingItemConfig(new Date().getTime());
//        itemConfigI.setMeasureName("I");
//        itemConfigI.setMeasureUnit("A");
//        itemConfigI.setDescription("电流");
//        itemConfigI.setSensor(sensorConfigIS);
//        itemConfigI.setDownAlarm(0.3);
//        itemConfigI.setUpAlarm(0.6);
//        PollingSensorConfig sensorConfigRS = new PollingSensorConfig("电阻传感器");
//        sensorConfigRS.setDescription("测量电阻");
//        sensorConfigRS.setAddress("9324");
//        PollingItemConfig itemConfigR = new PollingItemConfig(new Date().getTime());
//        itemConfigR.setMeasureName("R");
//        itemConfigR.setMeasureUnit("O");
//        itemConfigR.setDescription("电阻");
//        itemConfigR.setSensor(sensorConfigRS);
//        itemConfigR.setDownAlarm(20.0);
//        itemConfigR.setUpAlarm(50.0);
//        missionConfig11.getItems().add(itemConfigU);
//        missionConfig11.getItems().add(itemConfigI);
//        missionConfig11.getItems().add(itemConfigR);
//        PollingMissionConfig missionConfig12 = new PollingMissionConfig("实际传感器测试");
//        missionConfig12.setDescription("其中的测量参数均由实际传感器通过wifi提供");
//        missionConfig12.setDeviceImageData(getBitmap(R.drawable.i2));
//        PollingSensorConfig sensorConfigWD = new PollingSensorConfig("无线温度传感器");
//        sensorConfigWD.setDescription("测量温度");
//        sensorConfigWD.setAddress("111-05-1066");
//        PollingItemConfig itemConfigWD = new PollingItemConfig(new Date().getTime());
//        itemConfigWD.setMeasureName("温度");
//        itemConfigWD.setMeasureUnit("℃");
//        itemConfigWD.setDescription("测量温度");
//        itemConfigWD.setSensor(sensorConfigWD);
//        itemConfigWD.setDownAlarm(35);
//        itemConfigWD.setUpAlarm(60);
//        PollingSensorConfig sensorConfigWSD = new PollingSensorConfig("无线温湿度传感器");
//        sensorConfigWSD.setDescription("测量温湿度");
//        sensorConfigWSD.setAddress("111-40-8003");
//        PollingItemConfig itemConfigWSD = new PollingItemConfig(new Date().getTime());
//        itemConfigWSD.setMeasureName("湿度");
//        itemConfigWSD.setMeasureUnit("%Rh");
//        itemConfigWSD.setDescription("测量湿度");
//        itemConfigWSD.setSensor(sensorConfigWSD);
//        itemConfigWSD.setDownAlarm(10);
//        itemConfigWSD.setUpAlarm(20);
//        PollingSensorConfig sensorConfigMC = new PollingSensorConfig("无线门磁传感器");
//        sensorConfigMC.setDescription("测量开关状态");
//        sensorConfigMC.setAddress("111-45-B322");
//        PollingItemConfig itemConfigMC = new PollingItemConfig(new Date().getTime());
//        itemConfigMC.setMeasureName("门状态");
//        //itemConfigMC.setMeasureUnit("");
//        itemConfigMC.setDescription("测量门是否打开");
//        itemConfigMC.setSensor(sensorConfigMC);
//        itemConfigMC.setDownAlarm(0);
//        itemConfigMC.setUpAlarm(1);
//        PollingSensorConfig sensorConfigBleWD = new PollingSensorConfig("智能避雷器");
//        sensorConfigBleWD.setDescription("测量温度");
//        sensorConfigBleWD.setAddress("111-05-CAF01000");
//        PollingItemConfig itemConfigBleWD = new PollingItemConfig(new Date().getTime());
//        itemConfigBleWD.setMeasureName("温度BLE");
//        itemConfigBleWD.setMeasureUnit("℃");
//        itemConfigBleWD.setDescription("测量避雷器温度");
//        itemConfigBleWD.setSensor(sensorConfigBleWD);
//        itemConfigBleWD.setDownAlarm(0);
//        itemConfigBleWD.setUpAlarm(30);
//        PollingSensorConfig sensorConfigBleMC = new PollingSensorConfig("门磁传感器BLE");
//        sensorConfigBleMC.setDescription("测量门状态");
//        sensorConfigBleMC.setAddress("111-54-64FF0400");
//        PollingItemConfig itemConfigBleMC = new PollingItemConfig(new Date().getTime());
//        itemConfigBleMC.setMeasureName("门状态BLE");
//        //itemConfigBleMC.setMeasureUnit("℃");
//        itemConfigBleMC.setDescription("测量门是否打开");
//        itemConfigBleMC.setSensor(sensorConfigBleMC);
//        itemConfigBleMC.setDownAlarm(0);
//        itemConfigBleMC.setUpAlarm(1);
//        PollingSensorConfig sensorConfigBleZljsdX = new PollingSensorConfig("重力加速度BLE-X");
//        sensorConfigBleZljsdX.setDescription("测量重力加速度X轴");
//        sensorConfigBleZljsdX.setAddress("111-65-8BFF0801");
//        PollingItemConfig itemConfigBleZljsdX = new PollingItemConfig(new Date().getTime());
//        itemConfigBleZljsdX.setMeasureName("重力加速度X轴");
//        itemConfigBleZljsdX.setMeasureUnit("m/s2");
//        itemConfigBleZljsdX.setDescription("测量重力加速度X轴");
//        itemConfigBleZljsdX.setSensor(sensorConfigBleZljsdX);
//        itemConfigBleZljsdX.setDownAlarm(-10);
//        itemConfigBleZljsdX.setUpAlarm(10);
//        PollingSensorConfig sensorConfigBleZljsdY = new PollingSensorConfig("重力加速度BLE-Y");
//        sensorConfigBleZljsdY.setDescription("测量重力加速度Y轴");
//        sensorConfigBleZljsdY.setAddress("111-65-8BFF0802");
//        PollingItemConfig itemConfigBleZljsdY = new PollingItemConfig(new Date().getTime());
//        itemConfigBleZljsdY.setMeasureName("重力加速度Y轴");
//        itemConfigBleZljsdY.setMeasureUnit("m/s2");
//        itemConfigBleZljsdY.setDescription("测量重力加速度Y轴");
//        itemConfigBleZljsdY.setSensor(sensorConfigBleZljsdY);
//        itemConfigBleZljsdY.setDownAlarm(-10);
//        itemConfigBleZljsdY.setUpAlarm(10);
//        PollingSensorConfig sensorConfigBleZljsdZ = new PollingSensorConfig("重力加速度BLE-Z");
//        sensorConfigBleZljsdZ.setDescription("测量重力加速度Z轴");
//        sensorConfigBleZljsdZ.setAddress("111-65-8BFF0803");
//        PollingItemConfig itemConfigBleZljsdZ = new PollingItemConfig(new Date().getTime());
//        itemConfigBleZljsdZ.setMeasureName("重力加速度Z轴");
//        itemConfigBleZljsdZ.setMeasureUnit("m/s2");
//        itemConfigBleZljsdZ.setDescription("测量重力加速度Z轴");
//        itemConfigBleZljsdZ.setSensor(sensorConfigBleZljsdZ);
//        itemConfigBleZljsdZ.setDownAlarm(-10);
//        itemConfigBleZljsdZ.setUpAlarm(10);
//        missionConfig12.getItems().add(itemConfigWD);
//        missionConfig12.getItems().add(itemConfigWSD);
//        missionConfig12.getItems().add(itemConfigMC);
//        missionConfig12.getItems().add(itemConfigBleWD);
//        missionConfig12.getItems().add(itemConfigBleMC);
//        missionConfig12.getItems().add(itemConfigBleZljsdX);
//        missionConfig12.getItems().add(itemConfigBleZljsdY);
//        missionConfig12.getItems().add(itemConfigBleZljsdZ);
//        PollingMissionConfig missionConfig13 = new PollingMissionConfig("巡检任务13");
//        missionConfig13.setDescription("这是巡检项目1中的巡检任务3");
//        missionConfig13.setDeviceImageData(getBitmap(R.drawable.i3));
//        projectConfig1.getMissions().add(missionConfig11);
//        projectConfig1.getMissions().add(missionConfig12);
//        projectConfig1.getMissions().add(missionConfig13);
//        PollingProjectConfig projectConfig2 = new PollingProjectConfig("巡检项目2");
//        projectConfig2.setDescription("这是巡检项目2");
//        projectConfig2.getScheduledTimes().add(new SimpleTime(10, 0));
//        projectConfig2.getScheduledTimes().add(new SimpleTime(15, 0));
//        PollingMissionConfig missionConfig21 = new PollingMissionConfig("巡检任务21");
//        missionConfig21.setDescription("这是巡检项目2中的巡检任务1");
//        missionConfig21.setDeviceImageData(getBitmap(R.drawable.i2));
//        PollingMissionConfig missionConfig22 = new PollingMissionConfig("巡检任务22");
//        missionConfig22.setDescription("这是巡检项目2中的巡检任务2");
//        missionConfig22.setDeviceImageData(getBitmap(R.drawable.i3));
//        PollingMissionConfig missionConfig23 = new PollingMissionConfig("巡检任务23");
//        missionConfig23.setDescription("这是巡检项目2中的巡检任务3");
//        missionConfig23.setDeviceImageData(getBitmap(R.drawable.i1));
//        projectConfig2.getMissions().add(missionConfig21);
//        projectConfig2.getMissions().add(missionConfig22);
//        projectConfig2.getMissions().add(missionConfig23);
//        projectConfigs.add(projectConfig1);
//        projectConfigs.add(projectConfig2);
//        sensorConfigs.add(sensorConfigUS);
//        sensorConfigs.add(sensorConfigIS);
//        sensorConfigs.add(sensorConfigRS);
//
//        setValue(ArgumentTag.AT_LIST_PROJECT_CONFIG, projectConfigs);
//        setValue(ArgumentTag.AT_LIST_SENSOR_CONFIG, sensorConfigs);
    }

    //从数据库载入项目巡检配置
    private void importProjectConfig() {
        List<PollingProjectConfig> projectConfigs = new ArrayList<>();
        InspProjectCfg project = new  InspProjectCfg();
        //载入项目信息
        List<InspProjectCfg> dataList = project.query();
        for (InspProjectCfg itermProject : dataList)
        {
            PollingProjectConfig projectConfig = new PollingProjectConfig(itermProject.getName());
            projectConfig.setDescription(itermProject.getDesc());

            for (int time : itermProject.getTime_insp()){
                SimpleTime simpleTime = new SimpleTime(time);
                projectConfig.getScheduledTimes().add(simpleTime);
            }

            //载入任务信息
            InspMissionCfg missionDB = new InspMissionCfg();
            List<InspMissionCfg> missionList = missionDB.queryByProjectName(itermProject.getName());
            for (InspMissionCfg missionIterm : missionList) {
                PollingMissionConfig missionConfig = new PollingMissionConfig(missionIterm.getName());
                missionConfig.setDescription(missionIterm.getDesc());
                missionConfig.setDeviceImageData(missionIterm.getDeviceImageData());

                //载入条目信息
                InspItermCfg InspIterm = new InspItermCfg();
                List<InspItermCfg> itermList = InspIterm.queryByMission(missionIterm.getName());
                for (InspItermCfg iterm : itermList) {
                    PollingItemConfig itemConfig = new PollingItemConfig(iterm.getId());
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

    private PollingSensorConfig getSensor(String name) {
        SensorCfg sensorCfg = new SensorCfg();
        SensorCfg iterm = sensorCfg.query(name);
        if (iterm != null)
        {
            PollingSensorConfig sensorConfig= new PollingSensorConfig(iterm.getName());
            sensorConfig.setDescription(iterm.getDesc());
            sensorConfig.setAddress(iterm.getAddr_text());
            return  sensorConfig;
        }

        return  new PollingSensorConfig("");
    }

    private void impotSensorConfig() {
        List<PollingSensorConfig> sensorConfigs = new ArrayList<>();

        SensorCfg data1 = new SensorCfg();

        List<SensorCfg> dataList = data1.query();
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (SensorCfg iterm : dataList)
        {
            PollingSensorConfig sensorConfig= new PollingSensorConfig(iterm.getName());
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
