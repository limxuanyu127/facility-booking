package client;

import commons.rpc.Communicator;

public class ServiceManager {
    Communicator router;

    public ServiceManager(Communicator router) {
        this.router = router;
    }
    public void queryAvailability() {
        System.out.println("Querying availability...");
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
}
