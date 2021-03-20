package client;

import commons.rpc.Communicator;
import client.CLI;

public class Main {
    public static void main(String[] args) {
        Communicator router = new Communicator(3000);
        CLI.run(router);
    }

}
