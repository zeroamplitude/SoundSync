package net.uoit.distributedsystems.soundsync.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.uoit.distributedsystems.soundsync.R;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void beHost(View view) {
        Intent intent = new Intent(this, HostActivity.class);
        startActivity(intent);
        finish();
    }

    public void bePeer(View view) {
        Intent intent = new Intent(this, PeerActivity.class);
        startActivity(intent);
        finish();
    }
}
