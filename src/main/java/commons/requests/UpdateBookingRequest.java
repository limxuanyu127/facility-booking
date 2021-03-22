package commons.requests;

import commons.utils.Datetime;


public class UpdateBookingRequest extends Request{
    public int bookingID;
    public String facilityName;
    public Datetime startTime;
    public Datetime endTime;

    public UpdateBookingRequest(int bookingID, String facilityName, Datetime startTime, Datetime endTime) {
        this.bookingID = bookingID;
        this.facilityName = facilityName;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
