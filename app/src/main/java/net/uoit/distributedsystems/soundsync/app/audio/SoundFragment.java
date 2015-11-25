package net.uoit.distributedsystems.soundsync.app.audio;

import android.app.Fragment;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.uoit.distributedsystems.soundsync.R;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by nicholas on 19/11/15.
 */
public class SoundFragment extends Fragment {

    private View view;
    private byte[] audioBytes;
    private AudioTrack track;

    private int sampleRate = 44100;
    private Thread t;
    private Thread converter;
    private boolean isRunning = true;

    private int buffersize = AudioTrack.getMinBufferSize(sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_8BIT);
    private Uri audioUri;
    private MediaCodec decoder;
    ByteBuffer[] codecInputBuffers;
    ByteBuffer[] codecOutputBuffers;

    public static SoundFragment newInstance(Uri file) {
        SoundFragment soundFragment = new SoundFragment();
        Bundle args = new Bundle();
        args.putString("song", file.toString());
        soundFragment.setArguments(args);
        return soundFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sound, container, false);
        audioUri = Uri.parse(getArguments().getString("song"));

        converter = new Thread() {
            @Override
            public void run() {
                MediaExtractor extractor = new MediaExtractor();
                try {
                    extractor.setDataSource(getContext(), audioUri, null);
                    MediaFormat format = extractor.getTrackFormat(0);
                    String mime = format.getString(MediaFormat.KEY_MIME);
                    extractor.selectTrack(0);
                    decoder = MediaCodec.createDecoderByType(mime);
                    decoder.configure(format, null, null, 0);


                    if (decoder == null)
                        return;
                    decoder.start();
                    codecInputBuffers = decoder.getInputBuffers();
                    codecOutputBuffers = decoder.getOutputBuffers();
                    MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                    boolean isEOS = false;

                    long startMs = System.currentTimeMillis();

                    while (!Thread.interrupted()) {
                        if(!isEOS) {
                            int inIndex = decoder.dequeueInputBuffer(10000);
                            if (inIndex >= 0) {
                                ByteBuffer buffer = codecInputBuffers[inIndex];
                                int sampleSize = extractor.readSampleData(buffer, 0);
                                if (sampleSize < 0) {
                                    decoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                    isEOS = true;
                                } else {
                                    decoder.queueInputBuffer(inIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                                    extractor.advance();
                                }
                            }
                        }

                        int outIndex = decoder.dequeueOutputBuffer(info, 10000);
                        switch (outIndex) {
                            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                                codecOutputBuffers = decoder.getOutputBuffers();
                                break;

                            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                                break;

                            case MediaCodec.INFO_TRY_AGAIN_LATER:
                                break;

                            default:
                                ByteBuffer buffer = codecOutputBuffers[outIndex];

                                while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
                                    try {
                                        sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        break;
                                    }
                                }
                                decoder.releaseOutputBuffer(outIndex, true);
                                break;
                        }
                        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            break;
                        }
                    }

                    decoder.stop();
                    decoder.release();
                    extractor.release();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };



        track = new AudioTrack(
                AudioManager.STREAM_RING,
                AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_NOTIFICATION),
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT,
                audioBytes.length,
                AudioTrack.MODE_STATIC);

        t = new Thread() {
            public void run() {

                track.write(audioBytes, 0, audioBytes.length);
                track.play();

//                int pos = 0;
//                while (pos != -1) {
//                    try {
//                        pos = inputStream.read(samples);
//                        Log.w("BOB", Arrays.toString(samples));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    track.write(samples, 0, buffersize);
//                }


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
