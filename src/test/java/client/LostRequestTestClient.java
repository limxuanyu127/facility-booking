package client;

import commons.requests.QueryAvailabilityRequest;
import commons.requests.Request;
import commons.requests.TestRequest;
import commons.rpc.ClientCommunicator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LostRequestTestClient {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void main() {
        System.out.println("Lost Request Test with At Most Once Semantics");
        System.out.println("##################");
        InetAddress serverAddress = null;
        String hostname = "localhost";
        int serverPort = 5000;
        try {
            serverAddress = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ClientCommunicator router = new ClientCommunicator(3000, serverAddress, serverPort, 3, 100);

        Request req = new TestRequest();
        ServiceManager.request(router, req);
    }
}