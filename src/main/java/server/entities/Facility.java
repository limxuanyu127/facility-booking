package server.entities;

import server.managers.*;

import java.time.*;
import java.util.*;

public class Facility {
    private ArrayList<FacilityObserver> observerList = new ArrayList<FacilityObserver>();
    private String name;
    private Hashtable<LocalDate, ArrayList<Booking>> bookingsTable = new Hashtable<LocalDate, ArrayList<Booking>>();


    public Facility(String name){
        this.name = name;
    }

    public void addBooking(Booking booking){

        //Check
        LocalTime startTime = booking.getStart().toLocalTime();
        LocalTime endTime = booking.getEnd().toLocalTime();
        LocalDate startDate =booking.getStart().toLocalDate();
        LocalDate endDate = booking.getEnd().toLocalDate();
        //TODO should thre be a
        if((startTime.compareTo(BookingManager.getOpenTime()) <0) || endTime.compareTo(BookingManager.getCloseTime()) > 0){
            System.out.println("Booking invalid: start time is too early, or end time is too late");
            return;
        }
        if (!startDate.toString().equals(endDate.toString()) ){

            System.out.println("Booking invalid:start date and end date are different");
            return;
        }

        LocalDate bookingDate =booking.getStart().toLocalDate();

        if (this.bookingsTable.containsKey(bookingDate)){
            this.bookingsTable.get(bookingDate).add(booking);
        }
        else{
            ArrayList<Booking> newList = new ArrayList<>();
            newList.add(booking);
            this.bookingsTable.put(bookingDate, newList);
        }
    }

    public Exception removeBooking(Booking booking){
        LocalDate bookingDate = booking.getStart().toLocalDate();
        ArrayList bookingsList = this.bookingsTable.get(bookingDate);
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

    public ArrayList<Booking> getBookingsByDate(LocalDate date){
        return this.bookingsTable.get(date);
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


    public String getName() {
        return name;
    }


}
