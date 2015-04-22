package com.ftp;

import java.io.FileOutputStream;
import java.io.IOException;

public class TestSimpleFTPServerData {
	public void writeToFile (FileOutputStream file, String data) throws IOException {
		byte[] dataBytes = data.getBytes();
		file.write(dataBytes);
	}
	
	public String getSendPacket(int ackNumber) {
		String sendString = "10101010101010100000000000000000";
		String ackNumberString = Integer.toBinaryString(ackNumber);
		int length = ackNumberString.length();
		for (int i = 0; i < 32 - length; i++) {
			ackNumberString = "0".concat(ackNumberString);
		}
		sendString = sendString.concat(ackNumberString);
		return sendString;
	}
	
	public void closeFileStream (FileOutputStream file) {
		try {
			file.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
