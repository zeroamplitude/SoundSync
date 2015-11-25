package net.uoit.distributedsystems.soundsync.app.audio;

import android.content.res.AssetFileDescriptor;

import net.uoit.distributedsystems.soundsync.app.tools.player.PlayerBufferListener;
import net.uoit.distributedsystems.soundsync.transport.Server;

import java.io.IOException;

/**
 * Created by nicholas on 25/11/15.
 */
public class SoundManager implements Runnable {


    private final PlayerBufferListener listener;
    private final Thread server;

    public SoundManager(PlayerBufferListener listener, AssetFileDescriptor fd) throws IOException {
        this.listener = listener;
        server = new Server();
        server.start();
    }

    @Override
    public void run() {



    }
}
