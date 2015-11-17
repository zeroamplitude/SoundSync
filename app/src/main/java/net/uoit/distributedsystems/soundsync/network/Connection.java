package net.uoit.distributedsystems.soundsync.network;

import android.util.Log;

import net.uoit.distributedsystems.soundsync.network.Client.SoundSyncClient;
import net.uoit.distributedsystems.soundsync.network.Server.SoundSyncServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by nicholas on 15/11/15.
 */
public class Connection {

    private static final String TAG = "Connection";

    private int mPort = -1;

    private SoundSyncClient mClient;
    private SoundSyncServer mServer;
    private Socket mSocket;

    public Connection() {
        mServer = new SoundSyncServer(this);
    }

    public void tearDown() {
        mServer.tearDown();
        mClient.tearDown();
    }

    public void connectToServer(InetAddress address, int port) {
        mClient = new SoundSyncClient(address, port, this);
    }

    public SoundSyncClient getClient() {
        return mClient;
    }

    public SoundSyncServer getServer() {
        return mServer;
    }

    public int getLocalPort() {
        return mPort;
    }

    public void setLocalPort(int port) {
        mPort = port;
    }

    public Socket getSocket() {
        return mSocket;
    }
    public synchronized void setSocket(Socket socket) {
        Log.d(TAG, "setSocket being called.");
        if (socket == null) {
            Log.d(TAG, "Setting a null socket.");
        }
        if (mSocket != null) {
            if (mSocket.isConnected()) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        mSocket = socket;
    }
}
