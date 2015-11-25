package net.uoit.distributedsystems.soundsync.transport;

import java.net.Socket;

/**
 * Created by nicholas on 25/11/15.
 */
public class Peer {

    private Socket socket;

    public Peer(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

}
