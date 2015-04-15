package com.ftp;

import java.io.IOException;

public class SimpleFTPServer {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		
		FileOperations f1 = new FileOperations();
		
		f1.openAndReadFile("AppliedCompanies.txt");

	}

}
