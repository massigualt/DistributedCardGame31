package project.server.main;

import project.server.ChatImpl;
import project.server.ChatInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServerDriver {


    public static void main(String[] args) {


        try {
            Registry registry = LocateRegistry.createRegistry(9999);
            ChatImpl server = new ChatImpl();
            System.out.println("Starting ChatInterface");
            //registry.rebind("rmi://localhost/"+ ChatInterface.DEFAULT_NAME, server);
            registry.rebind(ChatInterface.DEFAULT_NAME, server);

            System.out.println("ChatInterface ready: "+ registry.list());
        } catch (RemoteException e) {
            Logger.getLogger(ChatImpl.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("ChatInterface start problem "+ e.getMessage());
        } catch (Exception e) {
            Logger.getLogger(ChatImpl.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("Exception  "+ e.getMessage());
        }
    }


}
