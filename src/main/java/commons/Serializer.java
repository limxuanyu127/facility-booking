package commons;

import commons.requests.Request;
import commons.requests.TestRequest;
import commons.responses.Response;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;

public class Serializer {
    public static void main(String[] args) {
        TestRequest r = new TestRequest();
        ByteBuffer bb = ByteBuffer.allocate(2000);
        serializeObject(r, bb);
    }
    public static void serializeObject(Object o, ByteBuffer bb) {
//         TODO: add more primitive checks if necessary
        if (o.getClass().equals(Integer.class)) {
            serializeInteger((Integer) o, bb);
        } else if (o.getClass().equals(String.class)) {
            serializeString((String) o, bb);
        } else if (o.getClass().equals(ArrayList.class)) {
            serializeList((ArrayList<?>) o, bb);
        } else {
            String className = o.getClass().getName();
            System.out.println("Serializing " + className);
            serializeString(className, bb);
            Field[] fields = o.getClass().getDeclaredFields();
            for (Field f: fields) {
                serializeString(f.getName(), bb);
                try {
                    Object val = f.get(o);
                    serializeObject(val, bb);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public static void serializeInteger(Integer i, ByteBuffer bb) {
        bb.putInt(i);
    }

    public static void serializeString(String s, ByteBuffer bb) {
        byte[] sBytes = s.getBytes();
        Integer length = sBytes.length;
        System.out.println("serializing string " + s + " with length " + length);
        bb.putInt(length);
        bb.put(sBytes);
    }

    public static void serializeList(List<?> l, ByteBuffer bb) {
        Integer length = l.size();
        String listObjectType = l.get(0).getClass().getName();
        serializeString(listObjectType, bb);
        bb.putInt(length);
        for (Object o: l) {
            serializeObject(o, bb);
        }
    }

}
