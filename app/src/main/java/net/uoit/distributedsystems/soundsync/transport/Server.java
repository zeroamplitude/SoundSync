package net.uoit.distributedsystems.soundsync.transport;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by nicholas on 25/11/15.
 */
public class Server {
    private Socket socket;

    private InputStream inputStream;
    private OutputStream outputStream;

    public Server(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    public void send(byte[] bytes) throws IOException {
        this.outputStream.write(bytes);
    }

    public byte[] receive() throws IOException {

        BufferedInputStream bis = new BufferedInputStream(inputStream);

        int bufferSize;
        if((bufferSize = bis.available()) > 0) {
            byte[] buffer = new byte[bufferSize];
            int bytes = this.inputStream.read(buffer);
            return buffer;
        }

        return new byte[0];
    }
}
