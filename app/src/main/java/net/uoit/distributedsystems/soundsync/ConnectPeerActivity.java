package net.uoit.distributedsystems.soundsync;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ConnectPeerActivity extends ConnectP2PActivity implements WifiP2pManager.PeerListListener {

    private IntentFilter intentFilter;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;

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

        PeerAdapter peerAdapter = new PeerAdapter(createList(30));
        peerlist.setAdapter(peerAdapter);
    }

    private List<Peer> createList(int size) {

        List<Peer> result = new ArrayList<Peer>();
        for (int i=1; i <= size; i++) {
            Peer peer = new Peer();
            peer.name = peer.NAME_PREFIX + i;
            peer.address = peer.ADDRESS_PREFIX + i;
            result.add(peer);
        }
        return result;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {

    }
}
