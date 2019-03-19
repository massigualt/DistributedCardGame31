package project;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Utility {

    public static String TrovaIp() throws Exception {
        List< String > Indirizzo = new ArrayList< String >();
        InetAddress addr = Inet4Address.getLocalHost();

        // Get IP Address
        String ipAddr = addr.getAddress().toString();

        // Get hostname
        String hostname = addr.getHostName();

        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();
                if (i instanceof Inet4Address && i.getHostAddress().equals("127.0.0.1") == false && (i.getHostAddress().contains("130.136") || i.getHostAddress().contains("192.168"))) {
                    return i.getHostAddress();
                }
            }
        }
        return "error";

    }
}
