package commons.utils;

public class ResponseMessage {
    public int statusCode;
    public String message;

    public ResponseMessage(int statusCode, String message){
        this.statusCode = statusCode;
        this.message = message;
    }

}
