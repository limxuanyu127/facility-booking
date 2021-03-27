package commons.rpc;
import commons.Deserializer;
import commons.Serializer;
import commons.requests.Request;
import commons.requests.TestRequest;
import commons.responses.NullResponse;
import commons.responses.Response;
import commons.responses.TestResponse;
import commons.utils.Packet;
import commons.utils.ResponseMessage;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

// To use, run TestServer on 1 terminal then run TestClient on another terminal

public class ClientCommunicator {
    int clientPort;
    InetAddress serverAddress;
    int serverPort;
    int packetSize;
    int messageSize;
    int headerSize;
    DatagramSocket socket;
    int maxTries;
    int requestID;

    /**
     * Creates a socket to send / receive UDP packets at specified port number
     * Packet size should be fixed across both client and main.java.server (hence its not an arg)
     *
     * @param clientPort port number for UDP socket
     */
    public ClientCommunicator(int clientPort, InetAddress serverAddress, int serverPort, int maxTries, int timeout) {
        this.clientPort = clientPort;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.packetSize = 512;
        /**
         * 4 bytes for requestID
         * 4 bytes for the datagram number
         * 4 bytes to send the total number of datagrams.
         * 4 bytes to send length of message in datagram
         */
        this.headerSize = 16;
        this.messageSize = this.packetSize - this.headerSize;
        this.maxTries = maxTries;
        try {
            this.socket = new DatagramSocket(clientPort);
            this.socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.requestID = 0;
    }

    /**
     * To Test, run ServerCommunicator then run ClientCommunicator
     */

    public static void main(String[] args) {
        // Success
        InetAddress serverAddress = null;
        String hostname = args[0];
        int serverPort = Integer.parseInt(args[1]);
        try {
            serverAddress = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println("\n###### Testing Successful Request #######");
        ClientCommunicator clientCommunicator = new ClientCommunicator(22, serverAddress, serverPort, 3, 5000);
        TestRequest request = new TestRequest();
        Response response = clientCommunicator.sendRequest(request);
        System.out.println("Client Hash: " + request.hashCode());
        TestResponse expected = new TestResponse();

        System.out.println("Response: " + response.name + " , Expected: " + expected.name);

        //Test Duplicate
        System.out.println("\n###### Testing Duplicate Request #######");
        response = clientCommunicator.sendRequest(request, 0);
        System.out.println("Client Hash should be same as above: " + request.hashCode());
        System.out.println("Response: " + response.name + " , Expected: " + expected.name);

        //Test Timeout
        System.out.println("\n###### Testing Timeout ######");
        clientCommunicator.socket.close();
        clientCommunicator = new ClientCommunicator(22, serverAddress, 17, 3, 1);

        response = clientCommunicator.sendRequest(request);
        expected = new TestResponse();

        try{
            System.out.println("Response: " + response.name + " , Expected: " + expected.name);
        }
        catch (NullPointerException e){
            System.out.println("No Response after all retries");
        }
    }

    public Response sendRequest(Request r){
        int currTries = 0;
        Response response = null;
        ByteBuffer dataBuf = ByteBuffer.allocate(2000);
        Serializer.serializeObject(r, dataBuf);

        while (currTries < this.maxTries){
            currTries += 1;
            System.out.println("Sending request " + currTries);
            this.send(dataBuf, this.requestID);
            try{
                response = this.receive();
                this.requestID += 1;
                return response;
            } catch (RuntimeException e){
                if (e.getCause() instanceof SocketTimeoutException) {
                    System.out.println("Socket Timeout, no response received");
                }
            }
        }
        if (currTries == this.maxTries){
            System.out.println("No response from server after all retries exceeded");
        }
        this.requestID += 1;
        ResponseMessage responseMessage = new ResponseMessage(500, "Request Timeout while waiting for reply.");
        return new NullResponse(responseMessage);
    }

    //To send duplicate requests
    public Response sendRequest(Request r, int requestID){
        int currTries = 0;
        Response response = null;
        ByteBuffer dataBuf = ByteBuffer.allocate(2000);
        Serializer.serializeObject(r, dataBuf);

        while (currTries < this.maxTries){
            currTries += 1;
            this.send(dataBuf, requestID);
            try{
                response = this.receive();
                return response;
            } catch (RuntimeException e){
                if (e.getCause() instanceof SocketTimeoutException) {
                    System.out.println("Socket Timeout, no response received");
                }
            }
        }
        if (currTries == this.maxTries){
            System.out.println("No response from server after all retries exceeded");
        }
        ResponseMessage responseMessage = new ResponseMessage(500, "Request Timeout while waiting for reply.");
        return new NullResponse(responseMessage);
    }

    public void send(ByteBuffer dataBuf, int requestID) {

        int totalDatagramPackets = (int) Math.ceil(dataBuf.position() / (float) this.messageSize);
        int dataBufPtr = 0;
        int dataBufMaxPos = dataBuf.position();

        for (int i = 0; i < totalDatagramPackets; i++){
            ByteBuffer packetBuf = ByteBuffer.allocate(this.packetSize);
            packetBuf.putInt(requestID);
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
            DatagramPacket message = new DatagramPacket(packetByteArray, packetByteArray.length, this.serverAddress, this.serverPort);

            try {
                this.socket.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Allows socket to listen for UDP packets
     *
     * @return ByteBuffer containing only message data
     */
    public Response receive() {
        try {
            System.out.println("listening for " + this.socket.getSoTimeout() + "ms");
        } catch (SocketException e) {
            e.printStackTrace();
        }
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
        Response response = (TestResponse) Deserializer.deserializeObject(combinedMessageBuffer);
        System.out.println(((TestResponse) response).testString);

        return response;
    }

    private Packet receivePacket(){
        byte[] buffer = new byte[this.packetSize];
        DatagramPacket message = new DatagramPacket(buffer, buffer.length);

        try {
            this.socket.receive(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public void close(){
        this.socket.close();
    }
}