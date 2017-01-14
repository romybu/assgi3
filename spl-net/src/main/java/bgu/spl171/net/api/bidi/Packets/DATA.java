package bgu.spl171.net.api.bidi.Packets;

import bgu.spl171.net.api.bidi.BidiMessagingProtocolPacket;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by romybu on 13/01/17.
 */
public class DATA implements Packet {
    private short opcode;
    private short packetSize;
    private short blockNumber;
    private byte[] data;
    private AtomicInteger index= new AtomicInteger();

    public DATA(short packetSize){
        opcode=3;
        this.packetSize=packetSize;
        blockNumber=0;
        data=new byte[packetSize];
    }

    public DATA(){
        opcode=3;
    }

    public DATA(short packetSize,byte[] data,short blockNumber ){
        opcode=3;
        this.packetSize=packetSize;
        this.blockNumber=blockNumber;
        this.data=data;
    }


    @Override
    public short getOpcode() {
        return opcode;
    }

    public short getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(short ps){ this.packetSize=ps;}

    public short getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(short blockNumber) {
        this.blockNumber = blockNumber;
    }

    public byte[] getData() {
        return data;
    }

//    public void setData(byte[] data) {
//        this.data = data;
//    }

    public void initDataArray(short size){ data=new byte[size]; }

    public void addToData(byte b){
        data[index.get()]=b;
        index.incrementAndGet();
    }

    public void execute(BidiMessagingProtocolPacket p){
        p.execute(this);
    }
}
