package net.uoit.distributedsystems.soundsync.app.peers;

import android.content.res.AssetFileDescriptor;

import net.uoit.distributedsystems.soundsync.app.peers.tree.PeerTree;
import net.uoit.distributedsystems.soundsync.app.tools.decoder.BufferReadyListener;
import net.uoit.distributedsystems.soundsync.app.tools.decoder.DecoderThread;
import net.uoit.distributedsystems.soundsync.rtp.RtpPacket;
import net.uoit.distributedsystems.soundsync.rtp.RtpReceiver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by nicholas on 27/11/15.
 */
public class ControlPeer extends Peer implements BufferReadyListener, RtpReceiver.PacketReadyListener{

    PeerTree peers;

    private RtpReceiver receiver;
    private DecoderThread decoder;
    int songDataCount = 0;

    public ControlPeer(AssetFileDescriptor fd, int port) throws IOException {
        super("ControlPeer");


        decoder = new DecoderThread(fd, this);
        decoder.start();

        receiver = new RtpReceiver();
        receiver.setPackageReadyListener(this);
        receiver.start();
    }

    @Override
    public void run() {
        InetAddress root = null;
        try {
            root = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        peers = new PeerTree(root);
        super.run();
    }

    @Override
    public void close() {
        decoder.interrupt();
        super.close();
    }

    @Override
    public void sendAudioBuffer(byte[] buffer) {

        try {
            sender.addMsg(songDataCount, buffer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        songDataCount++;

        playerBufferListener.bufferToPlayer(buffer);
    }

    @Override
    public void onNewPacketReady(RtpPacket packet) {
        System.out.println("Packet Ready");
        if (packet.getHeader() == -1) {
            System.out.println("Adding Peer");
            InetAddress parentAddress = peers.addPeerToTree(packet.getAddress());
            try {
                sender.sendAddClientMsg(packet.getAddress(), parentAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
