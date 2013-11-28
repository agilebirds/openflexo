package org.openflexo.fib;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.validation.ValidationReport;

public abstract class AbstractTestFIBFile {

	private static final String[] FIB_EXTENSIONS = { "fib", "inspector" };

	public void validateFIBDirectory(File... fibDirectories) {
		for (File dir : fibDirectories) {
			Collection<File> files = FileUtils.listFiles(dir, FIB_EXTENSIONS, true);
			validateFIBFile(files.toArray(new File[files.size()]));
		}
	}

	public void validateFIBFile(File... fibFiles) {
		for (File file : fibFiles) {
			FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(file, false);
			try {
				ValidationReport report = fibComponent.validate();
				if (report.getErrorNb() > 0) {
					fail("Validation failed for " + file.getAbsolutePath() + "\n" + report.errorAsString());
				}
			} finally {
				FIBLibrary.instance().removeFIBComponentFromCache(file);
			}
		}
	}

}
