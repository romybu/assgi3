package bgu.spl171.net.api.bidi.Packets;

/**
 * Created by romybu on 11/01/17.
 */
public class RRQ extends PacketsWithString {
    private short opcode;

    public RRQ(){
        super();
        opcode=1;
    }

    public short getOpcode() {
        return opcode;
    }
}
