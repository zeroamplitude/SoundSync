package net.uoit.distributedsystems.soundsync.tools.peerslist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.uoit.distributedsystems.soundsync.R;

/**
 * Created by nicholas on 10/11/15.
 */
public class PeerViewHolder extends RecyclerView.ViewHolder {

    protected TextView vName;
    protected TextView vAddress;


    public PeerViewHolder(View itemView) {
        super(itemView);
        vName = (TextView) itemView.findViewById(R.id.lblDeviceName);
        vAddress = (TextView) itemView.findViewById(R.id.lblDeviceAddress);

    }

}
