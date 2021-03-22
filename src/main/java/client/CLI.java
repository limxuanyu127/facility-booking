package client;

import java.net.InetAddress;
import java.util.Scanner;
import client.ServiceManager;
import commons.rpc.ClientCommunicator;

public class CLI {
    public static void run(ClientCommunicator router, InetAddress serverAddress, int serverPort) {
        ServiceManager serviceManager = new ServiceManager(router, serverAddress, serverPort);
        String startUp = "Hello!! Welcome to the facility booking system. These are the facilities you can book: \n\n" +
                "Badminton Court \n" +
                "...";
        System.out.println(startUp);
        String menu = "Please enter a number.\n\n" +
                "1: Query for a facility's availability \n" +
                "2: Book a facility \n" +
                "3: Offset booking \n" +
                "4: Update booking \n" +
                "5: Delete booking \n" +
                "6: Register interest for a facility \n";
        System.out.println(menu);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input!! Please enter a number from 1 to 6.");
                continue;
            }
            switch (choice) {
                case 1:
                    serviceManager.queryAvailability();
                    break;
                case 2:
                    serviceManager.bookFacility();
                    break;
                case 3:
                    serviceManager.offsetBooking();
                    break;
                case 4:
                    serviceManager.updateBooking();
                    break;
                case 5:
                    serviceManager.deleteBooking();
                    break;
                case 6:
                    serviceManager.registerInterest();
                    break;
                default:
                    System.out.println("Invalid number!! Please enter a number from 1 to 6.");
                    break;
            }
        }
    }
}
