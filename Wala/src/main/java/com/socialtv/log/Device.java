package com.socialtv.log;

import java.util.List;

/**
 * Created by wlanjie on 14/11/7.
 */
public class Device {
    private String deviceId;
    private String phoneModel;
    private List<String> carrier;
    private List<String> network;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public List<String> getCarrier() {
        return carrier;
    }

    public void setCarrier(List<String> carrier) {
        this.carrier = carrier;
    }

    public List<String> getNetwork() {
        return network;
    }

    public void setNetwork(List<String> network) {
        this.network = network;
    }
}
