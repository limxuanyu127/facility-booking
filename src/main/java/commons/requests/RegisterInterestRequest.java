package commons.requests;

public class RegisterInterestRequest {
    public String facilityName;
    public int numDays;

    public RegisterInterestRequest(String facilityName, int numDays){
        this.facilityName = facilityName;
        this.numDays = numDays;
    }
}
