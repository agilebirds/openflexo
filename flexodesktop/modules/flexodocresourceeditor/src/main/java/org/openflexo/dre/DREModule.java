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
package org.openflexo.dre;

import org.openflexo.application.FlexoApplication;
import org.openflexo.components.ProgressWindow;
import org.openflexo.dre.controller.DREController;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.external.ExternalDREModule;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.InteractiveFlexoEditor;


/**
 * DocResourceEditor module
 *
 * @author yourname
 */
public class DREModule extends FlexoModule implements ExternalDREModule
{
	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[]{Inspectors.DRE};
	/**
	 * The 'main' method of module allow to launch this module as a
	 * single-module application
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		ToolBox.setPlatform();
		FlexoLoggingManager.initialize();
		FlexoApplication.initialize();
		ModuleLoader.initializeSingleModule(Module.DRE_MODULE);
	}

	public DREModule() throws Exception
	{
		super(InteractiveFlexoEditor.makeInteractiveEditorWithoutProject());
		setFlexoController(new DREController(this));
		getDREController().loadRelativeWindows();
		DREPreferences.init(getDREController());
		ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("build_editor"));

		// Put here a code to display default view
		getDREController().setCurrentEditedObjectAsModuleView(DocResourceManager.instance().getDocResourceCenter().getRootFolder());

	}

	@Override
	public InspectorGroup[] getInspectorGroups()
	{
		return inspectorGroups;
	}

	public DREController getDREController()
	{
		return (DREController) getFlexoController();
	}

	/**
	 * Overrides getDefaultObjectToSelect
	 * @see org.openflexo.module.FlexoModule#getDefaultObjectToSelect()
	 */
	@Override
	public FlexoModelObject getDefaultObjectToSelect()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Overrides moduleWillClose
	 * @see org.openflexo.module.FlexoModule#moduleWillClose()
	 */
	@Override
	public void moduleWillClose()
	{
		super.moduleWillClose();
		DREPreferences.reset();
	}
}
