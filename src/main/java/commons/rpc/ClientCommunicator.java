package commons.rpc;
import commons.Deserializer;
import commons.Serializer;
import commons.requests.Request;
import commons.responses.Response;
import commons.responses.TestResponse;
import commons.utils.Packet;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

// To use, run TestServer on 1 terminal then run TestClient on another terminal

public class ClientCommunicator extends Communicator {

    /**
     * Creates a socket to send / receive UDP packets at specified port number
     * Packetsize should be fixed across both client and main.java.server (hence its not an arg)
     *
     * @param port port number for UDP socket
     */
    public ClientCommunicator(int port) {
        super(port);
    }

    /**
     * Sends request / response to destIP and destPort
     *
     * @param o        request / response object
     * @param destIP   destination IP
     * @param destPort destination port
     */
    @Override
    public void send(Object o, InetAddress destIP, int destPort) {
        if (!(o instanceof commons.requests.Request)){
            throw new Error("Client communicator only sends Request");
        }
        else{
            super.send(o, destIP, destPort);
        }
    }

    /**
     * Allows socket to listen for UDP packets
     *
     * @return ByteBuffer containing only message data
     * @Todo: Fix deserialization
     */
    public Response receive() {
        System.out.println("receiving");
        Packet currPacket = this.receivePacket();
        int combinedMessageSize = currPacket.totalDatagramPackets * currPacket.messageSize;
        ByteBuffer combinedMessageBuffer = ByteBuffer.allocate(combinedMessageSize);
        combinedMessageBuffer.put(currPacket.messageBuffer);

        if (currPacket.totalDatagramPackets != 1) {
            while (currPacket.datagramNum < currPacket.totalDatagramPackets - 1) {
                currPacket = this.receivePacket();
                combinedMessageBuffer.put(currPacket.messageBuffer);
            }
        }
//        byte[] testBuffer = combinedMessageBuffer.array();
//        String quote = new String(testBuffer, 0, combinedMessageSize);
//        System.out.println("Message Received: " + quote);

        combinedMessageBuffer.flip();
        Response response = (TestResponse) Deserializer.deserializeObject(combinedMessageBuffer);
        System.out.println(((TestResponse) response).testString);

        return response;
    }
}