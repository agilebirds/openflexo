package org.openflexo.builders;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.openflexo.foundation.Format;

public class TestDocGeneration extends AbstractTestExternalBuilders<FlexoDocGeneratorMain> {

	public TestDocGeneration() {
		super(FlexoDocGeneratorMain.class);
	}

	@Test
	public void testPDFGeneration() {
		Format format = Format.LATEX;
		createDocForFormat(format, false);
	}

	@Test
	public void testHTMLGeneration() {
		Format format = Format.HTML;
		createDocForFormat(format, true);
	}

	@Test
	public void testDocXGeneration() {
		Format format = Format.DOCX;
		createDocForFormat(format, true);
	}

	private FlexoDocGeneratorMain createDocForFormat(Format format, boolean buildPostBuildFile) {
		File outputFile = new File(getWorkingDir(), "Test" + format + format.getPostBuildFileExtension());
		List<String> argList = getArgList();
		argList.add(getProjectFile().getAbsolutePath());
		addArgument(argList, FlexoDocGeneratorMain.DOC_TYPE_ARGUMENT_PREFIX + "Business");
		addArgument(argList, FlexoDocGeneratorMain.TOC_FLEXOID + " 5243");
		addArgument(argList, FlexoDocGeneratorMain.TOC_UID + " DOM");
		addArgument(argList, FlexoDocGeneratorMain.WORKING_DIR + getWorkingDir().getAbsolutePath());
		addArgument(argList, FlexoDocGeneratorMain.FORMAT + format.name());
		addArgument(argList, FlexoDocGeneratorMain.OUTPUT_FILE_ARGUMENT_PREFIX + outputFile.getAbsolutePath());
		if (!buildPostBuildFile) {
			addArgument(argList, FlexoDocGeneratorMain.NO_POST_BUILD);
		}
		FlexoDocGeneratorMain main = executeWithArgs(argList.toArray(new String[argList.size()]));
		assertEquals(0, main.getExitCode());
		if (buildPostBuildFile) {
			assertTrue(outputFile.exists());
		}
		return main;
	}
}
