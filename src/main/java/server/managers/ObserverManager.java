package server.managers;

import server.entities.*;
import java.time.*;
import java.util.Hashtable;

public class ObserverManager {

    public void attachObserver(int clientId, String facilName, int numDays, Hashtable facilTable){

        //TODO fix sync with client
        // for demo purposes, we monitor in the magnitude of minutes instead (1 day = 1 minute)

        //TODO add the necessary checks ie. facilnameis valid, endDate is not too late?
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(numDays);
        FacilityObserver o = new FacilityObserver(clientId, facilName,endDate);

        Facility f  = (Facility) facilTable.get(facilName);
        f.attach(o);

    }

    public void notifyObservers(){

    }
}
