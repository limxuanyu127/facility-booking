package server.managers;

import commons.rpc.ServerCommunicator;
import commons.responses.*;
import javafx.util.Pair;

import java.net.InetAddress;
import java.util.ArrayList;

public class ServiceManager {

    ServerCommunicator router;
    InetAddress serverAddress;
    int serverPort;

    public ServiceManager(ServerCommunicator router, InetAddress serverAddress, int serverPort) {
        this.router = router;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    //TODO ensure that the server method returns the right things to create the necessary response
//    public QueryAvailabilityResponse queryAvailabilityResponse(Pair<ArrayList, Exception> output){
//
//        QueryAvailabilityResponse r = new QueryAvailabilityResponse();
//    }
//


}
