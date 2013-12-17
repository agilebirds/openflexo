package org.openflexo.foundation.xml;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.ProjectData;
import org.openflexo.model.ModelContext;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

public class TestPAMELAResourceData extends FlexoTestCase {

	private ModelFactory modelFactory;

	public void testProjectData() throws ModelDefinitionException {
		checkInterface(ProjectData.class);
	}

	private void checkInterface(Class<?> type) throws ModelDefinitionException {
		this.modelFactory = new ModelFactory(new ModelContext(type));
		modelFactory.newInstance(type);
	}
}
