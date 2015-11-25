package net.uoit.distributedsystems.soundsync.transport;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholas on 25/11/15.
 */
public class Server extends Thread {

    public static final int PORT = 2000;

    public static final int CAP_PEERS = 2;

    private ServerSocket socket;

    private List<Socket> peers;

    public Server() throws IOException {
        this.socket = new ServerSocket(PORT);
        this.peers = new ArrayList<Socket>(CAP_PEERS);
    }

    @Override
    public void run() {
       while (true) {
           try {
               peers.add(socket.accept());
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }

    public List<Socket> getPeers() {
        return peers;
    }

    public void send(byte[] bytes) throws IOException {
        for (Socket peer : peers) {
            OutputStream out = peer.getOutputStream();
            out.write(bytes);
        }
    }

    public ArrayList<byte[]> recieve() throws IOException {
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
