package commons.requests;

import commons.utils.Datetime;
import java.util.List;


public class QueryAvailabilityRequest extends Request {
    public String facilityName;
    public List<String> days;

    public QueryAvailabilityRequest(String facilityName, List<String> days) {
        this.facilityName = facilityName;
        this.days = days;
    }
}
