package org.openflexo.foundation.modelconversions;

import java.io.File;
import java.io.IOException;

import org.openflexo.xmlcode.pamelagenerator.PAMELAGenerator;

public class ConvertVirtualModel {

	public static void main(String[] args) {
		File currentDir = new File(System.getProperty("user.dir"));
		File rootDir = currentDir.getParentFile().getParentFile().getParentFile();
		File foundationResourceDir = new File(currentDir.getParentFile().getParentFile(),
				"flexodesktop/model/flexofoundation/src/main/resources");
		File foundationJavaDir = new File(currentDir.getParentFile().getParentFile(), "flexodesktop/model/flexofoundation/src/main/java");
		System.out.println("rootDir=" + rootDir);
		System.out.println("foundationResourceDir=" + foundationResourceDir);
		File mappingFile = new File(foundationResourceDir, "Models/ViewPointModel/VirtualModel/virtual_model_1.0.xml");
		File tempFile = null;
		try {
			tempFile = File.createTempFile("PAMELAGenerator", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File outputDir = new File(tempFile.getParentFile(), "PAMELAGenerator");
		PAMELAGenerator generator = new PAMELAGenerator(mappingFile, rootDir, outputDir);
	}

}
