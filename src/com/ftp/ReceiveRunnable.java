package com.ftp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ReceiveRunnable implements Runnable {
	private volatile boolean running = true;
	private DatagramSocket udpClientSocket;
	ReceiveRunnable(DatagramSocket udpSock)
	{
		udpClientSocket = udpSock;
	}
	public void terminate() {
		running = false;
	}
	public void run() {
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
						String receive = new String(
								receivedPacket.getData());
						for (int i = 32; i < 64; i++) {
							ACKNumberString = ACKNumberString
									.concat(Character.toString(receive
											.charAt(i)));
						}
						receivedACK.add(ACKNumberString);
						int seqNo = Integer.parseInt(ACKNumberString, 2)
								- MSS;
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