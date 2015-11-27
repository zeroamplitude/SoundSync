package net.uoit.distributedsystems.soundsync.rtp;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by nicholas on 27/11/15.
 */
public class RtpPacket {

    private int header;
    int sizeOfData;
    private byte[] data;
    private InetAddress address;
    private int port;

    public RtpPacket(int header, byte[] data) {
        this.header = header;
        this.sizeOfData = data.length;
        this.data = data;
    }

    public RtpPacket(DatagramPacket packet) {
        address = packet.getAddress();
        port = packet.getPort();
        byte[] tmpData = packet.getData();

        // Extract header
        header = ((tmpData[0] & 0xFF) << 24)
               | ((tmpData[1] & 0xFF) << 16)
               | ((tmpData[2] & 0xFF) << 8)
               |  (tmpData[3] & 0xFF);

        sizeOfData = ((tmpData[4] & 0xFF) << 24)
                   | ((tmpData[5] & 0xFF) << 16)
                   | ((tmpData[6] & 0xFF) << 8)
                   |  (tmpData[7] & 0xFF);

        // Extract data fragment
        data = new byte[sizeOfData];
        System.arraycopy(tmpData, 8, data, 0, data.length);
    }

    public int getHeader() {
        return header;
    }

    public byte[] getData() {
        return data;
    }

    public int getSizeOfData() {
        return sizeOfData;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte[] encode() {
        byte[] packet = new byte[8 + data.length];
        // Encode header
        packet[0] = (byte) ((header & 0xFF000000) >> 24);
        packet[1] = (byte) ((header & 0x00FF0000) >> 16);
        packet[2] = (byte) ((header & 0x0000FF00) >> 8);
        packet[3] = (byte) ((header & 0x000000FF));

        packet[4] = (byte) ((sizeOfData & 0xFF000000) >> 24);
        packet[5] = (byte) ((sizeOfData & 0x00FF0000) >> 16);
        packet[6] = (byte) ((sizeOfData & 0x0000FF00) >> 8);
        packet[7] = (byte) ((sizeOfData & 0x000000FF));

        // Copy data bytes
        System.arraycopy(data, 0, packet, 8, data.length);

        return packet;
    }

}
