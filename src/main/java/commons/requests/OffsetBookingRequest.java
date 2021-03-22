package commons.requests;


public class OffsetBookingRequest extends Request{
    public int bookingID;
    public String facilityName;
    public int offset;

    public OffsetBookingRequest(int bookingID, String facilityName, int offset) {
        this.bookingID = bookingID;
        this.facilityName = facilityName;
        this.offset = offset;
    }
}
