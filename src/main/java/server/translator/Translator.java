package server.translator;

import commons.requests.BookFacilityRequest;
import commons.requests.*;
import commons.responses.*;
import commons.rpc.ServerCommunicator;
import commons.utils.Datetime;
import commons.utils.Day;
import commons.utils.ResponseMessage;
import javafx.util.Pair;
import server.entities.Booking;
import server.entities.FacilityObserver;
import server.managers.BookingManager;
import server.managers.ObserverManager;

import java.net.InetAddress;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class Translator {
    private ArrayList<String> allDays = new ArrayList<String>();
    private ServerCommunicator serverCommunicator;

    public Translator(ServerCommunicator serverCommunicator){
        List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        allDays.addAll(days);

        this.serverCommunicator = serverCommunicator;
    }

    public Response queryAvailability(QueryAvailabilityRequest r, BookingManager bookingManager, Hashtable facilTable) {

        Response response;
        ResponseMessage responseMessage;

        String facilName = r.facilityName;
        ArrayList days = (ArrayList) r.days;

        Pair<Hashtable, Exception> outputPair = bookingManager.queryAvailability(facilName, days, facilTable);
        Hashtable outputBooking = outputPair.getKey();
        Exception outputException = outputPair.getValue();

        if (outputException != null){
            responseMessage = new ResponseMessage(400, outputException.getMessage());
            response = new ErrorResponse(responseMessage);
        }
        else{
            responseMessage = new ResponseMessage(200, "success") ;
            ArrayList convertedList =localToDatetime(outputBooking);
            response = new QueryAvailabilityResponse(facilName, convertedList, responseMessage);
        }

        return response;

    }

    public Response createBooking(BookFacilityRequest r, int bookingId, int clientId, BookingManager bookingManager, Hashtable facilTable){

        Response response;
        ResponseMessage responseMessage;

        Datetime requestStart = r.startTime;
        Datetime requestEnd = r.endTime;

        LocalTime start = LocalTime.of(requestStart.hour, requestStart.minute, 00);
        LocalTime end = LocalTime.of(requestEnd.hour, requestEnd.minute, 00);
        Day day = requestStart.day;
        String facilName = r.facilityName;

        Pair<Booking,Exception> outputPair = bookingManager.createBooking(day, bookingId, clientId, facilName, start, end, facilTable);
        Booking outputBooking = outputPair.getKey();
        Exception outputException = outputPair.getValue();

        if (outputException != null){
            responseMessage = new ResponseMessage(400, outputException.getMessage()) ;
            response = new ErrorResponse(responseMessage);
        }
        else{
            responseMessage = new ResponseMessage(200, "success") ;
            Datetime dtStart = localToDatetime(outputBooking.getStart(), day);
            Datetime dtEnd = localToDatetime(outputBooking.getEnd(), day);
            response = new BookFacilityResponse(outputBooking.getBookingId(), facilName, dtStart, dtEnd, responseMessage);
        }
        return response;
    }

    public Response offsetBooking(OffsetBookingRequest r, BookingManager bookingManager,Hashtable facilTable ){

        Response response;
        ResponseMessage responseMessage;

        String facilName = r.facilityName;
        int bookingId = r.bookingID;
        int offset = r.offset;
        int offsetMin = offset*30;

        Pair<Booking, Exception> outputPair = bookingManager.offsetBooking(facilName, bookingId, offsetMin, facilTable);
        Booking outputBooking = outputPair.getKey();
        Exception outputException = outputPair.getValue();

        if (outputException != null){
            responseMessage = new ResponseMessage(400, outputException.getMessage()) ;
            response = new ErrorResponse(responseMessage);

        }
        else{
            responseMessage = new ResponseMessage(200, "success") ;
            Day day = outputBooking.getDay();
            Datetime dtStart = localToDatetime(outputBooking.getStart(),day);
            Datetime dtEnd = localToDatetime(outputBooking.getEnd(), day);
            response = new OffsetBookingResponse(bookingId, facilName, dtStart, dtEnd, responseMessage);
        }

        return response;
    }

    public Response extendBooking(ExtendBookingRequest r, BookingManager bookingManager,Hashtable facilTable){
        Response response;
        ResponseMessage responseMessage;

        String facilName = r.facilityName;
        int bookingId = r.bookingID;
        int offset = r.extension;
        int offsetMin = offset*30;

        Pair<Booking, Exception> outputPair = bookingManager.extendBooking(facilName, bookingId, offsetMin, facilTable);
        Booking outputBooking = outputPair.getKey();
        Exception outputException = outputPair.getValue();

        if (outputException != null){
            responseMessage = new ResponseMessage(400, outputException.getMessage()) ;
            response = new ErrorResponse(responseMessage);

        }
        else{
            responseMessage = new ResponseMessage(200, "success") ;
            Day day = outputBooking.getDay();
            Datetime dtStart = localToDatetime(outputBooking.getStart(),day);
            Datetime dtEnd = localToDatetime(outputBooking.getEnd(), day);
            response = new ExtendBookingResponse(bookingId, facilName, dtStart, dtEnd, responseMessage);
        }

        return response;
    }

    public Response deleteBooking(DeleteBookingRequest r, BookingManager bookingManager, Hashtable facilTable){
        Response response;
        ResponseMessage responseMessage;

        String facilName = r.facilityName;
        int bookingId = r.bookingID;

        Exception outputException = bookingManager.deleteBooking(facilName, bookingId, facilTable);
        if (outputException != null){
            responseMessage = new ResponseMessage(400, outputException.getMessage()) ;
            response = new ErrorResponse(responseMessage);

        }
        else{
            responseMessage = new ResponseMessage(200, "success") ;
            response = new DeleteBookingResponse(responseMessage, facilName);
        }
        return response;
    }

    public Response addObserver(RegisterInterestRequest r, ObserverManager observerManager, Hashtable facilTable, InetAddress ip, int port){
        Response response;
        ResponseMessage responseMessage;

        String facilName = r.facilityName;
        int numDays = r.numDays;

        Exception outputException = observerManager.addObserver(facilName, numDays, facilTable, ip, port);

        if (outputException != null){
            responseMessage = new ResponseMessage(400, outputException.getMessage()) ;
            response = new ErrorResponse(responseMessage);

        }
        else{
            responseMessage = new ResponseMessage(200, "success") ;
            response = new RegisterInterestResponse(responseMessage);
        }
        return response;
    }

    public Exception notifyObservers(String facilName,BookingManager bookingManager, ObserverManager observerManager, Hashtable facilTable){

        Exception e =null;

        ArrayList<FacilityObserver> observers = observerManager.getObservers(facilName, facilTable);

        QueryAvailabilityRequest request = new QueryAvailabilityRequest(facilName, new ArrayList<Day>(Arrays.asList(Day.values())));
        Response response = this.queryAvailability(request, bookingManager, facilTable);

        for (FacilityObserver o : observers){
            InetAddress ip = o.getIp();
            int port = o.getPort();
            try{
                serverCommunicator.send(response, ip, port, false);
            }catch(Exception newE){
                e = newE;
            }

        }

        return e;
    }


    /*---------------------Helper Functions-----------------------------------------------*/

    public Datetime localToDatetime(LocalTime localTime, Day day){
        Datetime datetime = new Datetime(day, localTime.getHour(), localTime.getMinute());
        return datetime;

    }


    public ArrayList localToDatetime(Hashtable resultsTable) {

        ArrayList allConvertedAvailability = new ArrayList();

        for (Day day: Day.values()){
            ArrayList dayResults = new ArrayList();
            if (!resultsTable.containsKey(day)){
                allConvertedAvailability.add(dayResults);
                continue;
            }
            ArrayList availabilityList = (ArrayList) resultsTable.get(day);
            for (ArrayList slot : (ArrayList<ArrayList>) availabilityList) {
                ArrayList newSlot = new ArrayList();
                LocalTime start = (LocalTime) slot.get(0);
                LocalTime end = (LocalTime) slot.get(1);

                Datetime newStart = new Datetime(day, start.getHour(), start.getMinute());
                Datetime newEnd = new Datetime(day, end.getHour(), end.getMinute());

                newSlot.add(newStart);
                newSlot.add(newEnd);

                dayResults.add(newSlot);
            }
            allConvertedAvailability.add(dayResults);


        }
        return allConvertedAvailability;
    }

}


