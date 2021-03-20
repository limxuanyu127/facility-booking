package commons.requests;

import commons.utils.Datetime;
import java.util.List;


public class QueryAvailabilityRequest extends Request {
    public String facilityName;
    public List<Datetime> days;

    public QueryAvailabilityRequest(String facilityName, List<Datetime> days) {
        this.facilityName = facilityName;
        this.days = days;
    }
}
