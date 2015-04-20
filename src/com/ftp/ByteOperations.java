package com.ftp;

public class ByteOperations {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int integer = 127;
	    String binary = Integer.toUnsignedString(integer, 2);
	    System.out.println("Binary value of " + integer + " is " + binary + ".");

	    int original = Integer.parseInt(binary, 2);
	    System.out.println("Integer value of binary '" + binary + "' is " + original + ".");
		
	    String test = "01000000000000000000000010000000";
	    System.out.println("Integer representation: " + Integer.parseInt(test, 2));
		int sequence = 500;
		String messageData = "This is some data ";
		String seqString = Integer.toString(sequence);
		messageData = messageData.concat(seqString);
		
		byte[] messageBytes = messageData.getBytes();
		System.out.println(messageBytes.toString());
		String decoded = new String(messageBytes);
		System.out.println("Decoded is: " + decoded);
		for (int i = 0; i < messageBytes.length; i++) {
			String s = new String(new byte[]{messageBytes[i]});
			//System.out.print(s);
		}
		int length = messageBytes.length;
		for  (int i = 0; i < length; i++) {
			//System.out.println(messageBytes[i]);
		}
	}

}
