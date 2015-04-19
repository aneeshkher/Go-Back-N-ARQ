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
		String fileName = args[0];
		double probability = Double.parseDouble(args[1]);
		int listenPort = Integer.parseInt(args[2]);
		byte[] process = new byte[8192];

		DatagramSocket udpServerSocket;
		FileOutputStream file = new FileOutputStream(fileName,true);
		byte[] receivedBytes = new byte[8192];
		DatagramPacket receivedPacket = new DatagramPacket(receivedBytes,
				receivedBytes.length);
		int sequenceNumber = 0;
		int firstTime = 1;
		udpServerSocket = new DatagramSocket(listenPort);

		while (true) {
			double r = Math.random();
			udpServerSocket.receive(receivedPacket);
			if (r <= probability) {
				continue;
			} else if (!computeChecksum()) {
				udpServerSocket.receive(receivedPacket);
			} else {
				int dataLength = receivedPacket.getLength();
				process = receivedPacket.getData();
				int replyPort = receivedPacket.getPort();
				InetAddress replyAddress = receivedPacket.getAddress();

				byte[] seqNumberByte = new byte[32];
				for (int i = 32; i < 63; i++) {
					seqNumberByte[i] = process[i];
				}
				String seqNumberString = seqNumberByte.toString();
				int seqNumberInt = Integer.parseInt(seqNumberString, 2);
				if (seqNumberInt == sequenceNumber) {
					byte[] dataBytes = new byte[dataLength - 64];
					for (int i = 64; i < dataLength; i++) {
						dataBytes[i] = process[i];
					}
					file.write(dataBytes);
					int ackNumber = seqNumberInt + dataBytes.length;
					String sendString = "10101010101010100000000000000000";
					sendString.concat(Integer.toString(ackNumber));
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
