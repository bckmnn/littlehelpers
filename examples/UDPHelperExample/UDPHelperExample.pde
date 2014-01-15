import com.bckmnn.udp.*;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.SocketAddress;

/**
 * PRESS a for broadcast message to all
 * PRESS l for message to local
 **/

UDPHelper udp;
SocketAddress local, all;

void setup() {
  udp = new UDPHelper(this);
  udp.setLocalPort(13370);
  udp.startListening();

  try {
    local = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 13370);
    all = new InetSocketAddress(InetAddress.getByName("255.255.255.255"), 13370);
  } 
  catch(Exception e) {
    e.printStackTrace();
  }
}

void draw() {
}

public void onUdpMessageRecieved(SocketAddress client, byte[] message) {
  String messageString = UDPHelper.stringFromBytes(message);
  println(client + " sent you this message: " + messageString);
}

void keyPressed() {
  if (key == 'a') {
    udp.sendMessage(UDPHelper.bytesFromString("message to all"), all);
  }
  else if (key == 'l') {
    udp.sendMessage(UDPHelper.bytesFromString("message to self"), local);
  }
}

