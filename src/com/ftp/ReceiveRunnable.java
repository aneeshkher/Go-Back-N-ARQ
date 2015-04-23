package com.ftp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Timer;

public class ReceiveRunnable implements Runnable {
	private volatile boolean running = true;
	public DatagramSocket udpClientSocket;

	public ReceiveRunnable(DatagramSocket udpClientSocket) {
		this.udpClientSocket = udpClientSocket;
	}

	public void terminate() {
		running = false;
	}

	public void run() {
		byte[] receivedBytes = new byte[8192];
		DatagramPacket receivedPacket = new DatagramPacket(receivedBytes,
				receivedBytes.length);
		TestSimpleFTPClientData data = new TestSimpleFTPClientData();
		while (running) {
			while (true) {
				try {
					udpClientSocket.receive(receivedPacket);
					//System.out.println("Received ACK:");
					//System.out.println(new String(receivedPacket.getData()));
					// synchronized (data)
					
					String received = new String(receivedPacket.getData());
					String ACK = TestSimpleFTPClientData
							.getACKNumberFromACK(received);
					int receivedACK = TestSimpleFTPClientData
							.getIntegerNumber(ACK);
					if (TestSimpleFTPClientData.timedOutPackets.contains(receivedACK)) {
						TestSimpleFTPClientData.timedOutPackets.remove(receivedACK);
					}

					TestSimpleFTPClientData.receivedACK.add(receivedACK);
					
					if (TestSimpleFTPClientData.unacknowledged.containsKey(receivedACK)) {
						//System.out.println("Removing ACK for: " + receivedACK);
					}
						
					TestSimpleFTPClientData.unacknowledged.remove(receivedACK);
					
					TestSimpleFTPClientData.acknowledged++;
					TestSimpleFTPClientData.lock.lock();
					TestSimpleFTPClientData.outstanding = TestSimpleFTPClientData.sentNotAcknowledged
							- TestSimpleFTPClientData.acknowledged;
					TestSimpleFTPClientData.lock.unlock();
					
					System.out.println("Received ACK: " + receivedACK + 
							". Outstanding: " + TestSimpleFTPClientData.outstanding);
					// Remove the timer objects since the packet has been
					// received.
					if (TestSimpleFTPClientData.timers.containsKey(receivedACK)) {
						Timer t = TestSimpleFTPClientData.timers
								.get(receivedACK);
						t.cancel();
						TestSimpleFTPClientData.timers.remove(receivedACK);
					}

				} catch (IOException e1) {

				}

			} // End of while(true)
		} // End of while(running)
	} // End of run()

	public void ru1n() {
	}
}