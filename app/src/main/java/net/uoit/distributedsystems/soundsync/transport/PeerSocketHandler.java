package net.uoit.distributedsystems.soundsync.transport;

import android.os.Handler;
import android.util.Log;

import net.uoit.distributedsystems.soundsync.app.MainActivity;
import net.uoit.distributedsystems.soundsync.app.chat.ChatManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by nicholas on 17/11/15.
 */
public class PeerSocketHandler extends Thread {

    private static final String TAG = "PeerSocketHandler";

    private Handler handler;
    private InetAddress mAddress;

    private ChatManager chat;

    public PeerSocketHandler(Handler handler, InetAddress mAddress) {
        this.handler = handler;
        this.mAddress = mAddress;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    MainActivity.SERVER_PORT), 5000);
            Log.d(TAG, "Launching I/O handler");
            chat = new ChatManager(socket, handler);
            new Thread(chat).start();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }
}
