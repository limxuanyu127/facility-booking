package client;

import commons.requests.*;
import commons.responses.Response;
import commons.rpc.Communicator;
import commons.utils.Datetime;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServiceManager {
    Communicator router;
    InetAddress serverAddress;
    int serverPort;

    public ServiceManager(Communicator router, InetAddress serverAddress, int serverPort) {
        this.router = router;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
    public void queryAvailability() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter facility name: ");
        String facilityName = scanner.nextLine();
        System.out.println("Please enter the start date [DD/MM/YYYY]: ");
        String startDate = scanner.nextLine();
        System.out.println("Please enter the end date [DD/MM/YYYY]: ");
        String endDate = scanner.nextLine();

        List<Datetime> dates = new ArrayList<>();
        dates.addAll(getListOfDates(startDate, endDate));
        String className = "server.BookingManager";
        String methodName = "queryAvailability";
        Request req = new QueryAvailabilityRequest(facilityName, dates);
        request(router, req, className, methodName, serverAddress, serverPort);
    }
    public void bookFacility() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter facility name: ");
        String facilityName = scanner.nextLine();
        System.out.println("Please enter start date and time [DD/MM/YYYY HH:MM]: ");
        String startDatetime = scanner.nextLine();
        System.out.println("Please enter end date and time [DD/MM/YYYY HH:MM]: ");
        String endDatetime = scanner.nextLine();

        String className = "server.BookingManager";
        String methodName = "bookFacility";

        Request req = new BookFacilityRequest(
                facilityName,
                getDatetimeFromString(startDatetime),
                getDatetimeFromString(endDatetime)
        );
        request(router, req, className, methodName, serverAddress, serverPort);
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

        String className = "server.BookingManager";
        String methodName = "offsetBooking";

        Request req = new OffsetBookingRequest(bookingID, offset);
        request(router, req, className, methodName, serverAddress, serverPort);
    }
    public void updateBooking() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter booking ID: ");
        int bookingID = Integer.parseInt(scanner.nextLine());
        System.out.println("Please enter start date and time [DD/MM/YYYY HH:MM]: ");
        String startDatetime = scanner.nextLine();
        System.out.println("Please enter end date and time [DD/MM/YYYY HH:MM]: ");
        String endDatetime = scanner.nextLine();

        String className = "server.BookingManager";
        String methodName = "updateBooking";

        Request req = new UpdateBookingRequest(
                bookingID,
                getDatetimeFromString(startDatetime),
                getDatetimeFromString(endDatetime)
        );
        request(router, req, className, methodName, serverAddress, serverPort);
    }
    public void deleteBooking() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter booking ID: ");
        int bookingID = Integer.parseInt(scanner.nextLine());

        String className = "server.BookingManager";
        String methodName = "deleteBooking";

        Request req = new DeleteBookingRequest(
                bookingID
        );
        request(router, req, className, methodName, serverAddress, serverPort);
    }
    // TODO: to think about callback -> spin up a new thread?
    public void registerInterest() {
        System.out.println("Registering interest...");
    }
//    TODO: implement
    public static List<Datetime> getListOfDates(String startDate, String endDate){
        List<Datetime> dates = new ArrayList<>();
        return dates;
    }

    public static Datetime getDatetimeFromString(String datetime) {
        String[] parts = datetime.split(" ");
        String date = parts[0];
        String time = parts[1];
        String[] dateParts = date.split("/");
        String[] timeParts = time.split(":");
        return new Datetime(
                Integer.parseInt(dateParts[2]),
                Integer.parseInt(dateParts[1]),
                Integer.parseInt(dateParts[0]),
                Integer.parseInt(timeParts[0]),
                Integer.parseInt(timeParts[1])
        );
    }

//    TODO: implement parser for each response type, change input type to Response
    public static void generateResponse(Response res) {
        String message = "";
        System.out.println(message);
    }

    public static void request(Communicator router,
                               Request req,
                               String className,
                               String methodName,
                               InetAddress serverAddress,
                               int serverPort) {
        router.send(req, className, methodName, serverAddress, serverPort);
        try {
            // TODO: update accordingly, depending on receive() impl
            Response res = router.receive();
            generateResponse(res);
//            TODO: specify exception
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
