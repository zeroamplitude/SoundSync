package net.uoit.distributedsystems.soundsync.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.uoit.distributedsystems.soundsync.R;
import net.uoit.distributedsystems.soundsync.app.audio.SoundFragment;
import net.uoit.distributedsystems.soundsync.network.WifiDirectBrodcastReciever;
import net.uoit.distributedsystems.soundsync.network.WifiServicesList;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HostActivity extends Activity implements ConnectionInfoListener {

    public static final String TAG = "Host";

    public static final String TXTRECORD_PROP_AVAILABLE =  "available";
    public static final String SERVICE_INSTANCE = "SoundSync";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    private final IntentFilter intentFilter = new IntentFilter();

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;

    private TextView statusTxtView;

    private WifiServicesList servicesList;
    private SoundFragment soundFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusTxtView = (TextView) findViewById(R.id.status_text);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        registerService();

//        servicesList = new WifiServicesList();
//        getFragmentManager().beginTransaction()
//                .add(R.id.container, servicesList, "services").commit();
    }

    @Override
    protected void onRestart() {
        Fragment frag = getFragmentManager().findFragmentByTag("services");
        if (frag != null) {
            getFragmentManager().beginTransaction().remove(frag).commit();
        }
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WifiDirectBrodcastReciever(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        if (manager != null && channel != null) {
            manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int reason) {
                    Log.e(TAG, "Disconnect failed. Reason: " + reason);
                }
            });
        }

        super.onStop();
    }

    private void registerService() {
        Map<String, String> record = new HashMap<>();
        record.put(TXTRECORD_PROP_AVAILABLE, "visible");

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
                SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, service, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                appendStatus("Added Local Service");
            }

            @Override
            public void onFailure(int reason) {
                appendStatus("Failed to add Service");
            }
        });
    }

    private void appendStatus(String status) {
        String current = statusTxtView.getText().toString();
        statusTxtView.setText(current + "\n" + status);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Thread thread = null;
        String role = "";

        if (info.isGroupOwner) {
            Log.d(TAG, "Connected ad group owner");

            role = "control";

            try {
                AssetFileDescriptor fd = getAssets().openFd("audio1.mp3");

//                thread = new Server(fd);
//                thread.start();
            } catch (IOException e) {
                Log.d(TAG, "Failed to create a server thread - " + e.getMessage());
                return;
            }

        } else {

            Log.d(TAG, "Connected as Peer");
            role = "peer";
//            thread = new Peer(info.groupOwnerAddress);
//            thread.start();

        }

        soundFragment = SoundFragment.newInstance(role);
        getFragmentManager().beginTransaction().replace(R.id.container, soundFragment).commit();
        statusTxtView.setVisibility(View.GONE);
    }

}
