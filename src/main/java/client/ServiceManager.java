package client;

import commons.exceptions.InvalidDateException;
import commons.exceptions.InvalidDateFormatException;
import commons.exceptions.InvalidDayException;
import commons.exceptions.InvalidIntervalException;
import commons.requests.*;
import commons.responses.*;
import commons.rpc.ClientCommunicator;
import commons.utils.Datetime;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.min;
import static java.lang.Math.toIntExact;

public class ServiceManager {
    ClientCommunicator router;
    InetAddress serverAddress;
    int serverPort;
    static final List<String> daysOfWeek = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");


    public ServiceManager(ClientCommunicator router, InetAddress serverAddress, int serverPort) {
        this.router = router;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
    public void queryAvailability() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter facility name: ");
        String facilityName = scanner.nextLine();
        System.out.println("Please enter list of days (Monday, Tuesday etc), separated by spaces: ");
        String daysString = scanner.nextLine();

        List<String> days = new ArrayList<>(Arrays.asList(daysString.split(" ")));
        for (String d : days) {
            try {
                if (checkValidDay(d)) {
                    continue;
                }
            } catch (InvalidDayException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
        Request req = new QueryAvailabilityRequest(facilityName, days);
        request(router, req);
    }
    public void bookFacility() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter facility name: ");
        String facilityName = scanner.nextLine();
        System.out.println("Please enter start day and time [D/HH/MM]: ");
        String startDatetimeStr = scanner.nextLine();
        System.out.println("Please enter end day and time [D/HH/MM]: ");
        String endDatetimeStr = scanner.nextLine();
        Datetime startDatetime = null;
        Datetime endDatetime = null;
        try {
            startDatetime = getDatetimeFromString(startDatetimeStr);
        } catch (InvalidDayException | InvalidDateFormatException e) {
            System.out.println(e.getMessage());
        }
        try {
            endDatetime = getDatetimeFromString(endDatetimeStr);
        } catch (InvalidDayException | InvalidDateFormatException e) {
            System.out.println(e.getMessage());
        }
        try {
            checkValidStartEnd(startDatetime, endDatetime);
        } catch (InvalidIntervalException e) {
            System.out.println(e.getMessage());
        }

        Request req;
        req = new BookFacilityRequest(
                facilityName,
                startDatetime,
                endDatetime
            );
        request(router, req);
    }
    public void offsetBooking() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter booking ID: ");
        int bookingID = Integer.parseInt(scanner.nextLine());
        System.out.println("Please enter facility name: ");
        String facilityName =scanner.nextLine();
        System.out.println("Please enter offset in terms of number of 30-minute slots. \n" +
                "1 = push back by 30 minutes \n" +
                "2 = push back by 1 hour \n " +
                "-1 = push forward by 30 minutes \n" +
                "and so on. ");
        int offset = Integer.parseInt(scanner.nextLine());

        Request req = new OffsetBookingRequest(bookingID, facilityName, offset);
        request(router, req);
    }
    public void extendBooking() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter booking ID: ");
        int bookingID = Integer.parseInt(scanner.nextLine());
        System.out.println("Please enter facility name: ");
        String facilityName =scanner.nextLine();
        System.out.println("Please enter number of 30-minute slots to extend by: ");
        int extension = scanner.nextInt();

