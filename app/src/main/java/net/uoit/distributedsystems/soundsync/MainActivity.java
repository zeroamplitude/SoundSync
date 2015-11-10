package net.uoit.distributedsystems.soundsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void connectHost(View view) {
        Intent intent = new Intent(this, ConnectHostActivity.class);

        startActivity(intent);
    }

    public void connectPeer(View view){
        Intent intent = new Intent(this, ConnectHostActivity.class);

        startActivity(intent);
    }


}
