package commons.requests;

import commons.utils.Datetime;


public class DeleteBookingRequest extends Request{
    public int bookingID;
    public String facilityName;

    public DeleteBookingRequest(int bookingID, String facilityName){
        this.bookingID = bookingID;
        this.facilityName = facilityName;
    }

}
