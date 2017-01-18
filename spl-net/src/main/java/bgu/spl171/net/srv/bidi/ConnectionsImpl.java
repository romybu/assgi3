package bgu.spl171.net.srv.bidi;

import bgu.spl171.net.api.MessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.api.bidi.Packets.Packet;
import bgu.spl171.net.srv.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> allConnections=new ConcurrentHashMap<>();
    public AtomicInteger numOfConnections=new AtomicInteger();

    public boolean send(int connectionId, T msg) {
        System.out.println("im in sending connections");
        ConnectionHandler<T> c = allConnections.get(connectionId);
        if (c != null) {
            c.send(msg);
            System.out.println("i did c.send");
            return true;
        }
        return false;
    }


    public void broadcast(T msg){
        for (int key: allConnections.keySet()){
            send(key,msg);
        }
    }

    public synchronized void disconnect(int connectionId){
        try {
            allConnections.remove(connectionId).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addToConnections(ConnectionHandler<T> connectionHandler){
        allConnections.put(numOfConnections.get(), connectionHandler);
        numOfConnections.incrementAndGet();
    }
}
