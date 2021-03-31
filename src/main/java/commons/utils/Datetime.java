package commons.utils;

public class Datetime {
    public Day day;
    public Integer hour;
    public Integer minute;

    public Datetime(){}

    public Datetime(Day day, int hour, int minute) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }
}
