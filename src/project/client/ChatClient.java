package project.client;

import project.server.Server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ChatClient extends UnicastRemoteObject implements Client, Runnable {
    private static Scanner input;
    private String username = null;
    private Server server;
    // private Registry registry;
    // private Thread t;

    public ChatClient(Server server) throws RemoteException, NotBoundException {
        input = new Scanner(System.in);  // Create a Scanner object
        // System.out.print("Choose your name: ");
        // this.username = input.nextLine();// Read user input
        this.server = server;
        // System.out.print("Insert server ip/domain (at the moment use localhost): ");
        // String url = input.nextLine();
        // registry = LocateRegistry.getRegistry(url);
        //hand = (ServerManager) registry.lookup("lucky");
        // t = new Thread(this);
        // t.start();
    }

    @Override
    public void receive(String message) throws RemoteException {
        System.out.println(message);
    }

    @Override
    public void run() {
        String message;
        boolean isValidUsername = false;

        try {
            while (!isValidUsername) {
                System.out.print("Choose your name: ");
                this.username = input.nextLine();// Read user input
                isValidUsername = server.loginPossible(this.username);
                if (isValidUsername) {
                    System.out.println("Username valid!");
                } else {
                    System.out.println("Username not valid!");
                }
            }

            server.addUser(this.username, this);
            System.out.println("Successfully logged in!");
            System.out.println("Now you can chat! Type your messages");

            while (true) {
                message = input.nextLine();
                server.broadcastMessage(username, message);
                System.out.println("Type your message: ");
            }

        } catch (RemoteException e) {
            System.out.println("Error with Remote Connection! " + e.getMessage());
        }

    }

}
