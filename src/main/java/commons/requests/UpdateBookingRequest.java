package commons.requests;

import commons.utils.Datetime;

public class UpdateBookingRequest extends Request{
    public int bookingID;
    public Datetime startTime;
    public Datetime endTime;

    public UpdateBookingRequest(int bookingID, Datetime startTime, Datetime endTime) {
        this.bookingID = bookingID;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
