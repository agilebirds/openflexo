package org.openflexo;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.InteractiveFlexoEditor;

public class TestModule extends FlexoModule {

	public TestModule(InteractiveFlexoEditor projectEditor) {
		super(projectEditor);
	}

	@Override
	public void setFlexoController(FlexoController controller) {
		super.setFlexoController(controller);
	}

	@Override
	public InspectorGroup[] getInspectorGroups() {
		return new InspectorGroup[] { Inspectors.WKF };
	}

	@Override
	public FlexoModelObject getDefaultObjectToSelect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Module getModule() {
		return Module.TEST_MODULE;
	}
}
