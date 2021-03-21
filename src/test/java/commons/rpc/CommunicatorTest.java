package commons.rpc;

import commons.requests.TestRequest;
import commons.responses.TestResponse;
import commons.utils.ClientRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class CommunicatorTest {
    ServerCommunicator serverCommunicator;
    ClientCommunicator clientCommunicator;
    InetAddress serverAddress;
    InetAddress clientAddress;
    int serverPort = 17;
    int clientPort = 22;
    {
        try {
            this.serverAddress = InetAddress.getByName("localhost");
            this.clientAddress = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    @BeforeEach
    void setUp() {

        this.serverCommunicator = new ServerCommunicator(this.serverPort);
        this.clientCommunicator = new ClientCommunicator(this.clientPort, this.serverAddress, this.serverPort);
    }

    @AfterEach
    void tearDown() {
        this.serverCommunicator.socket.close();
        this.clientCommunicator.socket.close();
    }


    @Test
    void clientToServerBasic() {
        TestRequest testRequest = new TestRequest();

        clientCommunicator.send(testRequest);
        ClientRequest clientRequest = new ClientRequest(this.serverAddress, 22, 0, testRequest);

        ClientRequest received = this.serverCommunicator.receive();
        assertEquals(clientRequest.clientAddress, received.clientAddress);
        assertEquals(clientRequest.clientPort, received.clientPort);
        assertEquals(clientRequest.requestID, received.requestID);
        assertEquals(clientRequest.request.name, received.request.name);
    }

    @Test
    void serverToClientBasic() {
        TestResponse testResponse = new TestResponse();
        serverCommunicator.send(testResponse, clientAddress, clientPort);

        TestResponse received = (TestResponse) this.clientCommunicator.receive();
        assertEquals(testResponse.testInt, received.testInt);
        assertEquals(testResponse.testString, received.testString);
        assertEquals(testResponse.name, received.name);
    }

}