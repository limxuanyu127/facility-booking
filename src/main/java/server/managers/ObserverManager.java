package server.managers;

import commons.rpc.ServerCommunicator;
import commons.utils.Datetime;
import server.entities.*;

import java.net.InetAddress;
import java.time.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.NoSuchElementException;

public class ObserverManager {


    public ObserverManager(){
    }

    /**
     * Add facility observer
     * @param facilName name of the facility
     * @param numDays number of days to monitor
     * @param facilTable facility table
     * @param ip client IP address
     * @param port client port
     * @return
     */
    public Exception addObserver(String facilName, int numDays, Hashtable facilTable, InetAddress ip, int port){
        //TODO fix sync with client
        // for demo purposes, we monitor in the magnitude of minutes instead (1 day = 1 minute)

        Exception e;

        e = doFacilCheck(facilName, facilTable);

        if (e != null){
            return e;
        }
        else{
            LocalDateTime endDate = LocalDateTime.now().plusMinutes(numDays);
            FacilityObserver o = new FacilityObserver(facilName,endDate, ip, port);

            Facility f  = (Facility) facilTable.get(facilName);
            f.addObserver(o);
        }
        return null;
    }

    /**
     * Check if facility exists
     * @param facilName name of the facility
     * @param facilTable facility table
     * @return
     */
    private Exception doFacilCheck(String facilName, Hashtable facilTable){
        if (facilTable.containsKey(facilName)){
            return null;
        }
        else{
            Exception e = new NoSuchElementException("Facility does not exist");
            return e;
        }
    }

    /**
     * Retrieve list of observers
     * @param facilName name of the facility
     * @param facilTable facility table
     * @return
     */
    public ArrayList<FacilityObserver> getObservers(String facilName, Hashtable facilTable){
        Facility facil = (Facility) facilTable.get(facilName);
        ArrayList observers = facil.getUpdatedObservers();

        return observers;
    }
}

