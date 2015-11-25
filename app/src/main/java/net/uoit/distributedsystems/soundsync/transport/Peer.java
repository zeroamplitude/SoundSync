package net.uoit.distributedsystems.soundsync.transport;

import net.uoit.distributedsystems.soundsync.app.MainActivity;
import net.uoit.distributedsystems.soundsync.app.tools.player.AudioPlayer;
import net.uoit.distributedsystems.soundsync.app.tools.player.PlayerBufferListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by nicholas on 25/11/15.
 */
public class Peer extends Thread {

    private Socket socket;
    private InetAddress mAddress;

    private Protocol sound;

    public Peer(InetAddress address) {
        this.mAddress = address;
    }

    @Override
    public void run() {
        socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    MainActivity.SERVER_PORT), 5000);
            sound = new Protocol(this);
            new Thread(sound).start();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }

    }

    public void write(byte[] bytes) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write(bytes);
    }

    public byte[] receive() throws IOException {
        BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
        int size;
        if ((size = in.available()) > 0) {
            byte[] buffer = new byte[size];
            int result = in.read(buffer);
            return buffer;
        }
        return new byte[0];
    }

    public void close() throws IOException {
        socket.close();
    }
}
