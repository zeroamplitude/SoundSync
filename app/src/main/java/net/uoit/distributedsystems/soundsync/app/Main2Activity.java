package net.uoit.distributedsystems.soundsync.app;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.uoit.distributedsystems.soundsync.R;
import net.uoit.distributedsystems.soundsync.app.tools.decoder.AudioBufferListener;
import net.uoit.distributedsystems.soundsync.app.tools.decoder.DecoderThread;
import net.uoit.distributedsystems.soundsync.app.tools.player.AudioPlayer;

import java.io.IOException;

public class Main2Activity extends Activity {

    AudioPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        try {
            AssetFileDescriptor fd = getAssets().openFd("audio1.mp3");
            player = new AudioPlayer();
            Thread decoder = new DecoderThread(player, fd);
            player.play();
            decoder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        player.play();
    }
}
