package net.uoit.distributedsystems.soundsync.app.peers;

import net.uoit.distributedsystems.soundsync.app.MainActivity;
import net.uoit.distributedsystems.soundsync.rtp.RtpPacket;
import net.uoit.distributedsystems.soundsync.rtp.RtpReceiver;
import net.uoit.distributedsystems.soundsync.rtp.RtpReceiver.PacketReadyListener;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by nicholas on 27/11/15.
 */
public class ListenerPeer extends Peer implements PacketReadyListener {

    private RtpReceiver receiver;
    private int hostPort;
    private InetAddress hostAddress;

    public ListenerPeer(int port, InetAddress address) throws IOException, InterruptedException {
        super("ListenerPeer");
        hostPort = port;
        hostAddress = address;
        receiver = new RtpReceiver(port, address);
        receiver.setPackageReadyListener(this);
        receiver.start();

    }

    public void handshake() throws InterruptedException, IOException {
        System.out.println("Performing handshake");
        sender.sendHandshake(hostPort, hostAddress);
    }

    @Override
    public void run() {
        try {
            handshake();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        super.run();
    }

    @Override
    public void close() {
        receiver.close();
        super.close();
    }

    @Override
    public void onNewPacketReady(RtpPacket packet) {
        if (sender.hasRecipients()) {
            try {
                sender.addMsg(packet.getHeader(), packet.getData());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        playerBufferListener.bufferToPlayer(packet.getData());
    }
}
