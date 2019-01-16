package assignment1;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Server {
  // SimpleEchoServer.java
  // This class is the server side of a simple echo server based on
  // UDP/IP. The server receives from a client a packet containing a character
  // string, then echoes the string back to the client.
  // Last edited January 9th, 2016

  DatagramPacket sendPacket, receivePacket;
  DatagramSocket sendSocket, receiveSocket;
  byte readResponse[] = {0, 3, 0, 1};
  byte writeResponse[] = {0, 4, 0, 0};

  public Server() {
    try {
      // Construct a datagram socket and bind it to any available
      // port on the local host machine. This socket will be used to
      // send UDP Datagram packets.
      sendSocket = new DatagramSocket();

      // Construct a datagram socket and bind it to port 5000
      // on the local host machine. This socket will be used to
      // receive UDP Datagram packets.
      receiveSocket = new DatagramSocket(69);

      // to test socket timeout (2 seconds)
      // receiveSocket.setSoTimeout(2000);
    } catch (SocketException se) {
      se.printStackTrace();
      System.exit(1);
    }
  }

  public void receive(byte[] data) {
    receivePacket = new DatagramPacket(data, data.length);
    System.out.println("Server: Waiting for Packet.\n");

    // Block until a datagram packet is received from receiveSocket.
    try {
      receiveSocket.receive(receivePacket);
    } catch (IOException e) {
      System.out.print("IO Exception: likely:");
      System.out.println("Receive Socket Timed Out.\n" + e);
      e.printStackTrace();
      System.exit(1);
    }

    // Process the received datagram.
    System.out.println("Server: Packet received:");
    System.out.println("From host: " + receivePacket.getAddress());
    System.out.println("Host port: " + receivePacket.getPort());
    int len = receivePacket.getLength();
    System.out.println("Length: " + len);
    String received = new String(data, 0, len);
    System.out.print("Containing: " + received + "\n");

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void send() {
    System.out.println("Server: Sending packet:");
    System.out.println("To host: " + sendPacket.getAddress());
    System.out.println("Destination host port: " + sendPacket.getPort());
    int len = sendPacket.getLength();
    System.out.println("Length: " + len);
    String str  = new String(sendPacket.getData(), 0, len);
    System.out.println("Containing: " + str);
    System.out.println("Containing (bytes): " + Arrays.toString(sendPacket.getData()));

    // Send the datagram packet to the client via the send socket.
    try {
      sendSocket.send(sendPacket);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println("Server: packet sent");
  }

  // validate checks that the packet follows the following format:
  // - 0 1 or 0 2
  // - some text
  // - 0
  // - some text
  // - 0
  // - nothing else after that!
  public Boolean validate(byte[] data, Boolean isRead) {
    // Validate read/write header
    int readOrWrite = isRead ? (byte) 1 : (byte) 2;
    if (data[0] != (byte) 0 && data[1] != readOrWrite) {
      return false;
    }

    // Validate text
    // Text should start at position 2 becase the read/write header is 2 bytes
    int counter = 2;
    while (data[counter] != (byte) 0) {
      counter++;
      if (counter >= data.length) {
        return false;
      }
    }

    // Validate that 0 exists inbetween text
    counter++;
    if (counter >= data.length) {
      return false;
    }


    // validate that text exists
    while (data[counter] != (byte) 0) {
      counter++;
      if (counter >= data.length) {
        return false;
      }
    }

    // validate that the rest is 0
    while (data[counter] == (byte) 0) {
      counter++;
      if (counter >= data.length) {
        return true;
      }
    }
    return false;
  }

  public void run() {
    try  {
      while(true) {
        byte data[] = new byte[100];
        receive(data);

        if (validate(data, true)) {
          sendPacket = new DatagramPacket(readResponse, 4, receivePacket.getAddress(), receivePacket.getPort());
        } else if (validate(data, false)) {
          sendPacket = new DatagramPacket(writeResponse, 4, receivePacket.getAddress(), receivePacket.getPort());
        } else throw new Exception("Received invalid packet");

        send();
      }
    } catch (Exception e) {
      e.printStackTrace();
      close();
    }
  }

  public void close() {
    sendSocket.close();
    receiveSocket.close();
  }

  public static void main(String args[]) {
    Server s = new Server();
    s.run();
  }
}
