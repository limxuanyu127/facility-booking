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

        if((startTime.compareTo(BookingManager.getOpenTime()) <0) || endTime.compareTo(BookingManager.getCloseTime()) > 0){
            System.out.println("Booking invalid: start time is too early, or end time is too late");
            return;
        }

        if (this.bookingsTable.containsKey(day)){
            this.bookingsTable.get(day).add(booking);
        }
        else{
            ArrayList<Booking> newList = new ArrayList<>();
            newList.add(booking);
            this.bookingsTable.put(day, newList);
        }
    }

    public Exception removeBooking(Booking booking){
        String bookingDay = booking.getDay();
        ArrayList bookingsList = this.bookingsTable.get(bookingDay);
        for (int i =0; i< bookingsList.size(); i++){
            Booking b = (Booking) bookingsList.get(i);
            if (b.getBookingId() == booking.getBookingId()){
                bookingsList.remove(b);
                return null;
            }
        }
        Exception e = new NoSuchElementException("Unable to find booking to remove");
        return e;
    }



    public ArrayList<FacilityObserver> getObserverList() {
        return observerList;
    }

    public void attach(FacilityObserver o){
        observerList.add(o);
    }

    public void notifyObservers(){
        //TODO
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

}
