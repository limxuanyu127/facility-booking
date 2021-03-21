package commons.rpc;

import commons.Deserializer;
import commons.Serializer;
import commons.requests.Request;
import commons.requests.TestRequest;
import commons.responses.Response;
import commons.utils.ClientRequest;
import commons.utils.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

// To use, run TestServer on 1 terminal then run TestClient on another terminal

public class ServerCommunicator{

    /**
     * Creates a socket to send / receive UDP packets at specified port number
     * Packetsize should be fixed across both client and main.java.server (hence its not an arg)
     *
     * @param port port number for UDP socket
     */

    int serverPort;
    int packetSize;
    int messageSize;
    int headerSize;
    byte[] buffer_req;
    DatagramSocket socket;
    int requestID;

    public ServerCommunicator(int serverPort) {
        this.requestID = 0;
        this.packetSize = 512;
        this.headerSize = 16;
        this.messageSize = this.packetSize - this.headerSize;
        this.serverPort = serverPort;
        try {
            this.socket = new DatagramSocket(serverPort);
        } catch (SocketException e) {}
        /**
         * 4 bytes for requestID
         * 4 bytes for the datagram number
         * 4 bytes to send the total number of datagrams.
         * 4 bytes to send length of message in datagram
         */
    }

    public void send(Response r, InetAddress clientAddress, int clientPort) {
        ByteBuffer dataBuf = ByteBuffer.allocate(2000);
        Serializer.serializeObject(r, dataBuf);

        int totalDatagramPackets = (int) Math.ceil(dataBuf.position() / (float) this.messageSize);
        int dataBufPtr = 0;
        int dataBufMaxPos = dataBuf.position();

        for (int i = 0; i < totalDatagramPackets; i++){
            ByteBuffer packetBuf = ByteBuffer.allocate(this.packetSize);
            packetBuf.putInt(this.requestID);
            packetBuf.putInt(i);
            packetBuf.putInt(totalDatagramPackets);
            byte[] packetByteArray;
            dataBuf.position(dataBufPtr);

            if (i == totalDatagramPackets - 1){ // last packet or when only 1 packet needed - only writes remainder of dataBuf
                int lengthRemaining = dataBufMaxPos - dataBufPtr;
                packetBuf.putInt(lengthRemaining);
                packetByteArray = packetBuf.array();
                dataBuf.get(packetByteArray, this.headerSize, lengthRemaining);
            }
            else{ // writes messageSize worth of data to DatagramPacket
                packetBuf.putInt(this.messageSize);
                packetByteArray = packetBuf.array();
                dataBuf.get(packetByteArray, this.headerSize, this.messageSize);
                dataBufPtr += this.messageSize;
            }
            DatagramPacket message = new DatagramPacket(packetByteArray, packetByteArray.length, clientAddress, clientPort);

            try {
                this.socket.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /**
         * comment out row below to test duplicate request
         */
        this.requestID += 1;

//        System.out.println("Sending message" + " to " + destIP + " " + destPort);
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

        combinedMessageBuffer.flip();
        Object deserializedRequest = Deserializer.deserializeObject(combinedMessageBuffer);
        deserializedRequest.getClass().cast(deserializedRequest);

        ClientRequest clientRequest;

        if (deserializedRequest instanceof commons.requests.Request){
             clientRequest = new ClientRequest(currPacket.senderAddress, currPacket.senderPort,
                    currPacket.requestID, (Request) deserializedRequest);
             System.out.println(((Request) deserializedRequest).name);
        }
        else{
            throw new Error("Server only receives Requests");
        }

        return clientRequest;
    }

    protected Packet receivePacket(){
        byte[] buffer = new byte[this.packetSize];
        DatagramPacket message = new DatagramPacket(buffer, buffer.length);

        try {
            this.socket.receive(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteBuffer received_bb = ByteBuffer.wrap(buffer);
        int requestID = received_bb.getInt();
        int datagramNum = received_bb.getInt();
        int totalDatagramPackets = received_bb.getInt();
        int messageSize = received_bb.getInt();

        InetAddress senderAddress = message.getAddress();
        int senderPort = message.getPort();

        ByteBuffer messageBuffer = ByteBuffer.allocate(messageSize);
        messageBuffer.put(buffer, this.headerSize, messageSize);
        messageBuffer.position(0);

        return new Packet(requestID, datagramNum, totalDatagramPackets, messageSize, senderAddress, senderPort, messageBuffer);
    }
}
