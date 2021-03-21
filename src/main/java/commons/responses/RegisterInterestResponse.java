package commons.responses;

import commons.utils.ResponseMessage;

public class RegisterInterestResponse extends Response{
    ResponseMessage responseMessage;

    public RegisterInterestResponse(ResponseMessage responseMessage){
        this.responseMessage = responseMessage;
    }
}
