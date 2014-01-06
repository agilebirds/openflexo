package org.openflexo.foundation;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * This unit test is intented to test project creation facilities
 * 
 * @author sylvain
 * 
 */
public class TestCreateProject extends OpenflexoRunTimeTestCase {

	@Test
	public void testCreateProject() {
		FlexoEditor editor = createProject("TestProject");
		FlexoProject project = editor.getProject();
		System.out.println("Created project " + project.getProjectDirectory());
		assertTrue(project.getProjectDirectory().exists());
		assertTrue(project.getProjectDataResource().getFile().exists());
	}

}
