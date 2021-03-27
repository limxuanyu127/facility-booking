package server.translator;

import commons.requests.QueryAvailabilityRequest;
import commons.responses.QueryAvailabilityResponse;
import commons.utils.Datetime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.entities.Booking;
import server.entities.Facility;
import server.managers.BookingManager;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class TranslatorTest {


    BookingManager bookingManager;
    Hashtable<String, Facility> facilTable;
    Translator translator;

    @BeforeEach
    void setUp() {

        facilTable = new Hashtable<>();
        bookingManager = new BookingManager();
        translator = new Translator();

        //Populate facilityList
        Facility badmintonCourt = new Facility("badmintonCourt");
        Facility gym = new Facility("gym");
        //FIXME Have to ensure that facil name is lower case
        facilTable.put("badmintoncourt", badmintonCourt);
        facilTable.put("gym", gym);

        //Populate booking
        LocalTime startOne = LocalTime.of(14, 00);
        LocalTime endOne = LocalTime.of( 16, 00);
        Booking bookingOne = new Booking("Tuesday",1, 100, "badmintoncourt", startOne, endOne);

        LocalTime startTwo = LocalTime.of(20, 00);
        LocalTime endTwo = LocalTime.of(22, 00);
        Booking bookingTwo = new Booking("Tuesday", 2, 100, "badmintoncourt", startTwo, endTwo);

        Facility targetFacil = facilTable.get("badmintoncourt");
        targetFacil.addBooking(bookingOne);
        targetFacil.addBooking(bookingTwo);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void queryAvailability_NoConflict_NoException() {

        String facilName = "badmintoncourt";
        ArrayList expectedInterval = new ArrayList();
        expectedInterval.addAll(Arrays.asList(8,22,8,14,16,20));
        ArrayList outputInterval = new ArrayList<>();

        List<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Tuesday");

        QueryAvailabilityRequest request = new QueryAvailabilityRequest(facilName, days);

        QueryAvailabilityResponse response = translator.queryAvailability(request, bookingManager, facilTable);

        List availabilities =  response.intervals;


        //TODO make the check check minutes also maybe
        for (ArrayList dayAvail: (ArrayList<ArrayList>) availabilities){
            for (ArrayList slot: (ArrayList<ArrayList>) dayAvail){
                Datetime start = (Datetime) slot.get(0);
                Datetime end = (Datetime) slot.get(1);
                outputInterval.add(start.hour);
                outputInterval.add(end.hour);

            }
        }
        assertEquals(expectedInterval, outputInterval);
    }

    @Test
    void localToDatetime() {
    }
}