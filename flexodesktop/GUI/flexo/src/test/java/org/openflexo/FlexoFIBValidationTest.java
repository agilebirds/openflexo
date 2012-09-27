package org.openflexo;

import org.junit.Test;
import org.openflexo.fib.AbstractTestFIBFile;
import org.openflexo.toolbox.FileResource;

public class FlexoFIBValidationTest extends AbstractTestFIBFile {

	@Test
	public void testFlexoFIB() {
		validateFIBDirectory(new FileResource("Fib/DescriptionPanel.fib").getParentFile(), new FileResource(
				"Inspectors/COMMON/ModelObject.inspector").getParentFile().getParentFile());
	}
}
