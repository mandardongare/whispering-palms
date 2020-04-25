package com.fss.dev.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

public class ApiUtil {
	
	public static void CorrectFile(String oldFileName, String newFileName) throws Exception {
		// SAN
		File smbOldFileName = new File(oldFileName);
		File smbNewFileName = new File(newFileName);
		FileInputStream fr = new FileInputStream(smbOldFileName);
		FileOutputStream fw = new FileOutputStream(smbNewFileName);

		byte[] buffer = new byte[1012];
		int numOfChars;

		do {
			numOfChars = fr.read(buffer);

			if (numOfChars == 1012) {
				fr.read();
				fr.read();
			}

			if (numOfChars > 0)
				fw.write(buffer, 0, numOfChars);

		} while (numOfChars > 0);

		fw.close();
		fr.close();
	}	

	 public static String getBITMAP(char[] pCharArr) throws UnsupportedEncodingException{
		 StringBuilder binary = new  StringBuilder();
		 for(int i=0;i<pCharArr.length;i++){
			 char c = pCharArr[i];
			 int asciiDesc = (int) c;
			 String asciiStr = Integer.toBinaryString(asciiDesc);
			 String asciiStr8bit = StringUtils.leftPad(asciiStr, 8,"0");
			 binary.append(asciiStr8bit);
		 }
		 return binary.toString();
	 }

}
