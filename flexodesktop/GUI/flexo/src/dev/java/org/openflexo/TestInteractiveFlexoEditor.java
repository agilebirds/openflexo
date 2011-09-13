/**
 * 
 */
package org.openflexo;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.view.controller.InteractiveFlexoEditor;

public class TestInteractiveFlexoEditor extends InteractiveFlexoEditor
{
	private final TestModule module;
	private final TestController controller;
	
	public TestInteractiveFlexoEditor(final FlexoProject project) throws Exception 
	{
		super(project);
		module = new TestModule(this);
		controller = new TestController(this,module) {
			@Override
			public void objectWasClicked(FlexoModelObject object)
			{
				TestInteractiveFlexoEditor.this.objectWasClicked(object);
			}

			@Override
			public void objectWasDoubleClicked(FlexoModelObject object)
			{
				TestInteractiveFlexoEditor.this.objectWasDoubleClicked(object);
			}
			
			@Override
			public boolean displayInspectorTabForContext(String context) 
			{
				return TestInteractiveFlexoEditor.this.displayInspectorTabForContext(context);
			}
		};
		module.setFlexoController(controller);
	}

	@Override
	public boolean isAutoSaveEnabledByDefault() 
	{
		return false;
	}

	public TestController getController() 
	{
		return controller;
	}
	
	@Override
	public TestModule getActiveModule() 
	{
		return module;
	}

	public void objectWasClicked(FlexoModelObject object)
	{
	}

	public void objectWasDoubleClicked(FlexoModelObject object)
	{
	}

	public boolean displayInspectorTabForContext(String context) 
	{
		return false;
	}

}