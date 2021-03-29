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


    public Pair<Hashtable, Exception> queryAvailability(String facilName, ArrayList<String> dates, Hashtable facilTable){

        Exception e;
        Hashtable allResults = new Hashtable();
        Facility currFacil = (Facility) facilTable.get(facilName);
        facilName = facilName.toLowerCase();

        //Checks
        e= doValidFacilCheck(facilName, facilTable);
        if (e!=null){
            return new Pair<Hashtable, Exception>(null, e);
        }

        for (int j=0; j < dates.size();j++){
            String currDay = dates.get(j);
            ArrayList dayResults = new ArrayList<>();

            if(!currFacil.getBookingsTable().containsKey(currDay)){

                ArrayList slot = new ArrayList();
                slot.add(openTime);
                slot.add(closeTime);
                dayResults.add(slot);
                allResults.put(currDay, dayResults);
                continue;
            }

            ArrayList<Booking> bookingList = currFacil.getBookingsByDay(currDay);

            this.sortBookings(bookingList);
            LocalTime availStart = openTime;
            LocalTime availEnd = closeTime;
            ArrayList<LocalTime> slot;

            for(int k=0; k < bookingList.size();k++){
                Booking currBooking = bookingList.get(k);
                availEnd = currBooking.getStart();

                Boolean check1 = availEnd.compareTo(openTime) == 0;
                Boolean check2 = availEnd.compareTo(availStart) ==0;
                if(check1 || check2 ){
                    availStart = currBooking.getEnd();
                    continue;
                }

                slot = new ArrayList<LocalTime>();
                slot.add(availStart);
                slot.add(availEnd);
                dayResults.add(slot);

                availStart = currBooking.getEnd();
            }

            // if the last booking ends at closing time
            if(availStart.compareTo(closeTime) == 0){
                allResults.put(currDay, dayResults);
                continue;
            }
            else{
                slot = new ArrayList<LocalTime>();
                slot.add(availStart);
                slot.add(closeTime);
                dayResults.add(slot);
                allResults.put(currDay, dayResults);
            }
        }
        return new Pair<Hashtable, Exception>(allResults, null);
    }


    public Pair<Booking, Exception> createBooking(String day, int bookingId, int clientId, String facilName, LocalTime newStart, LocalTime newEnd, Hashtable facilTable){

        Exception e;

        e = doBookingCheck(facilName,newStart,newEnd,facilTable);
        if (e != null){
            return new Pair<Booking, Exception>(null, e);
        }
        e = doAvailabilityCheck(day, facilName,newStart,newEnd,facilTable);
        if (e != null){
            return new Pair<Booking, Exception>(null, e);
        }

        //Booking procedure
        Booking b = new Booking(day, bookingId, facilName, newStart, newEnd);
        Facility f = (Facility) facilTable.get(facilName);
        e = doValidFacilCheck(facilName, facilTable);
        if(e != null){
            return new Pair<Booking, Exception>(null, e);
        }

        try{
            f.addBooking(b);
        }catch (Exception unexpectedE){
            return new Pair<>(null,unexpectedE);
        }

        return new Pair<Booking, Exception>(b, null);
    }

    public Pair<Booking, Exception> offsetBooking(String facilName, int bookingId, int offset, Hashtable facilTable){
        //FIXME offset should be in minutes
        Exception e;
        Facility f = (Facility) facilTable.get(facilName);
        e = doValidFacilCheck(facilName, facilTable);
        if(e != null){
            return new Pair<Booking, Exception>(null, e);
        }

        Booking b = this.findFacilBookingById(facilName, bookingId, facilTable);
        e = doBookingNotNullCheck(b);
        if(e != null){
            return new Pair<Booking, Exception>(null, e);
        }

        String day = b.getDay();
        LocalTime newStart = b.getStart().plusMinutes(offset);
        LocalTime newEnd = b.getEnd().plusMinutes(offset);
        e = doBookingCheck(facilName,newStart,newEnd,facilTable);
        if (e != null){
            return new Pair<Booking, Exception>(null, e);
        }
        e = doAvailabilityCheckExceptCurrent(day, bookingId, facilName,newStart,newEnd,facilTable);
        if (e != null){
            return new Pair<Booking, Exception>(null, e);
        }

        f.offsetBooking(b, newStart, newEnd);

        return new Pair<Booking, Exception>(b, null);

    }
    public Pair<Booking, Exception> extendBooking(String facilName, int bookingId, int offset, Hashtable facilTable){
        //FIXME offset should be in minutes
        Exception e;
        Facility f = (Facility) facilTable.get(facilName);
        e = doValidFacilCheck(facilName, facilTable);
        if(e != null){
            return new Pair<Booking, Exception>(null, e);
        }

        Booking b = findFacilBookingById(facilName, bookingId, facilTable);
        e = doBookingNotNullCheck(b);
        if(e != null){
            return new Pair<Booking, Exception>(null, e);
        }

        LocalTime bStart = b.getStart();
        LocalTime bEnd = b.getEnd();
        LocalTime newEnd = bEnd.plusMinutes(offset);
        String day = b.getDay();
        e = doBookingCheck(facilName,bStart,newEnd,facilTable);
        if (e != null){
            return new Pair<Booking, Exception>(null, e);
        }
        e = doAvailabilityCheckExceptCurrent(day, bookingId, facilName,bStart,newEnd,facilTable);
        if (e != null){
            return new Pair<Booking, Exception>(null, e);
        }

        f.extendBooking(b, newEnd);
        return new Pair<Booking, Exception>(b, null);
    }


    public Exception deleteBooking(String facilName, int bookingId, Hashtable facilTable){

        Exception e;

        Facility facil =(Facility) facilTable.get(facilName);
        e = doValidFacilCheck(facilName, facilTable);
        if(e != null){
            return e;
        }

        Booking b = findFacilBookingById(facilName, bookingId, facilTable);

        e = doBookingNotNullCheck(b);
        if(e != null){
            return e;
        }

        try {
            facil.removeBooking(b);
        }
        catch (Exception unexpectedE){
            return unexpectedE;
        }
        return e;
    }

