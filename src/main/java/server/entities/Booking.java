package server.entities;

import commons.utils.Day;

import java.time.*;
import java.util.Comparator;
//import java.util.Date;


public class Booking {
    private int bookingId;
    private int clientId; //TODO check if client it is int or string
    private String facilityName;
    private Day day;
    private LocalTime start;
    private LocalTime end;


    public Booking(Day day, int bookingId, String facilityName, LocalTime start, LocalTime end) {
        this.day = day;
        this.bookingId = bookingId;
        this.facilityName = facilityName;
        this.start = start;
        this.end = end;
    }

    /**
     * Comparator to sort bookings
     */
    public static Comparator<Booking> BookingComparator = new Comparator<Booking>(){
        public int compare(Booking b1, Booking b2){
            LocalTime s1 = b1.getStart();
            LocalTime s2 = b2.getStart();

            return s1.compareTo(s2);
        }
    };

    public Day getDay() {
        return day;
    }

    public int getBookingId(){
        return bookingId;
    }

    public int getClientId() {
        return clientId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Booking from " + this.start + " to " + this.end;
    }
}


