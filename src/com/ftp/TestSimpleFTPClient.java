package com.ftp;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.Map.Entry;
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

				TestSimpleFTPClientData.window.put(0, sequenceNumber);

				byte[] sendDataBytes = dataString.getBytes();
				System.out.print("Sending data from client: "
						+ dataString.length() + " " + sendDataBytes.length);
				System.out.print(". Sequence number: " + sequenceNumber + ". ");
				// System.out.println(dataString);
				DatagramPacket sendPacket = new DatagramPacket(sendDataBytes,
						sendDataBytes.length, IPAddress, serverPort);

				// synchronized (data)
				
				udpClientSocket.send(sendPacket);
				
				
				TestSimpleFTPClientData.unacknowledged.put(sequenceNumber,
						dataString);
				TestSimpleFTPClientData.lock.lock();
				TestSimpleFTPClientData.sentNotAcknowledged++;
				TestSimpleFTPClientData.outstanding = TestSimpleFTPClientData.sentNotAcknowledged
						- TestSimpleFTPClientData.acknowledged;
				TestSimpleFTPClientData.lock.unlock();
				
				TimerPacket p1 = new TimerPacket(sequenceNumber);
				Timer t1 = new Timer();
				TestSimpleFTPClientData.timers.put(sequenceNumber, t1);
				t1.schedule(p1, (long) 1000);
				if (TestSimpleFTPClientData.unacknowledged.containsKey(sequenceNumber)) {
					System.out.println(". Value inserted successfully!");
				} else {
					System.out.println(". Could not insert value into unacknowledged");
				}
				sequenceNumber += MSS;
				System.out.print("Outstanding: " + TestSimpleFTPClientData.outstanding);
				System.out.println(". Sending next packet with seq number: "
						+ sequenceNumber);
				System.out.println("");
				if (last == 1) {
					break;
				}
				//System.out.println("Timed Out Queue: " + );
				

				// } // End of synchronized
				if (!TestSimpleFTPClientData.timedOutPackets.isEmpty()) {
					System.out.println("Timeout triggered");
					//sendOutstandingPackets(TestSimpleFTPClientData.timedOutPackets.poll());
				}
				

			} // End of while for outstanding <= window size
			
			if (!TestSimpleFTPClientData.timedOutPackets.isEmpty()) {
				//System.out.println("Timeout triggered");
				//sendOutstandingPackets(TestSimpleFTPClientData.timedOutPackets.poll());
			}
			
			if (last == 1) {
				break;
			}

		} // End of while(true)

	} // End of static void main

	public static synchronized void sendOutstandingPackets(int sequenceNumber)
			throws IOException {
		//TestSimpleFTPClientData.lock.lock();
		System.out.println("Resending all packets from: " + sequenceNumber);
		/*for (Entry<Integer, Timer> entry : TestSimpleFTPClientData.timers.entrySet()) {
			System.out.println(entry.getKey());
		}*/

		/*
		 * Get the timer object for the associated sequence number Cancel the
		 * timer for the remaining outstanding packets.
		 */
		int temp = sequenceNumber + MSS;
		while (TestSimpleFTPClientData.timers.containsKey(temp)) {
			System.out.println("Canceling timer for: " + temp);
			Timer t = TestSimpleFTPClientData.timers.get(temp);
			t.cancel();
			TestSimpleFTPClientData.timers.remove(temp);
			temp += MSS;
		}
		
		

		/*
		 * From that sequence till the end, send the packets again. Start timer
		 * for each packet.
		 */
		//int temp = sequenceNumber;
		/*while(true) {
			if (TestSimpleFTPClientData.unacknowledged.containsKey(temp)) {
				System.out.println("Checking for: " + temp);
			} else {
				break;
			}
			temp = temp + MSS;
		}*/
		boolean test = TestSimpleFTPClientData.unacknowledged
				.containsKey(sequenceNumber);
		System.out.println("Before getting unACKed packets: " + test);
		System.out.println(TestSimpleFTPClientData.unacknowledged.get(sequenceNumber));
		temp = sequenceNumber + MSS;
		while (TestSimpleFTPClientData.unacknowledged.containsKey(temp)) {
			System.out.println("Sending: " + temp );
			String sendData = TestSimpleFTPClientData.unacknowledged
					.get(temp);
			System.out.println("Resending: " + temp);
			byte[] sendDataBytes = sendData.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendDataBytes,
					sendDataBytes.length, IPAddress, serverPort);
			udpClientSocket.send(sendPacket);
			TimerPacket p1 = new TimerPacket(temp);
			Timer t1 = new Timer();
			TestSimpleFTPClientData.timers.put(temp, t1);
			t1.schedule(p1, (long) 1000);
			sequenceNumber += MSS;

		}
		for (int i = 200; i < 4001; i += 200) {
			System.out.println("Getting for i = " + i);
			TestSimpleFTPClientData.unacknowledged.get(i);
		}
		/*System.out.println("After getting unACKed packets");
		for (Entry<Integer, String> entry : TestSimpleFTPClientData.unacknowledged
				.entrySet()) {
			if (entry.getKey() == sequenceNumber) 
				System.out.println("Checking for equality for first one");
			System.out.println(entry.getKey() + ": "
					+ entry.getValue().substring(64, 80));
		}*/
		//TestSimpleFTPClientData.lock.lock();
	}

} // End of class
