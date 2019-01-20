import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Intermediate {

  DatagramPacket sendPacket, receivePacket;
  DatagramSocket sendAndReceive, receiveSocket;

  public Intermediate() {
    try {
      // Construct a datagram socket and bind it to any available
      // port on the local host machine. This socket will be used to
      // send UDP Datagram packets.
      sendAndReceive = new DatagramSocket();

      // Construct a datagram socket and bind it to port 23
      // on the local host machine. This socket will be used to
      // receive UDP Datagram packets.
      receiveSocket = new DatagramSocket(23);

      // to test socket timeout (2 seconds)
      // receiveSocket.setSoTimeout(2000);
    } catch (SocketException se) {
      se.printStackTrace();
      System.exit(1);
    }
  }

  public void receive(byte[] data, DatagramSocket socket) {
    // Construct a DatagramPacket for receiving packets up
    // to 100 bytes long (the length of the byte array).

    receivePacket = new DatagramPacket(data, data.length);
    System.out.println("Proxy: Waiting for Packet.\n");

    // Block until a datagram packet is received from receiveSocket.
    try {
      socket.receive(receivePacket);
    } catch (IOException e) {
      System.out.print("IO Exception: likely:");
      System.out.println("Receive Socket Timed Out.\n" + e);
      e.printStackTrace();
      System.exit(1);
    }

    // Process the received datagram.
    System.out.println("Proxy: Packet received:");
    System.out.println("From host: " + receivePacket.getAddress());
    System.out.println("Host port: " + receivePacket.getPort());
    int len = receivePacket.getLength();
    System.out.println("Length: " + len);
    // Form a String from the byte array.
    String received = new String(data, 0, len);
    System.out.print("Containing: " + received + "\n");
  }

  public void send() {
    System.out.println("Proxy: Sending packet:");
    System.out.println("To host: " + sendPacket.getAddress());
    System.out.println("Destination host port: " + sendPacket.getPort());
    int len = sendPacket.getLength();
    System.out.println("Length: " + len);
    String str  = new String(sendPacket.getData(), 0, len);
    System.out.println("Containing: " + str);
    System.out.println("Containing (bytes): " + Arrays.toString(sendPacket.getData()));
    // or (as we should be sending back the same thing)
    // System.out.println(received);

    // Send the datagram packet to the client via the send socket.
    try {
      sendAndReceive.send(sendPacket);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println("Proxy: packet sent");
  }

  public void receiveAndForward() {
    byte data[] = new byte[100];
    receive(data, receiveSocket);

    InetAddress originAddress = receivePacket.getAddress();
    int originPort = receivePacket.getPort();

    // create new data packet but addressed to upstream
    try {
      sendPacket = new DatagramPacket(data, receivePacket.getLength(), InetAddress.getLocalHost(), 69);
    } catch (UnknownHostException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // forward
    send();

    // get upstream response
    data = new byte[100];
    receive(data, sendAndReceive);

    sendPacket = new DatagramPacket(data, receivePacket.getLength(), originAddress, originPort);
    send();
  }

  public void close() {
    sendAndReceive.close();
    receiveSocket.close();
  }

  public void run() {
    try  {
      while(true) {
        receiveAndForward();
      }
    } catch (Exception e) {
      close();
    }
  }

  public static void main(String args[]) {
    Intermediate proxy = new Intermediate();
    proxy.run();
  }
}
