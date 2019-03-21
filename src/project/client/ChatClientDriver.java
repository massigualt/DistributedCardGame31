package project.client;


import project.server.ChatServer;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ChatClientDriver {
    public static void main(String[] args) {
        try {

            //Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ChatServer server = (ChatServer) Naming.lookup("rmi://localhost/prova");


            //new Thread(new StartClient(server)).start();
        } catch (RemoteException e) {
            System.out.println("Network Error!");
        } catch (NotBoundException e) {
            System.out.println("StartServer not reachable!");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
