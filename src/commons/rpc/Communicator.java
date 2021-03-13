package commons.rpc;
import commons.Serializer;
import commons.requests.Request;
import commons.responses.Response;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

// To use, run TestServer on 1 terminal then run TestClient on another terminal

public class Communicator {

    InetAddress address;
    int port;
    byte[] buffer_req;
    DatagramSocket socket;

    public Communicator(int port) {
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {}
    }

    public void send(Request request, InetAddress destIP, int destPort){

        byte[] data_buf = Serializer.serializeTestRequest(request).array();
        System.out.println("Sending message" + " to " + destIP + " " + destPort);

        DatagramPacket message = new DatagramPacket(data_buf, data_buf.length, destIP, destPort);
        try {
            this.socket.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Response response, InetAddress destIP, int destPort){

        byte[] data_buf = Serializer.serializeTestResponse(response).array();
        System.out.println("Sending message" + " to " + destIP + " " + destPort);

        DatagramPacket message = new DatagramPacket(data_buf, data_buf.length, destIP, destPort);
        try {
            this.socket.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ByteBuffer receive(){
        System.out.println("receiving");
        byte[] buffer = new byte[512];
        DatagramPacket message = new DatagramPacket(buffer, buffer.length);
        InetAddress senderAddress = message.getAddress();
        int senderPort = message.getPort();

        try {
            this.socket.receive(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteBuffer received_bb = ByteBuffer.wrap(buffer);

        String quote = new String(buffer, 0, message.getLength());

        System.out.println("Message Received: " + quote);
        return received_bb;
    }

}
