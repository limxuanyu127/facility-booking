package commons.exceptions;

public class LostPacketError extends Exception {
    public LostPacketError(){
        super("Packet Lost in transmission");
    }
}
