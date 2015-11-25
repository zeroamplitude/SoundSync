package net.uoit.distributedsystems.soundsync.app.tools.decoder;

/**
 * Created by nicholas on 25/11/15.
 */
public interface BufferReadyListener {
    void sendAudioBuffer(byte[] buffer);
}
