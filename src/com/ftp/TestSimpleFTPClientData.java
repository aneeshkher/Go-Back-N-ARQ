package com.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class TestSimpleFTPClientData {
	
	static HashMap<Integer, Integer> window = new HashMap<>();
	static HashMap<Integer, String> unacknowledged = new HashMap<>();
	static Queue<Integer> timedOutPackets = new LinkedList<Integer>();
	
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

	public static String getBinaryString(int sequenceNumber) {
		String seqNumString = Integer.toBinaryString(sequenceNumber);
		for (int i = 0; i < 32 - seqNumString.length(); i++) {
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
