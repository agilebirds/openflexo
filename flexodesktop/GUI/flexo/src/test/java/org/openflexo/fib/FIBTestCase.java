package org.openflexo.fib;

import java.io.File;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.validation.ValidationError;
import org.openflexo.fib.model.validation.ValidationReport;

public class FIBTestCase extends TestCase {

	static final Logger logger = Logger.getLogger(FIBTestCase.class.getPackage().getName());

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
}
