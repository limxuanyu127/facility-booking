package server.translator;

import commons.requests.*;
import commons.responses.*;
import javafx.util.Pair;
import server.managers.BookingManager;

import java.util.*;

public class translator {
//    public QueryAvailabilityResponse queryAvailability(QueryAvailabilityRequest r, BookingManager bookingManager, Hashtable facilTable){
//
//        String facilName = r.facilityName;
//        ArrayList days = (ArrayList) r.days;
//
//        Pair<Hashtable, Exception> output = bookingManager.queryAvailability(facilName, days, facilTable);
//
//        return new QueryAvailabilityResponse()

//    }

    public Response test(){
        return new TestResponse();
    }
}
