package com.ftp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.UnsupportedCharsetException;

import sun.security.krb5.internal.SeqNumber;

public class SimpleFTPServer {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 3) {
			System.out.println("Error:");
			System.out.println("Usage: simple_ftp_server"
					+ " <port_no> <file_name> <probability>");
			System.exit(1);
		}
		DatagramSocket udpServerSocket = null;
		String fileName = args[1];
		double probability = Double.parseDouble(args[2]);
		int listenPort = Integer.parseInt(args[0]);
		byte[] process = new byte[8192];


		FileOutputStream file = new FileOutputStream(fileName,true);
		byte[] receivedBytes = new byte[8192];
		DatagramPacket receivedPacket = new DatagramPacket(receivedBytes,
				receivedBytes.length);
		int sequenceNumber = 0;
		int firstTime = 1;
		
		udpServerSocket = new DatagramSocket(listenPort);
		
		while (true) {
			double r = Math.random();
			System.out.println("Before receiving");
			udpServerSocket.receive(receivedPacket);
			String filePacket = new String(receivedPacket.getData());
			System.out.println("Received packet from client: ");
			System.out.println(filePacket);
			if (r <= probability) {
				System.out.println("Packet Loss due to probability " 
						+ probability);
				continue;
			} else if (!computeChecksum()) {
				udpServerSocket.receive(receivedPacket);
			} else {
				System.out.println("Receiving packet");
				//int dataLength = receivedPacket.getLength();
				//process = receivedPacket.getData();
				
				int dataLength = filePacket.length();
				int replyPort = receivedPacket.getPort();
				InetAddress replyAddress = receivedPacket.getAddress();
				String seqNumberString = "";
				for (int i = 32; i < 64; i++) {
					seqNumberString.concat(Character.toString(filePacket.charAt(i)));
				}
				//String seqNumberString = seqNumberByte.toString();
				seqNumberString = seqNumberString.substring(1);
				int seqNumberInt = Integer.parseInt(seqNumberString, 2);
				if (seqNumberInt == sequenceNumber) {
					//byte[] dataBytes = new byte[dataLength - 64];
					String dataString = "";
					for (int i = 64; i < dataLength; i++) {
						dataString.concat(Character.toString(filePacket.charAt(i)));
					}
					byte[] dataBytes = dataString.getBytes();
					file.write(dataBytes);
					int ackNumber = seqNumberInt + dataBytes.length;
					String sendString = "10101010101010100000000000000000";
					sendString.concat(Integer.toBinaryString(ackNumber));
					byte[] sendBytes = sendString.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, replyAddress, replyPort);
					udpServerSocket.send(sendPacket);
					
				} else {
					
				}
			}
		}

		// f1.openAndReadFile("AppliedCompanies.txt");
		// testBytes();
	}

	public static boolean computeChecksum() {
		return true;
	}

	public static void testBytes() {
		/*
		 * String testString = "My Name is Aneesh Kher." +
		 * " Here are some weird chars ------- *&%&%" + "$(()!)*#^&$#";
		 */
		String testString = "abcdefghijklmnopqrstuvwxyz"
				+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789"
				+ ")!@#$%^&*(_+=-;:|,\"<>.?/";
		System.out.println("Original String:");
		System.out.println(testString);
		System.out.println("String Length: " + testString.length());
		try {
			byte[] bytes = testString.getBytes();
			// System.out.println(bytes);
			System.out.println("Length is: " + bytes.length);
			System.out.println("Decoding -> ");
			for (byte b : bytes) {
				// System.out.println("First byte: " + b);
				String s = new String(new byte[] { b });
				System.out.println(b + " -> " + s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// System.out.println("Worked");
		}

	}

}
