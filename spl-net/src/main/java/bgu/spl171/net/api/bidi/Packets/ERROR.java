package bgu.spl171.net.api.bidi.Packets;

/**
 * Created by alonam on 1/11/17.
 */
public class ERROR implements Packet {
    short opcode;
    short errorCode;
    String errMsg;
    byte lastByte;


    public ERROR(){
        opcode=5;
        errorCode=-1;
        errMsg="";
        lastByte=-1;
    }

    public short getOpcode() {
        return opcode;
    }


    public short getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(short errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public byte getLastByte() {
        return lastByte;
    }

    public void setLastByte(byte lastByte) {
        this.lastByte = lastByte;
    }





}
