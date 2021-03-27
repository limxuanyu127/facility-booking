package server.translator;

import commons.requests.*;
import commons.responses.*;
import commons.utils.Datetime;
import commons.utils.ResponseMessage;
import javafx.util.Pair;
import server.managers.BookingManager;

import java.lang.reflect.Array;
import java.util.*;
import java.time.*;

import java.util.*;

public class Translator {
    private ArrayList<String> allDays = new ArrayList<String>();

    public Translator(){
            List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
            allDays.addAll(days);
    }

    public QueryAvailabilityResponse queryAvailability(QueryAvailabilityRequest r, BookingManager bookingManager, Hashtable facilTable) {

        QueryAvailabilityResponse response;
        String facilName = r.facilityName;
        ArrayList days = (ArrayList) r.days;

        Pair<Hashtable, Exception> outputPair = bookingManager.queryAvailability(facilName, days, facilTable);
        Hashtable outputBooking = outputPair.getKey();
        Exception outputException = outputPair.getValue();


        ArrayList convertedList =localToDatetime(outputBooking);

        ResponseMessage responseMessage;
        if (outputException != null){
            responseMessage = new ResponseMessage(400, outputException.getMessage()) ;
        }
        else{
            responseMessage = new ResponseMessage(200, "success") ;
        }
        response = new QueryAvailabilityResponse(facilName, convertedList, responseMessage);
        return response;

    }

    public ArrayList localToDatetime(Hashtable resultsTable) {

        ArrayList allConvertedAvailability = new ArrayList();

        for (String day: allDays){
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
