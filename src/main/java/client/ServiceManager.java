package client;

import commons.requests.QueryAvailabilityRequest;
import commons.responses.Response;
import commons.rpc.Communicator;
import commons.utils.Datetime;

import javax.management.Query;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceManager {
    Communicator router;
    InetAddress serverAddress;
    int serverPort;

    public ServiceManager(Communicator router, InetAddress serverAddress, int serverPort) {
        this.router = router;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
    public void queryAvailability(String facilityName, String startDate, String endDate) {
        System.out.println("Querying availability...");
        List<Datetime> dates = new ArrayList<>();
        dates.addAll(getListOfDates(startDate, endDate));
        String className = "server.BookingManager";
        String methodName = "queryAvailability";
        QueryAvailabilityRequest req = new QueryAvailabilityRequest(facilityName, dates);
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
    public void bookFacility() {
        System.out.println("Booking facility...");
    }
    public void offsetBooking() {
        System.out.println("Offseting booking...");
    }
    public void updateBooking() {
        System.out.println("Updating booking..");
    }
    public void deleteBooking() {
        System.out.println("Deleting booking..");
    }
    public void registerInterest() {
        System.out.println("Registering interest...");
    }
//    TODO: implement
    public static List<Datetime> getListOfDates(String startDate, String endDate){
        List<Datetime> dates = new ArrayList<>();
        return dates;
    }
//    TODO: implement parser for each response type, change input type to Response
    public static void generateResponse(Response res) {
        String message = "";
        System.out.println(message);
    }
}
