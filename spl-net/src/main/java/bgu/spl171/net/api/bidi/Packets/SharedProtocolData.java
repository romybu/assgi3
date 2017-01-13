package bgu.spl171.net.api.bidi.Packets;


import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


public class SharedProtocolData {
    private ConcurrentHashMap<String, Integer> allUsers=new ConcurrentHashMap<>();
    private LinkedList<String> filesInProcess= new LinkedList<String>();

    public void addToFiles(String s){
        filesInProcess.add(s);
    }

    public void addToUsers(String s, Integer i){
        allUsers.put(s,i);
    }
    public ConcurrentHashMap<String, Integer> getAllUsers() {
        return allUsers;
    }

    public LinkedList<String> getFilesInProcess() {
        return filesInProcess;
    }
}
