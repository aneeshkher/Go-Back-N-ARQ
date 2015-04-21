package com.ftp;

import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TestSimpleFTPClient {

	static int acknowledged;
	static int outstanding;
	static int sentNotAcknowledged;
	static DatagramSocket udpClientSocket = null;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*
		 * try { FileInputStream fileStream = new
		 * FileInputStream("AppliedCompanies.txt");
		 * 
		 * //FileOperations.testReader("AppliedCompanies.txt"); String first =
		 * FileOperations.testReader(fileStream); String second =
		 * FileOperations.testReader(fileStream); System.out.println(first);
		 * System.out.println(second); } catch (Exception e1) {
		 * 
		 * }
		 */

		if (args.length != 5) {
			System.out.println("Please call the program with proper arguments");
			System.exit(1);
		}
		String serverHostName = args[0];
		int serverPort = Integer.parseInt(args[1]);
		String fileName = args[2];
		int windowSize = Integer.parseInt(args[3]);
		int MSS = Integer.parseInt(args[4]);

		TestSimpleFTPClientData data = new TestSimpleFTPClientData();
		FileInputStream fileStream = new FileInputStream(fileName);

		udpClientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(serverHostName);
		Long fileLength;
		fileLength = data.getFileLength(fileName);
		int fileLengthBytes = fileLength.intValue();

		acknowledged = 0;
		outstanding = 0;
		sentNotAcknowledged = 0;
		int sequenceNumber = 0;

		while (true) { // Outer loop. Keep on sending
			while (outstanding <= windowSize) { // Inner loop. Keep doing this
												// as long as window permits
				String dataString = data.getDataPacket(fileStream, MSS,
						sequenceNumber);
				TestSimpleFTPClientData.window.put(0, sequenceNumber);

				byte[] sendDataBytes = dataString.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendDataBytes,
						sendDataBytes.length, IPAddress, serverPort);

				synchronized (data) {
					udpClientSocket.send(sendPacket);
					TestSimpleFTPClientData.unacknowledged.put(sequenceNumber,
							dataString);
					sentNotAcknowledged++;
					outstanding = sentNotAcknowledged - acknowledged;

				} // End of synchronized

			} // End of while for outstanding <= window size

		} // End of while(true)

	} // End of static void main

} // End of class
