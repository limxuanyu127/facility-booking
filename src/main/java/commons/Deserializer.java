package commons;

import commons.Serializer;
import commons.requests.DeleteBookingRequest;
import commons.requests.QueryAvailabilityRequest;
import commons.requests.TestRequest;
import commons.utils.Datetime;

import javax.management.Query;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Deserializer {
    public static void main(String[] args) {
        TestRequest r = new TestRequest();
        Datetime dateOne = new Datetime("Monday", 0,0);
        Datetime dateTwo = new Datetime("Tuesday", 0,0);
        List<Datetime> days = new ArrayList<Datetime>(Arrays.asList(dateOne, dateTwo));
        QueryAvailabilityRequest queryReq = new QueryAvailabilityRequest("badminton", days);
        ByteBuffer bb = ByteBuffer.allocate(2000);
        Serializer.serializeObject(queryReq, bb);
        bb.flip();
        Object o = deserializeObject(bb);
        System.out.println(o.getClass().getName());
        System.out.println(((QueryAvailabilityRequest) o).days);
        System.out.println(((QueryAvailabilityRequest) o).facilityName);

//        System.out.println(((TestRequest) o).testString);
//        System.out.println(((TestRequest) o).testInt);
//        System.out.println(((TestRequest) o).testList);
//        System.out.println(((TestRequest) o).testNestedList);
    }
    public static Object deserializeObject(ByteBuffer bb) {
        String className = deserializeString(bb);
        int numFields = deserializeInteger(bb);
        System.out.println(className);
        try {
            Class<?> c = Class.forName(className);
            Object o = c.newInstance();

            HashMap<String, Object> fieldTypeMap = new HashMap<>();
            Field[] fields = c.getFields();
            for (Field f: fields) {
                fieldTypeMap.put(f.getName(), f.getType());
            }
            System.out.println(fieldTypeMap);
            for (int i = 0; i < numFields; i++) {
                String fieldName = deserializeString(bb);
                Object val;
                System.out.println(fieldTypeMap.get(fieldName));
                if (fieldTypeMap.get(fieldName).equals(String.class)) {
                    val = deserializeString(bb);
                } else if (fieldTypeMap.get(fieldName).equals(Integer.class)) {
                    val = deserializeInteger(bb);
                } else if (fieldTypeMap.get(fieldName).equals(List.class)) {
                    val = deserializeList(bb);
                }
                else {
                    val = Deserializer.deserializeObject(bb);
                }
                try {
                    o.getClass().getDeclaredField(fieldName).set(o, val);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            return c.cast(o);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static int deserializeInteger(ByteBuffer bb) {
        return bb.getInt();
    }

    public static String deserializeString(ByteBuffer bb) {
        int length = bb.getInt();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++){
            bytes[i] = bb.get();
        }
        return new String(bytes);
    }

    public static List<Object> deserializeList(ByteBuffer bb) {
        String listObjectType = deserializeString(bb);
        System.out.println("List object type is " + listObjectType);
        int length = deserializeInteger(bb);
        List<Object> l = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (listObjectType.equals("java.lang.String")) {
                l.add(deserializeString(bb));
            } else if (listObjectType.equals("java.lang.Integer")) {
                l.add(deserializeInteger(bb));
            } else if (listObjectType.equals("java.util.ArrayList")) {
                l.add(deserializeList(bb));
            } else {
                l.add(deserializeObject(bb));
            }
        }
        return l;
    }
}
