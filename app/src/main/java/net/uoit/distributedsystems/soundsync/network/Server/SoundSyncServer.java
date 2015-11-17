package net.uoit.distributedsystems.soundsync.network.Server;

import android.util.Log;

import net.uoit.distributedsystems.soundsync.network.Connection;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by nicholas on 15/11/15.
 */
public class SoundSyncServer {

    private static final String TAG = "SoundSyncServer";

    private ServerSocket mServerSocket = null;
    private Thread mThread = null;

    public SoundSyncServer(Connection connection) {
        mThread = new Thread(new ServerThread(connection));
        mThread.start();
    }

    public ServerSocket getServerSocket() {
        return mServerSocket;
    }

    public void setServerSocket(ServerSocket mServerSocket) {
        this.mServerSocket = mServerSocket;
    }

    public void tearDown() {
        mThread.interrupt();
        try {
            mServerSocket.close();
        } catch (IOException ioe) {
            Log.e(TAG, "Error when closing server socket.");
        }
    }

}