/*------------------------------------- Helper Functions ----------------------------------------------------------------------*/
    public void sortBookings(ArrayList<Booking> bookingsList){
        Collections.sort(bookingsList, Booking.BookingComparator);
    }

    private Booking findFacilBookingById(String facilName, int bookingId, Hashtable facilTable){
        Facility facil = (Facility) facilTable.get(facilName);
        Booking b = facil.getBookingById(bookingId);
        return b;
    }

    private Exception doValidFacilCheck(String facilName, Hashtable facilTable){
        if (!facilTable.containsKey(facilName)){
            Exception e = new NoSuchElementException("Facility does not exist");
            return e;
        }
        else{
            return null;
        }
    }


    private Exception doBookingNotNullCheck(Booking b){
        if(b == null){
            Exception e = new NoSuchElementException("Booking does not exist");
            return e;
        }
        else{
            return null;
        }

    }

    private Exception doAvailabilityCheck(String day, String facilName, LocalTime newStart, LocalTime newEnd, Hashtable facilTable){
        //Check if timeslot is available
        ArrayList<String> queryDates = new ArrayList<>();
        queryDates.add(day);


        Pair<Hashtable, Exception> queryResults = this.queryAvailability(facilName, queryDates, facilTable);
        Hashtable queryTable = queryResults.getKey();
        ArrayList availStartEnd = (ArrayList) queryTable.get(day);

        boolean isAvailable = false;
        for (int i =0; i< availStartEnd.size(); i++){
            ArrayList startEnd = (ArrayList) availStartEnd.get(i);
            LocalTime start = (LocalTime) startEnd.get(0);
            LocalTime end = (LocalTime) startEnd.get(1);

            Boolean check1 = (newStart.compareTo(start)>=0) && (newEnd.compareTo(end)<=0);
            if (check1){
                isAvailable = true;
            }
        }

        if(isAvailable){
            return null;
        }
        else{
            Exception e = new NoSuchElementException("Timeslot is not available"); //TODO review this exception
            return e;
        }
    }

    // doAvailabilityCheckExceptCurrent() checks if the timeslots of a booking is valid
    private Exception doAvailabilityCheckExceptCurrent(String day, int bookingId, String facilName, LocalTime newStart, LocalTime newEnd, Hashtable facilTable){


        Facility facil =(Facility) facilTable.get(facilName);
        ArrayList<Booking> bookingsList = facil.getBookingsByDay(day);

        for (Booking b:bookingsList){
            LocalTime bStart = b.getStart();
            LocalTime bEnd = b.getEnd();

            Boolean conditionOne = (newStart.compareTo(bStart) <0) &&  (newEnd.compareTo(bStart)<=0);
            Boolean conditionTwo = (newStart.compareTo(bEnd) >=0) &&  (newEnd.compareTo(bEnd)>0);
            Boolean conditionThree = b.getBookingId() == bookingId;
            if (conditionOne || conditionTwo){
                continue;
            }
            else{
                if (conditionThree){
                    continue;
                }
                else{
                    Exception e = new NoSuchElementException("Timeslot is not available"); //TODO review this exception
                    return e;
                }
            }
        }
        return null;
    }

    // doBookingCheck() checks if a booking is valid, independent of other bookings
    private Exception doBookingCheck(String facilName, LocalTime newStart, LocalTime newEnd, Hashtable facilTable){
//        if (!isValidFacil(facilName, facilTable)){
//            Exception e = new NoSuchElementException("Facility does not exist"); //TODO review this exception
//            return e;
//        }
        if (!isStartBeforeEnd(newStart, newEnd)){
            Exception e = new NoSuchElementException("Start time must be before end time"); //TODO review this exception
            return e;
        }

        if (!this.isValidStartEndTime(newStart, newEnd)){
            Exception e = new NoSuchElementException(String.format("Start time is before %s or End time is after %s", this.openTime.toString(), this.closeTime.toString())); //TODO review this exception
            return e;
        }

        return null;
    }


    private Boolean isStartBeforeEnd(LocalTime newStart, LocalTime newEnd){
        if(newStart.compareTo(newEnd) < 0){
            return true;
        }
        else{
            return false;
        }
    }

