package com.ftp;

import java.util.TimerTask;

public class PacketTimer extends TimerTask {
	int sequenceNumber;
	long delay;

	public PacketTimer(int sequenceNumber, long delay) {
		this.sequenceNumber = sequenceNumber;
		this.delay = delay;
	}

	public void run() {
		System.out.println("Inside timer thread for: " + sequenceNumber);
		String seqNumString = TestSimpleFTPClientData
				.getBinaryString(sequenceNumber);
		
		
		this.cancel();
	}
}
