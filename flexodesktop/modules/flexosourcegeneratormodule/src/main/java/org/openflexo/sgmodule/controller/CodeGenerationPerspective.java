/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.sgmodule.controller;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.icon.SGIconLibrary;
import org.openflexo.sgmodule.SGModule;
import org.openflexo.sgmodule.TechnologyModuleGUIFactory;
import org.openflexo.sgmodule.view.CGFileModuleView;
import org.openflexo.sgmodule.view.CGTemplateFileModuleView;
import org.openflexo.sgmodule.view.GeneratedSourcesModuleView;
import org.openflexo.sgmodule.view.ImplementationModelView;
import org.openflexo.sgmodule.view.SourceRepositoryModuleView;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class CodeGenerationPerspective extends FlexoPerspective {

	private final SGController _controller;

	public CodeGenerationPerspective(SGController controller) {
		super("code_generation");
		_controller = controller;
		setTopLeftView(_controller.getBrowserView());
	}

	@Override
	public ImageIcon getActiveIcon() {
		return SGIconLibrary.SG_SGP_ACTIVE_ICON;
	}

	@Override
	public ImageIcon getSelectedIcon() {
		return SGIconLibrary.SG_SGP_SELECTED_ICON;
	}

	@Override
	public JComponent getFooter() {
		return _controller.getFooter();
	}

	@Override
	public FlexoModelObject getDefaultObject(FlexoModelObject proposedObject) {
		if (hasModuleViewForObject(proposedObject)) {
			return proposedObject;
		} else {
			return null;
		}
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof GeneratedSources || object instanceof SourceRepository || object instanceof CGFile
				|| object instanceof CGTemplate || object instanceof ImplementationModel || object instanceof TechnologyModelObject;
	}

	@Override
	public ModuleView<? extends FlexoModelObject> createModuleViewForObject(FlexoModelObject object, FlexoController controller) {
		if (object instanceof GeneratedSources) {
			return new GeneratedSourcesModuleView((GeneratedOutput) object, (SGController) controller);
		} else if (object instanceof SourceRepository) {
			return new SourceRepositoryModuleView((SourceRepository) object, (SGController) controller);
		} else if (object instanceof CGFile) {
			return new CGFileModuleView((CGFile) object, (SGController) controller);
		} else if (object instanceof CGTemplate) {
			return new CGTemplateFileModuleView((CGTemplate) object, (SGController) controller);
		} else if (object instanceof ImplementationModel) {
			return new ImplementationModelView((ImplementationModel) object, (SGController) controller, this);
		} else if (object instanceof TechnologyModelObject) {

			TechnologyModuleGUIFactory technologyModuleGUIFactory = SGModule.getTechnologyModuleGUIFactory(((TechnologyModelObject) object)
					.getTechnologyModuleImplementation().getClass());
			if (technologyModuleGUIFactory != null) {
				ModuleView<? extends FlexoModelObject> view = technologyModuleGUIFactory.createModelView((TechnologyModelObject) object,
						(SGController) controller, this);
				if (view != null) {
					return view;
				}
			}

			return new ImplementationModelView(((TechnologyModelObject) object).getImplementationModel(), (SGController) controller, this);
		}

		return new EmptyPanel<FlexoModelObject>(controller, this, object);

	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		_controller.notifyModuleViewDisplayed(moduleView);
	}

}
