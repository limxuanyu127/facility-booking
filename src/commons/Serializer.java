package commons;

import commons.requests.Request;
import commons.requests.TestRequest;
import commons.responses.Response;

import java.nio.ByteBuffer;

public class Serializer {
    public static void main(String[] args) {
        TestRequest r = new TestRequest();
        serializeObject(r);
    }
    public static ByteBuffer serializeObject(Object o) {
        String className = o.getClass().getName();
        System.out.println("Serializing " + className);
        ByteBuffer bb = ByteBuffer.allocate(10);
        return bb;
    }

    public static ByteBuffer serializeTestRequest(Object o) { //For XQ's testing for Communicator
        Request request = (Request) o;
        String className = request.getClass().getName();
//        System.out.println("Serializing " + className);

        byte[] data_buf = request.name.getBytes();
        ByteBuffer bb = ByteBuffer.allocate(data_buf.length + 5);
        bb.put(data_buf, 0, data_buf.length);

        return bb;
    }

    public static ByteBuffer serializeTestResponse(Object o) { //For XQ's testing for Communicator
        Response response = (Response) o;
        String className = response.getClass().getName();
//        System.out.println("Serializing " + className);

        byte[] data_buf = response.name.getBytes();
        ByteBuffer bb = ByteBuffer.allocate(data_buf.length + 5);
        bb.put(data_buf, 0, data_buf.length);

        return bb;
    }
}
