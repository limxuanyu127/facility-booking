package commons.rpc;

import commons.requests.TestRequest;
import commons.responses.TestResponse;

import java.io.IOException;
import java.net.*;

public class TestServer {
    public static void main(String[] argv) throws UnknownHostException {

        Communicator communicator = new Communicator(17);
        InetAddress address = InetAddress.getByName("localhost");
        TestResponse testResponse = new TestResponse();

        while (true) {
            communicator.receive();
            communicator.send(testResponse, address, 22);
        }
    }


}
