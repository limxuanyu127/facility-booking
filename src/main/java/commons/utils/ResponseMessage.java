package commons.utils;

public class ResponseMessage {
    public Integer statusCode;
    public String message;

    public ResponseMessage(){}

    public ResponseMessage(int statusCode, String message){
        this.statusCode = statusCode;
        this.message = message;
    }

}
