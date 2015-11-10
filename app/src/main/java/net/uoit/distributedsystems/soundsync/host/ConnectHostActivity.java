package net.uoit.distributedsystems.soundsync.host;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.uoit.distributedsystems.soundsync.ConnectP2PActivity;

/**
 * Created by nicholas on 10/11/15.
 */
public class ConnectHostActivity extends ConnectP2PActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
