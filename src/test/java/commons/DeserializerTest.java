package commons;

import commons.requests.BookFacilityRequest;
import commons.requests.TestRequest;
import commons.utils.Datetime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import commons.requests.QueryAvailabilityRequest;
import commons.Serializer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DeserializerTest {

    @Test
    void deserializeQueryAvailabilityRequest() {
        Datetime dateOne = new Datetime("Monday", 0,0);
        Datetime dateTwo = new Datetime("Tuesday", 0,0);
        List<Datetime> days = new ArrayList<Datetime>(Arrays.asList(dateOne, dateTwo));
        QueryAvailabilityRequest request = new QueryAvailabilityRequest("badminton court", days);
        ByteBuffer bb = ByteBuffer.allocate(2000);
        Serializer.serializeObject(request, bb);
        bb.flip();
        Object deserialisedRequest = Deserializer.deserializeObject(bb);
        assert deserialisedRequest != null;
        assertEquals(request.days, ((QueryAvailabilityRequest) deserialisedRequest).days);
        assertEquals(request.facilityName, ((QueryAvailabilityRequest) deserialisedRequest).facilityName);
    }

    @Test
    void deserializeBookFacilityRequest() {
        Datetime startTime = new Datetime("Monday", 13,30);
        Datetime endTime = new Datetime("Monday", 14,30);
        BookFacilityRequest request = new BookFacilityRequest("badminton court", startTime, endTime);
        ByteBuffer bb = ByteBuffer.allocate(2000);
        Serializer.serializeObject(request, bb);
        bb.flip();
        Object deserialisedRequest = Deserializer.deserializeObject(bb);
        assert deserialisedRequest != null;
        assertEquals(request.facilityName, ((BookFacilityRequest) deserialisedRequest).facilityName);
        assertEquals(request.startTime.day, ((BookFacilityRequest) deserialisedRequest).startTime.day);
        assertEquals(request.startTime.hour, ((BookFacilityRequest) deserialisedRequest).startTime.hour);
        assertEquals(request.startTime.minute, ((BookFacilityRequest) deserialisedRequest).startTime.minute);
        assertEquals(request.endTime.day, ((BookFacilityRequest) deserialisedRequest).endTime.day);
        assertEquals(request.endTime.hour, ((BookFacilityRequest) deserialisedRequest).endTime.hour);
        assertEquals(request.endTime.minute, ((BookFacilityRequest) deserialisedRequest).endTime.minute);
    }

    @Test
    void deserializeTestRequest() {
        TestRequest request = new TestRequest();
        ByteBuffer bb = ByteBuffer.allocate(2000);
        Serializer.serializeObject(request, bb);
        bb.flip();
        Object deserialisedRequest = Deserializer.deserializeObject(bb);
        assert deserialisedRequest != null;
        assertEquals(request.testInt, ((TestRequest) deserialisedRequest).testInt);
        assertEquals(request.testString, ((TestRequest) deserialisedRequest).testString);
        assertEquals(request.testList, ((TestRequest) deserialisedRequest).testList);
        assertEquals(request.testNestedList, ((TestRequest) deserialisedRequest).testNestedList);

    }
    @Test
    void deserialiseDatetime(){
        Datetime expectedDate = new Datetime("Monday", 13, 30);
        ByteBuffer bb = ByteBuffer.allocate(2000);
        Serializer.serializeObject(expectedDate, bb);
        bb.flip();
        Object actualDate = Deserializer.deserializeObject(bb);
        assertEquals(((Datetime) actualDate).day, expectedDate.day);
    }
}