package commons.requests;

import commons.utils.Datetime;

public class OffsetBookingRequest extends Request{
    public int bookingID;
    public int offset;

    public OffsetBookingRequest(int bookingID, int offset) {
        this.bookingID = bookingID;
        this.offset = offset;
    }
}
