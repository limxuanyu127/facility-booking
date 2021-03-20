package client;

import commons.rpc.Communicator;
import client.CLI;

import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        Communicator router = new Communicator(3000);
        InetAddress serverAddress;
        int serverPort;
        CLI.run(router, serverAddress, serverPort);
    }

}
