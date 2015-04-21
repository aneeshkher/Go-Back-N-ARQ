package com.ftp;

import java.io.FileOutputStream;
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

		TestSimpleFTPServerData data1 = new TestSimpleFTPServerData();
		FileOutputStream file = new FileOutputStream(fileName, true);
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
				System.out.println("Receiving packet");
				String filePacket = new String(receivedPacket.getData());
				int dataLength = filePacket.length();
				int replyPort = receivedPacket.getPort();
				InetAddress replyAddress = receivedPacket.getAddress();
				String sequenceNumberString = (filePacket.substring(32, 63))
						.substring(1);
				int sequenceNumberInt = Integer.parseInt(sequenceNumberString,
						2);
				if (sequenceNumberInt == sequenceNumber) {
					String dataString = filePacket
							.substring(64, dataLength - 1);
					data1.writeToFile(file, dataString);
					String sendString = data1.getSendPacket(sequenceNumber);
					byte[] sendBytes = sendString.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendBytes,
							sendBytes.length, replyAddress, replyPort);
					udpServerSocket.send(sendPacket);
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
