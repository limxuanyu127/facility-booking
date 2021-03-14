package commons.rpc;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ClientRequest {
    InetAddress clientAddress;
    int clientPort;
    int requestID;
    ByteBuffer message;

    public ClientRequest(InetAddress clientAddress, int clientPort, int requestID, ByteBuffer message) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.requestID = requestID;
        this.message = message;
    }
}
