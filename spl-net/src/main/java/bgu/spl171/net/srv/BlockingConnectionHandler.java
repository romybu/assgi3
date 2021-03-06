package bgu.spl171.net.srv;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.api.MessagingProtocol;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.srv.bidi.ConnectionHandler;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    public final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private BaseServer server;
    private int connectionId;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol, int connectionId) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.connectionId=connectionId;
    }

    @Override
    public void run() {
        //protocol.start(connectionId,server.connections);
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                System.out.println(read);
                if (nextMessage != null) {
                    System.out.println("I'm sending to process");
                    protocol.process(nextMessage);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    public void send(T msg) {
        System.out.println("im in sending conectionHandler");
        try {
            if (msg != null) {
                System.out.println("start");
                out.write(encdec.encode(msg));
                out.flush();
                System.out.println("finished");
            }
        } catch (IOException ex) {
            System.out.println("im in sending conectionHandler error");
            ex.printStackTrace();
        }
    }
}
