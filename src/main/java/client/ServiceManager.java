package client;

import commons.requests.*;
import commons.responses.*;
import commons.rpc.ClientCommunicator;
import commons.utils.Datetime;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServiceManager {
    ClientCommunicator router;
    InetAddress serverAddress;
    int serverPort;

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
        String dates = scanner.nextLine();

        List<Datetime> dateObjs = new ArrayList<>();
        dateObjs.addAll(getListOfDates(dates));

        Request req = new QueryAvailabilityRequest(facilityName, dateObjs);
        request(router, req);
    }
    public void bookFacility() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter facility name: ");
        String facilityName = scanner.nextLine();
        System.out.println("Please enter start day and time [D/HH/MM]: ");
        String startDatetime = scanner.nextLine();
        System.out.println("Please enter end day and time [D/HH/MM]: ");
        String endDatetime = scanner.nextLine();

        Request req = new BookFacilityRequest(
                facilityName,
                getDatetimeFromString(startDatetime),
                getDatetimeFromString(endDatetime)
        );
        request(router, req);
    }
    public void offsetBooking() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter booking ID: ");
        int bookingID = Integer.parseInt(scanner.nextLine());
        System.out.println("Please enter facility name: ");
        String facilityName =scanner.nextLine();
        System.out.println("Please enter offset in terms of number of 30-minute slots. \n " +
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
            try {
                // expecting QueryAvailabilityResponse; can use the same one because it is essentially a query for availability
                Response res = router.receive();
                generateResponse(res);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Datetime> getListOfDates(String days){
        List<Datetime> dateObjs = new ArrayList<>();
        String [] dayList = days.split(" ");
        for (String d: dayList) {
            Datetime dateObj = new Datetime(d,0,0);
            dateObjs.add(dateObj);
        }
        return dateObjs;
    }

    public static Datetime getDatetimeFromString(String datetime) {
        String[] datetimeParts = datetime.split("/");
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
                for (List<Datetime> l : response.intervals) {
                    for (Datetime d: l) {
                        String day = d.day;
                        String hour = d.hour > 9 ? String.valueOf(d.hour) : "0" + d.hour;
                        String minute = d.minute > 0 ? String.valueOf(d.minute) : "00";
                        System.out.println(day + " " + hour + ":" + minute);
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

        } else {
            throw new Error("Not implemented");
        }
    }

    public static void request(ClientCommunicator router, Request req) {
        router.sendRequest(req);
        try {
            Response res = router.receive();
            generateResponse(res);
        } catch (RuntimeException e){
            e.printStackTrace();
        }
    }
}
