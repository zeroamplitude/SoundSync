package net.uoit.distributedsystems.soundsync.app.tools.player;

/**
 * Created by nicholas on 25/11/15.
 */
public interface PlayerBufferListener {
    void bufferToPlayer(byte[] buffer);

    int getBufferSize();
}
