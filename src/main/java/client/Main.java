package client;

import commons.rpc.ClientCommunicator;
import client.CLI;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Entrypoint for client, takes in 2 runtime arguments: server hostname and server port
 * @param args list of String arguments to configure the server (hostname and port)
 */

public class Main {
    public static void main(String[] args) {
        InetAddress serverAddress = null;

        String hostname = args[0];
        int serverPort = Integer.parseInt(args[1]);
        int clientPort = 3002;
        int timeout = 1000; // in ms
        int maxTries = 5;
        double packetDropOffRate = 0;

//        int p = Integer.parseInt(null);
//        System.out.println(p.hashcode());
        
//        String hostname = "localhost";
        //        int serverPort = 5000;
        try {
            serverAddress = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ClientCommunicator router = new ClientCommunicator(clientPort, serverAddress, serverPort, maxTries, timeout, packetDropOffRate);
        CLI.run(router, serverAddress, serverPort);
    }



}
