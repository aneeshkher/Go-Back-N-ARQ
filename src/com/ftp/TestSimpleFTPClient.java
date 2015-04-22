package com.ftp;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;

public class TestSimpleFTPClient {

	static DatagramSocket udpClientSocket = null;
	static int MSS;
	static InetAddress IPAddress;
	static int serverPort;

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
		serverPort = Integer.parseInt(args[1]);
		String fileName = args[2];
		int windowSize = Integer.parseInt(args[3]);
		MSS = Integer.parseInt(args[4]);

		TestSimpleFTPClientData data = new TestSimpleFTPClientData();
		FileInputStream fileStream = new FileInputStream(fileName);

		udpClientSocket = new DatagramSocket();
		IPAddress = InetAddress.getByName(serverHostName);
		// Long fileLength;
		// fileLength = data.getFileLength(fileName);
		// int fileLengthBytes = fileLength.intValue();

		TestSimpleFTPClientData.acknowledged = 0;
		TestSimpleFTPClientData.outstanding = 0;
		TestSimpleFTPClientData.sentNotAcknowledged = 0;
		int sequenceNumber = 0;
		int packetSize = MSS + 64;
		int last = 0;

		ReceiveRunnable receiveThread = new ReceiveRunnable(udpClientSocket);
		Thread thread = new Thread(receiveThread, "receive");
		thread.start();

		while (true) { // Outer loop. Keep on sending
			while (TestSimpleFTPClientData.outstanding <= windowSize) {

				String dataString = data.getDataPacket(fileStream, MSS,
						sequenceNumber);
				if (dataString.length() < packetSize) {
					last = 1;
				}
				System.out.println("Sending data from client: ");
				System.out.println(dataString);
				TestSimpleFTPClientData.window.put(0, sequenceNumber);

				byte[] sendDataBytes = dataString.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendDataBytes,
						sendDataBytes.length, IPAddress, serverPort);

				// synchronized (data)
				TestSimpleFTPClientData.lock.lock();
				udpClientSocket.send(sendPacket);
				TestSimpleFTPClientData.unacknowledged.put(sequenceNumber,
						dataString);
				TestSimpleFTPClientData.sentNotAcknowledged++;
				TestSimpleFTPClientData.outstanding = TestSimpleFTPClientData.sentNotAcknowledged
						- TestSimpleFTPClientData.acknowledged;
				TestSimpleFTPClientData.lock.unlock();
				TimerPacket p1 = new TimerPacket(sequenceNumber);
				Timer t1 = new Timer();
				TestSimpleFTPClientData.timers.put(sequenceNumber, t1);
				t1.schedule(p1, (long)1000);
				sequenceNumber += MSS;
				System.out.println("Sending next packet with seq number: " + sequenceNumber);
				System.out.println("");
				if (last == 1) {
					break;
				}

				// } // End of synchronized

			} // End of while for outstanding <= window size
			if (last == 1) {
				break;
			}

		} // End of while(true)

	} // End of static void main

	public static void sendOutstandingPackets(int sequenceNumber)
			throws IOException {
		/*
		 * Get the timer object for the associated sequence number
		 * Cancel the timer for the remaining outstanding packets.
		 */
		while (TestSimpleFTPClientData.timers.containsKey(sequenceNumber)) {
			Timer t = TestSimpleFTPClientData.timers.get(sequenceNumber);
			t.cancel();
			TestSimpleFTPClientData.timers.remove(sequenceNumber);
			sequenceNumber += MSS;
		}

		/*
		 * From that sequence till the end, send the packets again. Start timer
		 * for each packet.
		 */
		while (TestSimpleFTPClientData.unacknowledged
				.containsKey(sequenceNumber)) {
			String sendData = TestSimpleFTPClientData.unacknowledged
					.get(sequenceNumber);
			byte[] sendDataBytes = sendData.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendDataBytes,
					sendDataBytes.length, IPAddress, serverPort);
			udpClientSocket.send(sendPacket);
			TimerPacket p1 = new TimerPacket(sequenceNumber);
			Timer t1 = new Timer();
			TestSimpleFTPClientData.timers.put(sequenceNumber, t1);
			t1.schedule(p1, (long)1000);
			sequenceNumber += MSS;

		}
	}

} // End of class
