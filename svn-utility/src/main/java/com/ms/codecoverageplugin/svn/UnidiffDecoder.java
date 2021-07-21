package com.ms.codecoverageplugin.svn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;



public class UnidiffDecoder {

	private Scanner scanner;

	public static void main(String[] args) {

		try {

			Map<Integer, LineModificationData> modifiedLineMap = new UnidiffDecoder("").getModifiedLineMap();
			System.out.println(modifiedLineMap);
			
		} finally {
			try {

			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	public UnidiffDecoder(String string) {
		scanner = new Scanner(string);
	}

	public Map<Integer, LineModificationData> getModifiedLineMap() {
		int startLineNo = 0;
		Character signToCheck = null;
		Map<Integer, LineModificationData> modifiedLineMap = new LinkedHashMap<>();
		while (scanner.hasNext()) {

			String line = scanner.nextLine();
			if (line.startsWith("@@")) {
				startLineNo = getContexStartLine(line);
				signToCheck = line.charAt(3);
				continue;
			}

			if (signToCheck != null && line.startsWith(signToCheck.toString())) {

				modifiedLineMap.put(startLineNo,new LineModificationData(line.substring(1, line.length()) + "\n","",startLineNo++,"",""));

			}
			startLineNo++;

		}
		System.out.println(modifiedLineMap);
		return modifiedLineMap;
	}

	/**
	 * @param line
	 */
	private int getContexStartLine(String line) {
		int startLineNo;
		String lineNoStr = line.substring(3, line.indexOf(','));
		startLineNo = Math.abs(Integer.parseInt(lineNoStr));
		return startLineNo;
	}

}
