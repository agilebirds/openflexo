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
import javax.swing.JPanel;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.icon.SGIconLibrary;
import org.openflexo.sgmodule.view.CGTemplateFileModuleView;
import org.openflexo.sgmodule.view.GeneratedSourcesModuleView;
import org.openflexo.sgmodule.view.ParsedCGFileModuleView;
import org.openflexo.sgmodule.view.SourceRepositoryModuleView;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

public class ModelReinjectionPerspective extends FlexoPerspective<FlexoModelObject> {
	private final SGController _controller;

	public ModelReinjectionPerspective(SGController controller) {
		super("model_reinjection");
		_controller = controller;
	}

	@Override
	public ImageIcon getActiveIcon() {
		return SGIconLibrary.SG_MRP_ACTIVE_ICON;
	}

	@Override
	public ImageIcon getSelectedIcon() {
		return SGIconLibrary.SG_MRP_SELECTED_ICON;
	}

	@Override
	public JPanel getFooter() {
		return _controller.getFooter();
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
		return ((object instanceof GeneratedSources) || (object instanceof SourceRepository) || (object instanceof CGFile) || (object instanceof CGTemplate));
	}

	@Override
	public ModuleView<? extends FlexoModelObject> createModuleViewForObject(FlexoModelObject object, FlexoController controller) {
		if (object instanceof GeneratedSources) {
			return new GeneratedSourcesModuleView((GeneratedSources) object, (SGController) controller);
		} else if (object instanceof SourceRepository) {
			return new SourceRepositoryModuleView((SourceRepository) object, (SGController) controller);
		} else if (object instanceof CGFile) {
			return new ParsedCGFileModuleView((CGFile) object, (SGController) controller);
		} else if (object instanceof CGTemplate) {
			return new CGTemplateFileModuleView((CGTemplate) object, (SGController) controller);
		}
		return null;
	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		_controller.notifyModuleViewDisplayed(moduleView);
	}

}
