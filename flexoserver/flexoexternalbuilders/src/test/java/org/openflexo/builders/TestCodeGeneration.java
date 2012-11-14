package org.openflexo.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.FlexoObject;

public class TestCodeGeneration extends AbstractTestExternalBuilders<FlexoCodeGeneratorMain> {

	public TestCodeGeneration() {
		super(FlexoCodeGeneratorMain.class);
	}

	@Test
	public void testWARGeneration() {
		// Just forcing FlexoObject to load before CodeType
		FlexoObject.initialize(true);
		List<String> argList = getArgList();
		String warName = "TestWAR";
		File warFile = new File(getWorkingDir(), warName + ".war");
		argList.add(getProjectFile().getAbsolutePath());
		addArgument(argList, FlexoCodeGeneratorMain.CODE_TYPE_ARGUMENT_PREFIX + CodeType.PROTOTYPE);
		addArgument(argList, FlexoCodeGeneratorMain.WORKING_DIR + getWorkingDir().getAbsolutePath());
		addArgument(argList, FlexoCodeGeneratorMain.WAR_DIR_ARGUMENT_PREFIX + warFile.getParentFile().getAbsolutePath());
		addArgument(argList, FlexoCodeGeneratorMain.WAR_NAME_ARGUMENT_PREFIX + warName);
		FlexoCodeGeneratorMain main = executeWithArgs(argList.toArray(new String[argList.size()]));
		assertEquals(0, main.getExitCode());
		assertTrue(warFile.exists());
	}

}
