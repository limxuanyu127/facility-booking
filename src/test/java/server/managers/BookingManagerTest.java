package server.managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.entities.Booking;
import server.entities.Facility;


import java.lang.reflect.Array;
import java.time.*;
import java.util.*;
import javafx.util.Pair;

import static org.junit.jupiter.api.Assertions.*;

class BookingManagerTest {

    BookingManager bookingManager;
    Hashtable<String, Facility> facilityList;

    @BeforeEach
    void setUp() {

        //Dictionary is abstract class
        facilityList = new Hashtable<>();
        bookingManager = new BookingManager();

        //Populate facilityList
        Facility badmintonCourt = new Facility("badmintonCourt");
        Facility gym = new Facility("gym");
        //FIXME Have to ensure that facil name is lower case
        facilityList.put("badmintoncourt", badmintonCourt);
        facilityList.put("gym", gym);

        //Populate booking
        LocalDateTime startOne = LocalDateTime.of(2021, 3, 18, 14, 00);
        LocalDateTime endOne = LocalDateTime.of(2021, 3, 18, 16, 00);
        Booking bookingOne = new Booking(1, 001, "badmintoncourt", startOne, endOne);

        LocalDateTime startTwo = LocalDateTime.of(2021, 3, 18, 20, 00);
        LocalDateTime endTwo = LocalDateTime.of(2021, 3, 18, 22, 00);
        Booking bookingTwo = new Booking(1, 001, "badmintoncourt", startTwo, endTwo);

        Facility targetFacil = facilityList.get("badmintoncourt");
        targetFacil.addBooking(bookingOne);
        targetFacil.addBooking(bookingTwo);


    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void queryAvailability_NoConflict_NoException() {
        ArrayList<LocalDate> queryDates = new ArrayList<LocalDate>();
        queryDates.add(LocalDate.of(2021, 3, 18));
        Pair<ArrayList, Exception> queryResults = bookingManager.queryAvailability("badmintoncourt", queryDates, facilityList);
        ArrayList outputBookings = queryResults.getKey();
        Exception outputException = queryResults.getValue();

        ArrayList expectedBookings = new ArrayList<>();
        ArrayList<LocalDateTime> slotOne = new ArrayList<>();
        ArrayList<LocalDateTime> slotTwo = new ArrayList<>();
        slotOne.add(LocalDateTime.of(2021,3, 18, 8, 0, 0));
        slotOne.add(LocalDateTime.of(2021,3, 18, 14, 0, 0));
        slotTwo.add(LocalDateTime.of(2021,3, 18, 16, 0, 0));
        slotTwo.add(LocalDateTime.of(2021,3, 18, 20, 0, 0));
        expectedBookings.add(slotOne);
        expectedBookings.add(slotTwo);
        Exception expectedException = null;

        assertEquals(outputBookings, expectedBookings);
        assertEquals(outputException, expectedException);
    }



    @Test
    void sortBookings() {
    }

    @Test
    void createBooking_NoConflict_NoException() {
        int bookingId = 1;
        int clientId = 100;
        String facilName = "badmintoncourt";

        LocalDateTime startThree = LocalDateTime.of(2021, 3, 18, 18, 00);
        LocalDateTime endThree = LocalDateTime.of(2021, 3, 18, 20, 00);

        Pair<Booking, Exception> createBookingResults = bookingManager.createBooking(bookingId, clientId, facilName, startThree, endThree, facilityList);
        Booking outputBooking = createBookingResults.getKey();
        Exception outputException = createBookingResults.getValue();

        assertEquals(outputException, null);
        assertEquals(outputBooking.getBookingId(), bookingId);
        assertEquals(outputBooking.getClientId(), clientId);
        assertEquals(outputBooking.getFacilityName(), facilName);
        assertEquals(outputBooking.getStart(), startThree);
        assertEquals(outputBooking.getEnd(), endThree);
    }

    @Test
    void createBooking_Conflict_Exception() {
        int bookingId = 1;
        int clientId = 100;
        String facilName = "badmintoncourt";

        LocalDateTime startThree = LocalDateTime.of(2021, 3, 18, 19, 00);
        LocalDateTime endThree = LocalDateTime.of(2021, 3, 18, 21, 00);

        Pair<Booking, Exception> createBookingResults = bookingManager.createBooking(bookingId, clientId, facilName, startThree, endThree, facilityList);
        Booking outputBooking = createBookingResults.getKey();
        Exception outputException = createBookingResults.getValue();

        assertEquals(outputException.getClass(), NoSuchElementException.class);
        assertEquals(outputException.getMessage(), "There is a booking at that time slot");
    }


    @Test
    void createBooking_TooEarly_Exception() {

    }


}


