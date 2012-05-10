package org.openflexo.builders;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.validation.ValidationReport;

public class TestConsistencyChecker extends AbstractTestExternalBuilders<FlexoConsistencyCheckerMain> {

	public TestConsistencyChecker() {
		super(FlexoConsistencyCheckerMain.class);
	}

	@Test
	public void testConsistencyCheck() {
		// Just forcing FlexoObject to load before CodeType
		FlexoObject.initialize(true);
		List<String> argList = getArgList();
		argList.add(getProjectFile().getAbsolutePath());
		addArgument(argList, FlexoConsistencyCheckerMain.CODE_TYPE_ARGUMENT_PREFIX + CodeType.PROTOTYPE);
		FlexoConsistencyCheckerMain main = executeWithArgs(argList.toArray(new String[argList.size()]));
		assertNotNull(main.getReports());
		for (ValidationReport report : main.getReports()) {
			assertEquals(0, report.getErrorNb());
			System.err.println(report.errorAsString());
		}
		assertEquals(0, main.getExitCode());

	}
}
