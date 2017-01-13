package bgu.spl171.net.api;

import bgu.spl171.net.api.bidi.Packets.Packet;
import bgu.spl171.net.api.bidi.Packets.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;


//TODO: case 3;

public class MessageEncoderDecoderImp implements MessageEncoderDecoder<Packet> {
    private int counter=0;
    //private int countAck=0;
    byte[] start= new byte[2];
    short opcode=-1;
    boolean isStarted=false;
    Packet toReturn;
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;


    public Packet decodeNextByte(byte nextByte){
        if(counter<2){
            start[counter]=nextByte;
            counter++;
        }
        if (counter==2){
            opcode=bytesToShort(start);
            counter++;
        }
        if (opcode!=-1){
            switch (opcode){
                case 1:{
                    return buildRRQ(nextByte);
                }
                case 2:{
                    return buildWRQ(nextByte);
                }
                case 4:{
                    return buildACK(nextByte);
                }
                case 5:{
                    return buildERROR(nextByte);
                }
                case 6:{
                    return new DIRO();
                }
                case 7:{
                    return buildLOGRQ(nextByte);
                }
                case 8:{
                    return buildDELRQ(nextByte);
                }
                case 9:{
                    return buildBCAST(nextByte);
                }
                case 10:{
                    return new DISC();
                }
            }
        }
        return null;
    }

    public byte[] encode(Packet message){
        switch (message.getOpcode()){
            case 1:{
                return  createBytesArrayWithString((PacketsWithString)message);
            }

            case 2:{
                return  createBytesArrayWithString((PacketsWithString)message);
            }
            case 4:{
                byte[] tmp=shortToBytes((message).getOpcode());
                byte[] tmp2=shortToBytes(((ACK)message).getBlockNumber());
                return  mergeArrays(tmp, tmp2);
            }
            case 5:{
                byte[] tmp=shortToBytes((message).getOpcode());
                byte[] tmp2=shortToBytes(((ERROR)message).getErrorCode());
                byte[] midAns= mergeArrays(tmp, tmp2);
                byte[] tmp3=(((ERROR)message).getErrMsg() + "0").getBytes();
                return mergeArrays(midAns, tmp3);
            }
            case 6:{
                return shortToBytes((message).getOpcode());
            }
            case 7:{
                return  createBytesArrayWithString((PacketsWithString)message);
            }
            case 8:{
                return  createBytesArrayWithString((PacketsWithString)message);
            }
            case 9:{
                byte[] tmp=shortToBytes((message).getOpcode());
                byte[] tmp2=shortToBytes(((BCAST)message).getDeletedOrAdded());
                byte[] midAns= mergeArrays(tmp, tmp2);
                byte[] tmp3=(((BCAST)message).getFileName() + "0").getBytes();
                return mergeArrays(midAns, tmp3);
            }
            case 10:{
                return shortToBytes((message).getOpcode());
            }
        }
        return null;
    }



    private Packet buildRRQ(byte nextByte) {
        if (!isStarted) {
            toReturn = new RRQ();
            isStarted=true;
        }

        if (nextByte == '0') {
            ((RRQ)toReturn).setString(popString());
            return toReturn;
        }

        pushByte(nextByte);
        return null;

    }

    private Packet buildWRQ(byte nextByte) {
        if (!isStarted) {
            toReturn = new WRQ();
            isStarted=true;
        }

        if (nextByte == '0') {
            ((RRQ)toReturn).setString(popString());
            return toReturn;
        }

        pushByte(nextByte);
        return null;

    }

    private Packet buildACK(byte nextByte){
        if (!isStarted) {
            toReturn = new WRQ();
            isStarted=true;
            counter=10;
        }

        if(counter<12){
            start[counter-10]=nextByte;
            counter++;
        }

        if (counter==12){
            ((ACK)toReturn).setBlockNumber(bytesToShort(start));
            return toReturn;
        }

        return null;

    }

    private Packet buildERROR(byte nextByte){
        if (!isStarted) {
            toReturn = new WRQ();
            isStarted=true;
            counter=10;
        }
        if(counter<12){
            start[counter-10]=nextByte;
            counter++;
        }

        if (counter==12){
            ((ERROR)toReturn).setErrorCode(bytesToShort(start));
            counter++;
            return null;
        }

        if (counter>12) {
            if (nextByte == '0') {
                ((ERROR) toReturn).setErrMsg(popString());
                return toReturn;
            }

            pushByte(nextByte);
        }

        return null;

    }

    private Packet buildLOGRQ(byte nextByte){
        if (!isStarted) {
            toReturn = new LOGRQ();
            isStarted=true;
        }

        if (nextByte == '0') {
            ((LOGRQ)toReturn).setString(popString());
            return toReturn;
        }

        pushByte(nextByte);
        return null;
    }

    private Packet buildDELRQ(byte nextByte){
        if (!isStarted) {
            toReturn = new DELRQ();
            isStarted=true;
        }

        if (nextByte == '0') {
            ((DELRQ)toReturn).setString(popString());
            return toReturn;
        }

        pushByte(nextByte);
        return null;
    }

    private Packet buildBCAST(byte nextByte){
        if (!isStarted) {
            toReturn = new BCAST();
            isStarted=true;
            ((BCAST)toReturn).setDeletedOrAdded(nextByte);
        }

        else {
            if (nextByte == '0') {
                ((BCAST) toReturn).setFileName(popString());
                return toReturn;
            }

            pushByte(nextByte);
        }

        return null;
    }


    private byte[] createBytesArrayWithString(PacketsWithString p){
        byte[] tmp=shortToBytes((p).getOpcode());
        byte[] tmp2=(p.getString() + "0").getBytes();
       return  mergeArrays(tmp, tmp2);
    }



    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    private byte[] mergeArrays(byte[] tmp1, byte[] tmp2){
        byte[] ans= new byte[tmp1.length+tmp2.length];
        int i=0;
        for (int j = 0; j < tmp1.length; j++) {
            ans[i] = tmp1[j];
            i++;
        }
        for (int j = 0; j < tmp2.length; j++) {
            ans[i] = tmp2[j];
            i++;
        }
        return ans;
    }

}

