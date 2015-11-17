package net.uoit.distributedsystems.soundsync.server;

import android.os.Handler;
import android.util.Log;

import net.uoit.distributedsystems.soundsync.WifiServiceDiscoveryActivity;
import net.uoit.distributedsystems.soundsync.chat.ChatManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by nicholas on 17/11/15.
 */
public class GroupOwnerSocketHandler extends Thread {

    private static final String TAG = "GroupOwnerSocketHandler";

    private Handler handler;

    ServerSocket socket = null;
    private final int THREAD_COUNT = 10;

    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());


    public GroupOwnerSocketHandler(Handler handler) throws IOException {
        try {
            socket = new ServerSocket(WifiServiceDiscoveryActivity.SERVER_PORT);
            this.handler = handler;
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                pool.execute(new ChatManager(socket.accept(), handler));
                Log.d(TAG, "Launching I/O handler");
            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
                pool.shutdownNow();
                break;
            }
        }
    }
}
