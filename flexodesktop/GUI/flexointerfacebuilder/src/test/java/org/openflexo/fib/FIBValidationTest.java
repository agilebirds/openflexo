package org.openflexo.fib;

import org.junit.Test;
import org.openflexo.toolbox.FileResource;

public class FIBValidationTest extends AbstractTestFIBFile {

	@Test
	public void validateFIB() {
		validateFIBDirectory(new FileResource("Fib/ClassSelector.fib").getParentFile());
	}
}
