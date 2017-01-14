package bgu.spl171.net.api.bidi.Packets;

import bgu.spl171.net.api.bidi.BidiMessagingProtocolPacket;

/**
 * Created by romybu on 11/01/17.
 */
public class LOGRQ extends PacketsWithString { //TODO: MAYBE WE SHOULD KNOW THE USERNAME?
    private short opcode;

    public LOGRQ(){
        super();
        opcode=7;
    }
    public short getOpcode() {
        return opcode;
    }

    public void execute(BidiMessagingProtocolPacket p){
        p.execute(this);
    }

}
