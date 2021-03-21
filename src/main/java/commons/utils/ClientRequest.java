package commons.utils;

import commons.requests.Request;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ClientRequest {
    public InetAddress clientAddress;
    public int clientPort;
    public int requestID;
    public Request request;

    public ClientRequest(InetAddress clientAddress, int clientPort, int requestID, Request request) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.requestID = requestID;
        this.request = request;
    }
}
