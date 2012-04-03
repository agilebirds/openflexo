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
package org.openflexo.doceditormodule;

import java.util.logging.Logger;

import org.openflexo.doceditor.DEPreferences;
import org.openflexo.doceditor.controller.DEController;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.module.FlexoModule;
import org.openflexo.view.controller.InteractiveFlexoEditor;

/**
 * Documentation generator module
 * 
 * @author gpolet
 */
public class DEModule extends FlexoModule {

	private static final Logger logger = Logger.getLogger(DEModule.class.getPackage().getName());
	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[] { Inspectors.DE };

	public DEModule(InteractiveFlexoEditor projectEditor) throws Exception {
		super(projectEditor);
		setFlexoController(new DEController(projectEditor, this));
		DEPreferences.init(getDEController());
		if (getProject().getTOCData().getRepositories().size() == 0) {
			getDEController().setCurrentEditedObjectAsModuleView(getProject().getTOCData());
			getDEController().selectAndFocusObject(getProject().getTOCData());
		} else {
			getDEController().setCurrentEditedObjectAsModuleView(getProject().getTOCData().getRepositories().firstElement());
			getDEController().selectAndFocusObject(getProject().getTOCData().getRepositories().firstElement());
		}
	}

	@Override
	public InspectorGroup[] getInspectorGroups() {
		return inspectorGroups;
	}

	public DEController getDEController() {
		return (DEController) getFlexoController();
	}

	/**
	 * Overrides getDefaultObjectToSelect
	 * 
	 * @see org.openflexo.module.FlexoModule#getDefaultObjectToSelect()
	 */
	@Override
	public FlexoModelObject getDefaultObjectToSelect() {
		return getProject().getTOCData();
	}

	/**
	 * Overrides moduleWillClose
	 * 
	 * @see org.openflexo.module.FlexoModule#moduleWillClose()
	 */
	@Override
	public void moduleWillClose() {
		super.moduleWillClose();
		DEPreferences.reset();
	}
}
