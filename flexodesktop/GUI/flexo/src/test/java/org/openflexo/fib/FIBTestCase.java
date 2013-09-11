package org.openflexo.fib;

import java.io.File;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.validation.ValidationError;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.toolbox.FileResource;

public class FIBTestCase extends TestCase {

	static final Logger logger = Logger.getLogger(FIBTestCase.class.getPackage().getName());

	public void validateFIB(String fibRelativePath) {
		validateFIB(new FileResource(fibRelativePath));
	}

	public void validateFIB(File fibFile) {
		FIBComponent component = FIBLibrary.instance().retrieveFIBComponent(fibFile);
		if (component == null) {
			fail("Component not found: " + fibFile.getAbsolutePath());
		}
		ValidationReport validationReport = component.validate();
		for (ValidationError error : validationReport.getErrors()) {
			logger.severe("FIBComponent validation error: Object: " + error.getObject() + " message: " + error.getMessage());
		}
		assertEquals(0, validationReport.getErrorNb());
	}

	public static String generateFIBTestCaseClass(File directory, String relativePath) {
		StringBuffer sb = new StringBuffer();
		for (File f : directory.listFiles()) {
			if (f.getName().endsWith(".fib")) {
				String fibName = f.getName().substring(0, f.getName().indexOf(".fib"));
				sb.append("public void test" + fibName + "() {\n");
				sb.append("  validateFIB(\"" + relativePath + f.getName() + "\");\n");
				sb.append("}\n\n");
			}
		}
		return sb.toString();
	}

	public static String generateInspectorTestCaseClass(File directory, String relativePath) {
		StringBuffer sb = new StringBuffer();
		for (File f : directory.listFiles()) {
			if (f.getName().endsWith(".inspector")) {
				String fibName = f.getName().substring(0, f.getName().indexOf(".inspector"));
				sb.append("public void test" + fibName + "Inspector() {\n");
				sb.append("  validateFIB(\"" + relativePath + f.getName() + "\");\n");
				sb.append("}\n\n");
			}
		}
		return sb.toString();
	}

}
