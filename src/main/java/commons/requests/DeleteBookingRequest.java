package commons.requests;

import commons.utils.Datetime;


public class DeleteBookingRequest extends Request{
    public int bookingID;

    public DeleteBookingRequest(int bookingID){
        this.bookingID = bookingID;
    }

}
