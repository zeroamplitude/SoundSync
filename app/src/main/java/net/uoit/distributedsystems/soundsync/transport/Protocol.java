package net.uoit.distributedsystems.soundsync.transport;

import android.content.res.AssetFileDescriptor;
import android.util.Log;

import net.uoit.distributedsystems.soundsync.app.tools.decoder.BufferReadyListener;
import net.uoit.distributedsystems.soundsync.app.tools.decoder.DecoderThread;
import net.uoit.distributedsystems.soundsync.app.tools.player.AudioPlayer;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by nicholas on 19/11/15.
 */
public class Protocol implements Runnable, BufferReadyListener{

    private static final String TAG = "Protocol";

    Server server;
    Peer peer;

    private AudioPlayer player;

    AssetFileDescriptor fd;

    Thread sender;

    BlockingQueue<SoundBuffer> msgQueue = new ArrayBlockingQueue<SoundBuffer>(1000);

    int msgCount = 0;

    public Protocol(AssetFileDescriptor fd, Server server) throws IOException {
        this.server = server;
        player = new AudioPlayer();
        this.fd = fd;

        try {
            Thread decoder = new DecoderThread(fd, this);
            player.play();
            decoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender = new SendingThread();
        sender.start();
    }

    public Protocol(Peer peer) throws IOException {
        this.peer = peer;
        server = new Server();

        player = new AudioPlayer();
        player.play();

        sender = new SendingThread();
        sender.start();
    }


    @Override
    public void run() {
        try {
            while (true) {

                SoundBuffer soundBuffer;

                if (peer != null) {
                    soundBuffer = peer.receive();
                    byte[] bytes = soundBuffer.getSound();
                    if (bytes.length == 0)
                        continue;

//                    byte[] buffer = extractData(bytes);
                    Log.d(TAG, "Received bytes: msg#: " + soundBuffer.getId());

                    if (server.hasPeers()) {
                        addToQueue(bytes);
                    }

                    player.bufferToPlayer(bytes);

                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addToQueue(final byte[] buffer) throws InterruptedException {

        msgQueue.put(new SoundBuffer(msgCount, buffer));

        msgCount++;
    }

    @Override
    public void sendAudioBuffer(final byte[] buffer) {

        try {
            addToQueue(buffer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        player.bufferToPlayer(buffer);

    }

    class SendingThread extends Thread {

        public volatile boolean finished = false;

        @Override
        public void run() {

            while (!finished) {
                if (!msgQueue.isEmpty()) {
                    try {

                        SoundBuffer soundBuffer = msgQueue.take();
                        server.send(soundBuffer);

                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }

    }
}
