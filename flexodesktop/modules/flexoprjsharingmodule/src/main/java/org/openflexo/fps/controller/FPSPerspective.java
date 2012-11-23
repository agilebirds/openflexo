/**
 * 
 */
package org.openflexo.fps.controller;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.view.CVSFileModuleView;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

public abstract class FPSPerspective extends FlexoPerspective {
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
	public ModuleView<?> createModuleViewForObject(FlexoModelObject file, FlexoController controller) {
		if (file instanceof CVSFile) {
			return new CVSFileModuleView((CVSFile) file, (FPSController) controller);
		} else {
			return null;
		}
	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		this.fpsController.refreshFooter();
		if (moduleView instanceof CVSFileModuleView) {
			((CVSFileModuleView) moduleView).refresh();
		}
	}

}