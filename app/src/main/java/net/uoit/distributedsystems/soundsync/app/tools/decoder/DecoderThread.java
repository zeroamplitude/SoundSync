package net.uoit.distributedsystems.soundsync.app.tools.decoder;

import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by nicholas on 25/11/15.
 */
public class DecoderThread extends Thread {

    private final int CODEC_TIMEOUT = 5000;

    private AudioBufferListener listener;

    private MediaExtractor extractor;

    private boolean inputEnded;

    public DecoderThread(AudioBufferListener listener, AssetFileDescriptor fd)
            throws IOException {
        this.listener = listener;

        inputEnded = false;

        extractor = new MediaExtractor();
        extractor.setDataSource(
                fd.getFileDescriptor(),
                fd.getStartOffset(),
                fd.getLength()
        );
        fd.close();
    }

    @Override
    public void run() {
        MediaFormat format = extractor.getTrackFormat(0);
        extractor.selectTrack(0);

        String mime = format.getString(MediaFormat.KEY_MIME);

        try {
            MediaCodec codec = MediaCodec.createByCodecName(mime);

            codec.configure(
                    format,
                    null,
                    null,
                    0
            );

            codec.start();

            ByteBuffer[] inputBuffers = codec.getInputBuffers();
            ByteBuffer[] outputBuffers = codec.getOutputBuffers();

            for (;;) {
                int inputBufferId = codec.dequeueInputBuffer(CODEC_TIMEOUT);
                if (inputBufferId >= 0) {
                    inputBuffers[inputBufferId].clear();
                    int bufferSize = extractor.readSampleData(inputBuffers[inputBufferId], 0);

                    if (bufferSize < 0) {
                        inputEnded = true;
                        bufferSize = 0;
                    }

                    codec.queueInputBuffer(
                            inputBufferId,
                            0,
                            bufferSize,
                            CODEC_TIMEOUT,
                            inputEnded ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0
                    );
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }







}
