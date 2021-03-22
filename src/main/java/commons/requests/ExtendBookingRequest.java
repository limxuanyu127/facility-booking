package commons.requests;


public class ExtendBookingRequest extends Request{
    public int bookingID;
    public String facilityName;
    public int extension;

    public ExtendBookingRequest(int bookingID, String facilityName, int extension) {
        this.bookingID = bookingID;
        this.facilityName = facilityName;
        this.extension = extension;
    }
}
