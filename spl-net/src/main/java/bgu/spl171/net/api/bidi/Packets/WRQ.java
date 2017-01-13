package bgu.spl171.net.api.bidi.Packets;

/**
 * Created by romybu on 11/01/17.
 */
public class WRQ extends PacketsWithString {
    private short opcode;

    public WRQ(){
        super();
        opcode=2;
    }

    public short getOpcode() {
        return opcode;
    }
}
