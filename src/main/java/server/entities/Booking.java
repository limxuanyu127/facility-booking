package server.entities;

import java.time.*;
import java.util.Comparator;
//import java.util.Date;


public class Booking {
    private int bookingId;
    private int clientId; //TODO check if client it is int or string
    private String facilityName;
    private LocalDateTime start;
    private LocalDateTime end;


    public Booking(int bookingId, int clientId, String facilityName, LocalDateTime start, LocalDateTime end) {
        this.bookingId = bookingId;
        this.clientId = clientId;
        this.facilityName = facilityName;
        this.start = start;
        this.end = end;
    }

    public static Comparator<Booking> BookingComparator = new Comparator<Booking>(){
        public int compare(Booking b1, Booking b2){
            LocalDateTime s1 = b1.getStart();
            LocalDateTime s2 = b2.getStart();

            return s1.compareTo(s2);
        }
    };


    public int getBookingId(){
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}


