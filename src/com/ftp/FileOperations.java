package com.ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileOperations {

	public void openAndReadFile (String fileName) throws IOException {
		ArrayList<String> lines = new ArrayList<>();
		String line;
		File file = new File(fileName);
		Long fLength;
		fLength = file.length();
		System.out.println("Length of file: " + file.length());
		FileInputStream f1 = new FileInputStream(file);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			//BufferedReader copy = new BufferedReader(new FileReader(file));
			//int fileLength = getFileLength(copy);
			//copy.close();
			byte[] bytes = new byte[fLength.intValue()];
			//System.out.println("Length of file: " + fileLength );
			while ((line = reader.readLine()) != null) {
				//byte[] temp = line.getBytes();
				lines.add(line);
			}
			f1.read(bytes);
			for (byte b : bytes) {
				//System.out.println("Reading byte");
				//System.out.println(b);
				String s = new String(new byte[]{b});
				System.out.print(s);
			}
			reader.close();
		} catch (IOException e1) {
			System.out.println("Caught IOException");
			e1.printStackTrace();
		} catch (Exception e2) {
			System.out.println("Caught exception");
			e2.printStackTrace();
		} finally {
			
		}
		
		for (String companies : lines) {
			//System.out.println(companies);
		}
	}
	
	private static int getFileLength (BufferedReader reader) {
		String line;
		int count = 0;
		try {
			while ((line = reader.readLine()) != null) {
				count = count + line.length();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}
		
		return count;
	}
}
