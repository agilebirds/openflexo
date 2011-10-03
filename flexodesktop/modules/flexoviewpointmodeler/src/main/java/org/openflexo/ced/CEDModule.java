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
package org.openflexo.ced;

import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.application.FlexoApplication;
import org.openflexo.ced.controller.CEDController;
import org.openflexo.ced.drawingshema.CalcDrawingShemaController;
import org.openflexo.ced.palette.CalcPaletteController;
import org.openflexo.components.ProgressWindow;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.viewpoint.CalcDrawingShema;
import org.openflexo.foundation.viewpoint.CalcPalette;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.external.ExternalCEDModule;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.InteractiveFlexoEditor;


/**
 * CalcEditor module
 * 
 * @author sylvain
 */
public class CEDModule extends FlexoModule implements ExternalCEDModule
{

	private static final Logger logger = Logger.getLogger(CEDModule.class.getPackage().getName());
	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[] { Inspectors.CED, Inspectors.OE };

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
		ModuleLoader.initializeSingleModule(Module.FPS_MODULE);
	}

	public CEDModule() throws Exception
	{
		super(InteractiveFlexoEditor.makeInteractiveEditorWithoutProject());
		setFlexoController(new CEDController(this));
		getCEDController().loadRelativeWindows();
		CEDPreferences.init(getCEDController());
		ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("build_editor"));

		// Put here a code to display default view
		getCEDController().setCurrentEditedObjectAsModuleView(getCEDController().getCalcLibrary());


		// Retain here all necessary resources
		//retain(<the_required_resource_data>);
	}

	@Override
	public InspectorGroup[] getInspectorGroups()
	{
		return inspectorGroups;
	}

	public CEDController getCEDController()
	{
		return (CEDController) getFlexoController();
	}

	@Override
	public FlexoModelObject getDefaultObjectToSelect()
	{
		return getCEDController().getCalcLibrary();
	}

	@Override
	public void moduleWillClose()
	{
		getCEDController().getEditorMenuBar().getFileMenu(getCEDController()).closeModule();
		super.moduleWillClose();
		CEDPreferences.reset();
	}

	@Override
	public float getScreenshotQuality()
	{
		float reply = Float.valueOf(CEDPreferences.getScreenshotQuality())/100f;
		if(reply>1) {
			return 1f;
		}
		if (reply<0.1f) {
			return 0.1f;
		}
		return reply;
	}

	@Override
	public JComponent createScreenshotForShema(CalcDrawingShema target)
	{
		if (target==null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Cannot create screenshot for null target!");
			}
			return null;
		}

		logger.info("createScreenshotForShema() "+target);

		screenshotObject = target;

		// prevent process to be marked as modified during screenshot generation
		target.setIgnoreNotifications();
		screenshotController = new CalcDrawingShemaController(getCEDController(),target,true);

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
	public JComponent createScreenshotForPalette(CalcPalette target)
	{
		if (target==null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Cannot create screenshot for null target!");
			}
			return null;
		}

		logger.info("createScreenshotForPalette() "+target);

		screenshotObject = target;

		// prevent process to be marked as modified during screenshot generation
		target.setIgnoreNotifications();
		screenshotController = new CalcPaletteController(getCEDController(),target,true);

		screenshot = screenshotController.getDrawingView();
		drawWorkingArea = screenshot.getDrawingGraphicalRepresentation().getDrawWorkingArea();
		screenshot.getDrawingGraphicalRepresentation().setDrawWorkingArea(false);
		screenshot.getPaintManager().disablePaintingCache();
		screenshot.validate();
		Dimension d = new Dimension(
				(int)screenshot.getDrawingGraphicalRepresentation().getWidth(),
				(int)screenshot.getDrawingGraphicalRepresentation().getHeight());
		screenshot.setSize(d);
		screenshot.setPreferredSize(d);
		target.resetIgnoreNotifications();

		return screenshot;
	}

	@Override
	public void finalizeScreenshotGeneration()
	{
		logger.info("finalizeScreenshotGeneration()");

		if (screenshot != null) {
			if (screenshot.getDrawingGraphicalRepresentation() != null) {
				screenshotObject.setIgnoreNotifications();
				screenshot.getDrawingGraphicalRepresentation().setDrawWorkingArea(drawWorkingArea);
				screenshotObject.resetIgnoreNotifications();
			}
			screenshotController.delete();
			if (screenshot.getParent()!=null) {
				screenshot.getParent().remove(screenshot);
			}
			screenshotController = null;
			screenshot = null;
		}
	}


}
