package project.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ChatServerDriver {


    public static void main(String[] args) {
        //int portNumber = 4567;

        try {
            ChatServer server = new ChatServer();
            System.out.println("Starting Server");

            Registry registry = LocateRegistry.getRegistry();

            registry.rebind(Server.DEFAULT_NAME, server);
            System.out.println("Server ready on port: " );//+ portNumber);
        } catch (RemoteException e) {
            System.out.println("Network Error!");
        }
    }
}
