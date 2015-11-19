package net.uoit.distributedsystems.soundsync.sound;

import android.app.Fragment;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.uoit.distributedsystems.soundsync.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by nicholas on 19/11/15.
 */
public class SoundFragment extends Fragment {

    private View view;
    private AudioTrack track;

    private int sampleRate = 44100;
    private Thread t;
    private boolean isRunning = true;

    private int buffersize = AudioTrack.getMinBufferSize(sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_8BIT);

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sound, container, false);
        track = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT,
                buffersize,
                AudioTrack.MODE_STATIC);

        t = new Thread() {
            public void run() {
                byte samples[] = new byte[buffersize];
                int amp = 10000;
                double twopi = 8. * Math.atan(1.);
                double fr = 440.f;
                double ph = 0.0;

                track.play();

                Resources res = getResources();
                InputStream inputStream = res.openRawResource(R.raw.audio1);

                int pos = 0;
                while (pos != -1) {
                    try {
                        pos = inputStream.read(samples);
                        Log.w("BOB", Arrays.toString(samples));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    track.write(samples, 0, buffersize);
                }

                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                track.stop();
                track.release();
            }
        };

        t.start();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        track.stop();
//        track.release();
        isRunning = false;
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface MessageTarget {
        Handler getHandler();
    }

    public void pushSound(byte[] sound) {
        track.write(sound, 0, buffersize);
    }
}
