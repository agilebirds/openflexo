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
package org.openflexo.dm;

import java.awt.Dimension;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.ApplicationContext;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.dm.view.erdiagram.ERDiagramController;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.external.ExternalDMModule;
import org.openflexo.view.controller.FlexoController;

/**
 * Data Model Editor module
 * 
 * @author sguerin
 */
public class DMModule extends FlexoModule implements ExternalDMModule {

	private static final Logger logger = Logger.getLogger(DMModule.class.getPackage().getName());
	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[] { Inspectors.DM };

	public DMModule(ApplicationContext applicationContext) throws Exception {
		super(applicationContext);
		getDMController().loadRelativeWindows();
		DMPreferences.init();
	}

	@Override
	public Module getModule() {
		return Module.DM_MODULE;
	}

	@Override
	protected FlexoController createControllerForModule() {
		return new DMController(this);
	}

	@Override
	public InspectorGroup[] getInspectorGroups() {
		return inspectorGroups;
	}

	public File getInspectorDirectory() {
		// No inspectors to load !!!
		return null;
	}

	public DMController getDMController() {
		return (DMController) getFlexoController();
	}

	/**
	 * Overrides getDefaultObjectToSelect
	 * 
	 * @see org.openflexo.module.FlexoModule#getDefaultObjectToSelect(FlexoProject)
	 */
	@Override
	public FlexoModelObject getDefaultObjectToSelect(FlexoProject project) {
		return project.getDataModel();
	}

	@Override
	public void showDMEntity(DMEntity entity) {
		getDMController().setCurrentEditedObjectAsModuleView(entity);
		getDMController().selectAndFocusObject(entity);
	}

	/**
	 * Overrides moduleWillClose
	 * 
	 * @see org.openflexo.module.FlexoModule#moduleWillClose()
	 */
	@Override
	public void moduleWillClose() {
		super.moduleWillClose();
		DMPreferences.reset();
	}

	private DrawingController<? extends Drawing<ERDiagram>> screenshotController;
	private DrawingView<? extends Drawing<ERDiagram>> screenshot = null;

	@Override
	public JComponent createScreenshotForObject(ERDiagram target) {
		if (target == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Cannot create screenshot for null target!");
			}
			return null;
		}
		screenshotController = new ERDiagramController(getDMController(), target);
		screenshot = screenshotController.getDrawingView();
		screenshot.getDrawingGraphicalRepresentation().setDrawWorkingArea(false);
		screenshot.getPaintManager().disablePaintingCache();
		screenshot.validate();
		Dimension d = screenshot.getComputedMinimumSize();
		d.height += 20;
		d.width += 20;
		screenshot.setSize(d);
		screenshot.setPreferredSize(d);
		return screenshot;
	}

}
