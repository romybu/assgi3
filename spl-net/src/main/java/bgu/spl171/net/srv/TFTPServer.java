package bgu.spl171.net.srv;

import bgu.spl171.net.api.MessageEncoderDecoderImp;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.BidiMessagingProtocolPacket;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.api.bidi.Packets.Packet;
import bgu.spl171.net.impl.ExampleProtocol;
import bgu.spl171.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl171.net.srv.bidi.ConnectionsImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by alonam on 1/17/17.
 */
public class TFTPServer {

    public static void main(String[] args) {
        ConcurrentHashMap< Integer,String> shared = new ConcurrentHashMap<>();
//
//        Server reactor = Server.reactor(
//                4,
//                7777,
//                () -> new BidiMessagingProtocolPacket(shared),
//                MessageEncoderDecoderImp::new,
//                new ConnectionsImpl<>());
//
//        reactor.serve();

        Server tcp = Server.threadPerClient(
                7777,
                () -> new BidiMessagingProtocolPacket(shared),
                MessageEncoderDecoderImp::new,
                new ConnectionsImpl<>()
        );

        tcp.serve();
    }


}
