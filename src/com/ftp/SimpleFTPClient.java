package com.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class SimpleFTPClient {

	static ArrayList<String> receivedACK = new ArrayList<>();
	static DatagramSocket udpClientSocket;
	static int ACKed = 0;
	static int sentNotAcked = 0;
	static int MSS;
	static HashMap<Integer, String> unackData = new HashMap<>();
	static Queue<String> timedOutPackets = new LinkedList<String>();
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(args.length);
		if (args.length != 5) {
			System.out.println("Please call the program with proper arguments");
			System.exit(1);
		}

		String serverHostName = args[0];
		int serverPort = Integer.parseInt(args[1]);
		String fileName = args[2];
		int windowSize = Integer.parseInt(args[3]);
		MSS = Integer.parseInt(args[4]);
		byte[] buffer = new byte[MSS*windowSize];
		udpClientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(serverHostName);
		
		File file = new File(fileName);
		Long fLength;
		fLength = file.length();
		int byteCount = fLength.intValue();
		FileInputStream fileStream = new FileInputStream(file);
		int changingWindow = windowSize;
		int sequenceNumber = 0;
		HashMap<Integer, Long> expectedReceiveTime = new HashMap<>();
		
		int maxSend = windowSize;
		
		new Thread(new ReceiveRunnable()).start();
		
		while(true) {
			int bytesRead = fileStream.read(buffer, 0, buffer.length);
			int readOffset = 0;
			while (changingWindow <= windowSize) {
				byte[] sendData = new byte[MSS];
				for (int i = readOffset; i < readOffset + MSS - 1; i++) {
					sendData[i] = buffer[i];
				}
				String sendDataString = sendData.toString();
				readOffset = readOffset + MSS;
				String sendDataPacket = "01010101010101011111111111111111";
				sendDataPacket = sendDataPacket.concat(Integer.toBinaryString(sequenceNumber));
				sendDataPacket = sendDataPacket.concat(sendDataString);
				byte[] sendBytes = sendDataPacket.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, IPAddress, serverPort);
				udpClientSocket.send(sendPacket);
				PacketTimer p1 = new PacketTimer(sequenceNumber, (long)1000);
				Timer t1 = new Timer();
				t1.schedule(p1, (long)1000);
				if (sequenceNumber == 0) {
					expectedReceiveTime.put(sequenceNumber, (long)1000);
				} else {
					
				}
				
				//Call the thread for the timer here on this line
				synchronized (SimpleFTPClient.class){
					unackData.put(sequenceNumber, sendDataString);
					sequenceNumber = sequenceNumber + MSS;
					changingWindow++;
					sentNotAcked++;
				}
			}
			
			break;
		}
		byte[] sendDataBytes = "Hope this is received".getBytes();
		for (byte b : sendDataBytes) {
			System.out.print(b);
		}
		/*DatagramPacket sendPacket = new DatagramPacket(sendDataBytes, sendDataBytes.length
				, IPAddress, serverPort);*/
		DatagramPacket sendPacket = new DatagramPacket(sendDataBytes, 5, 5,
				IPAddress, serverPort);
		try {
			udpClientSocket.send(sendPacket);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
	}
	public static void receiveACK() {
		
	}
	
	public static DatagramSocket getSocket () {
		return udpClientSocket;
	}
	
	public static class ReceiveRunnable implements Runnable {
		public void run() {
			byte[] receivedBytes = new byte[8192];
			DatagramPacket receivedPacket = new DatagramPacket(receivedBytes,
					receivedBytes.length);
			while (true) {
				try {
					byte[] ACKNumber = new byte[32];
					
					udpClientSocket.receive(receivedPacket);
					synchronized (this) {
						for (int i = 32; i < 64; i++) {
							ACKNumber[i-32] = receivedBytes[i];
						}
						receivedACK.add(ACKNumber.toString());
						int seqNo = Integer.parseInt(ACKNumber.toString(), 2) - MSS;
						unackData.remove(seqNo);
						ACKed++;
					}
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public class TimerRunnable implements Runnable {
		public void run() {
			
		}
	}
	
	public static class PacketTimer extends TimerTask {
		int sequenceNumber;
		long delay;
		public PacketTimer (int sequenceNumber, long delay) {
			this.sequenceNumber = sequenceNumber;
			this.delay = delay;
		}
		public void run() {
			String seqNumString = Integer.toString(sequenceNumber);
			
			if (receivedACK.contains(seqNumString)) {
				
			} else {
				timedOutPackets.add(seqNumString);
			}
			this.cancel();
		}
	}
}
