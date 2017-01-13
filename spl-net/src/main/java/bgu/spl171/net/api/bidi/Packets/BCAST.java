package bgu.spl171.net.api.bidi.Packets;

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
        lastByte=-1;
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

    public byte getLastByte() {
        return lastByte;
    }

    public void setLastByte(byte lastByte) {
        this.lastByte = lastByte;
    }
}
