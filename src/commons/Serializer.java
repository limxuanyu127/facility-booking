package commons;

import commons.requests.TestRequest;
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
}
