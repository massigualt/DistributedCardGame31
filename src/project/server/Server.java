package project.server;

import project.client.Client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Server extends Remote, Serializable {
    String DEFAULT_NAME = "ChatServer";

    boolean loginPossible(String username) throws RemoteException;

    void addUser(String username, Client chatClient) throws RemoteException;

    void logOut(String username) throws RemoteException;

    void broadcastMessage(String username, String message) throws RemoteException;


}

