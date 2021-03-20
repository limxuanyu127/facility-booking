package commons.rpc;

import commons.Deserializer;
import commons.requests.TestRequest;
import commons.utils.ClientRequest;
import commons.utils.Packet;

import java.net.InetAddress;
import java.nio.ByteBuffer;

// To use, run TestServer on 1 terminal then run TestClient on another terminal

public class ServerCommunicator extends Communicator {

    /**
     * Creates a socket to send / receive UDP packets at specified port number
     * Packetsize should be fixed across both client and main.java.server (hence its not an arg)
     *
     * @param port port number for UDP socket
     */
    public ServerCommunicator(int port) {
        super(port);

    }

    /**
     * Sends response to destIP and destPort
     *
     * @param o       request / response object
     * @param destIP   destination IP
     * @param destPort destination port
     */

    @Override
    public void send(Object o, InetAddress destIP, int destPort) {
        if (!(o instanceof commons.responses.Response)){
            throw new Error("Server communicator only sends Responses");
        }
        else{
            super.send(o, destIP, destPort);
        }
    }

    /**
     * Allows socket to listen for UDP packets
     *
     * @return ByteBuffer containing only message data
     */
    public ClientRequest receive() {
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

        //TODO fix casting
        TestRequest tempReq = (TestRequest) Deserializer.deserializeObject(combinedMessageBuffer);
        System.out.println(tempReq.testString);

        ClientRequest clientRequest = new ClientRequest(currPacket.senderAddress, currPacket.senderPort,
                currPacket.requestID, combinedMessageBuffer);

        return clientRequest;
    }
}
