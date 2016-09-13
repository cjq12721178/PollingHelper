package com.example.kat.pollinghelper.bean.config;

import com.example.kat.pollinghelper.protocol.SensorBleInfo;
import com.example.kat.pollinghelper.protocol.SensorDataType;
import com.example.kat.pollinghelper.protocol.SensorUdpInfo;

/**
 * Created by KAT on 2016/5/4.
 */
public class ScoutSensorConfig {
    public ScoutSensorConfig(String name) {
        this.name = name;
    }

    public ScoutSensorConfig() {
        this("");
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(String address) {
        this.address = address;
        //暂时这样处理，以后有时间，修改数据库定义
        if (address != null) {
            String[] addressInfo = address.split("-");
            if (addressInfo.length == 3) {
                byte dataTypeValue = (byte)Integer.parseInt(addressInfo[1], 16);
                type = addressInfo[2].length() == 4 ?
                        SensorUdpInfo.getDataType(dataTypeValue) :
                        SensorBleInfo.getDataType(dataTypeValue);
            }
        }
    }

    public SensorDataType getType() {
        return type;
    }

    @Override
    public String toString() {
        return getName();
    }

    private String name;
    private String description;
    private String address;
    private SensorDataType type;
}
