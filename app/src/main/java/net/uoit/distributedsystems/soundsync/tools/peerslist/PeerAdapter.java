package net.uoit.distributedsystems.soundsync.tools.peerslist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.uoit.distributedsystems.soundsync.R;

import java.util.List;

/**
 * Created by ubuntu on 10/11/15.
 */
public class PeerAdapter extends RecyclerView.Adapter<PeerViewHolder> {
    private List<Peer> peerList;

    public PeerAdapter(List<Peer> peerList){
        this.peerList = peerList;
    }

    @Override
    public PeerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_peer, parent, false);
        return new PeerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PeerViewHolder holder, int position) {
        Peer p = peerList.get(position);
        holder.vName.setText(p.name);
        holder.vAddress.setText(p.address);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void updatePeerList(List<Peer> peers) {
        this.peerList = peers;
        notifyDataSetChanged();
    }
}

