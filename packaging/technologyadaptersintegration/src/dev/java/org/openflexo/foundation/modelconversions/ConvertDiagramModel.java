package org.openflexo.foundation.modelconversions;

import java.io.File;
import java.io.IOException;

import org.openflexo.xmlcode.pamelagenerator.PAMELAGenerator;

public class ConvertDiagramModel {

	public static void main(String[] args) {
		File currentDir = new File(System.getProperty("user.dir"));
		File foundationResourceDir = new File(currentDir.getParentFile().getParentFile(),
				"flexodesktop/model/flexofoundation/src/main/resources");
		File foundationJavaDir = new File(currentDir.getParentFile().getParentFile(), "flexodesktop/model/flexofoundation/src/main/java");
		File mappingFile = new File(foundationResourceDir, "Models/DiagramModel/diagram_model_1.0_test.xml");
		File tempFile = null;
		try {
			tempFile = File.createTempFile("PAMELAGenerator", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File outputDir = new File(tempFile.getParentFile(), "PAMELAGenerator");
		PAMELAGenerator generator = new PAMELAGenerator(mappingFile, foundationJavaDir, outputDir);
	}

}
