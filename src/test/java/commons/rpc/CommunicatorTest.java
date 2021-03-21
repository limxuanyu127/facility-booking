package commons.rpc;

import commons.requests.Request;
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
    InetAddress address;
    int serverPort = 17;
    int clientPort = 22;


    @BeforeEach
    void setUp() {
        this.serverCommunicator = new ServerCommunicator(this.serverPort);
        this.clientCommunicator = new ClientCommunicator(this.clientPort);
        try{
            this.address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
//        this.serverCommunicator.receive();
    }

    @AfterEach
    void tearDown() {
        this.serverCommunicator.socket.close();
        this.clientCommunicator.socket.close();
    }


    @Test
    void clientToServer() {
        TestRequest testRequest = new TestRequest();

        clientCommunicator.send(testRequest, address, this.serverPort);
        ClientRequest clientRequest = new ClientRequest(this.address, 22, 0, testRequest);

        ClientRequest received = this.serverCommunicator.receive();
        assertEquals(clientRequest.clientAddress, received.clientAddress);
        assertEquals(clientRequest.clientPort, received.clientPort);
        assertEquals(clientRequest.requestID, received.requestID);
        assertEquals(clientRequest.request.name, received.request.name);
    }

    @Test
    void serverToClient() {
        TestResponse testResponse = new TestResponse();
        serverCommunicator.send(testResponse, address, this.clientPort);

        TestResponse received = (TestResponse) this.clientCommunicator.receive();
        assertEquals(testResponse.testInt, received.testInt);
        assertEquals(testResponse.testString, received.testString);
        assertEquals(testResponse.name, received.name);
    }

}