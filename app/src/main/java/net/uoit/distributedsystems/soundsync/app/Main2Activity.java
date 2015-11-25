package net.uoit.distributedsystems.soundsync.app;

import android.app.Activity;
import android.os.Bundle;

import net.uoit.distributedsystems.soundsync.R;
import net.uoit.distributedsystems.soundsync.app.tools.decoder.BufferReadyListener;
import net.uoit.distributedsystems.soundsync.app.tools.player.AudioPlayer;

public class Main2Activity extends Activity implements BufferReadyListener {

    AudioPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);





    }

    @Override
    protected void onPause() {
        super.onPause();
        player.stop();
    }

    @Override
    public void sendAudioBuffer(byte[] buffer) {

    }
}
