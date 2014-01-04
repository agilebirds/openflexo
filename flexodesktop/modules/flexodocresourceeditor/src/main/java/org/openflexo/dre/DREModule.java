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

import org.openflexo.ApplicationContext;
import org.openflexo.ch.DocResourceManager;
import org.openflexo.components.ProgressWindow;
import org.openflexo.dre.controller.DREController;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.icon.DREIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.external.ExternalDREModule;
import org.openflexo.view.controller.FlexoController;

/**
 * DocResourceEditor module
 * 
 * @author yourname
 */
public class DREModule extends FlexoModule implements ExternalDREModule {

	public static final String DRE_MODULE_SHORT_NAME = "DRE";

	public static final String DRE_MODULE_NAME = "doc_resource_manager";

	public static class DocResourceEditor extends Module {

		public DocResourceEditor() {
			super(DRE_MODULE_NAME, DRE_MODULE_SHORT_NAME, "org.openflexo.dre.DREModule", "modules/flexodocresourceeditor", "10010", "dre",
					DREIconLibrary.DRE_SMALL_ICON, DREIconLibrary.DRE_MEDIUM_ICON, DREIconLibrary.DRE_MEDIUM_ICON_WITH_HOVER,
					DREIconLibrary.DRE_BIG_ICON, false);
		}

	}

	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[] { Inspectors.DRE };

	public DREModule(ApplicationContext applicationContext) {
		super(applicationContext);
		DREPreferences.init();
	}

	@Override
	public void initModule() {
		super.initModule();
		ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("build_editor"));
		// Put here a code to display default view
		getDREController().setCurrentEditedObjectAsModuleView(DocResourceManager.instance().getDocResourceCenter().getRootFolder());
	}

	@Override
	public Module getModule() {
		return Module.DRE_MODULE;
	}

	@Override
	protected FlexoController createControllerForModule() {
		return new DREController(this);
	}

	@Override
	public InspectorGroup[] getInspectorGroups() {
		return inspectorGroups;
	}

	public DREController getDREController() {
		return (DREController) getFlexoController();
	}

	/**
	 * Overrides moduleWillClose
	 * 
	 * @see org.openflexo.module.FlexoModule#moduleWillClose()
	 */
	@Override
	public void moduleWillClose() {
		super.moduleWillClose();
		DREPreferences.reset();
	}
}
