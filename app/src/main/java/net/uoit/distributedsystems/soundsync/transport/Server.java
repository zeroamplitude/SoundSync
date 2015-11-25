package net.uoit.distributedsystems.soundsync.transport;

import android.content.res.AssetFileDescriptor;

import net.uoit.distributedsystems.soundsync.app.MainActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholas on 25/11/15.
 */
public class Server extends Thread {

    public static final int CAP_PEERS = 2;

    private ServerSocket socket;

    private List<Socket> peers;

    private AssetFileDescriptor fd = null;

    public Server() throws IOException {
        this.socket = new ServerSocket(MainActivity.SERVER_PORT);
        this.peers = new ArrayList<Socket>(CAP_PEERS);
    }

    public Server(AssetFileDescriptor fd) throws IOException {
        this.socket = new ServerSocket(MainActivity.SERVER_PORT);
        this.peers = new ArrayList<Socket>(CAP_PEERS);
        this.fd = fd;
    }

    @Override
    public void run() {
       while (true) {
           try {
               peers.add(socket.accept());
               Thread protocol = new Protocol(fd, );
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }

    public boolean hasPeers() {
        return peers.size() != 0;
    }

    public boolean isFull() {
        return !(peers.size() < CAP_PEERS);
    }

    public void send(byte[] bytes) throws IOException {
        for (Socket peer : peers) {
            OutputStream out = peer.getOutputStream();
            out.write(bytes);
        }
    }

    public ArrayList<byte[]> receive() throws IOException {
        ArrayList<byte[]> bytes = new ArrayList<>(CAP_PEERS);
        for (Socket peer: peers) {
            BufferedInputStream in = new BufferedInputStream(peer.getInputStream());
            int size;
            if((size = in.available()) > 0) {
                byte[] buffer = new byte[size];
                int result = in.read(buffer);
                bytes.add(buffer);
            }
            bytes.add(new byte[0]);
        }
        return bytes;
    }


}
