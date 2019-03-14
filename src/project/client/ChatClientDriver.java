package project.client;


import project.server.Server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ChatClientDriver {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Server server = (Server) registry.lookup(Server.DEFAULT_NAME);

            // String chatServerURL = "rmi://localhost/lucky";
            // Server chatServer = (Server) Naming.lookup(chatServerURL);
            // RMISocketFactory.setSocketFactory(new SocketFactory());

            new Thread(new ChatClient(server)).start();
        } catch (RemoteException e) {
            System.out.println("Network Error!");
        } catch (NotBoundException e) {
            System.out.println("Server not reachable!");
        }
    }
}
