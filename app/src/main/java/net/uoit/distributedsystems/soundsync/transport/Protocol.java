package net.uoit.distributedsystems.soundsync.transport;

import android.content.res.AssetFileDescriptor;
import android.os.Handler;
import android.util.Log;

import net.uoit.distributedsystems.soundsync.app.MainActivity;
import net.uoit.distributedsystems.soundsync.app.tools.decoder.BufferReadyListener;
import net.uoit.distributedsystems.soundsync.app.tools.decoder.DecoderThread;
import net.uoit.distributedsystems.soundsync.app.tools.player.AudioPlayer;
import net.uoit.distributedsystems.soundsync.app.tools.player.PlayerBufferListener;
import net.uoit.distributedsystems.soundsync.transport.Peer;
import net.uoit.distributedsystems.soundsync.transport.Server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by nicholas on 19/11/15.
 */
public class Protocol implements Runnable, BufferReadyListener, PlayerBufferListener{

    private static final String TAG = "Protocol";

    Server server;
    Peer peer;

    private AudioPlayer player;


    private PlayerBufferListener listener;

    public Protocol(AssetFileDescriptor fd, Server server) throws IOException {
        this.server = server;
        try {
            player = new AudioPlayer();

            Thread decoder = new DecoderThread(fd, this);
            player.play();
            decoder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Protocol(Peer peer) throws IOException {
        this.peer = peer;
        server = new Server();

        player = new AudioPlayer();
        player.play();
    }


    @Override
    public void run() {
        try {
            while (true) {
                byte[] bytes = peer.receive();

                if (bytes == null) {
                    break;
                }
                if (server.hasPeers()) {
                    server.send(bytes);
                }
                listener.bufferToPlayer(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                peer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendAudioBuffer(byte[] buffer) {
        if(server.hasPeers())
    }

    @Override
    public void bufferToPlayer(byte[] buffer) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }
}
