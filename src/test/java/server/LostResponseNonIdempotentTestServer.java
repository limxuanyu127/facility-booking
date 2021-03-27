package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LostResponseNonIdempotentTestServer {
    @Test
    void Main(){
        System.out.println("Lost Response Test with At Least Once Semantics");
        System.out.println("##################");
        int serverPort = 5000;
        Server server = new Server(serverPort, false);

        for (int i = 0; i < 3; i++) {
            server.run(10000);
        }
        assertEquals(3, server.getTestCounter());
        System.out.println("Expected Counter Value: 3, Actual Counter value: " + server.getTestCounter());
        server.serverCommunicator.close();
    }
}