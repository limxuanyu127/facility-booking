package server;

//import commons.rpc.Communicator;
import javafx.util.Pair;
import server.entities.*;
import server.managers.*;

import java.time.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class Main {
    public static void main(String[] args) {

        Hashtable facilTable = new Hashtable<>();
        BookingManager bookingManager = new BookingManager();



    }
}


    //    int port = 5005;
    //    int packetSize = 512;
    //    byte[] buffer = new byte[packetSize];
//        DatagramPacket message = new DatagramPacket(buffer, buffer.length);
//        DatagramSocket sock;
//
//        try{
//            sock = new DatagramSocket(port);
//            System.out.println("main.java.server listening");
//            sock.receive(message);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
