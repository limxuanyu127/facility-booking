package server;

import commons.requests.Request;
import commons.responses.Response;
import commons.responses.TestResponse;
import commons.rpc.ServerCommunicator;
import commons.utils.ClientRequest;
import javafx.util.Pair;
import server.entities.*;
import server.managers.*;
//import server.translator.Translator;

import java.awt.print.Book;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;

public class Server {
    BookingManager bookingManager;
    ServerCommunicator serverCommunicator;
//    Translator translator;
    boolean atMostOnce;

    public Server(int serverPort, boolean atMostOnce) {
        this.bookingManager = new BookingManager();
        this.serverCommunicator = new ServerCommunicator(serverPort, atMostOnce);
//        this.translator = new Translator();
    }

    public static void main(String[] args) {
        int serverPort = Integer.parseInt(args[0]);
        boolean atMostOnce = Boolean.parseBoolean(args[1]);
        Server server = new Server(serverPort, atMostOnce);
        while (true) {
            server.run();
        }
    }

    private void run() {
        Optional<ClientRequest> optionalClientRequest = serverCommunicator.receive();
        if (optionalClientRequest.isPresent()) {
            ClientRequest clientRequest = optionalClientRequest.get();
            Request request = clientRequest.request;
            Response response = null;

            //TODO add server functions
            switch(request.getClass().getName()){
                case "commons.requests.BookFacilityRequest":
                    System.out.println("Book Facility Received, calling Translator Function...");
//                    response = translator.test();
                    break;
                case "commons.requests.DeleteBookingRequest":
                    System.out.println("Delete Booking Received, calling Translator Function...");
//                    response = translator.test();
                    break;
                case "commons.requests.OffsetBookingRequest":
                    System.out.println("Offset Booking Request Received, calling Translator Function...");
//                    response = translator.test();
                    break;
                case "commons.requests.QueryAvailabilityRequest":
                    System.out.println("Query Availability Request Received, calling Translator Function...");
//                    response = translator.test();
                    break;
                case "commons.requests.RegisterInterestRequest":
                    System.out.println("Register Interest Request Received, calling Translator Function...");
//                    response = translator.test();
                    break;
                case "commons.requests.UpdateBookingRequest":
                    System.out.println("Update Booking Request Received, calling Translator Function...");
//                    response = translator.test();
                    break;
                case "commons.requests.TestRequest":
                    System.out.println("Test Request Received, calling Translator Function...");
                    response = new TestResponse();
                    break;
                default:
                    System.out.println("Invalid Request Received");
                    throw new RuntimeException("Invalid Request Type");
            }
            clientRequest.setSentResponse(response);
            serverCommunicator.send(response, clientRequest.clientAddress, clientRequest.clientPort);
        }
    }


}


    //    int port = 5005;
    //    int packetSize = 512;
    //    byte[] buffer = new byte[packetSize];
//        DatagramPacket message = new DatagramPacket(buffer, buffer.length);
//        DatagramSocket sock;
//
//        try{
//            sock = new DatagramSocket(port);
//            System.out.println("main.java.server listening");
//            sock.receive(message);
//        } catch (IOException e){
//            e.printStackTrace();
//        }