package commons.rpc;

import commons.responses.TestResponse;

import java.net.*;

public class TestServer {
    public static void main(String[] argv) throws UnknownHostException {

        Communicator communicator = new Communicator(17);
        InetAddress address = InetAddress.getByName("localhost");
        TestResponse testResponse = new TestResponse();
        ClientRequestLog clientRequestLog = new ClientRequestLog();

        while (true) {
            ClientRequest clientRequest = communicator.receive();
            Boolean isNewRequest = clientRequestLog.addClientRequest(clientRequest);
            if (isNewRequest){
                communicator.send(testResponse, address, 22);
            }
            else{
                System.out.println("Duplicated request, no response");
            }
        }
    }


}
