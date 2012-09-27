package org.openflexo.builders;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.openflexo.toolbox.FileResource;

public class TestReinjection extends AbstractTestExternalBuilders<FlexoReinjectDocxMain> {

	public TestReinjection() {
		super(FlexoReinjectDocxMain.class);
	}

	@Test
	public void testReinjection() {
		List<String> argList = getArgList();
		FileResource docX = new FileResource("Step1.docx");
		argList.add(getProjectFile().getAbsolutePath());
		addArgument(argList, FlexoReinjectDocxMain.DOCXFILE_ARGUMENT_PREFIX + docX.getAbsolutePath());
		FlexoReinjectDocxMain main = executeWithArgs(argList.toArray(new String[argList.size()]));
		assertEquals(0, main.getExitCode());
		assertTrue(main.getDocxAction().getNumberOfEPIUpdated() > 0);
		assertTrue(main.getDocxAction().getNumberOfNameUpdated() > 0);
		assertTrue(main.getDocxAction().getNumberOfDescriptionUpdated() > 0);
	}

}
