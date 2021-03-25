package commons.rpc;

import commons.Deserializer;
import commons.Serializer;
import commons.requests.Request;
import commons.responses.Response;
import commons.responses.TestResponse;
import commons.utils.ClientRequest;
import commons.utils.Packet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ServerCommunicator {
    ArrayList<ClientRequest> clientRequests;
    ArrayList<Integer> clientRequestsHashed;

    int serverPort;
    int packetSize;
    int messageSize;
    int headerSize;
    DatagramSocket socket;
    int requestID;

    public ServerCommunicator(int serverPort) {
        this.clientRequests = new ArrayList<>();
        this.clientRequestsHashed = new ArrayList<>();
        this.requestID = 0;
        this.packetSize = 512;
        this.headerSize = 16;
        this.messageSize = this.packetSize - this.headerSize;
        this.serverPort = serverPort;
        try {
            this.socket = new DatagramSocket(serverPort);
        } catch (SocketException e) {}
        /**
         * Headersize Param
         * 4 bytes for requestID
         * 4 bytes for the datagram number
         * 4 bytes to send the total number of datagrams.
         * 4 bytes to send length of message in datagram
         */
    }

    /**
     * To Test, run ServerCommunicator then run ClientCommunicator
     */


    public static void main(String[] args) {
        ServerCommunicator serverCommunicator = new ServerCommunicator(17);
        while (true){
            serverCommunicator.receive(10);
        }
    }

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
        ClientRequest clientRequest;

        if (deserializedRequest instanceof commons.requests.Request){
            clientRequest = new ClientRequest(currPacket.senderAddress, currPacket.senderPort,
                    currPacket.requestID, (Request)deserializedRequest);
        }
        else{
            throw new Error("Server only receives Requests");
        }
        return clientRequest;
    }

    // to test timeout
    public ClientRequest receive(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.receive();
    }


//    private void processClientRequest(ClientRequest clientRequest){
//        int duplicateIndex = checkDuplicateRequest(clientRequest);
//        if (duplicateIndex == -999){
//            Request r = clientRequest.request;
//            Response response = null;
//
//            //TODO add server functions
//            switch(r.getClass().getName()){
//                case "commons.requests.BookFacilityRequest":
//                    System.out.println("Book Facility Received, calling Server Function...");
//                    break;
//                case "commons.requests.DeleteBookingRequest":
//                    System.out.println("Delete Booking Received, calling Server Function...");
//                    break;
//                case "commons.requests.OffsetBookingRequest":
//                    System.out.println("Offset Booking Request Received, calling Server Function...");
//                    break;
//                case "commons.requests.QueryAvailabilityRequest":
//                    System.out.println("Query Availability Request Received, calling Server Function...");
//                    break;
//                case "commons.requests.RegisterInterestRequest":
//                    System.out.println("Register Interest Request Received, calling Server Function...");
//                    break;
//                case "commons.requests.UpdateBookingRequest":
//                    System.out.println("Update Booking Request Received, calling Server Function...");
//                    break;
//                case "commons.requests.TestRequest":
//                    System.out.println("Test Request Received, calling Server Function...");
//                    response = new TestResponse();
//                    break;
//                default:
//                    System.out.println("Invalid Request Received");
//                    throw new RuntimeException("Invalid Request Type");
//            }
//
//            clientRequest.setSentResponse(response);
//            send(response, clientRequest.clientAddress, clientRequest.clientPort);
//        }
//        else{ //Duplicate Request - send original reply
//            ClientRequest orgRequest = this.clientRequests.get(duplicateIndex);
//            send(orgRequest.sentResponse, clientRequest.clientAddress, clientRequest.clientPort);
//        }
//    }

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
//        this.requestID += 1;

//        System.out.println("Sending message" + " to " + destIP + " " + destPort);
    }

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

    private int checkDuplicateRequest(ClientRequest clientRequest){
        int clientRequestHash = hashClientRequest(clientRequest);
        System.out.println("Hashed: " + clientRequestHash);
        if (clientRequestsHashed.contains(clientRequestHash)){
            System.out.println("Duplicate request received");
            int index = clientRequestsHashed.indexOf(clientRequestHash);
            return index;
        }
        else{
            this.clientRequests.add(clientRequest);
            this.clientRequestsHashed.add(clientRequestHash);
            return -999;
        }
    }

    private int hashClientRequest(ClientRequest clientRequest){
        int hash = 0;
        hash += clientRequest.clientAddress.hashCode();
        hash += clientRequest.clientPort;
        hash += clientRequest.requestID;

//        hash += clientRequest.request.hashCode();
        Field[] fields = clientRequest.request.getClass().getDeclaredFields();
        for (Field f: fields) {
            hash += f.hashCode();
        }
        return hash;
    }

}
