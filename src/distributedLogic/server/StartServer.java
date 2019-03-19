package distributedLogic.server;

import distributedLogic.Connection;
import distributedLogic.Player;

import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class StartServer {
    public static final int PORT = 1099;

    public static void main(String[] args) {
        final int seconds = 50;
        final int maxPlayers = 2;

        try {
            final Connection connection = new Connection(maxPlayers);
            LocateRegistry.createRegistry(PORT);
            final String rmiPath = "rmi://localhost:" + PORT + "/Server";
            Naming.rebind(rmiPath, connection);
            System.out.println("SERVER: RemoteAnnounce service is up.");
            System.out.println("EMILIO: "+Inet4Address.getLocalHost().getHostAddress());

            //THREAD
            Thread t = new Thread() {
                public void run() {
                    try {
                        sleep(seconds * 1000);
                        connection.endSigning();
                        Naming.unbind(rmiPath);
                        System.out.println("SERVER: RemoteAnnounce service is down.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (NotBoundException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            };

            t.start();
            Player[] players = connection.getPlayers();

            if (connection.getPlayersNumber() > 0) {
                for (int i = 0; i < connection.getPlayersNumber(); i++) {
                    Player player = players[i];
                    System.out.println("Player " + (i + 1) + " is "
                            + player.getName() + " ("
                            + player.getInetAddress().getHostAddress()
                            + ":" + player.getPort() + ")");
                }
            }


            System.out.println("Press Any Key To Exit...");
            new java.util.Scanner(System.in).nextLine();
            System.exit(0);
        } catch (
                RemoteException e) {
            System.out.println("RemoteException");
            e.printStackTrace();
        } catch (
                MalformedURLException e) {
            System.out.println("MalformedURLException");
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }
}
