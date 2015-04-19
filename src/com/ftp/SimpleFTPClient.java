package com.ftp;

import java.net.DatagramSocket;
import java.net.SocketException;

public class SimpleFTPClient {

	public static void main(String[] args) {
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
		
		DatagramSocket udpSocket;
		try {
			udpSocket = new DatagramSocket();
			
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}
