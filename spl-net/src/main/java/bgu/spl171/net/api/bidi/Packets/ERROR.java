package bgu.spl171.net.api.bidi.Packets;

import bgu.spl171.net.api.bidi.BidiMessagingProtocolPacket;


public class ERROR implements Packet {
    short opcode;
    short errorCode;
    String errMsg;
    byte lastByte;

    public ERROR(short errorCode, String msg){
        opcode=5;
        this.errorCode=errorCode;
        errMsg=msg;
        lastByte=0;
    }
    public ERROR(){
        opcode=5;
        errorCode=-1;
        errMsg="";
        lastByte=0;
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

    public void execute(BidiMessagingProtocolPacket p){
        p.execute(this);
    }



}
