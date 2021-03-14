package commons.rpc;

import java.util.ArrayList;

public class ClientRequestLog {
    ArrayList<ClientRequest> clientRequests;
    ArrayList<Integer> clientRequestsHashed;

    public ClientRequestLog() {
        this.clientRequests = new ArrayList<>();
        this.clientRequestsHashed = new ArrayList<>();
    }

    public Boolean addClientRequest(ClientRequest clientRequest){
        int clientRequestHash = hashClientRequest(clientRequest);
        System.out.println("Hashed: " + clientRequestHash);
        if (clientRequestsHashed.contains(clientRequestHash)){
            System.out.println("Duplicate request received");
            return false;
        }
        else{
            this.clientRequests.add(clientRequest);
            this.clientRequestsHashed.add(clientRequestHash);
            return true;
        }
    }

    private int hashClientRequest(ClientRequest clientRequest){
        int addressHash = clientRequest.clientAddress.hashCode();
        int messageHash = clientRequest.message.hashCode();
        return addressHash + clientRequest.clientPort + clientRequest.requestID + messageHash;
    }
}
