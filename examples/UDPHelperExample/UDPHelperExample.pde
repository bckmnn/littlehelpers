import com.bckmnn.udpdiscover.*;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.SocketAddress;

UDPHelper udp;

void setup(){
  udp = new UDPHelper(this);
  udp.startListening();
}

void draw(){
  udp.run();
}

public void onUdpMessageRecieved(SocketAddress client, byte[] message){
  String messageString = UDPHelper.stringFromBytes(message);
  println(client + " sent you this message: " + messageString);
}

void keyPressed(){
  if(key == 'a'){
    try{
      SocketAddress all = new InetSocketAddress(InetAddress.getByName("255.255.255.255"),13370);
      udp.sendMessage(UDPHelper.bytesFromString("message to all"),all);
    } catch(Exception e){
      e.printStackTrace();
    }
  }
}


