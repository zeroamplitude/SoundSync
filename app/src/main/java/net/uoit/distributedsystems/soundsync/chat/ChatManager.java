package net.uoit.distributedsystems.soundsync.chat;

import android.os.Handler;
import android.util.Log;

import net.uoit.distributedsystems.soundsync.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by nicholas on 17/11/15.
 */
public class ChatManager implements Runnable {

    private static final String TAG = "ChatManager";

    private Socket socket = null;
    private Handler handler;

    public ChatManager(Socket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
    }

    private InputStream is;
    private OutputStream os;

    @Override
    public void run() {
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();

            byte[] buffer = new byte[1024];
            int bytes;

            handler.obtainMessage(MainActivity.MY_HANDLE, this).sendToTarget();

            while (true) {
                try {
                    bytes = is.read(buffer);
                    if (bytes == -1) {
                        break;
                    }

                    Log.d(TAG, "Rec: " + String.valueOf(buffer));
                    handler.obtainMessage(MainActivity.MESSAGE_READ,
                            bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "Disconnected", e);
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
            os.write(buffer);
        } catch (IOException e) {
            Log.e(TAG, "Exception occurred during write", e);
        }
    }
}
