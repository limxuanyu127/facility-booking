package commons.rpc;
import commons.requests.TestRequest;

import java.io.*;
import java.net.*;

public class TestClient {
    public static void main(String[] argv) throws UnknownHostException {
        Communicator communicator = new Communicator(22);
        System.out.println("Client init");
        InetAddress address = InetAddress.getByName("localhost");
        TestRequest testRequest = new TestRequest();
        communicator.send(testRequest, address, 17);
        communicator.receive();

        communicator.send(testRequest, address, 17);
        communicator.receive();
    }
}
