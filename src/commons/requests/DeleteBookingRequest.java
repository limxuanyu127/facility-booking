package commons.requests;

import commons.utils.Datetime;

public class DeleteBookingRequest {
    public int bookingID;

    public DeleteBookingRequest(int bookingID){
        this.bookingID = bookingID;
    }

}
