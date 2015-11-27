package net.uoit.distributedsystems.soundsync.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.uoit.distributedsystems.soundsync.R;
import net.uoit.distributedsystems.soundsync.app.audio.SoundFragment;
import net.uoit.distributedsystems.soundsync.app.peers.ControlPeer;
import net.uoit.distributedsystems.soundsync.app.peers.ListenerPeer;
import net.uoit.distributedsystems.soundsync.app.peers.Peer;
import net.uoit.distributedsystems.soundsync.network.WifiDirectBrodcastReciever;
import net.uoit.distributedsystems.soundsync.network.WifiP2PService;
import net.uoit.distributedsystems.soundsync.network.WifiServicesList;
import net.uoit.distributedsystems.soundsync.network.WifiServicesList.DeviceClickListener;
import net.uoit.distributedsystems.soundsync.network.WifiServicesList.WifiDeviceAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements
        DeviceClickListener, ConnectionInfoListener {

    public static final String TAG = "DiscoveryService";

    public static final String TXTRECORD_PROP_AVAILABLE =  "available";
    public static final String SERVICE_INSTANCE = "SoundSync";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    private WifiP2pManager manager;

    // SERVER PORT
    public static final int SERVER_PORT = 4545;

    private final IntentFilter intentFilter = new IntentFilter();

    private Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    private WifiServicesList servicesList;

    private SoundFragment soundFragment;

    private TextView statusTxtView;

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
        startRegistrationAndDiscovery();


        servicesList = new WifiServicesList();
        getFragmentManager().beginTransaction()
                .add(R.id.container, servicesList, "services").commit();

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

    private void startRegistrationAndDiscovery() {
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

        discoverService();

    }

    private void discoverService() {
        manager.setDnsSdResponseListeners(channel,
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                        WifiP2pDevice srcDevice) {
                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                            WifiServicesList fragment = (WifiServicesList) getFragmentManager()
                                    .findFragmentByTag("services");

                            if (fragment != null) {
                                WifiP2PService service = new WifiP2PService();
                                service.setDevice(srcDevice);
                                service.setInstanceName(instanceName);
                                service.setServiceRegType(registrationType);

                                WifiDeviceAdapter adapter = (WifiDeviceAdapter) fragment
                                        .getListAdapter();

                                adapter.add(service);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }, new WifiP2pManager.DnsSdTxtRecordListener() {
                    @Override
                    public void onDnsSdTxtRecordAvailable(String fullDomainName,
                                                          Map<String, String> txtRecordMap,
                                                          WifiP2pDevice srcDevice) {
                        Log.d(TAG, srcDevice.deviceName + " is " +
                                txtRecordMap.get(TXTRECORD_PROP_AVAILABLE));
                    }
                });

        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        appendStatus("Added Service discovery request");
                    }

                    @Override
                    public void onFailure(int reason) {
                        appendStatus("Failed to add server discovery request");
                    }
                });

        manager.discoverServices(channel,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        appendStatus("Service discovery initialized");
                    }

                    @Override
                    public void onFailure(int reason) {
                        appendStatus("Service discovery failed");
                    }
                });
    }

    private void appendStatus(String status) {
        String current = statusTxtView.getText().toString();
        statusTxtView.setText(current + "\n" + status);
    }


    @Override
    public void connectP2P(WifiP2PService service) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.getDevice().deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        if (serviceRequest != null) {
            manager.removeServiceRequest(channel, serviceRequest,
                    new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(int reason) {

                        }
                    });
        }

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                appendStatus("Connecting to service");
            }

            @Override
            public void onFailure(int reason) {
                appendStatus("Failed on connection to service");
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Peer thread = null;
        String role = "";

        if (info.isGroupOwner) {
            Log.d(TAG, "Connected ad group owner");

            role = "control";

            try {
                AssetFileDescriptor fd = getAssets().openFd("audio1.mp3");

                thread = new ControlPeer(fd, MainActivity.SERVER_PORT);
                thread.start();
            } catch (IOException e) {
                Log.d(TAG, "Failed to create a server thread - " + e.getMessage());
                return;
            }

        } else {

            Log.d(TAG, "Connected as Peer");
            role = "peer";
            try {
                thread = new ListenerPeer(MainActivity.SERVER_PORT, info.groupOwnerAddress);
                thread.start();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }



        soundFragment = SoundFragment.newInstance(role);
        getFragmentManager().beginTransaction().replace(R.id.container, soundFragment).commit();
        statusTxtView.setVisibility(View.GONE);
    }

}
