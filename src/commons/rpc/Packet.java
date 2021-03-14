package commons.rpc;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Packet {
    int requestID;
    int datagramNum;
    int totalDatagramPackets;
    int messageSize;
    InetAddress senderAddress;
    int senderPort;
    ByteBuffer messageBuffer;

    /**
     * Class to log the info of a UDP packet
     * @param datagramNum datagram number in a message (useful when totalDatagramPackets > 1)
     * @param totalDatagramPackets total datagrams needed to send a message
     * @param messageSize length of message in this packet
     * @param senderAddress IP address of sender
     * @param senderPort port of Sender
     * @param messageBuffer Bytebuffer containing the message data
     */
    public Packet(int requestID, int datagramNum, int totalDatagramPackets, int messageSize,
                  InetAddress senderAddress, int senderPort, ByteBuffer messageBuffer) {
        this.requestID = requestID;
        this.datagramNum = datagramNum;
        this.totalDatagramPackets = totalDatagramPackets;
        this.messageSize = messageSize;
        this.senderAddress = senderAddress;
        this.senderPort = senderPort;
        this.messageBuffer = messageBuffer;
    }
}
