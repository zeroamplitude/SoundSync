package net.uoit.distributedsystems.soundsync.app.audio;

import android.os.Handler;
import android.util.Log;

import net.uoit.distributedsystems.soundsync.app.MainActivity;
import net.uoit.distributedsystems.soundsync.app.tools.player.AudioPlayer;
import net.uoit.distributedsystems.soundsync.app.tools.player.PlayerBufferListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by nicholas on 19/11/15.
 */
public class SoundManager implements Runnable {

    private static final String TAG = "SoundManager";

    private Socket socket = null;
    private Handler handler;
    private PlayerBufferListener listener;

    private InputStream inStream;
    private OutputStream outStream;

    byte[] buffer;

    public SoundManager(Socket socket, Handler handler,
                        PlayerBufferListener listener) {
        this.socket = socket;
        this.handler = handler;
        this.listener = listener;
        buffer = new byte[this.listener.getBufferSize()];
    }

    @Override
    public void run() {
        try {
            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();

            receive();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void receive() throws IOException {
        int bytes = inStream.read(buffer);
        while (true) {

            if (bytes == -1) {
                break;
            }
            handler.obtainMessage(MainActivity.SOUND_STREAM,
                    bytes, -1, buffer).sendToTarget();

            listener.bufferToPlayer(buffer);
        }
    }

    public void send(byte[] buffer) {
        try {
            outStream.write(buffer);
        } catch (IOException e) {
            Log.e(TAG, "Exception occurred during write", e);
        }
    }
}
