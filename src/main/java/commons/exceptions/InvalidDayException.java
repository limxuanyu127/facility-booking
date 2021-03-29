package commons.exceptions;

public class InvalidDayException extends Exception {
    public InvalidDayException() {
        super("Day of week should be one of the following: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday");
    }
}
