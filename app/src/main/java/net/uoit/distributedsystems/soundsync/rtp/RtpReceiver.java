package net.uoit.distributedsystems.soundsync.rtp;

import net.uoit.distributedsystems.soundsync.app.MainActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by nicholas on 27/11/15.
 */
public class RtpReceiver {

    int hostPort;
    InetAddress hostAddress;
    DatagramSocket socket = null;

    private ReceiverThread receiver;

    private UnpackerThread unpacker;

    public RtpReceiver() throws SocketException {
        socket = new DatagramSocket(MainActivity.SERVER_PORT);

        receiver = new ReceiverThread();

        unpacker = new UnpackerThread();
    }

    public RtpReceiver(int hostPort, InetAddress hostAddress) throws SocketException {
        this.hostPort = hostPort;
        this.hostAddress = hostAddress;
        System.out.println(hostPort + hostAddress.toString());

        socket = new DatagramSocket(MainActivity.SERVER_PORT);

        receiver = new ReceiverThread();

        unpacker = new UnpackerThread();
    }

    public void start() {
        receiver.start();
        unpacker.start();
    }

    public void setPackageReadyListener(PacketReadyListener listener) {
        unpacker.setPacketlistener(listener);
    }

    public void close() {
        receiver.close();
        unpacker.close();
        socket.close();
    }

    private class ReceiverThread extends Thread {

        private volatile boolean isRunning;

        public ReceiverThread() {
            isRunning = true;
        }

        public void close() {
            isRunning = false;
        }

        @Override
        public void run() {
            while (isRunning) {

                byte[] buffer = new byte[65508];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {

                    socket.receive(packet);
                    System.out.println("Package Received: " + packet.toString());
                    unpacker.addMsg(packet);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public interface PacketReadyListener {
        void onNewPacketReady(RtpPacket packet);
    }

    private class UnpackerThread extends Thread {

        private  volatile boolean isRunning;

        private BlockingQueue<DatagramPacket> msgQueue;

        private PacketReadyListener packetlistener = null;

        public UnpackerThread() {
            msgQueue = new ArrayBlockingQueue<>(100);
            isRunning = true;
        }

        public void setPacketlistener(PacketReadyListener packetlistener) {
            this.packetlistener = packetlistener;
        }

        public void addMsg(DatagramPacket data) throws InterruptedException {
            System.out.println("Preparing Package for unpacker");
            msgQueue.put(data);
        }

        public void close() {
            isRunning = false;
        }

        @Override
        public void run() {
            while (isRunning) {
                if (!msgQueue.isEmpty()) {
                    try {

                        System.out.println("Unpacking ");
                        RtpPacket packet = new RtpPacket(msgQueue.take());

                        if (packetlistener != null)
                            packetlistener.onNewPacketReady(packet);
                        else
                            doSomethingWithPacket(packet);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void doSomethingWithPacket(RtpPacket packet) {
            System.out.println(packet.toString());
        }

    }
}
