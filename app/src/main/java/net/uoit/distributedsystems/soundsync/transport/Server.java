package net.uoit.distributedsystems.soundsync.transport;

import android.content.res.AssetFileDescriptor;

import net.uoit.distributedsystems.soundsync.app.MainActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by nicholas on 25/11/15.
 */
public class Server extends Thread {

    public static final int CAP_PEERS = 2;
    public final int KEEP_ALIVE_TIME = 10;

    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            CAP_PEERS, CAP_PEERS, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private ServerSocket socket;
//    private List<Socket> peers;
    private HashMap<Socket, ObjectOutputStream> peers;

    private AssetFileDescriptor fd = null;

    public Server() throws IOException {
        this.socket = new ServerSocket(MainActivity.SERVER_PORT);
        this.peers = new HashMap<>(CAP_PEERS);
    }

    public Server(AssetFileDescriptor fd) throws IOException {
        try {
            this.socket = new ServerSocket(MainActivity.SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }
        this.peers = new HashMap<>(CAP_PEERS);
        this.fd = fd;
    }

    @Override
    public void run() {
       while (true) {
           try {
               pool.execute(new Protocol(fd, this));
               Socket soc = socket.accept();
               peers.put(soc, new ObjectOutputStream(soc.getOutputStream()));
           } catch (IOException e) {
               try {
                   if (socket != null && !socket.isClosed()) {
                       socket.close();
                   }
               } catch (IOException e1) {
                   e1.printStackTrace();
               }
               e.printStackTrace();
               pool.shutdownNow();
               break;
           }
       }
    }

    public boolean hasPeers() {
        return peers.size() != 0;
    }

    public boolean isFull() {
        return !(peers.size() < CAP_PEERS);
    }

    public void send(SoundBuffer bytes) throws IOException {
        for(ObjectOutputStream peer : peers.values()) {
            peer.writeObject(bytes);
        }
    }

//    public ArrayList<byte[]> receive() throws IOException {
//        ArrayList<byte[]> bytes = new ArrayList<>(CAP_PEERS);
//        for (Socket peer: peers.keySet()) {
//            BufferedInputStream in = new BufferedInputStream(peer.getInputStream());
//            int size;
//            if((size = in.available()) > 0) {
//                byte[] buffer = new byte[size];
//                int result = in.read(buffer);
//                bytes.add(buffer);
//            }
//            bytes.add(new byte[0]);
//        }
//        return bytes;
//    }

    public void close() throws IOException {
        socket.close();
    }


}
