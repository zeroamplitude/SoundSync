package net.uoit.distributedsystems.soundsync.network.Server;

import android.util.Log;

import net.uoit.distributedsystems.soundsync.network.Client.SoundSyncClient;
import net.uoit.distributedsystems.soundsync.network.Connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by nicholas on 15/11/15.
 */
public class ServerThread implements Runnable {

    private static final String TAG = "ServerThread";

    private Connection mConnection;
    private SoundSyncServer mServer;
    private SoundSyncClient mClient;
    private Socket mSocket;

    protected ServerThread(Connection connection) {
        super();
        mConnection = connection;
        mServer = connection.getServer();
        mClient = connection.getClient();
        mSocket = connection.getSocket();
    }

    @Override
    public void run() {

        try {
            ServerSocket serverSocket = new ServerSocket(0);

            mServer.setServerSocket(serverSocket);
            mConnection.setLocalPort(serverSocket.getLocalPort());
            while (!Thread.currentThread().isInterrupted()) {
                Log.d(TAG, "ServerSocket Created, awaiting connection");
                mConnection.setSocket(serverSocket.accept());
                Log.d(TAG, "Connected.");

                if (mClient == null) {
                    int port = mSocket.getPort();
                    InetAddress address = mSocket.getInetAddress();
                    mConnection.connectToServer(address, port);
                }

            }
        } catch (IOException e) {
            Log.e(TAG, "Error creating ServerSocket: ", e);
            e.printStackTrace();
        }
    }
}
