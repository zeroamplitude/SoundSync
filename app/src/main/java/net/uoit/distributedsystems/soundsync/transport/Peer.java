package net.uoit.distributedsystems.soundsync.transport;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by nicholas on 25/11/15.
 */
public class Peer extends Thread {

    private Socket socket;

    public Peer(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

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
}
