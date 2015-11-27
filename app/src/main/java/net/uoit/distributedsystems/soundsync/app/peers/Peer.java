package net.uoit.distributedsystems.soundsync.app.peers;

import net.uoit.distributedsystems.soundsync.app.MainActivity;
import net.uoit.distributedsystems.soundsync.app.tools.player.AudioPlayer;
import net.uoit.distributedsystems.soundsync.app.tools.player.PlayerBufferListener;
import net.uoit.distributedsystems.soundsync.rtp.RtpSender;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by nicholas on 27/11/15.
 */
public class Peer extends Thread {

    private volatile boolean isRunning;

    protected RtpSender sender;

    protected PlayerBufferListener playerBufferListener;
    
    private AudioPlayer player;

    public Peer(String name) throws IOException {
        super(name);
        isRunning = true;
        sender = new RtpSender();
        player = new AudioPlayer();
        this.playerBufferListener = player;
        player.play();
    }


    public void addPeer(int port, InetAddress address) {
        sender.addClient(port, address);
    }

    @Override
    public void run() {
        while (isRunning) {

        }
    }

    public void close() {
        isRunning = false;
        sender.close();
    }
}
