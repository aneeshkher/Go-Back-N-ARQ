package com.old;

import java.util.TimerTask;

import com.ftp.TestSimpleFTPClientData;

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
