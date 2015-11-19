package net.uoit.distributedsystems.soundsync.sound;

import android.os.Handler;
import android.util.Log;

import net.uoit.distributedsystems.soundsync.MainActivity;

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

    public SoundManager(Socket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
    }

    private InputStream inStream;
    private OutputStream outStream;


    @Override
    public void run() {

        byte[] buffer = new byte[1024];
        int bytes;

        try {
            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();

            while (true) {
                try {
                    bytes = inStream.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    handler.obtainMessage(MainActivity.SOUND_STREAM,
                            bytes, -1, buffer).sendToTarget();
                } catch (IOException eio) {
                    Log.e(TAG, "Something went wrong");
                }
            }
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

    public void write(byte[] buffer) {
        try {
            outStream.write(buffer);
        } catch (IOException e) {
            Log.e(TAG, "Exception occurred during write", e);
        }
    }
}
