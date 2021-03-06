/**
 * 
 */
package org.openflexo.cgmodule.controller;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.openflexo.cgmodule.view.CGFileHistoryModuleView;
import org.openflexo.cgmodule.view.CGFileModuleView;
import org.openflexo.cgmodule.view.CGRepositoryModuleView;
import org.openflexo.cgmodule.view.CGTemplateFileModuleView;
import org.openflexo.cgmodule.view.GeneratedCodeModuleView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.icon.CGIconLibrary;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

class VersionningPerspective extends FlexoPerspective {

	/**
	 * 
	 */
	private final GeneratorController generatorController;

	/**
	 * @param generatorController
	 *            TODO
	 * @param name
	 */
	public VersionningPerspective(GeneratorController generatorController) {
		super("versionning");
		this.generatorController = generatorController;
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return CGIconLibrary.CG_VP_ACTIVE_ICON;
	}

	@Override
	public JPanel getFooter() {
		return this.generatorController._footer;
	}

	@Override
	public CGFile getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject instanceof CGFile) {
			return (CGFile) proposedObject;
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof GeneratedOutput || object instanceof GenerationRepository || object instanceof CGFile
				|| object instanceof CGTemplate;
	}

	@Override
	public ModuleView<? extends FlexoModelObject> createModuleViewForObject(FlexoModelObject object, FlexoController controller) {
		if (object instanceof GeneratedOutput) {
			return new GeneratedCodeModuleView((GeneratedOutput) object, (GeneratorController) controller);
		}

		else if (object instanceof GenerationRepository) {
			return new CGRepositoryModuleView((CGRepository) object, (GeneratorController) controller);
		}

		else if (object instanceof CGFile) {
			return new CGFileHistoryModuleView((CGFile) object, (GeneratorController) controller);
		}

		else if (object instanceof CGTemplate) {
			return new CGTemplateFileModuleView((CGTemplate) object, (GeneratorController) controller);
		}
		return null;
	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		if (moduleView.getRepresentedObject() instanceof CGObject) {
			this.generatorController._lastEditedCGRepository = AbstractGCAction.repositoryForObject((CGObject) moduleView
					.getRepresentedObject());
		}
		this.generatorController.refreshFooter();
		if (moduleView instanceof CGFileModuleView) {
			((CGFileModuleView) moduleView).refresh();
		}
	}

}