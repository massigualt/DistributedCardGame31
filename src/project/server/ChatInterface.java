package project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatInterface extends Remote {
    String DEFAULT_NAME = "ChatImpl";

    boolean login(String username) throws RemoteException;

    void logout(String username) throws RemoteException;

    void sendMessage(Message message) throws RemoteException;

    List<Message> getAllMesage() throws RemoteException;

    List<String> getAllUsers() throws RemoteException;

    //void addUser(String username, StartClient chatClient) throws RemoteException;

    // void logOut(String username) throws RemoteException;

    //void broadcastMessage(String username, String message) throws RemoteException;


}

