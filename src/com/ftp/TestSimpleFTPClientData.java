package com.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestSimpleFTPClientData {
	
	static ConcurrentHashMap<Integer, Integer> window = new ConcurrentHashMap<>();
	static ConcurrentHashMap<Integer, String> unacknowledged = new ConcurrentHashMap<>();
	static ConcurrentLinkedQueue<Integer> timedOutPackets = new ConcurrentLinkedQueue<Integer>();
	static ArrayList<Integer> receivedACK =  new ArrayList<>();
	static ConcurrentHashMap<Integer, Timer> timers = new ConcurrentHashMap<>();
	static Lock lock = new ReentrantLock();
	static int acknowledged;
	static int outstanding;
	static int sentNotAcknowledged;
	static int fileLengthInt;
	
	public ReturnValues getDataPacket(FileInputStream fileStream, int MSS,
			int sequenceNumber) {
		byte[] bytes = new byte[MSS];
		ReturnValues r1 = new ReturnValues();
		r1.last = 0;
		String padding = "";
		try {
			int total = fileStream.read(bytes, 0, bytes.length);
			if (total == MSS) {
				r1.last = 0;
			} else if (total == 0) {
				r1.last = 2;
			} else {
				r1.last = 1;
				int zeros = MSS-total;
				for (int i = 0; i < zeros; i++) {
					padding = "0".concat(padding);
				}
				System.out.println("Padded: " + padding.length() + " zeros");
			}
			String dataBytesString = new String(bytes);
			String startFrame = "0101010101010101";
			String checkSum = "1111111111111111";
			if (r1.last == 1) {
				dataBytesString = dataBytesString.substring(0, total);
				String dataBytes = startFrame.concat(checkSum)
						.concat(getBinaryString(sequenceNumber))
						.concat(dataBytesString).concat(padding).concat(Integer.toString(total));
				System.out.println("Read bytes: " + total + " Sending length: " + dataBytes.length());
				System.out.println("Length of data: " + dataBytesString.length());
				r1.dataBytes = dataBytes;
				return r1;
				
			} else {
				String dataBytes = startFrame.concat(checkSum)
						.concat(getBinaryString(sequenceNumber))
						.concat(dataBytesString).concat(Integer.toString(MSS));
				System.out.println("Read bytes: " + total + " Sending length: " + dataBytes.length());
				r1.dataBytes = dataBytes;
				return r1;
			}
			
			
		} catch (IOException e1) {

		}
		return r1;
	}
	
	public static String getACKNumberFromACK (String ACK) {
		String ACKNumberString = "";
		for (int i = 32; i < 64; i++) {
			ACKNumberString = ACKNumberString.concat(Character
					.toString(ACK.charAt(i)));
		}
		
		return ACKNumberString;
	}

	public static String getBinaryString(int sequenceNumber) {
		String seqNumString = Integer.toBinaryString(sequenceNumber);
		int length = seqNumString.length();
		for (int i = 0; i < 32 - length; i++) {
			seqNumString = "0".concat(seqNumString);
		}
		return seqNumString;
	}
	
	public static int getIntegerNumber(String sequenceNumberStr) {
		//sequenceNumberStr = sequenceNumberStr.substring(1);
		int sequenceNumber = Integer.parseInt(sequenceNumberStr.substring(1), 2);
		return sequenceNumber;
	}

	public long getFileLength(String fileName) {
		File file = new File(fileName);
		Long fileLength = file.length();
		fileLengthInt = fileLength.intValue();
		return fileLength;
	}
}
