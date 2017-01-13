package bgu.spl171.net.api.bidi.Packets;

/**
 * Created by romybu on 11/01/17.
 */
public class LOGRQ extends PacketsWithString {
    private short opcode;

    public LOGRQ(){
        super();
        opcode=7;
    }
    public short getOpcode() {
        return opcode;
    }
}
