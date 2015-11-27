package net.uoit.distributedsystems.soundsync.app.peers;

import android.content.res.AssetFileDescriptor;

import net.uoit.distributedsystems.soundsync.app.MainActivity;
import net.uoit.distributedsystems.soundsync.app.tools.decoder.BufferReadyListener;
import net.uoit.distributedsystems.soundsync.app.tools.decoder.DecoderThread;
import net.uoit.distributedsystems.soundsync.rtp.RtpPacket;
import net.uoit.distributedsystems.soundsync.rtp.RtpReceiver;

import java.io.IOException;

/**
 * Created by nicholas on 27/11/15.
 */
public class ControlPeer extends Peer implements BufferReadyListener, RtpReceiver.PacketReadyListener{

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
            sender.addClient(MainActivity.SERVER_PORT, packet.getAddress());

        }
    }
}
