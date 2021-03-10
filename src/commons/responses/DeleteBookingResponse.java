package commons.responses;

import commons.utils.ResponseMessage;

public class DeleteBookingResponse {
    ResponseMessage responseMessage;

    public DeleteBookingResponse(ResponseMessage responseMessage){
        this.responseMessage = responseMessage;
    }
}
