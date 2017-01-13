package bgu.spl171.net.api.bidi.Packets;


import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.BidiMessagingProtocolPacket;

public interface Packet {

    short getOpcode();

    void execute(BidiMessagingProtocolPacket p);

}
