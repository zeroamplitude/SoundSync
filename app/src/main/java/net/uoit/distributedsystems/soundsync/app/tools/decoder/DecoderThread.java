package net.uoit.distributedsystems.soundsync.app.tools.decoder;

import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by nicholas on 25/11/15.
 */
public class DecoderThread extends Thread {

    private final int CODEC_TIMEOUT = 5000;

    private AudioBufferListener listener;

    private AssetFileDescriptor fd;

    private MediaExtractor extractor;

    private boolean inputEnded;

    public DecoderThread(AudioBufferListener listener, AssetFileDescriptor fd)
            throws IOException {
        this.listener = listener;

        inputEnded = false;

        this.fd = fd;

        extractor = new MediaExtractor();
        extractor.setDataSource(
                fd.getFileDescriptor(),
                fd.getStartOffset(),
                fd.getLength()
        );
    }

    @Override
    public void run() {
        Log.d("DECODER", String.valueOf(extractor.getTrackCount()));
        MediaFormat format = extractor.getTrackFormat(0);
        extractor.selectTrack(0);

        String mime = format.getString(MediaFormat.KEY_MIME);

        try {
            MediaCodec codec = MediaCodec.createDecoderByType(mime);

            codec.configure(
                    format,
                    null,
                    null,
                    0
            );

            codec.start();

            ByteBuffer[] inputBuffers = codec.getInputBuffers();
            ByteBuffer[] outputBuffers = codec.getOutputBuffers();

            while(!inputEnded) {
                int inputBufferId = codec.dequeueInputBuffer(-1);
                if (inputBufferId >= 0) {
                    ByteBuffer dstBuf = inputBuffers[inputBufferId];
                    int bufferSize = extractor.readSampleData(dstBuf, 0);
                    long audioClipTime = 0;
                    if (bufferSize < 0) {
                        inputEnded = true;
                        bufferSize = 0;
                    } else {
                        audioClipTime = extractor.getSampleTime();
                    }

                    codec.queueInputBuffer(
                            inputBufferId,
                            0,
                            bufferSize,
                            audioClipTime,
                            inputEnded ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0
                    );

                    if (!inputEnded) {
                        extractor.advance();
                    }
                }
                BufferInfo bufferInfo = new BufferInfo();
                int result = codec.dequeueOutputBuffer(bufferInfo, CODEC_TIMEOUT);
                if(result >= 0) {

                    ByteBuffer buffer = outputBuffers[result];
                    byte[] audioBuffer = new byte[bufferInfo.size];
                    buffer.get(audioBuffer, 0, bufferInfo.size);
                    buffer.clear();
                    codec.releaseOutputBuffer(result, false);
                    // send bytes to peers
                    listener.sendAudioBuffer(audioBuffer);


                } else if (result == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    outputBuffers = codec.getOutputBuffers();
                } else if (result == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat outFormat = codec.getOutputFormat();
                } else if (result == MediaCodec.INFO_TRY_AGAIN_LATER) {
                }

            }

            fd.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }







}
