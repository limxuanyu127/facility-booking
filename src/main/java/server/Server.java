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
                    BookFacilityResponse createResponse = translator.createBooking((BookFacilityRequest)request, bookingIdCounter, 0, bookingManager, facilTable);
                    bookingIdCounter +=1;

                    facilName = createResponse.facilityName;
                    e = translator.notifyObservers(facilName, bookingManager, observerManager, facilTable);
                    response = (Response) createResponse;
                    break;
                case "commons.requests.DeleteBookingRequest":
                    System.out.println("Delete Booking Received, calling Translator Function...");
                    DeleteBookingResponse deleteResponse = translator.deleteBooking((DeleteBookingRequest) request, bookingManager, facilTable);

                    facilName = deleteResponse.facilityName;
                    e = translator.notifyObservers(facilName, bookingManager, observerManager, facilTable);
                    response = (Response) deleteResponse;
                    break;
                case "commons.requests.OffsetBookingRequest":
                    System.out.println("Offset Booking Request Received, calling Translator Function...");
                    OffsetBookingResponse offsetResponse = translator.offsetBooking((OffsetBookingRequest) request, bookingManager, facilTable);

                    facilName = offsetResponse.facilityName;
                    e = translator.notifyObservers(facilName, bookingManager, observerManager, facilTable);
                    response = (Response) offsetResponse;
                    break;
                case "commons.requests.QueryAvailabilityRequest":
                    System.out.println("Query Availability Request Received, calling Translator Function...");
                    QueryAvailabilityResponse queryResponse = translator.queryAvailability((QueryAvailabilityRequest) request, bookingManager, facilTable);

                    response = (Response) queryResponse;
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
                    RegisterInterestResponse registerResponse = translator.addObserver((RegisterInterestRequest) request, observerManager, facilTable, clientAddress, clientPort);

                    response = (Response) registerResponse;
                    break;
                case "commons.requests.ExtendBookingRequest":
                    System.out.println("Extend Booking Request Received, calling Translator Function...");
                    ExtendBookingResponse extendResponse = translator.extendBooking((ExtendBookingRequest) request, bookingManager, facilTable);

                    facilName = extendResponse.facilityName;
                    e = translator.notifyObservers(facilName, bookingManager, observerManager, facilTable);
                    response = (Response) extendResponse;
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

