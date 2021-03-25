package server.entities;

import java.time.*;

public class FacilityObserver {
    private int clientId;
    private String facilityName;



    private LocalDateTime endDate;

    public FacilityObserver(int clientId, String facilityName, LocalDateTime endDate){
        this.clientId = clientId;
        this.facilityName = facilityName;
        this.endDate = endDate;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }


    public LocalDateTime getEndDate() {
        return endDate;
    }

}
