package net.uoit.distributedsystems.soundsync.app.tools.player;

import android.media.AudioFormat;
import android.media.AudioTrack;

import net.uoit.distributedsystems.soundsync.app.tools.decoder.AudioBufferListener;

import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by nicholas on 25/11/15.
 */
public class AudioPlayer implements PlayerBufferListener, AudioBufferListener{

    private static final int SAMPLE_RATE = 82000;

    private AudioTrack audioTrack;
    private byte[] pcm;
    private int minBufferSize;
    private int bufferSize;

    public AudioPlayer() {
        this.minBufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        this.audioTrack = new AudioTrack(
                STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize,
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

    @Override
    public void sendAudioBuffer(byte[] buffer) {
        bufferToPlayer(buffer);
    }
}
