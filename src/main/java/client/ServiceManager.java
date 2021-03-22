package client;

import commons.requests.*;
import commons.responses.Response;
import commons.rpc.ClientCommunicator;
import commons.utils.Datetime;
import java.net.InetAddress;
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
        System.out.println("Please enter list of days [1 - 7], separated by spaces: ");
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
        System.out.println("Please enter offset in terms of number of 30-minute slots. \n " +
                "1 = push back by 30 minutes \n" +
                "2 = push back by 1 hour \n " +
                "-1 = push forward by 30 minutes \n" +
                "and so on. ");
        int offset = Integer.parseInt(scanner.nextLine());

        Request req = new OffsetBookingRequest(bookingID, offset);
        request(router, req);
    }
    public void updateBooking() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter booking ID: ");
        int bookingID = Integer.parseInt(scanner.nextLine());
        System.out.println("Please enter start day and time [D/HH/MM]: ");
        String startDatetime = scanner.nextLine();
        System.out.println("Please enter end day and time [D/HH/MM]: ");
        String endDatetime = scanner.nextLine();

        Request req = new UpdateBookingRequest(
                bookingID,
                getDatetimeFromString(startDatetime),
                getDatetimeFromString(endDatetime)
        );
        request(router, req);
    }
    public void deleteBooking() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter booking ID: ");
        int bookingID = Integer.parseInt(scanner.nextLine());

        Request req = new DeleteBookingRequest(
                bookingID
        );
        request(router, req);
    }
    public void registerInterest() {

    }

    public static List<Datetime> getListOfDates(String days){
        List<Datetime> dateObjs = new ArrayList<>();
        String [] dayList = days.split(" ");
        for (String d: dayList) {
            Datetime dateObj = new Datetime(Integer.parseInt(d),0,0);
            dateObjs.add(dateObj);
        }
        return dateObjs;
    }

    public static Datetime getDatetimeFromString(String datetime) {
        String[] datetimeParts = datetime.split("/");
        return new Datetime(
                Integer.parseInt(datetimeParts[0]),
                Integer.parseInt(datetimeParts[1]),
                Integer.parseInt(datetimeParts[2])
        );
    }

//    TODO: implement parser for each response type, change input type to Response
    public static void generateResponse(Response res) {
        String message = "";
        System.out.println(message);
    }

    public static void request(ClientCommunicator router, Request req) {
        router.send(req);
        try {
            Response res = router.receive();
            generateResponse(res);
        } catch (RuntimeException e){
            e.printStackTrace();
        }
    }
}
