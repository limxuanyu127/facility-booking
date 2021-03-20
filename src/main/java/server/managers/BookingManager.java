package server.managers;

import server.entities.*;

import java.util.*;
import java.time.*;
import javafx.util.Pair;

public class BookingManager {

    private static LocalTime openTime = LocalTime.of(8,0,0);
    private static LocalTime closeTime = LocalTime.of(22, 0,0);

    public static LocalTime getOpenTime() {
        return openTime;
    }

    public static LocalTime getCloseTime() {
        return closeTime;
    }

    // Use default constructor
    public Pair<ArrayList, Exception> queryAvailability(String facilName, ArrayList<LocalDate> dates, Hashtable facilTable){

        ArrayList results = new ArrayList<>();
        Facility currFacil = (Facility) facilTable.get(facilName);
        facilName = facilName.toLowerCase();

        //Checks
        if (!facilTable.containsKey(facilName)){
            Exception e = new NoSuchElementException("Facility does not exist"); //TODO review this exception
            return new Pair<ArrayList, Exception>(results, e);
        }

        for (int j=0; j < dates.size();j++){
            LocalDate currDate = dates.get(j);
            ArrayList<Booking> bookingList = currFacil.getBookings(currDate);

            this.sortBookings(bookingList);
            LocalDateTime availStart = openTime.atDate(currDate);
            LocalDateTime availEnd = closeTime.atDate(currDate);
            ArrayList<LocalDateTime> slot;

            for(int k=0; k < bookingList.size();k++){
                Booking currBooking = bookingList.get(k);
                availEnd = currBooking.getStart();

                Boolean check1 = availEnd.toLocalTime().compareTo(openTime) == 0;
                Boolean check2 = availEnd.compareTo(availStart) ==0;
                if(check1 || check2 ){
                    availStart = currBooking.getEnd();
                    continue;
                }

                slot = new ArrayList<LocalDateTime>();
                slot.add(availStart);
                slot.add(availEnd);
                results.add(slot);

                availStart = currBooking.getEnd();
            }

            // if the last booking ends at closing time
            if(availStart.toLocalTime().compareTo(closeTime) == 0){
                return new Pair<ArrayList, Exception>( results, null);
            }
            slot = new ArrayList<LocalDateTime>();
            slot.add(availStart);
            slot.add(closeTime.atDate(currDate));
            results.add(slot);
        }

        return new Pair<ArrayList, Exception>( results, null);
    }

    public void sortBookings(ArrayList<Booking> bookingsList){
        Collections.sort(bookingsList, Booking.BookingComparator);
    }

    public Pair<Booking, Exception> createBooking(int bookingId, int clientId, String facilName, LocalDateTime newStart, LocalDateTime newEnd, Hashtable facilTable){
        //TODO implement exception throw when booking is before open time/ after closing time
        facilName = facilName.toLowerCase();

        if (!facilTable.containsKey(facilName)){
            Exception e = new NoSuchElementException("Facility does not exist"); //TODO review this exception
            return new Pair<Booking, Exception>(null, e);
        }
        if (newStart.compareTo(newEnd) >= 0){
            Exception e = new NoSuchElementException("Invalid start and end time"); //TODO review this exception
            return new Pair<Booking, Exception>(null, e);
        }

        Boolean isAvailable = this.isAvailable(facilName, newStart, newEnd, facilTable);

        if (!isAvailable){
            Exception e = new NoSuchElementException("There is a booking at that time slot"); //TODO review this exception
            return new Pair<Booking, Exception>(null, e);
        }

        //Booking procedure
        Booking b = new Booking(bookingId, clientId, facilName, newStart, newEnd);
        Facility f = (Facility) facilTable.get(facilName);
        f.addBooking(b);
        return new Pair<Booking, Exception>(b, null);
    }

    // Helper Func
    private Boolean isAvailable(String facilName, LocalDateTime newStart, LocalDateTime newEnd, Hashtable facilTable){
        //Check if timeslot is available
        ArrayList<LocalDate> queryDates = new ArrayList<>();
        queryDates.add(newStart.toLocalDate());

        Pair<ArrayList, Exception> queryResults = this.queryAvailability(facilName, queryDates, facilTable);
        ArrayList availStartEnd = queryResults.getKey();

        boolean isAvailable = false;
        for (int i =0; i< availStartEnd.size(); i++){
            ArrayList startEnd = (ArrayList) availStartEnd.get(i);
            LocalDateTime start = (LocalDateTime) startEnd.get(0);
            LocalDateTime end = (LocalDateTime) startEnd.get(1);

            Boolean check1 = (newStart.compareTo(start)>=0) && (newEnd.compareTo(end)<=0);
            if (check1){
                isAvailable = true;
            }
        }
        return isAvailable;
    }
//
//    public Pair<Booking, Exception> offsetBooking(int bookingId, int offset){}
//
//    public Pair<Booking, Exception> updateBooking(int bookingId, LocalDateTime startTime, LocalDateTime endTime){}
//
//    public Exception deleteBooking(int bookingId){}
}
