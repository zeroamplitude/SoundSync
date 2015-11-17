package net.uoit.distributedsystems.soundsync.network.Client;

import android.util.Log;

import net.uoit.distributedsystems.soundsync.network.Connection;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by nicholas on 15/11/15.
 */
public class SoundSyncClient {

    private static final String TAG = "SoundSyncClient";

    private InetAddress mAddress;
    private int PORT;

    private Connection connection;
    private Thread mSendThread;
    private Thread mRecThread;

    public SoundSyncClient(InetAddress address, int port, Connection connection) {
        mAddress = address;
        PORT = port;

        mSendThread = new Thread(new SendingThread(connection));
    }

    public InetAddress getAddress() {
        return mAddress;
    }

    public int getPort() {
        return PORT;
    }

    public void setRecThread(Thread mRecThread) {
        this.mRecThread = mRecThread;
    }

    public void tearDown() {
        try {
            connection.getSocket().close();
        } catch (IOException ioe) {
            Log.e(TAG, "Error when closing socket");
        }
    }
}
