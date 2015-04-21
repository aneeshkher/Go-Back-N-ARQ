package com.ftp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

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
					synchronized (data) {
						String received = new String(receivedPacket.getData());
					}
				} catch (IOException e1) {
					
				}
				
			} // End of while(true)
		} // End of while(running)
	} // End of run()

	public void ru1n() {
		System.out.println("Inside thread for receiving");
		byte[] receivedBytes = new byte[8192];
		DatagramPacket receivedPacket = new DatagramPacket(receivedBytes,
				receivedBytes.length);
		String ACKNumberString = "";
		while (running) {
			while (true) {
				try {

					udpClientSocket.receive(receivedPacket);
					synchronized (this) {
						String receive = new String(receivedPacket.getData());
						for (int i = 32; i < 64; i++) {
							ACKNumberString = ACKNumberString.concat(Character
									.toString(receive.charAt(i)));
						}
						receivedACK.add(ACKNumberString);
						int seqNo = Integer.parseInt(ACKNumberString, 2) - MSS;
						unackData.remove(seqNo);
						ACKed++;
						System.out.println("Removed " + seqNo
								+ " from unacked packets");
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}