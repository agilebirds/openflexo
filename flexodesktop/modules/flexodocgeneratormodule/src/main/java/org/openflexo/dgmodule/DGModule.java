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
package org.openflexo.dgmodule;

import java.util.logging.Logger;

import org.openflexo.dgmodule.controller.DGController;
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
public class DGModule extends FlexoModule {

	private static final Logger logger = Logger.getLogger(DGModule.class.getPackage().getName());

	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[] { Inspectors.GENERATORS, Inspectors.DE, Inspectors.DG };

	public DGModule(InteractiveFlexoEditor projectEditor) throws Exception {
		super(projectEditor);
		setFlexoController(new DGController(projectEditor, this));
		DGPreferences.init(getDGController());
		/*if (getProject().getGeneratedDoc().getGeneratedRepositories().size() == 0) {
			getDGController().setCurrentEditedObjectAsModuleView(getProject().getGeneratedDoc());
			getDGController().selectAndFocusObject(getProject().getGeneratedDoc());
		} else {
			getDGController().setCurrentEditedObjectAsModuleView(getProject().getGeneratedDoc().getGeneratedRepositories().firstElement());
			getDGController().selectAndFocusObject(getProject().getGeneratedDoc().getGeneratedRepositories().firstElement());
		}*/
		getDGController().setCurrentEditedObjectAsModuleView(getProject().getTOCData());
		getDGController().selectAndFocusObject(getProject().getTOCData());
	}

	@Override
	public InspectorGroup[] getInspectorGroups() {
		return inspectorGroups;
	}

	public DGController getDGController() {
		return (DGController) getFlexoController();
	}

	/**
	 * Overrides getDefaultObjectToSelect
	 * 
	 * @see org.openflexo.module.FlexoModule#getDefaultObjectToSelect()
	 */
	@Override
	public FlexoModelObject getDefaultObjectToSelect() {
		return getProject().getGeneratedDoc();
	}

	/**
	 * Overrides moduleWillClose
	 * 
	 * @see org.openflexo.module.FlexoModule#moduleWillClose()
	 */
	@Override
	public void moduleWillClose() {
		super.moduleWillClose();
		getProject().getGeneratedDoc().setFactory(null);
		DGPreferences.reset();
	}
}
