package net.uoit.distributedsystems.soundsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }
            Log.d(WiFiDirectActivity.TAG, "P2P peers changed");
        }
    }
}
