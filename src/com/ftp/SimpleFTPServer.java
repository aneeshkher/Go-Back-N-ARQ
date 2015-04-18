package com.ftp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;

public class SimpleFTPServer {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		
		FileOperations f1 = new FileOperations();
		
		f1.openAndReadFile("AppliedCompanies.txt");
		//testBytes();

	}
	public static void testBytes() {
		/*String testString = "My Name is Aneesh Kher."
				+ " Here are some weird chars ------- *&%&%"
				+ "$(()!)*#^&$#";*/
		String testString = "abcdefghijklmnopqrstuvwxyz"
				+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				+ "0123456789"
				+ ")!@#$%^&*(_+=-;:|,\"<>.?/";
		System.out.println("Original String:");
		System.out.println(testString);
		System.out.println("String Length: " + testString.length());
		try {
			byte[] bytes = testString.getBytes();
			//System.out.println(bytes);
			System.out.println("Length is: " + bytes.length);
			System.out.println("Decoding -> ");
			for (byte b : bytes) {
				//System.out.println("First byte: " + b);
				String s = new String(new byte[]{b});
				System.out.println(b + " -> " + s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//System.out.println("Worked");
		}
		
	}

}
