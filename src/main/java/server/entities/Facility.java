package server.entities;

import server.managers.*;

import java.time.*;
import java.util.*;

public class Facility {
    private ArrayList<FacilityObserver> observerList = new ArrayList<FacilityObserver>();
    private String name;
    private Hashtable<String, ArrayList<Booking>> bookingsTable = new Hashtable<String, ArrayList<Booking>>();


    public Facility(String name){
        this.name = name;
    }

    public void addBooking(Booking booking){

        //Check
        LocalTime startTime = booking.getStart();
        LocalTime endTime = booking.getEnd();
        String day =booking.getDay();

        // All timing related checks are done in booking manager

        if (this.bookingsTable.containsKey(day)){
            this.bookingsTable.get(day).add(booking);
        }
        else{
            ArrayList<Booking> newList = new ArrayList<>();
            newList.add(booking);
            this.bookingsTable.put(day, newList);
        }

    }

    public void removeBooking(Booking booking){
        String bookingDay = booking.getDay();
        ArrayList bookingsList = this.bookingsTable.get(bookingDay);
        for (int i =0; i< bookingsList.size(); i++){
            Booking b = (Booking) bookingsList.get(i);
            if (b.getBookingId() == booking.getBookingId()){
                bookingsList.remove(b);
//                return null;
            }
        }
//        Exception e = new NoSuchElementException("Unable to find booking to remove");
//        return e;
    }

    public void offsetBooking(Booking booking, LocalTime newStart, LocalTime newEnd){
        String bookingDay = booking.getDay();
        ArrayList bookingsList = this.bookingsTable.get(bookingDay);
        for (int i =0; i< bookingsList.size(); i++){
            Booking b = (Booking) bookingsList.get(i);
            if (b.getBookingId() == booking.getBookingId()){
                b.setStart(newStart);
                b.setEnd(newEnd);
            }
        }
    }

    public void extendBooking(Booking booking, LocalTime newEnd){
        String bookingDay = booking.getDay();
        ArrayList bookingsList = this.bookingsTable.get(bookingDay);
        for (int i =0; i< bookingsList.size(); i++){
            Booking b = (Booking) bookingsList.get(i);
            if (b.getBookingId() == booking.getBookingId()){
                b.setEnd(newEnd);
            }
        }
    }

    public void addObserver(FacilityObserver o){
        observerList.add(o);
    }

    public ArrayList<FacilityObserver> getUpdatedObservers(){

        ArrayList<FacilityObserver> observersToRemove = new ArrayList<>();

        for (FacilityObserver o: observerList){
            if (!isValidObserver(o)){
                observersToRemove.add(o);
            }
        }

        for (FacilityObserver o: observersToRemove){
            observerList.remove(o);
        }

        return observerList;
    }

    private Boolean isValidObserver(FacilityObserver o){

        if (o.getEndDate().compareTo(LocalDateTime.now()) <0 ){
            return false;
        }
        else{
            return true;
        }

    }


    public ArrayList<Booking> getBookingsByDay(String day){
        return this.bookingsTable.get(day);
    }

    public Booking getBookingById (int bookingId){
        Enumeration<ArrayList<Booking>> allbookings = this.bookingsTable.elements();

        //iterate the values
        while(allbookings.hasMoreElements() ){
            ArrayList<Booking> bookingsList = allbookings.nextElement();
            for (Booking b:bookingsList){
                if (b.getBookingId() == bookingId){
                    return b;
                }
            }
        }
        return null;
    }

    public Hashtable getBookingsTable(){
        return this.bookingsTable;
    }

}
