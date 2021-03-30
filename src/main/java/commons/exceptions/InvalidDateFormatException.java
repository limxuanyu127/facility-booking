package commons.exceptions;

public class InvalidDateFormatException extends Exception {
    public InvalidDateFormatException() {
        super("Date format provided is invalid. Please provide date in the following format: [D/HH/MM] where D = day of the week");
    }
}
