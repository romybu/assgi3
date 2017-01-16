package bgu.spl171.net.api.bidi;

import bgu.spl171.net.api.bidi.Packets.*;
import bgu.spl171.net.srv.bidi.ConnectionHandler;
import bgu.spl171.net.srv.bidi.ConnectionsImpl;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by romybu on 11/01/17.
 */
public class BidiMessagingProtocolPacket implements BidiMessagingProtocol<Packet> {
    private Connections<Packet> connections;
    private int connectionId;
    private boolean shouldTerminate = false;
    private ConcurrentHashMap< Integer,String> allUsers=new ConcurrentHashMap<>();
    private String fileName;
    private Path path;
    private byte[] data;
    private short counterOfBlocks=0;
    private long CounterSend=0;



    public void start(int connectionId, Connections<Packet> connections){
        this.connections =connections ;
        this.connectionId=connectionId;
    }

    public void process(Packet message){
        message.execute(this);
    }

    public boolean shouldTerminate(){
        return  shouldTerminate;
    }


    public void execute(ACK msg){
        if (data!=null && msg.getBlockNumber()!=0){
            hadleWithReading();
        }
    }

    public void execute(DELRQ msg) {
        try {
            boolean isDeleted =Files.deleteIfExists(Paths.get("Files", "ReadyFiles", msg.getString()));
            if (!isDeleted){
                boolean isSent = connections.send(connectionId, new ERROR((short) 1, "File not found – RRQ of non-existing file"));
                if (!isSent) {
                    System.out.println("the Msg did'nt send");
                }
            }
            else{
                broadcast(new BCAST((byte)0,msg.getString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void execute(DIRO msg) {
        File folder=new File("Files/ReadyFiles");
        String [] allFiles= folder.list();
        data= new byte[allFiles.length];
        int i=0;
        while (i<data.length){
            byte[] temp=allFiles[i].getBytes();
            for(int j=0; j<temp.length & i<data.length;j++){
                data[i]=temp[j];
                i++;
            }
        }
        hadleWithReading();

    }

    public void execute(DISC msg) {
        shouldTerminate=true;
        connections.send(connectionId,new ACK((short)0));
        allUsers.remove(connectionId);
        connections.disconnect(connectionId);
    }

    public void execute(LOGRQ msg) {
        String name=msg.getString();
        boolean isFound=allUsers.containsKey(name);
        if(!isFound){
            allUsers.put(connectionId,name);
            connections.send(connectionId, new ACK((short)0));
        }
        else{
            connections.send(connectionId, new ERROR((short)7, "User already logged in - Login username already connected"));
        }
    }

    public void execute(WRQ msg) {
        fileName=msg.getString();
        boolean isExists= Files.exists(Paths.get("Files","ReadyFiles", fileName));
        if(isExists){
            boolean isSent=connections.send(connectionId, new ERROR((short)5, "File already exists - File name exists on WRQ"));
            return;
        }
        path= Paths.get("Files","InProcessFiles",fileName);
        try {
            Files.createFile(path);
        }
        catch (FileAlreadyExistsException e){
            boolean isSent=connections.send(connectionId, new ERROR((short)5, "File already exists - File name exists on WRQ"));
            if(!isSent){
                e.printStackTrace();
            }
        }
        catch(IOException e1){
            e1.printStackTrace();
        }
    }

    public void execute(RRQ msg) {
        String uploadFile = msg.getString();
        Path temp = Paths.get("Files", "ReadyFiles", uploadFile);
        boolean inReadyFiles = Files.exists(temp);
        if (!inReadyFiles) {
            boolean isSent = connections.send(connectionId, new ERROR((short) 1, "File not found – RRQ of non-existing file"));
            if (!isSent) {
                System.out.println("the Msg did'nt send");
            }
        } else {
            try {
                data = Files.readAllBytes(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            hadleWithReading();
        }
    }

    private void hadleWithReading(){

        if (data.length- CounterSend< 512) {
            byte [] currentSend=new byte[data.length- (int)CounterSend];
            for(int i=0; i<currentSend.length; i++){
                currentSend[i]=data[(int)CounterSend];
                CounterSend++;
            }
            counterOfBlocks++;
            boolean isSent = connections.send(connectionId, new DATA((short)currentSend.length, currentSend,counterOfBlocks));
            if (!isSent) {
                System.out.println("the Msg did'nt send");
            }
            else{
                counterOfBlocks=0;
                CounterSend=0;
                data=null;
            }
        } else {
            byte [] currentSend=new byte[512];
            for(int i=0; i<512; i++){
                currentSend[i]=data[(int)CounterSend];
                CounterSend++;
            }
            counterOfBlocks++;
            boolean isSent = connections.send(connectionId, new DATA((short)currentSend.length, currentSend,counterOfBlocks));
            if (!isSent) {
                System.out.println("the Msg did'nt send");
            }
        }
    }

    public void execute(DATA msg) {
        if (path==null){
            connections.send(connectionId, new ERROR((short)2, "Access violation - File cannot be written, read or deleted"));
        }
        else{
            byte[] currentData=msg.getData();
            if(msg.getBlockNumber()==(short)1){
                try {
                    Files.write(path,currentData);
                    connections.send(connectionId, new ACK(msg.getBlockNumber()));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            else {
                try{
                    Files.write(path,currentData, StandardOpenOption.APPEND);
                    connections.send(connectionId, new ACK(msg.getBlockNumber()));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            if(msg.getPacketSize()<(short)512){
                Path newDir=Paths.get("Files","ReadyFiles");
                try {
                    Files.move(path,newDir.resolve(path.getFileName()));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                broadcast(new BCAST((byte)1, fileName)); //TODO: what does this send?
                path=null;
            }
        }
    }

    private void broadcast(BCAST msg){
        for (int key: allUsers.keySet()){
            boolean isSent=connections.send(key,msg);
            if (!isSent) {
                System.out.println("the Msg did'nt send");
            }
        }
    }

    public void execute(ERROR msg){
        connections.send(connectionId,msg);
    }


}
