/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package com.bckmnn.udpdiscover;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Enumeration;

import processing.core.PApplet;

/**
 * @example UDPHelperExample
 */

public class UDPHelper {
	
	public final static String VERSION = "##library.prettyVersion##";
	
	private DatagramChannel channel;
	private DatagramSocket socket;
	private SocketAddress address;
	
	public final int messageBufferSize = 512;
	private ByteBuffer messageBuffer;
	
	private int localPort = 13370;

	private PApplet p;

	private Method onMessageRecievedMethod;

    public UDPHelper(PApplet theParent) {
    	messageBuffer = ByteBuffer.allocate(messageBufferSize);
    	
        p = theParent;
        p.registerMethod("dispose", this);
        
        onMessageRecievedMethod = null;
        
        // check to see if the host applet implements
        // public void onUdpMessageRecieved()
        try {
          onMessageRecievedMethod =
            p.getClass().getMethod("onUdpMessageRecieved",new Class[]{SocketAddress.class,byte[].class});
        }
        catch (Exception e) {
            // no such method
            System.out.println("could not find method public void onUdpMessageRecieved (SocketAddress client, byte[] message)");
        }
        
	   
        try {
            System.out.println("list of network interfaces:");
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            if(en != null){
            	for (Enumeration<NetworkInterface> e = en; e.hasMoreElements();) {
                    NetworkInterface intf = e.nextElement();
                    System.out.println("    " + intf.getName() + " " + intf.getDisplayName());
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                      System.out.println("        " + enumIpAddr.nextElement().toString());
                    }
                  }
            }
          } catch (SocketException e) {
            System.out.println(" (error retrieving network interface list)");
          }
    }
	
	public void startListening(){
		try {
			channel = DatagramChannel.open();
			channel.configureBlocking(false);
		    socket = channel.socket();
		    socket.setBroadcast(true);
		    address = new InetSocketAddress(localPort);
		    socket.bind(address);
		} catch (Exception e) {
			System.err.println("[UDPHelper] ERROR listening at port "+localPort);
			e.printStackTrace();
			return;
		}
	}
	
	public void sendMessage(byte[] message,SocketAddress client){
		if(channel != null && channel.isOpen()){
			try {
				ByteBuffer buffer = ByteBuffer.wrap(message);
				channel.send( buffer, client);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static byte[] bytesFromString(String string){
		return string.getBytes();
	}
	
	public static String stringFromBytes(byte[] bytes){
		String string = "";
		try {
			string = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return string;
	}
	
	
	public void endListening(){
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		channel = null;
		socket  = null;
	}
	
	
	public void dispose() {
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		if(channel.isOpen()){
			try {
				messageBuffer.clear();
				SocketAddress client = channel.receive(messageBuffer);
				messageBuffer.flip();
				if(messageBuffer.hasRemaining()){
					byte[] bytes = new byte[messageBuffer.remaining()];
		        	for (int i = 0; i < bytes.length; i++) {
		        		bytes[i] = messageBuffer.get(i);
					}
		        	if(onMessageRecievedMethod != null){
		        		onMessageRecievedMethod.invoke(p, client, bytes);
		        	}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public int getLocalPort() {
		return localPort;
	}
	
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	
	
	/**
	 * returns the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}

}
