package server.entities;

import java.net.InetAddress;
import java.time.*;

public class FacilityObserver {
    private String facilityName;
    private LocalDateTime endDate;

    InetAddress ip;
    int port;


    public FacilityObserver(String facilityName, LocalDateTime endDate, InetAddress ip, int port){
        this.facilityName = facilityName;
        this.endDate = endDate;
        this.ip = ip;
        this.port = port;
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

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

}
