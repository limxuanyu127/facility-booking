package commons.requests;

public class Request {
    public String name;
    public Request() {
        this.name = "test";
        System.out.println(this.name + " Request created");
    }
}