        Request req = new ExtendBookingRequest(
                bookingID,
                facilityName,
                extension
        );
        request(router, req);
    }
    public void deleteBooking() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter booking ID: ");
        int bookingID = Integer.parseInt(scanner.nextLine());
        System.out.println("Please enter facility name: ");
        String facilityName =scanner.nextLine();

        Request req = new DeleteBookingRequest(
                bookingID,
                facilityName
        );
        request(router, req);
    }
    public void registerInterest() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter facility name: ");
        String facilityName = scanner.nextLine();
        System.out.println("Please enter number of days you want to monitor: ");
        int numDays = scanner.nextInt();

        Request req = new RegisterInterestRequest(facilityName, numDays);
        request(router, req);
        System.out.println("=====================================");
        System.out.println("Monitoring availability for " + facilityName);

        // for demo purposes, we monitor in the magnitude of minutes instead (1 day = 1 minute)
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(numDays);
        while (LocalDateTime.now().isBefore(endTime)) {
            int timeout = toIntExact(LocalDateTime.now().until(endTime, ChronoUnit.MILLIS));
            System.out.println("Time left: " + timeout);
            try {
                // expecting QueryAvailabilityResponse; can use the same one because it is essentially a query for availability
                router.setSocketTimeout(timeout); // allow socket to listen for the duration of the listen
                Response res = router.receive();
                generateResponse(res);
            } catch (RuntimeException e) {
                if (e.getCause() instanceof SocketTimeoutException) {
                    System.out.println("Listening Duration Over");
                }
            }
        }
        router.setSocketTimeout(router.socketTimeout); //reset socket to default timeout
    }
    @Deprecated
    public static List<Datetime> getListOfDates(String days){
        List<Datetime> dateObjs = new ArrayList<>();
        String [] dayList = days.split(" ");
        for (String d: dayList) {
            Datetime dateObj = new Datetime(d,0,0);
            dateObjs.add(dateObj);
        }
        return dateObjs;
    }

    public static Datetime getDatetimeFromString(String datetime) throws InvalidDateFormatException, InvalidDayException {
        String[] datetimeParts = datetime.split("/");
        if (datetimeParts.length != 3) {
            throw new InvalidDateFormatException();
        }
        if (!daysOfWeek.contains(datetimeParts[0])) {
            throw new InvalidDayException();
        }
        try {
            checkValidHour(datetimeParts[1]);
            checkValidMinute(datetimeParts[2]);
        } catch (InvalidDateException e) {
            System.out.println(e.getMessage());
        }
        return new Datetime(
                datetimeParts[0],
                Integer.parseInt(datetimeParts[1]),
                Integer.parseInt(datetimeParts[2])
        );
    }

    public static void generateBookingDetails(int bookingID, String facilityName, Datetime startTime, Datetime endTime) {
        System.out.println("Booking Confirmation ID: " + bookingID);
        System.out.println("Facility Name: " + facilityName);
        String day = startTime.day;
        String startHour = startTime.hour > 9 ? String.valueOf(startTime.hour) : "0" + startTime.hour;
        String startMinute = startTime.minute > 0 ? String.valueOf(startTime.minute) : "00";
        String endHour = endTime.hour > 9 ? String.valueOf(endTime.hour) : "0" + endTime.hour;
        String endMinute = endTime.minute > 0 ? String.valueOf(endTime.minute) : "00";
        String dayTime = day + " " + startHour + ":" + startMinute + " - " + endHour + ":" + endMinute;
        System.out.println("Day and time: " + dayTime);
    }

