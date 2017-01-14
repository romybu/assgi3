package bgu.spl171.net.api.bidi.Packets;

import bgu.spl171.net.api.bidi.BidiMessagingProtocolPacket;

/**
 * Created by alonam on 1/11/17.
 */
public class BCAST implements Packet{
    short opcode;
    byte deletedOrAdded;
    String fileName;
    byte lastByte;

    public BCAST(){
        opcode=9;
        deletedOrAdded=-1;
        fileName="";
        lastByte=0;
    }

    public BCAST(byte deletedOrAdded){//TODO: should add filename and a message "... was added/del"??
        opcode=9;
        this.deletedOrAdded=deletedOrAdded;
        fileName="";
        lastByte=0;
    }

    public BCAST(byte deletedOrAdded, String str){//TODO: should add filename and a message "... was added/del"??
        opcode=9;
        this.deletedOrAdded=deletedOrAdded;
        fileName=str;
        lastByte=0;
    }

    public short getOpcode() {
        return opcode;
    }


    public byte getDeletedOrAdded() {
        return deletedOrAdded;
    }

    public void setDeletedOrAdded(byte deletedOrAdded) {
        this.deletedOrAdded = deletedOrAdded;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public void execute(BidiMessagingProtocolPacket p){
        p.execute(this);
    }
}
