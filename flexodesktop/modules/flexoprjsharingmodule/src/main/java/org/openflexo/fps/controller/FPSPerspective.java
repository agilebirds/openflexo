/**
 * 
 */
package org.openflexo.fps.controller;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.view.CVSFileModuleView;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

public abstract class FPSPerspective extends FlexoPerspective<CVSFile> {
	/**
	 * 
	 */
	private final FPSController fpsController;

	public FPSPerspective(FPSController fpsController, String name) {
		super(name);
		this.fpsController = fpsController;
	}

	public abstract void setFilters();

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return true;
	}

	@Override
	public CVSFile getDefaultObject(FlexoModelObject proposedObject) {
		return null;
	}

	@Override
	public ModuleView<CVSFile> createModuleViewForObject(CVSFile file, FlexoController controller) {
		return new CVSFileModuleView(file, (FPSController) controller);
	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		this.fpsController.refreshFooter();
		if (moduleView instanceof CVSFileModuleView) {
			((CVSFileModuleView) moduleView).refresh();
		}
	}

}