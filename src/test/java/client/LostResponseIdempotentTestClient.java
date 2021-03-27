package client;

import commons.requests.Request;
import commons.requests.TestRequest;
import commons.rpc.ClientCommunicator;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

class LostResponseIdempotentTestClient {
    @Test
    void main() {
        System.out.println("Lost Response Test with At Most Once Semantics");
        System.out.println("##################");
        InetAddress serverAddress = null;
        String hostname = "localhost";
        int serverPort = 5000;
        try {
            serverAddress = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ClientCommunicator router = new ClientCommunicator(3000, serverAddress, serverPort, 3, 1);
        Request req = new TestRequest();
        ServiceManager.request(router, req);
        router.close();
    }
}