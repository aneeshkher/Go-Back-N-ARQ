package com.ftp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class SimpleFTPClient {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(args.length);
		if (args.length != 5) {
			System.out.println("Please call the program with proper arguments");
			System.exit(1);
		}

		String serverHostName = args[0];
		int serverPort = Integer.parseInt(args[1]);
		String fileName = args[2];
		int windowSize = Integer.parseInt(args[3]);
		int MSS = Integer.parseInt(args[4]);

		DatagramSocket udpClientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(serverHostName);
		byte[] sendDataBytes = "Hope this is received".getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendDataBytes, sendDataBytes.length
				, IPAddress, serverPort);
		udpClientSocket.send(sendPacket);
	}
}
