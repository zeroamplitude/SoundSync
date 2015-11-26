package net.uoit.distributedsystems.soundsync.transport;

import java.io.Serializable;

/**
 * Created by nicholas on 26/11/15.
 */
public class SoundBuffer implements Serializable {

    private static final long serialVersionUID = 1234556789L;

    private int id;
    private byte[] sound;

    public SoundBuffer(int id, byte[] sound) {
        this.id = id;
        this.sound = sound;
    }

    public int getId() {
        return id;
    }

    public byte[] getSound() {
        return sound;
    }
}
