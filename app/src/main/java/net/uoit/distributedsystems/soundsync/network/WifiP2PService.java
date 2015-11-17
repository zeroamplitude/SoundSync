package net.uoit.distributedsystems.soundsync.network;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by nicholas on 17/11/15.
 */
public class WifiP2PService {

    WifiP2pDevice device;
    String instanceName = null;
    String serviceRegType = null;

    public WifiP2PService() {
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    public void setDevice(WifiP2pDevice device) {
        this.device = device;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getServiceRegType() {
        return serviceRegType;
    }

    public void setServiceRegType(String serviceRegType) {
        this.serviceRegType = serviceRegType;
    }
}
