package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LostResponseIdempotentTestServer {
    @Test
    void Main(){
        System.out.println("Lost Response Test with At Most Once Semantics");
        System.out.println("##################");
        int serverPort = 5000;
        Server server = new Server(serverPort, true);

        for (int i = 0; i < 3; i++) {
            server.run(10000);
        }
        assertEquals(1, server.getTestCounter());
        System.out.println("Expected Counter Value: 1, Actual Counter value: " + server.getTestCounter());
        server.serverCommunicator.close();
    }
}