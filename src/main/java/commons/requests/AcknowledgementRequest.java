package commons.requests;

import commons.responses.Response;
import commons.utils.ResponseMessage;

public class AcknowledgementRequest extends Request {
    public ResponseMessage responseMessage;

    public AcknowledgementRequest(){}
    public AcknowledgementRequest(ResponseMessage responseMessage){
        this.responseMessage = responseMessage;
    }
}
