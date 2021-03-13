package commons.requests;

public class TestRequest extends Request{
    String testString;
    int testInt;

    public TestRequest() {
        this.name = "TestRequest";
        this.testString = "this is a test string";
        this.testInt = 1234;
    }
}
