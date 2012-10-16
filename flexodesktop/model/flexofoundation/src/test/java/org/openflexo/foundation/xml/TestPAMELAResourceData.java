package org.openflexo.foundation.xml;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.rm.ProjectData;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

public class TestPAMELAResourceData extends FlexoTestCase {

	private ModelFactory modelFactory;

	@Override
	protected void setUp() throws Exception {
		this.modelFactory = new ModelFactory();
	}

	public void testProjectData() throws ModelDefinitionException {
		checkInterface(ProjectData.class);
	}

	private void checkInterface(Class<?> type) throws ModelDefinitionException {
		modelFactory.importClass(type);
		modelFactory.newInstance(type);
	}
}
