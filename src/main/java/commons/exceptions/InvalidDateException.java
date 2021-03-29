package commons.exceptions;

public class InvalidDateException extends Exception {
    public InvalidDateException() {
        super("Date format provided is invalid. Please provide date in the following format: [D/HH/MM] where D = day of the week");
    }
}
