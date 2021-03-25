package server.translator;

import commons.responses.Response;
import commons.responses.TestResponse;

public class Translator {

    public Response dummy(){
        return new TestResponse();
    };

}
