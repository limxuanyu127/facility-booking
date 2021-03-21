package client;

import commons.rpc.ClientCommunicator;
import client.CLI;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        InetAddress serverAddress = null;
        try {
            serverAddress = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int serverPort = 5000;
        ClientCommunicator router = new ClientCommunicator(3000, serverAddress, serverPort);
        CLI.run(router, serverAddress, serverPort);
    }

}