//    TODO: implement parser for each response type, change input type to Response
    public static void generateResponse(Response genericResponse) {
        String message = "";
        System.out.println(message);
        if (genericResponse instanceof QueryAvailabilityResponse) {
            QueryAvailabilityResponse response = ((QueryAvailabilityResponse) genericResponse);
            System.out.println(response.responseMessage.message);
            if (response.responseMessage.statusCode == 200) {
                System.out.println("======== AVAILABLITIY FOR " + response.facilityName + " ========");
                for (List<List<Datetime>> l : response.intervals) {
                    for (List<Datetime> m : l){
                        Datetime startDatetime = m.get(0);
                        Datetime endDatetime = m.get(1);
                        String day = startDatetime.day;
                        String startHour = startDatetime.hour > 9 ? String.valueOf(startDatetime.hour) : "0" + startDatetime.hour;
                        String startMinute = startDatetime.minute > 0 ? String.valueOf(startDatetime.minute) : "00";
                        String endHour = endDatetime.hour > 9 ? String.valueOf(endDatetime.hour) : "0" + endDatetime.hour;
                        String endMinute = endDatetime.minute > 0 ? String.valueOf(endDatetime.minute) : "00";
                        System.out.println(day + " " + startHour + ":" + startMinute + " - " + endHour + ":" + endMinute);

                    }
                }
            }

        } else if (genericResponse instanceof BookFacilityResponse) {
            BookFacilityResponse response = (BookFacilityResponse) genericResponse;
            System.out.println(response.responseMessage.message);
            if (response.responseMessage.statusCode == 200) {
                System.out.println("======== NEW BOOKING CONFIRMATION ========");
                generateBookingDetails(
                        response.bookingID,
                        response.facilityName,
                        response.startTime,
                        response.endTime
                );
            }

        } else if (genericResponse instanceof OffsetBookingResponse) {
            OffsetBookingResponse response = (OffsetBookingResponse) genericResponse;
            System.out.println(response.responseMessage.message);
            if (response.responseMessage.statusCode == 200) {
                System.out.println("======== UPDATED BOOKING CONFIRMATION ========");
                generateBookingDetails(
                        response.bookingID,
                        response.facilityName,
                        response.startTime,
                        response.endTime
                );
            }

        } else if (genericResponse instanceof ExtendBookingResponse) {
            ExtendBookingResponse response = (ExtendBookingResponse) genericResponse;
            System.out.println(response.responseMessage.message);
            if (response.responseMessage.statusCode == 200) {
                System.out.println("======== UPDATED BOOKING CONFIRMATION ========");
                generateBookingDetails(
                        response.bookingID,
                        response.facilityName,
                        response.startTime,
                        response.endTime
                );

            }

        } else if (genericResponse instanceof DeleteBookingResponse) {
            DeleteBookingResponse response = (DeleteBookingResponse) genericResponse;
            System.out.println(response.responseMessage.message);

        } else if (genericResponse instanceof RegisterInterestResponse) {
            RegisterInterestResponse response = (RegisterInterestResponse) genericResponse;
            System.out.println(response.responseMessage.message);

        } else if (genericResponse instanceof TestResponse){
            TestResponse response = (TestResponse) genericResponse;
            System.out.println(response.testString);

        } else if (genericResponse instanceof NullResponse){
            NullResponse response = (NullResponse) genericResponse;
            System.out.println(response.responseMessage.message);
        } else if (genericResponse instanceof ErrorResponse){
            ErrorResponse response = (ErrorResponse) genericResponse;
            System.out.println(response.responseMessage.message);
        }
        else {
            throw new Error("Not implemented");
        }
    }

    public static void request(ClientCommunicator router, Request req) {
        try {
            Response res = router.sendRequest(req);
//            System.out.println(res.getClass().getName());
            generateResponse(res);
            System.out.println(res.getClass().getName());
        } catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    public static boolean checkValidDay(String day) throws InvalidDayException {
        if (!daysOfWeek.contains(day)) {
            throw new InvalidDayException();
        }
        return true;
    }

    public static boolean checkValidHour(String hour) throws InvalidDateException {
        try {
            int hourInt = Integer.parseInt(hour);
            if (hourInt < 0 || hourInt > 23)  {
                throw new InvalidDateException();
            }
        } catch (NumberFormatException e) {
            throw new InvalidDateException();
        }
        return true;
    }

    public static boolean checkValidMinute(String minute) throws InvalidDateException {
        try {
            int minuteInt = Integer.parseInt(minute);
            if (!(minuteInt == 0 || minuteInt == 30)){
                throw new InvalidDateException();
            }
        } catch (NumberFormatException e) {
            throw new InvalidDateException();
        }
        return true;
    }

    public static boolean checkValidStartEnd(Datetime startTime, Datetime endTime) throws InvalidIntervalException {
        if (!(isBefore(startTime.day, endTime.day) &&  (startTime.hour * 60 + startTime.minute < endTime.hour * 60 + endTime.minute))) {
            throw new InvalidIntervalException();
        }
        return true;
    }

    public static boolean isBefore(String start, String end) {
        return daysOfWeek.indexOf(start) < daysOfWeek.indexOf(end);
    }
}
