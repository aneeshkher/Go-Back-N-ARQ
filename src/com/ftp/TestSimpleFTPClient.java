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
		// fileLength =
		data.getFileLength(fileName);
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

				/*
				 * String dataString = data.getDataPacket(fileStream, MSS,
				 * sequenceNumber);
				 */
				ReturnValues fileData = data.getDataPacket(fileStream, MSS,
						sequenceNumber);
				String dataString = fileData.dataBytes;
				// System.out.println("Last is: " + fileData.last);
				if (fileData.last == 1) {
					System.out.println("Last packet");
					last = 1;
				} else if (fileData.last == 2) {
					break;
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
				System.out.println("Sending packet at: "
						+ System.currentTimeMillis());
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
				try {
					t1.schedule(p1, (long) 500);
				} catch (IllegalStateException e1) {
					
				}
				
				if (TestSimpleFTPClientData.unacknowledged
						.containsKey(sequenceNumber)) {
					// System.out.println(". Value inserted successfully!");
				} else {
					System.out
							.println(". Could not insert value into unacknowledged");
				}
				sequenceNumber += MSS;
				System.out.print("Outstanding: "
						+ TestSimpleFTPClientData.outstanding);
				System.out.println(". Sending next packet with seq number: "
						+ sequenceNumber);
				System.out.println("");
				if (last == 1) {
					break;
				}
				// System.out.println("Timed Out Queue: " + );

				// } // End of synchronized
				if (!TestSimpleFTPClientData.timedOutPackets.isEmpty()) {
					System.out.println("Timeout triggered");
					sendOutstandingPackets(TestSimpleFTPClientData.timedOutPackets
							.poll());
				}

				Thread.sleep(20);
			} // End of while for outstanding <= window size

			if (!TestSimpleFTPClientData.timedOutPackets.isEmpty()) {
				// System.out.println("Timeout triggered");
				sendOutstandingPackets(TestSimpleFTPClientData.timedOutPackets
						.poll());
			}

			if (last == 1) {
				if (TestSimpleFTPClientData.timedOutPackets.isEmpty()) {
					break;
				} else {
					sendOutstandingPackets(TestSimpleFTPClientData.timedOutPackets
							.poll());
				}
			}

		} // End of while(true)
		System.out.println("Sent: " + TestSimpleFTPClientData.sentNotAcknowledged);
		System.out.println("Acked: " + TestSimpleFTPClientData.acknowledged);
			// thread.join();
		while (TestSimpleFTPClientData.sentNotAcknowledged != TestSimpleFTPClientData.acknowledged) {
			if (!TestSimpleFTPClientData.timedOutPackets.isEmpty()) {
				sendOutstandingPackets(TestSimpleFTPClientData.timedOutPackets
						.poll());
			}
		}

	} // End of static void main

	public static synchronized void sendOutstandingPackets(int sequenceNumber)
			throws Exception {
		// TestSimpleFTPClientData.lock.lock();
		System.out.println("Resending all packets from: " + sequenceNumber);

		/*
		 * Get the timer object for the associated sequence number Cancel the
		 * timer for the remaining outstanding packets.
		 */
		// int temp = sequenceNumber + MSS;
		int temp = sequenceNumber;
		int temp2 = sequenceNumber;
		System.out.print("Cancelling timers: ");
		while (TestSimpleFTPClientData.timers.containsKey(temp)) {
			System.out.print(temp + " ");
			Timer t = TestSimpleFTPClientData.timers.get(temp);
			t.cancel();
			TestSimpleFTPClientData.timers.remove(temp);
			temp += MSS;
		}
		System.out.println("");
		System.out.print("Resending: ");
		while (TestSimpleFTPClientData.unacknowledged.containsKey(temp2)) {
			System.out.print(temp2 + " ");
			temp2 += MSS;
		}
		System.out.println("");

		/*
		 * From that sequence till the end, send the packets again. Start timer
		 * for each packet.
		 */
		temp = sequenceNumber;
		while (TestSimpleFTPClientData.unacknowledged.containsKey(temp)) {
			String sendData = TestSimpleFTPClientData.unacknowledged.get(temp);
			byte[] sendDataBytes = sendData.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendDataBytes,
					sendDataBytes.length, IPAddress, serverPort);
			udpClientSocket.send(sendPacket);
			TimerPacket p1 = new TimerPacket(temp);
			Timer t1 = new Timer();
			TestSimpleFTPClientData.timers.put(temp, t1);
			try {
				t1.schedule(p1, (long) 500);
			} catch (IllegalStateException e1) {

			}
			temp += MSS;
			Thread.sleep(20);

		}
		System.out.println("");

	}

} // End of class
