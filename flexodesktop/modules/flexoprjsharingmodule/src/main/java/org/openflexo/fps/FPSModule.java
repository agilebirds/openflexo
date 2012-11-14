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
package org.openflexo.fps;

import java.util.logging.Logger;

import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.fps.controller.FPSController;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.external.ExternalFPSModule;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.controller.InteractiveFlexoEditor;

/**
 * FPS module
 * 
 * @author Sylvain
 */
public class FPSModule extends FlexoModule implements ExternalFPSModule {

	private static final Logger logger = Logger.getLogger(CVSRepository.class.getPackage().getName());
	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[] { Inspectors.FPS };

	public FPSModule() throws Exception {
		super(InteractiveFlexoEditor.makeInteractiveEditorWithoutProject());
		setFlexoController(new FPSController(this));
		getFPSController().loadRelativeWindows();
		FPSPreferences.init(getFPSController());
		ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("build_editor"));

		// Put here a code to display default view
		getFPSController().setCurrentEditedObjectAsModuleView(getFPSController().getRepositories());

		// Retain here all necessary resources
		// retain(<the_required_resource_data>);
	}

	@Override
	public InspectorGroup[] getInspectorGroups() {
		return inspectorGroups;
	}

	public FPSController getFPSController() {
		return (FPSController) getFlexoController();
	}

	@Override
	public FlexoModelObject getDefaultObjectToSelect() {
		return getFPSController().getSharedProject();
	}

	@Override
	public void moduleWillClose() {
		for (CVSRepository repository : getFPSController().getRepositories().getCVSRepositories()) {
			if (repository.getRepositoryExploringDirectory() != null && repository.getRepositoryExploringDirectory().exists()) {
				logger.info("Deleting " + repository.getRepositoryExploringDirectory());
				FileUtils.recursiveDeleteFile(repository.getRepositoryExploringDirectory());
			}
		}
		super.moduleWillClose();
		FPSPreferences.reset();
	}
}
