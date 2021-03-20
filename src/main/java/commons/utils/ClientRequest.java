package commons.utils;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ClientRequest {
    public InetAddress clientAddress;
    public int clientPort;
    public int requestID;
    public ByteBuffer message;

    public ClientRequest(InetAddress clientAddress, int clientPort, int requestID, ByteBuffer message) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.requestID = requestID;
        this.message = message;
    }
}
