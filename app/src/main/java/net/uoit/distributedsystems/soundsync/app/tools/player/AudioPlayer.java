package net.uoit.distributedsystems.soundsync.app.tools.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by nicholas on 25/11/15.
 */
public class AudioPlayer implements PlayerBufferListener{

    private static final int SAMPLE_RATE = 16000;

    private AudioTrack audioTrack;
    private byte[] pcm;
    private int minBufferSize;
    private int bufferSize;

    public AudioPlayer(int bufferSize) {
        this.bufferSize = bufferSize;
        this.minBufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        this.audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                this.bufferSize,
                AudioTrack.MODE_STREAM
        );
    }

    public void play() {
        audioTrack.play();
    }

    public void stop() {
        audioTrack.stop();
    }

    public void pause() {
        audioTrack.pause();
    }

    public void finish() {
        stop();
        audioTrack.release();
    }

    public void setPlaybackRate(int playbackRate) {
        audioTrack.setPlaybackRate(playbackRate);
    }

    @Override
    public void bufferToPlayer(byte[] buffer) {
        audioTrack.write(buffer, 0, buffer.length);
    }

    @Override
    public int getBufferSize() {
        return bufferSize;
    }


}
