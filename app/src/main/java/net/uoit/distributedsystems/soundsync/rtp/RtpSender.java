package net.uoit.distributedsystems.soundsync.rtp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by nicholas on 27/11/15.
 */
public class RtpSender {

    private DatagramSocket socket;

    HashMap<InetAddress, Integer> clients;

    private BlockingQueue<RtpPacket> msgQueue;

    private PacketBuilderThread packager;

    private SenderThread sender;

    public RtpSender() throws IOException {
        socket = new DatagramSocket();
        clients = new HashMap<>();
        msgQueue = new ArrayBlockingQueue<>(100);

        sender = new SenderThread();
        sender.start();

        packager = new PacketBuilderThread();
        packager.start();
    }

    public void sendHandshake(int port, InetAddress host) throws IOException, InterruptedException {
        RtpPacket p = new RtpPacket(-1, new byte[10]);
        byte[] data = p.encode();
        DatagramPacket packet = new DatagramPacket(data, data.length, host, port);
        socket.send(packet);
        System.out.println("HandShake Sent");
    }

    public void addMsg(int id, byte[] data) throws InterruptedException {
        packager.addData(id, data);
    }

    public void addClient(int address, InetAddress port) {
        System.out.println("Adding Client");
        clients.put(port, address);
    }

    public boolean hasRecipients() {
        return !clients.isEmpty();
    }

    public boolean hasRoomForClient() {
        return clients.size() < 2;
    }

    public void close() {
        socket.close();
        sender.close();
        packager.close();
    }

    private class SenderThread extends Thread {
        private volatile boolean isRunning;

        public SenderThread() {
            isRunning = true;
        }

        public void close() {
            isRunning = false;
        }

        @Override
        public void run() {
            while (isRunning) {
                if (!msgQueue.isEmpty()) {
                    try {
                        byte[] packet = msgQueue.take().encode();
                        for (Map.Entry client : clients.entrySet()) {
                            socket.send(
                                    new DatagramPacket(
                                            packet,
                                            packet.length,
                                            (InetAddress) client.getKey(),
                                            (int) client.getValue()
                                    )
                            );
                            System.out.println("packet sent: " + packet.toString());
                        }
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private class PacketBuilderThread extends Thread {

        private volatile boolean isRunning;

        private BlockingQueue<Map.Entry<Integer, byte[]>> dataQueue;

        public PacketBuilderThread() {
            isRunning = true;
            dataQueue = new ArrayBlockingQueue<>(100);
        }

        public void addData(int id, byte[] data) throws InterruptedException {
            dataQueue.put(new AbstractMap.SimpleEntry<>(id, data));
        }

        public void close() {
            isRunning = false;
        }

        @Override
        public void run() {
            while (isRunning) {
                if (!dataQueue.isEmpty()) {
                    try {
                        Map.Entry<Integer, byte[]> msg = dataQueue.take();
                        RtpPacket packet = new RtpPacket(msg.getKey(), msg.getValue());
                        msgQueue.put(packet);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

}
