package net.uoit.distributedsystems.soundsync;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ubuntu on 10/11/15.
 */
public class PeerAdapter extends RecyclerView.Adapter<PeerAdapter.PeerViewHolder> {
    private List<Peer> peerList;

    public PeerAdapter(List<Peer> peerList){
        this.peerList = peerList;
    }

    @Override
    public PeerAdapterPeerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;.
    }

    @Override
    public void onBindViewHolder(PeerAdapter.PeerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

