package bgu.spl171.net.api.bidi.Packets;

/**
 * Created by romybu on 11/01/17.
 */
public class DIRO implements Packet {
    private short opcode;

    public DIRO(){
        opcode=6;
    }

    public short getOpcode() {
        return opcode;
    }
}
