package net.uoit.distributedsystems.soundsync.peer;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.uoit.distributedsystems.soundsync.ConnectP2PActivity;
import net.uoit.distributedsystems.soundsync.R;
import net.uoit.distributedsystems.soundsync.tools.peerslist.Peer;
import net.uoit.distributedsystems.soundsync.tools.peerslist.PeerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ConnectPeerActivity extends ConnectP2PActivity implements WifiP2pManager.PeerListListener {

    private PeerAdapter peerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_peer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView peerlist = (RecyclerView) findViewById(R.id.peerList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        peerlist.setLayoutManager(linearLayoutManager);

        peerAdapter = new PeerAdapter(new ArrayList<Peer>());
        peerlist.setAdapter(peerAdapter);

        mReceiver
        mManager.discoverPeers(mChannel, this);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        List<Peer> peersList = new ArrayList<Peer>();
        for (WifiP2pDevice device: peers.getDeviceList()) {
            peersList.add(new Peer(device.deviceName, device.deviceAddress));
        }
        peerAdapter.updatePeerList(peersList);
    }
}
