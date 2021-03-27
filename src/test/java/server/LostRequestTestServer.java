package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.DatagramSocket;
import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

class LostRequestTestServer {
    @Test
    void LostRequestTest() {
        int serverPort = 5000;
        int sleep = 5000;
        Server server = new Server(serverPort, true);
        System.out.println("Lost Request Test");
        // only open port for 1ms every 5000ms
        server.serverCommunicator.close();
        for (int i = 0; i < 3; i++){
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                server.serverCommunicator.socket = new DatagramSocket(serverPort);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            server.run(1);
            server.serverCommunicator.close();
        }
        // server never receives request
        assertEquals(0, server.getTestCounter());
        System.out.println("Expected: 0, Actual: " + server.getTestCounter());
    }
}