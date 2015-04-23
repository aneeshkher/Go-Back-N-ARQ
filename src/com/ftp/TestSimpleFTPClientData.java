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
	
	public String getDataPacket(FileInputStream fileStream, int MSS,
			int sequenceNumber) {
		byte[] bytes = new byte[MSS];
		try {
			fileStream.read(bytes, 0, bytes.length);
			String dataBytesString = new String(bytes);
			String startFrame = "0101010101010101";
			String checkSum = "1111111111111111";
			String dataBytes = startFrame.concat(checkSum)
					.concat(getBinaryString(sequenceNumber))
					.concat(dataBytesString);
			return dataBytes;
		} catch (IOException e1) {

		}
		return "Cannot read from file";
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
		return fileLength;
	}
}
