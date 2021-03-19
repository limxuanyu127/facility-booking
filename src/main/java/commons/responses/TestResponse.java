package commons.responses;

public class TestResponse extends Response {
    String testString;
    int testInt;

    public TestResponse() {
        this.name = "TestResponse";
        this.testString = "this is a test string";
        this.testInt = 1234;
    }
}
