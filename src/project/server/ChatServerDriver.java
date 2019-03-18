package project.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServerDriver {


    public static void main(String[] args) {


        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            ChatServer server = new ChatServer();
            System.out.println("Starting Server");
            //registry.rebind("rmi://localhost/"+ Server.DEFAULT_NAME, server);
            registry.rebind(Server.DEFAULT_NAME, server);

            System.out.println("Server ready: "+ registry.list());
        } catch (RemoteException e) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("Server start problem "+ e.getMessage());
        } catch (Exception e) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("Exception  "+ e.getMessage());
        }
    }


}
