package bgu.spl171.net.api.bidi;

import bgu.spl171.net.api.bidi.Packets.*;
import bgu.spl171.net.srv.bidi.ConnectionHandler;
import bgu.spl171.net.srv.bidi.ConnectionsImpl;

import javax.imageio.IIOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.text.Bidi;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by romybu on 11/01/17.
 */
public class BidiMessagingProtocolPacket implements BidiMessagingProtocol<Packet> {
    private Connections<Packet> connections;
    private int connectionId;
    private boolean shouldTerminate = false;
    private ConcurrentHashMap< Integer,String> allUsers;
    private final ReadWriteLock lockTheFile=new ReentrantReadWriteLock();

    public String fileName;


    private Path path;
    private byte[] data;
    private short counterOfBlocks=0;
    private long CounterSend=0;
    private boolean logedIN=false;
    private int numOfBlocksInData=0;
    /****for the test***/
    public boolean connectionState;


    /************ for the protocol test ************/

    public ConcurrentHashMap<Integer, String> getLoggedInUsers(){
        return allUsers;
    }

    /**********************************************/

    public BidiMessagingProtocolPacket(ConcurrentHashMap< Integer,String> allUsers){
        this.allUsers=allUsers;
    }

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
        if(logedIN) {
            if (data != null && msg.getBlockNumber() != 0) {
                hadleWithReading();
            }
        }
        else{
            connections.send(connectionId, new ERROR((short) 6, "User not logged in"));
        }
    }

    public void execute(DELRQ msg) {
        try {
            if(logedIN) {
                boolean isDeleted = Files.deleteIfExists(Paths.get("Files", "ReadyFiles", msg.getString()));
                if (!isDeleted) {
                    boolean isSent = connections.send(connectionId, new ERROR((short) 1, "File not found – RRQ of non-existing file"));
                    if (!isSent) {
                        connections.send(connectionId, new ERROR((short) 0, "the Msg did'nt send"));
                    }
                    return;
                } else {
                    broadcast(new BCAST((byte) 0, msg.getString()));
                }
            }
            else{
                connections.send(connectionId, new ERROR((short) 6, "User not logged in"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void execute(DIRO msg) {
        if(logedIN) {
            System.out.println("im in dirq");
            File folder = Paths.get("Files"+File.separator+"ReadyFiles").toFile();
            String[] allFiles = folder.list();
            String stemp="";
            if (allFiles!=null) {
                for (int j = 0; j < allFiles.length; j++) {
                    stemp+=allFiles[j]+'0';
                }
                data=stemp.getBytes();
                System.out.println(stemp);
                hadleWithReading();
            }
            else{
                System.out.println("Im in the case that is null");
                data=new byte[0];
                connections.send(connectionId, new DATA((short)0,data, (short)0));
            }


            ///TODO: what happend with this

        }
        else{
            connections.send(connectionId, new ERROR((short) 6, "User not logged in"));
        }

    }

    public void execute(DISC msg) {
        if (logedIN) {
            shouldTerminate = true;
            boolean isSent = connections.send(connectionId, new ACK((short) 0));
            if (!isSent)
                connections.send(connectionId, new ERROR((short) 0, "the Msg did'nt send"));
            allUsers.remove(connectionId);
            connections.disconnect(connectionId);
        }
        else{
            connections.send(connectionId, new ERROR((short) 6, "User not logged in"));
        }
    }

    public void execute(LOGRQ msg) {
        System.out.println("I'm in Protocol");
        String name=msg.getString();
        boolean isFound=allUsers.containsValue(name);
        if(!isFound){
            allUsers.put(connectionId,name);
            boolean isSent=connections.send(connectionId, new ACK((short)0));
            if (!isSent) {
                connections.send(connectionId, new ERROR((short) 0, "the Msg did'nt send"));
                return;
            }
            logedIN=true;
        }
        else{
            boolean isSent=connections.send(connectionId, new ERROR((short)7, "User already logged in - Login username already connected"));
            if (!isSent) {
                connections.send(connectionId, new ERROR((short) 0, "the Msg did'nt send"));
            }
        }
    }

    public void execute(WRQ msg) {
        if(logedIN) {
            try{
                fileName = msg.getString();
                lockTheFile.writeLock().lock();
                if(!(Files.exists(Paths.get("Files", "ReadyFiles", fileName)))){
                    System.out.println("trying to create file");
                    path = Paths.get("Files", "InProcessFiles", fileName);
                    Files.createFile(path);
                }
                else{
                    System.out.println("didn't create for some reason");

                    connections.send(connectionId, new ERROR((short) 5, "File already exists - File name exists on WRQ"));
                }
                lockTheFile.writeLock().unlock();
            }
            catch (FileAlreadyExistsException e) {
                boolean isSent = connections.send(connectionId, new ERROR((short) 5, "File already exists - File name exists on WRQ"));
                if (!isSent) {
                    e.printStackTrace();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            connections.send(connectionId, new ACK((short)0));
        }

        else{
            connections.send(connectionId, new ERROR((short) 6, "User not logged in"));
        }
    }

    public void execute(RRQ msg) {
        System.out.println("i'm in rrq execute");
        System.out.println(msg.getString());
        if (logedIN) {
            path=Paths.get(System.getProperty("user.home"),"Desktop","asssssss","assgi3","Files", "ReadyFiles", msg.getString());
            //path = FileSystems.getDefault().getPath("Files", "ReadyFiles", msg.getString());
            //path = Paths.get("Home","Desktop","asssssss","assgi3","spl-net","Files", "ReadyFiles", msg.getString());
            System.out.println(path);
            //boolean inReadyFiles = exists("ReadyFiles", msg.getString());
            boolean inReadyFiles=Files.notExists(path);
            System.out.println("i'm p "+ inReadyFiles);
            if (inReadyFiles) {
                boolean isSent = connections.send(connectionId, new ERROR((short) 1, "File not found – RRQ of non-existing file"));
                if (!isSent) {
                    connections.send(connectionId, new ERROR((short) 0, "the Msg did'nt send"));
                }
            } else {
                try {
                    data = Files.readAllBytes(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                connections.send(connectionId,new ACK((short)0));
                hadleWithReading();
            }
        }
        else{
            connections.send(connectionId, new ERROR((short) 6, "User not logged in"));
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
                connections.send(connectionId, new ERROR((short) 0, "the Msg did'nt send"));
                return;
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
                connections.send(connectionId, new ERROR((short) 0, "the Msg did'nt send"));
                return;
            }
        }
    }

    public void execute(DATA msg) {
        if(logedIN) {
            if (path == null) {
                connections.send(connectionId, new ERROR((short) 2, "Access violation - File cannot be written, read or deleted"));
            } else if(numOfBlocksInData+1==msg.getBlockNumber()){
                byte[] currentData = msg.getData();
                if (msg.getBlockNumber() == (short) 1) {
                    try {
                        Files.write(path, currentData);
                        connections.send(connectionId, new ACK(msg.getBlockNumber()));
                        numOfBlocksInData++;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                } else {
                    try {
                        Files.write(path, currentData, StandardOpenOption.APPEND);
                        numOfBlocksInData++;
                        connections.send(connectionId, new ACK(msg.getBlockNumber()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }

                if (msg.getPacketSize() < (short) 512) {
                    Path newDir = Paths.get("Files", "ReadyFiles");
                    try {
                        Files.move(path, newDir.resolve(path.getFileName()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    broadcast(new BCAST((byte) 1, fileName)); //TODO: what does this send?
                    path = null;
                    numOfBlocksInData=0;
                }
                broadcast(new BCAST((byte)1, fileName));
                path=null;
            }
        }
        else{
            connections.send(connectionId, new ERROR((short) 6, "User not logged in"));
        }
    }

    private void broadcast(BCAST msg){
        for (int key: allUsers.keySet()){
            boolean isSent=connections.send(key,msg);
            if (!isSent) {
                connections.send(connectionId, new ERROR((short) 0, "the Msg did'nt send"));
            }
        }
    }

//TODO: what to do we this situation
    public void execute(ERROR msg) {
        if (msg.getErrorCode() == 4) {
            boolean isSent = connections.send(connectionId, msg);
            if (!isSent) {
                connections.send(connectionId, new ERROR((short) 0, "the Msg did'nt send"));
                return;
            }
        }
        if(msg.getErrorCode()==8){
            fileName=null;
            path=null;
            data=null;
            counterOfBlocks=0;
            CounterSend=0;
            logedIN=false;
            numOfBlocksInData=0;
        }
        if(!logedIN){
            connections.send(connectionId, new ERROR((short) 6, "User not logged in"));
        }
    }

    public void execute(BCAST msg){
        return;
    }



    private boolean exists(String folder, String file){
        File folders=new File("Files", folder);
        boolean ans=false;
        for(String f : folders.list()){
            if(f.equals(file)){
                ans=true;
                break;
            }
        }
        return ans;
    }
}