//    private Boolean isValidFacil(String facilName, Hashtable facilTable){
//        if (facilTable.containsKey(facilName)){
//            return true;
//        }
//        else{
//            return false;
//        }
//    }

    private Boolean isValidStartEndTime(LocalTime newStart, LocalTime newEnd){
        Boolean conditionOne = newStart.compareTo(this.openTime) <0;
        Boolean conditionTwo = newEnd.compareTo(this.closeTime)>0;
        if (conditionOne || conditionTwo){
            return false;
        }
        else{
            return true;
        }
    }

}


//    public Pair<Booking, Exception> updateBooking(String facilName, int bookingId, LocalTime newStart, LocalTime newEnd, Hashtable facilTable){
//
//        Exception e;
//        Booking b = findFacilBooking(facilName, bookingId, facilTable);
//        String day = b.getDay();
//        int clientId = b.getClientId();
//        Facility facil =(Facility) facilTable.get(facilName);
//
//        e = doBookingNotNullCheck(b);
//        if(e != null){
//            return new Pair<Booking, Exception>(null, e);
//        }
//        e = doBookingCheck(facilName,newStart,newEnd,facilTable);
//        if (e != null){
//            return new Pair<Booking, Exception>(null, e);
//        }
//        e = doAvailabilityCheckExceptCurrent(bookingId, facilName,newStart,newEnd,facilTable);
//        if (e != null){
//            return new Pair<Booking, Exception>(null, e);
//        }
//
//        e = facil.removeBooking(b);
//        if (e!= null){
//            return new Pair<Booking, Exception>(null, e);
//        }else{
//            Booking newBooking = new Booking(bookingId, clientId, facilName, newStart, newEnd);
//            facil.addBooking(newBooking);
//            return new Pair<Booking, Exception>(newBooking, null);
//        }
//    }



//    public ArrayList localToDatetime(ArrayList results, String day){
//        ArrayList convertedResults = new ArrayList();
//        for (ArrayList slot:(ArrayList<ArrayList>)results){
//            ArrayList newSlot = new ArrayList();
//            LocalTime start = (LocalTime) newSlot.get(0);
//            LocalTime end = (LocalTime) newSlot.get(1);
//
//            Datetime newStart = new Datetime(day, start.getHour(), start.getMinute());
//            Datetime newEnd = new Datetime(day, end.getHour(), end.getMinute());
//
//            newSlot.add(newStart);
//            newSlot.add(newEnd);
//
//            convertedResults.add(newSlot);
//        }
//
//        return convertedResults;
//    }
