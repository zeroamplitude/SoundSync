package net.uoit.distributedsystems.soundsync.network.Client;

import android.util.Log;

import net.uoit.distributedsystems.soundsync.network.Connection;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by nicholas on 15/11/15.
 */
public class SendingThread implements Runnable {

    private static final String TAG = "SendingThread";

    private BlockingQueue<Byte> mMessageQueue;
    private int CAPACITY = 10;

    private Connection mConnection;
    private SoundSyncClient mClient;

    public SendingThread(Connection connection) {
        mMessageQueue = new ArrayBlockingQueue<Byte>(CAPACITY);
        mConnection = connection;
        mClient = connection.getClient();

    }

    @Override
    public void run() {
        try {
            if (mConnection.getSocket() == null) {
                mConnection.setSocket(new Socket(mClient.getAddress(), mClient.getPort()));
                Log.d(TAG, "Client socket initialized.");
            } else {
                Log.d(TAG, "Socket already initialized. skipping!");
            }

            Thread recThread = new Thread(new ReceivingThread());
            mClient.setRecThread(recThread);
            recThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                Byte sound = mMessageQueue.take();
                //TODO: Add send sound byte methods
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
