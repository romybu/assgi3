package bgu.spl171.net.api.bidi.Packets;

import bgu.spl171.net.api.bidi.BidiMessagingProtocolPacket;

/**
 * Created by romybu on 11/01/17.
 */
public class DELRQ  extends PacketsWithString {
    private short opcode;

    public DELRQ(){
        super();
        opcode=8;
    }

    public short getOpcode() {
        return opcode;
    }
    public void execute(BidiMessagingProtocolPacket p){
        p.execute(this);
    }
}
