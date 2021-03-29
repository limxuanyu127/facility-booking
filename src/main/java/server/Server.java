package server;

import commons.requests.DeleteBookingRequest;
import commons.requests.QueryAvailabilityRequest;
import commons.requests.Request;

import commons.requests.*;
import commons.responses.*;
import commons.responses.Response;
import commons.responses.TestResponse;
import commons.rpc.ServerCommunicator;
import commons.utils.ClientRequest;
import javafx.util.Pair;
import server.entities.*;
import server.managers.*;
import server.translator.Translator;


import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Optional;

//TODO implement bookId count
//TODO remove client id from bookings(?)

public class Server {
    BookingManager bookingManager;
    ObserverManager observerManager;
    Hashtable<String, Facility> facilTable;
    ServerCommunicator serverCommunicator;
    Translator translator;

    int testCounter = 0;
    int bookingIdCounter;


    public Server(int serverPort, boolean atMostOnce) {
        this.bookingManager = new BookingManager();
        this.observerManager = new ObserverManager();
        this.facilTable = new Hashtable<>();
        this.serverCommunicator = new ServerCommunicator(serverPort, atMostOnce);
        this.translator = new Translator(serverCommunicator);

        bookingIdCounter =0;
        Facility badmintonCourt = new Facility("badmintonCourt");
        Facility gym = new Facility("gym");
        //FIXME Have to ensure that facil name is lower case
        facilTable.put("badmintoncourt", badmintonCourt);
        facilTable.put("gym", gym);
    }

    public static void main(String[] args) {
        int serverPort = Integer.parseInt(args[0]);
        boolean atMostOnce = Boolean.parseBoolean(args[1]);
        Server server = new Server(serverPort, atMostOnce);
        while (true) {
            server.run(0);
        }
    }

    void run(int timeout) {
        Optional<ClientRequest> optionalClientRequest;
        if (timeout == 0){
            optionalClientRequest = serverCommunicator.receive();
        }
        else{
            optionalClientRequest = serverCommunicator.receive(timeout);
        }

        if (optionalClientRequest.isPresent()) {
            ClientRequest clientRequest = optionalClientRequest.get();
            Request request = clientRequest.request;
            Response response = null;
            String facilName = null;
            Exception e = null;

            //TODO
            // Run tests to see if type castings (eg. BookingFacilityResponse to Response) is valid
            // For translator -> bookingManager + entities, there are some that still takes in client id, but those should be removed
            // Update translator.notifyObservers() function to send notifications

            //DONE
            //for the addObserver function, figure out how to get inet addr + port number

            switch(request.getClass().getName()){
                case "commons.requests.BookFacilityRequest":
                    System.out.println("Book Facility Received, calling Translator Function...");
                    Response createResponse = translator.createBooking((BookFacilityRequest)request, bookingIdCounter, 0, bookingManager, facilTable);
                    if (createResponse instanceof BookFacilityResponse) {
                        bookingIdCounter +=1;
                        facilName = ((BookFacilityRequest) request).facilityName;
                        e = translator.notifyObservers(facilName, bookingManager, observerManager, facilTable);
                    }
                    response = createResponse;
                    break;
                case "commons.requests.DeleteBookingRequest":
                    System.out.println("Delete Booking Received, calling Translator Function...");
                    Response deleteResponse = translator.deleteBooking((DeleteBookingRequest) request, bookingManager, facilTable);
                    if (deleteResponse instanceof DeleteBookingResponse) {
                        facilName = ((DeleteBookingRequest) request).facilityName;
                        e = translator.notifyObservers(facilName, bookingManager, observerManager, facilTable);
                    }
                    response = deleteResponse;
                    break;
                case "commons.requests.OffsetBookingRequest":
                    System.out.println("Offset Booking Request Received, calling Translator Function...");
                    Response offsetResponse = translator.offsetBooking((OffsetBookingRequest) request, bookingManager, facilTable);
                    if (offsetResponse instanceof  OffsetBookingResponse){
                        facilName = ((OffsetBookingRequest) request).facilityName;
                        e = translator.notifyObservers(facilName, bookingManager, observerManager, facilTable);
                    }
                    response = offsetResponse;
                    break;
                case "commons.requests.QueryAvailabilityRequest":
                    System.out.println("Query Availability Request Received, calling Translator Function...");
                    response = translator.queryAvailability((QueryAvailabilityRequest) request, bookingManager, facilTable);
                    break;
                case "commons.requests.RegisterInterestRequest":
                    System.out.println("Register Interest Request Received, calling Translator Function...");
                    InetAddress clientAddress = clientRequest.clientAddress;
                    int clientPort = clientRequest.clientPort;
//                    try {
//                        ip = InetAddress.getByName("127.0.0.1");
//                    } catch (Exception ex) {
//                        System.out.println(ex.getMessage());
//                    }
                    response = translator.addObserver((RegisterInterestRequest) request, observerManager, facilTable, clientAddress, clientPort);
                    break;
                case "commons.requests.ExtendBookingRequest":
                    System.out.println("Extend Booking Request Received, calling Translator Function...");
                    Response extendResponse = translator.extendBooking((ExtendBookingRequest) request, bookingManager, facilTable);
                    if (extendResponse instanceof  ExtendBookingResponse){
                        facilName = ((ExtendBookingRequest) request).facilityName;
                        e = translator.notifyObservers(facilName, bookingManager, observerManager, facilTable);
                    }
                    response = extendResponse;
                    break;
                case "commons.requests.TestRequest":
                    System.out.println("Test Request Received, calling Translator Function...");
                    this.testCounter++;
                    response = new TestResponse();
                    break;
                default:
                    System.out.println("Invalid Request Received");
                    throw new RuntimeException("Invalid Request Type");
            }
            clientRequest.setSentResponse(response);
            System.out.println(response.getClass().getName());
            serverCommunicator.send(response, clientRequest.clientAddress, clientRequest.clientPort);
        }
    }


    public int getTestCounter() {
        return testCounter;
    }

    public void setTestCounter(int testCounter) {
        this.testCounter = testCounter;
    }
}

