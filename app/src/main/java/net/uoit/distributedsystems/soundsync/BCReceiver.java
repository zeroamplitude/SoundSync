package net.uoit.distributedsystems.soundsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by ubuntu on 28/10/15.
 */
public class BCReceiver extends BroadcastReceiver {

    WifiP2pManager mManager;
    WifiP2pManager.Channel channel;

    public BCReceiver(WifiP2pManager mManager, WifiP2pManager.Channel channel) {
        this.mManager = mManager;
        this.channel = channel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO Receive things
    }
}
