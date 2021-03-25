package client;

import commons.rpc.ClientCommunicator;
import client.CLI;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        InetAddress serverAddress = null;

        String hostname = args[0];
        int serverPort = Integer.parseInt(args[1]);
        
//        String hostname = "localhost";
        //        int serverPort = 5000;
        try {
            serverAddress = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        ClientCommunicator router = new ClientCommunicator(3000, serverAddress, serverPort, 3, 10);
        CLI.run(router, serverAddress, serverPort);
    }

}
