package bgu.spl171.net.srv.bidi;

import bgu.spl171.net.api.MessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.api.bidi.Packets.Packet;
import bgu.spl171.net.srv.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> allConnections=new ConcurrentHashMap<>();
    private AtomicInteger numOfConnections=new AtomicInteger();


    public boolean send(int connectionId, T msg) {
        ConnectionHandler<T> c = allConnections.get(connectionId);
        if (c != null) {
            c.send(msg);
            return true;
        }
        return false;
    }


    public void broadcast(T msg){
        for (int key: allConnections.keySet()){
            send(key,msg);
        }
    }

    public void disconnect(int connectionId){
        allConnections.remove(connectionId);
    }

    public void addToConnections(ConnectionHandler<T> connectionHandler){
        allConnections.put(numOfConnections.get(), connectionHandler);
    }
}
