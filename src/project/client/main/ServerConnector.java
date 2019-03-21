package project.client.main;

import project.server.ChatInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerConnector {
    private static ServerConnector serverConnector;
    private ChatInterface chat;

    public ServerConnector() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 9999);
            chat = (ChatInterface) registry.lookup(ChatInterface.DEFAULT_NAME);
        } catch (RemoteException e) {
            System.out.println("RemoteException: " + e.getMessage());
        } catch (NotBoundException e) {
            System.out.println("NotBoundException: " + e.getMessage());
        }
    }

    public static ServerConnector getServerConnector() {
        if (serverConnector == null) {
            serverConnector = new ServerConnector();
        }
        return serverConnector;
    }

    public ChatInterface getChat() {
        return chat;
    }
}
