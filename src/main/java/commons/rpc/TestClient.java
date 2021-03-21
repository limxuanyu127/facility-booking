package commons.rpc;
import commons.requests.TestRequest;

import java.net.*;

@Deprecated
public class TestClient {
    public static void main(String[] argv) throws UnknownHostException {
        ClientCommunicator clientCommunicator = new ClientCommunicator(22);
        System.out.println("Client init");
        InetAddress address = InetAddress.getByName("localhost");
        TestRequest testRequest = new TestRequest();
        clientCommunicator.send(testRequest, address, 17);
        clientCommunicator.receive();

        clientCommunicator.send(testRequest, address, 17);
        clientCommunicator.receive();
    }
}
