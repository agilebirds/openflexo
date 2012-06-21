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
package org.openflexo.wse;

import org.openflexo.ApplicationContext;
import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.external.ExternalWSEModule;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wse.controller.WSEController;

/**
 * WSE module
 * 
 * @author yourname
 */
public class WSEModule extends FlexoModule implements ExternalWSEModule {
	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[] { Inspectors.WSE };

	public WSEModule(ApplicationContext applicationContext) throws Exception {
		super(applicationContext);
		WSEPreferences.init();
		ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("build_editor"));
	}

	@Override
	public Module getModule() {
		return Module.WSE_MODULE;
	}

	@Override
	protected FlexoController createControllerForModule() {
		return new WSEController(this);
	}

	@Override
	public InspectorGroup[] getInspectorGroups() {
		return inspectorGroups;
	}

	public WSEController getWSEController() {
		return (WSEController) getFlexoController();
	}

	/**
	 * Overrides getDefaultObjectToSelect
	 * 
	 * @see org.openflexo.module.FlexoModule#getDefaultObjectToSelect(FlexoProject)
	 */
	@Override
	public FlexoModelObject getDefaultObjectToSelect(FlexoProject project) {
		return project;
	}

}
