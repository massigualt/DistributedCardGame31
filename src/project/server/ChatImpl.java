package project.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ChatImpl extends UnicastRemoteObject implements ChatInterface {

    List<String> users = new ArrayList<>();
    List<Message> messages = new ArrayList<>();

    public ChatImpl() throws RemoteException {
        super(); // This activates code in UnicastRemoteObject that performs the RMI linking and remote object initialization.
    }

    @Override
    public boolean login(String username) throws RemoteException {
        if (!users.contains(username)) {
            users.add(username);
            Message message = new Message(username, " joined the conversation ", "join", new Date());
            messages.add(message);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void logout(String username) throws RemoteException {
        users.remove(username);
        Message message = new Message(username, " left the conversation ", "left", new Date());
        messages.add(message);
    }

    @Override
    public void sendMessage(Message message) throws RemoteException {
        messages.add(message);
    }

    @Override
    public List<Message> getAllMesage() throws RemoteException {
        for (Message m : messages) {
            System.out.println(m.getUsername() + " " + m.getMsg());
        }

        return messages;
    }

    @Override
    public List<String> getAllUsers() throws RemoteException {
        for (String u : users) {
            System.out.println(u);
        }
        return users;
    }

}
