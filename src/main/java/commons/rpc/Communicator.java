package commons.rpc;
import commons.Serializer;
import commons.requests.Request;
import commons.responses.Response;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

// To use, run TestServer on 1 terminal then run TestClient on another terminal

public class Communicator {

    InetAddress address;
    int port;
    int packetSize;
    int messageSize;
    int headerSize;
    byte[] buffer_req;
    DatagramSocket socket;
    int requestID;

    /**
     * Creates a socket to send / receive UDP packets at specified port number
     * Packetsize should be fixed across both client and server (hence its not an arg)
     * @param port port number for UDP socket
     */
    public Communicator(int port) {
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {}
        this.requestID = 0;
        this.packetSize = 512;
        this.headerSize = 16;
        this.messageSize = this.packetSize - this.headerSize;
        /**
         * 4 bytes for requestID
         * 4 bytes for the datagram number
         * 4 bytes to send the total number of datagrams.
         * 4 bytes to send length of message in datagram
         */
    }

    /**
     * Sends request / response to destIP and destPort
     * @param o request / response object
     * @param destIP destination IP
     * @param destPort destination port
     */
    public void send(Object o, InetAddress destIP, int destPort){

        String className = o.getClass().getName();
        ByteBuffer dataBuf = null;
        if (className == "commons.requests.TestRequest"){
            Request request = (Request) o;
            dataBuf = Serializer.serializeTestRequest(request);
        }
        else if (className == "commons.responses.TestResponse"){
            Response response = (Response) o;
            dataBuf = Serializer.serializeTestResponse(response);
        }
        else {
            System.out.println("unknown class");
            System.out.println(className);
        }

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
            DatagramPacket message = new DatagramPacket(packetByteArray, packetByteArray.length, destIP, destPort);

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
     * @return ByteBuffer containing only message data
     * @Todo: Add deserialization
     */
    public ClientRequest receive(){
        System.out.println("receiving");
        Packet currPacket = this.receivePacket();
        int combinedMessageSize = currPacket.totalDatagramPackets * currPacket.messageSize;
        ByteBuffer combinedMessageBuffer = ByteBuffer.allocate(combinedMessageSize);
        combinedMessageBuffer.put(currPacket.messageBuffer);

        if (currPacket.totalDatagramPackets != 1){
            while (currPacket.datagramNum < currPacket.totalDatagramPackets - 1) {
                currPacket = this.receivePacket();
                combinedMessageBuffer.put(currPacket.messageBuffer);
            }
        }
        byte[] testBuffer = combinedMessageBuffer.array();
        String quote = new String(testBuffer, 0, combinedMessageSize);
        System.out.println("Message Received: " + quote);

        ClientRequest clientRequest = new ClientRequest(currPacket.senderAddress, currPacket.senderPort,
                currPacket.requestID, combinedMessageBuffer);

        return clientRequest;
    }

    /**
     * Private method for this.receive() to use if the message size exceeds packet size
     * @return Packet object containing message info
     */
    private Packet receivePacket(){
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
