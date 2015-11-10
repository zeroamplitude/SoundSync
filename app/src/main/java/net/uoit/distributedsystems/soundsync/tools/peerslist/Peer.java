package net.uoit.distributedsystems.soundsync.tools.peerslist;

/**
 * Created by nicholas on 10/11/15.
 */
public class Peer {
    private String name;
    private String address;

    public Peer(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
