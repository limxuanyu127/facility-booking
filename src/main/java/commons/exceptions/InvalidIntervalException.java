package commons.exceptions;

public class InvalidIntervalException extends Exception {
    public InvalidIntervalException(){
        super("End datetime is not after start datetime");
    }
}
