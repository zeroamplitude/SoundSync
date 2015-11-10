package net.uoit.distributedsystems.soundsync;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholas on 10/11/15.
 */
public class ConnectHostActivity extends ConnectP2PActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView recList = (RecyclerView) findViewById(R.id.peerList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        PeerAdapter peerAdapter = new PeerAdapter(createList(30));
        recList.setAdapter(peerAdapter);
    }

    private List<Peer> createList(int size) {

        List<Peer> result = new ArrayList<Peer>();
        for (int i=1; i <= size; i++) {
            Peer peer = new Peer();
            peer.name = Peer.NAME_PREFIX + i;
            peer.address = Peer.ADDRESS_PREFIX + i;

            result.add(peer);

        }

        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setIsWifiP2pEnabled(boolean b) {
        super.setIsWifiP2pEnabled(b);
    }
}
