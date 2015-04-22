package com.ftp;

import java.util.TimerTask;

public class TimerPacket extends TimerTask {
	int sequenceNumber;
	long delay;
	public TimerPacket (int sequenceNumber, long delay) {
		this.sequenceNumber = sequenceNumber;
		this.delay = delay;
	}
	public void run() {
		System.out.println("Inside timer thread for: " + sequenceNumber);
		//String seqNumString = Integer.toString(sequenceNumber);
		
		if (TestSimpleFTPClientData.receivedACK.contains(sequenceNumber)) {
			
		} else {
			TestSimpleFTPClientData.timedOutPackets.add(sequenceNumber);
		}
		//this.cancel();
	}
}
