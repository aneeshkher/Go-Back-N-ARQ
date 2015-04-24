package com.ftp;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TestSimpleFTPServer {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		if (args.length != 3) {
			System.out.println("Error:");
			System.out.println("Usage: simple_ftp_server"
					+ " <port_no> <file_name> <probability>");
			System.exit(1);
		}
		int listenPort = Integer.parseInt(args[0]);
		String fileName = args[1];
		double probability = Double.parseDouble(args[2]);

		int MSS = 500;

		TestSimpleFTPServerData data1 = new TestSimpleFTPServerData();
		
		//FileOutputStream file = new FileOutputStream(fileName, true);
		//BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
		FileWriter file = new FileWriter(fileName, true);
		//PrintWriter outFile = new PrintWriter(fileName, "UTF-8");
		
		
		DatagramSocket udpServerSocket = null;
		byte[] receivedBytes = new byte[8192];
		DatagramPacket receivedPacket = new DatagramPacket(receivedBytes,
				receivedBytes.length);
		int sequenceNumber = 0;
		udpServerSocket = new DatagramSocket(listenPort);
		while (true) {
			double r = Math.random();
			System.out.println("Before receiving the packet");
			udpServerSocket.receive(receivedPacket);
			if (r <= probability) {
				System.out.println("Packet Loss due to probability "
						+ probability);
				continue;
			} else if (!computeChecksum()) {
				System.out.println("Checksum failed. Dropping packet");
				continue;
			} else {
				String filePacket = new String(receivedPacket.getData());
				System.out.print("Received Packet: ");

				//int dataLength = filePacket.length();
				int replyPort = receivedPacket.getPort();
				InetAddress replyAddress = receivedPacket.getAddress();
				String sequenceNumberString = (filePacket.substring(32, 64))
						.substring(1);

				int sequenceNumberInt = Integer.parseInt(sequenceNumberString,
						2);
				System.out.print("Extracted sequence number as: "
						+ sequenceNumberInt + ". ");
				if (sequenceNumberInt == sequenceNumber) {
					String numberOfBytes = filePacket.substring(MSS+64, MSS+67);
					System.out.println("Number of bytes: " + numberOfBytes);
					if (Integer.parseInt(numberOfBytes) < MSS) {
						String dataString = filePacket
								.substring(64, Integer.parseInt(numberOfBytes) + 64);
						System.out.println("Writing to file: " + dataString.length());
						file.write(dataString);
						file.flush();
						
						file.close();
					} else {
						String dataString = filePacket
								.substring(64, MSS + 64);
						System.out.println("Writing to file: " + dataString.length());
						file.write(dataString);
						file.flush();
					}
					//data1.writeToFile(file, dataString);
					//outFile.append(dataString);
					//writer.write(dataString);
					
					
					String sendString = data1.getSendPacket(sequenceNumber);
					System.out.println("Sending ACK packet as: " + sendString);
					byte[] sendBytes = sendString.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendBytes,
							sendBytes.length, replyAddress, replyPort);
					udpServerSocket.send(sendPacket);
					sequenceNumber += MSS;
					System.out.println("Next expected sequence number is: "
							+ sequenceNumber);
					System.out.println("");
				} else {
					System.out.println("Wrong sequence received");
					continue;
				}
			}
		}
	}

	public static boolean computeChecksum() {
		return true;
	}

}
