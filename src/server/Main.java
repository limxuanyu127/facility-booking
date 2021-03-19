package server;

//import commons.rpc.Communicator;
import javafx.util.Pair;
import server.entities.*;
import server.managers.*;

import java.io.IOException;
import java.net.*;
import java.time.*;
import java.time.Month;
import java.util.ArrayList;
import java.util.Hashtable;

public class Main {
    public static void main(String[] args) {

        //Dictionary is abstract class
        Hashtable<String, Facility> facilityList = new Hashtable<>();
        BookingManager bookingManager = new BookingManager();

        //Populate facilityList
        Facility badmintonCourt = new Facility("badmintonCourt");
        Facility gym = new Facility("gym");
        //FIXME Have to ensure that facil name is lower case
        facilityList.put("badmintoncourt", badmintonCourt);
        facilityList.put("gym", gym);

        //Populate booking
        LocalDateTime startOne = LocalDateTime.of(2021, 3, 18, 14, 10);
        LocalDateTime endOne = LocalDateTime.of(2021, 3, 18, 16, 50);
        Booking bookingOne = new Booking(1, 001, "badmintoncourt", startOne, endOne);

        LocalDateTime startTwo = LocalDateTime.of(2021, 3, 18, 20, 00);
        LocalDateTime endTwo = LocalDateTime.of(2021, 3, 18, 22, 00);
        Booking bookingTwo = new Booking(1, 001, "badmintoncourt", startTwo, endTwo);

        Facility targetFacil = facilityList.get("badmintoncourt");
        targetFacil.addBooking(bookingOne);
        targetFacil.addBooking(bookingTwo);

        // 1. Test queryAvailability
//        ArrayList<LocalDate> queryDates = new ArrayList<LocalDate>();
//        queryDates.add(LocalDate.of(2021, 3, 18));
//        //FIXME Have to ensure that facil name is lower case
//        Pair<ArrayList, Exception> queryResults = bookingManager.queryAvailability("badmintoncourt", queryDates, facilityList);
//        System.out.println(queryResults);

        //2. Test createbooking
        int bookingId = 1;
        int clientId = 100;
        String facilName = "badmintoncourt";
        Facility facil = facilityList.get(facilName);

        LocalDateTime startThree = LocalDateTime.of(2021, 3, 18, 18, 00);
        LocalDateTime endThree = LocalDateTime.of(2021, 3, 18, 20, 00);
        ArrayList<LocalDate> queryDates = new ArrayList<LocalDate>();
        queryDates.add(startThree.toLocalDate());
        System.out.println(facil.getBookings(startThree.toLocalDate()));
        System.out.printf("Before createbooking(): %s%n", facil.getBookings(startThree.toLocalDate()).toString());
        Pair<ArrayList, Exception> queryResults = bookingManager.queryAvailability(facilName, queryDates, facilityList);
        System.out.println(queryResults);
        Pair<Booking, Exception> createBookingResults = bookingManager.createBooking(bookingId, clientId, facilName, startThree, endThree, facilityList);
//        System.out.printf("Exception in booking: %s %n", createBookingResults.getValue().toString());
        System.out.printf("After createbooking(): %s%n", facil.getBookings(startThree.toLocalDate()).toString());
        queryResults = bookingManager.queryAvailability(facilName, queryDates, facilityList);
        System.out.println(queryResults);

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
//            System.out.println("server listening");
//            sock.receive(message);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
