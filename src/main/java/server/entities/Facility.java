package server.entities;

import server.managers.*;

import java.time.*;
import java.util.*;

public class Facility {
    private ArrayList<FacilityObserver> observerList;
    private String name;
//    private ArrayList<Booking> bookingsList = new ArrayList<Booking>();
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

    public void removeBooking(Booking booking){
        LocalDate bookingDate =booking.getStart().toLocalDate();
        ArrayList bookingsList = this.bookingsTable.get(bookingDate);
        for (int i =0; i< bookingsList.size(); i++){
            Booking b = (Booking) bookingsList.get(i);
            if (b.getBookingId() == booking.getBookingId()){
                bookingsList.remove(b);
                return;
            }
        }
        System.out.println("Booking to remove is not found");
    }



    public ArrayList<FacilityObserver> getObserverList() {
        return observerList;
    }

    public void attach(FacilityObserver o){
        //TODO
    }

    public void notifyObservers(){
        //TODO
    }

    public ArrayList<Booking> getBookings (LocalDate date){
        return this.bookingsTable.get(date);
    }


    public void setObserverList(ArrayList<FacilityObserver> observerList) {
        this.observerList = observerList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
