package assignment1;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Client {

  DatagramPacket sendPacket, receivePacket;
  DatagramSocket sendReceiveSocket;

  public Client()
  {
    try {
      // Construct a datagram socket and bind it to any available
      // port on the local host machine. This socket will be used to
      // send and receive UDP Datagram packets.
      sendReceiveSocket = new DatagramSocket();
    } catch (SocketException se) {   // Can't create the socket.
      se.printStackTrace();
      System.exit(1);
    }
  }

  byte[] concat(byte[]...arrays) {
    // Determine the length of the result array
    int totalLength = 0;
    for (int i = 0; i < arrays.length; i++)
    {
      totalLength += arrays[i].length;
    }

    // create the result array
    byte[] result = new byte[totalLength];

    // copy the source arrays into the result array
    int currentIndex = 0;
    for (int i = 0; i < arrays.length; i++) {
      System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
      currentIndex += arrays[i].length;
    }

    return result;
  }

  public byte[] getReadMessage() {
    String filename = "FILENAME";
    String mode = "MODE";
    byte prefix[] = new byte[]{(byte) 0, (byte) 1};
    byte postfix[] = new byte[]{(byte) 0};
    byte lineBreak[] = new byte[]{(byte) 0};
    return concat(prefix, filename.getBytes(), lineBreak, mode.getBytes(), postfix);
  }

  public byte[] getWriteMessage() {
    String filename = "FILENAME";
    String mode = "MODE";
    byte prefix[] = new byte[]{(byte) 0, (byte) 2};
    byte postfix[] = new byte[]{(byte) 0};
    byte lineBreak[] = new byte[]{(byte) 0};
    return concat(prefix, filename.getBytes(), lineBreak, mode.getBytes(), postfix);
  }

  public byte[] getInvalidMessage() {
    String filename = "FILENAME";
    String mode = "MODE";
    byte prefix[] = new byte[]{(byte) 1, (byte) 3};
    byte postfix[] = new byte[]{(byte) 2};
    byte lineBreak[] = new byte[]{(byte) 0};
    return concat(prefix, filename.getBytes(), mode.getBytes(), postfix);
  }

  public void sendAndReceive(char messageType)
  {
    byte msg[];
    switch (messageType) {
      case 'r': msg = getReadMessage();
                break;
      case 'w': msg = getWriteMessage();
                break;
      default: msg = getInvalidMessage();
               break;
    }
    // Prepare a DatagramPacket and send it via sendReceiveSocket
    // to port 5000 on the destination host.

    // Construct a datagram packet that is to be sent to a specified port
    // on a specified host.
    // The arguments are:
    //  msg - the message contained in the packet (the byte array)
    //  msg.length - the length of the byte array
    //  InetAddress.getLocalHost() - the Internet address of the
    //     destination host.
    //     In this example, we want the destination to be the same as
    //     the source (i.e., we want to run the client and server on the
    //     same computer). InetAddress.getLocalHost() returns the Internet
    //     address of the local host.
    //  5000 - the destination port number on the destination host.
    try {
      sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), 23);
    } catch (UnknownHostException e) {
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println("Client: Sending packet:");
    System.out.println("To host: " + sendPacket.getAddress());
    System.out.println("Destination host port: " + sendPacket.getPort());
    int len = sendPacket.getLength();
    System.out.println("Length: " + len);
    System.out.print("Containing: ");
    System.out.println(new String(sendPacket.getData(),0,len)); // or could print "s"

    // Send the datagram packet to the server via the send/receive socket.

    try {
      sendReceiveSocket.send(sendPacket);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println("Client: Packet sent.\n");

    // Construct a DatagramPacket for receiving packets up
    // to 100 bytes long (the length of the byte array).

    byte data[] = new byte[100];
    receivePacket = new DatagramPacket(data, data.length);

    try {
      // Block until a datagram is received via sendReceiveSocket.
      sendReceiveSocket.receive(receivePacket);
    } catch(IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // Process the received datagram.
    System.out.println("Client: Packet received:");
    System.out.println("From host: " + receivePacket.getAddress());
    System.out.println("Host port: " + receivePacket.getPort());
    len = receivePacket.getLength();
    System.out.println("Length: " + len);
    System.out.print("Containing: ");

    // Form a String from the byte array.
    String received = new String(data,0,len);
    System.out.println(received);

    // We're finished, so close the socket.
    sendReceiveSocket.close();
  }

  public static void main(String args[]) {
    Client c = new Client();
    for(int i=0;i<11;i++) {
      char mType = i%2 == 0 ? 'w' : 'r';
      c.sendAndReceive(mType);
    }
    c.sendAndReceive('i');
  }
}
