package org.openflexo.fib;

import java.io.File;
import java.io.IOException;

public class ConvertFIBModel {

	public static void main(String[] args) {
		File currentDir = new File(System.getProperty("user.dir"));
		System.out.println("current=" + currentDir);
		File sourceCode = currentDir;
		File fibCoreResourceDir = new File(currentDir, "src/main/resources");
		File mappingFile = new File(fibCoreResourceDir, "Models/FIBModel.xml");
		File tempFile = null;
		try {
			tempFile = File.createTempFile("PAMELAGenerator", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File outputDir = new File(tempFile.getParentFile(), "PAMELAGenerator");
		// PAMELAGenerator generator = new PAMELAGenerator(mappingFile, sourceCode, outputDir);
	}

}
