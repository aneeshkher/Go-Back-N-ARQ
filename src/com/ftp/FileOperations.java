package com.ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileOperations {

	public void openAndReadFile (String fileName) throws IOException {
		ArrayList<String> lines = new ArrayList<>();
		
		String line;
		File file = new File(fileName);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				lines.add(line);
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
			System.out.println(companies);
		}
		
	
	}
}
