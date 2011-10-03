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
package org.openflexo.oe;

import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.application.FlexoApplication;
import org.openflexo.components.ProgressWindow;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.view.OEShema;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.external.ExternalOEModule;
import org.openflexo.oe.controller.OEController;
import org.openflexo.oe.shema.OEShemaController;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.InteractiveFlexoEditor;


/**
 * Ontology Editor module
 * 
 * @author yourname
 */
public class OEModule extends FlexoModule implements ExternalOEModule
{
	private static final Logger logger = Logger.getLogger(OEModule.class.getPackage().getName());

	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[]{Inspectors.OE};

	private DrawingController<? extends Drawing<? extends FlexoModelObject>> screenshotController;
	private DrawingView<? extends Drawing<? extends FlexoModelObject>> screenshot = null;
	private boolean drawWorkingArea;
	private FlexoModelObject screenshotObject;

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
		ModuleLoader.initializeSingleModule(Module.OE_MODULE);
	}

	public OEModule(InteractiveFlexoEditor projectEditor) throws Exception
	{
		super(projectEditor);
		setFlexoController(new OEController(projectEditor,this));
		getOEController().loadRelativeWindows();
		OEPreferences.init(getOEController());
		ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("build_editor"));

		// Put here a code to display default view
		//getOEController().setCurrentEditedObjectAsModuleView(projectEditor.getProject());

		projectEditor.getProject().getStringEncoder()._addConverter(GraphicalRepresentation.POINT_CONVERTER);
		projectEditor.getProject().getStringEncoder()._addConverter(GraphicalRepresentation.RECT_POLYLIN_CONVERTER);

		// Retain here all necessary resources
		//retain(<the_required_resource_data>);
	}

	@Override
	public InspectorGroup[] getInspectorGroups()
	{
		return inspectorGroups;
	}

	public OEController getOEController()
	{
		return (OEController) getFlexoController();
	}

	@Override
	public FlexoModelObject getDefaultObjectToSelect()
	{
		return getOEController().getProject().getShemaLibrary();
	}

	@Override
	public float getScreenshotQuality()
	{
		float reply = Float.valueOf(OEPreferences.getScreenshotQuality())/100f;
		if(reply>1) {
			return 1f;
		}
		if (reply<0.1f) {
			return 0.1f;
		}
		return reply;
	}

	@Override
	public JComponent createScreenshotForShema(OEShema target)
	{
		if (target==null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Cannot create screenshot for null target!");
			}
			return null;
		}

		screenshotObject = target;

		// prevent process to be marked as modified during screenshot generation
		target.setIgnoreNotifications();
		screenshotController = new OEShemaController(getOEController(),target);

		screenshot = screenshotController.getDrawingView();
		drawWorkingArea = screenshot.getDrawingGraphicalRepresentation().getDrawWorkingArea();
		screenshot.getDrawingGraphicalRepresentation().setDrawWorkingArea(false);
		screenshot.getPaintManager().disablePaintingCache();
		screenshot.validate();
		Dimension d = screenshot.getComputedMinimumSize();
		d.height += 20;
		d.width +=20;
		screenshot.setSize(d);
		screenshot.setPreferredSize(d);
		target.resetIgnoreNotifications();

		return screenshot;
	}

	@Override
	public void finalizeScreenshotGeneration()
	{
		if (screenshot != null) {
			screenshotObject.setIgnoreNotifications();
			screenshot.getDrawingGraphicalRepresentation().setDrawWorkingArea(drawWorkingArea);
			screenshotObject.resetIgnoreNotifications();
			screenshotController.delete();
			if (screenshot.getParent()!=null) {
				screenshot.getParent().remove(screenshot);
			}
			screenshotController = null;
			screenshot = null;
		}
	}


}
