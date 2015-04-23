package com.ftp;

import java.io.IOException;
import java.util.TimerTask;

public class TimerPacket extends TimerTask {
	int sequenceNumber;
	long delay;
	public TimerPacket (int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
		//this.delay = delay;
	}
	public void run() {
		System.out.print("Inside timer thread for: " + sequenceNumber);
		System.out.println(". " + System.currentTimeMillis());
		//String seqNumString = Integer.toString(sequenceNumber);
		
		if (TestSimpleFTPClientData.receivedACK.contains(sequenceNumber)) {
			System.out.println("Packet received within time");
		} else {
			System.out.println("Packet timed out: " + sequenceNumber);
			TestSimpleFTPClientData.timedOutPackets.add(sequenceNumber);
			/*try {
				System.out.println("Sending all packets from: " + sequenceNumber);
				TestSimpleFTPClient.sendOutstandingPackets(sequenceNumber);
			} catch (IOException e1) {
				
			}*/
		}
		//this.cancel();
	}
}
