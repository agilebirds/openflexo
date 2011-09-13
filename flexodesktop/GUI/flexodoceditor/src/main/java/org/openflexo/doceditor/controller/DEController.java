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
package org.openflexo.doceditor.controller;

import java.util.logging.Logger;


import org.openflexo.FlexoCst;
import org.openflexo.components.ProgressWindow;
import org.openflexo.doceditor.controller.action.DEControllerActionInitializer;
import org.openflexo.doceditor.menu.DEMenuBar;
import org.openflexo.doceditor.view.DEFrame;
import org.openflexo.doceditor.view.DEMainPane;
import org.openflexo.doceditor.view.listener.DEKeyEventListener;


import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCObject;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.view.controller.SelectionManagingController;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Controller for Doc Editor module
 * 
 * @author gpolet
 */
public class DEController extends FlexoController implements FlexoObserver, SelectionManagingController
{

	protected static final Logger logger = Logger.getLogger(DEController.class.getPackage().getName());

	public final FlexoPerspective<TOCObject> DOCEDITOR_PERSPECTIVE = new DocEditorPerspective();

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	protected DEMenuBar _generatorMenuBar;

	protected DEFrame _generatorFrame;

	protected DEKeyEventListener _generatorKeyEventListener;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Default constructor
	 * 
	 * @param workflowFile
	 * @throws Exception
	 */
	public DEController(InteractiveFlexoEditor projectEditor, FlexoModule module) throws Exception
	{
		super(projectEditor, module);

		addToPerspectives(DOCEDITOR_PERSPECTIVE);
		_generatorMenuBar = (DEMenuBar) createAndRegisterNewMenuBar();
		_generatorKeyEventListener = new DEKeyEventListener(this);
		_generatorFrame = new DEFrame(FlexoCst.BUSINESS_APPLICATION_VERSION_NAME, this, _generatorKeyEventListener, _generatorMenuBar);
		init(_generatorFrame, _generatorKeyEventListener, _generatorMenuBar);
		if (_selectionManager == null) {
			_selectionManager = createSelectionManager();
		}

	}

	protected DESelectionManager createSelectionManager() {
		return new DESelectionManager(this);
	}

	protected DEClipboard createClipboard(DESelectionManager selectionManager) {
		return new DEClipboard(selectionManager, getEditorMenuBar().getEditMenu(this).copyItem, getEditorMenuBar().getEditMenu(this).pasteItem, getEditorMenuBar().getEditMenu(this).cutItem);
	}

	protected DEContextualMenuManager createContextualMenuManager(DESelectionManager selectionManager) {
		return new DEContextualMenuManager(selectionManager,getEditor(),this);
	}

	/**
	 * Creates a new instance of MenuBar for the module this controller refers
	 * to
	 * 
	 * @return
	 */
	@Override
	protected FlexoMenuBar createNewMenuBar()
	{
		return new DEMenuBar(this);
	}

	public DEMenuBar getEditorMenuBar()
	{
		return _generatorMenuBar;
	}

	/*
	 * public ModuleView getCurrentModuleView() { return null; }
	 */

	public DEKeyEventListener getGeneratorKeyEventListener()
	{
		return _generatorKeyEventListener;
	}

	@Override
	protected FlexoMainPane createMainPane()
	{
		return new DEMainPane(this, getEmptyPanel(), getFlexoFrame());
	}

	@Override
	public DEControllerActionInitializer createControllerActionInitializer()
	{
		return new DEControllerActionInitializer(this);
	}

	public DEKeyEventListener getKeyEventListener()
	{
		return _generatorKeyEventListener;
	}

	public void initProgressWindow(String msg, int steps)
	{
		ProgressWindow.showProgressWindow(msg, steps);
	}

	public void refreshProgressWindow(String msg)
	{
		ProgressWindow.setProgressInstance(msg);
	}

	public void refreshSecondaryProgressWindow(String msg)
	{
		ProgressWindow.setSecondaryProgressInstance(msg);
	}

	public void disposeProgressWindow()
	{
		ProgressWindow.hideProgressWindow();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
	}

	/**
	 * 
	 */
	 @Override
	public void initInspectors()
	{
		super.initInspectors();
		getDESelectionManager().addObserver(getSharedInspectorController());
	}

	 // =========================================================
	 // ================ Selection management ===================
	 // =========================================================

	 protected DESelectionManager _selectionManager;

	 @Override
	public DESelectionManager getSelectionManager()
	 {
		 return getDESelectionManager();
	 }

	 public DESelectionManager getDESelectionManager()
	 {
		 return _selectionManager;
	 }

	 /**
	  * Select the view representing supplied object, if this view exists. Try
	  * all to really display supplied object, even if required view is not the
	  * current displayed view
	  * 
	  * @param object:
	  *            the object to focus on
	  */
	 @Override
	public void selectAndFocusObject(FlexoModelObject object)
	 {
		 if (object instanceof TOCEntry) {
			 setCurrentEditedObjectAsModuleView(object);
		 }
		 getSelectionManager().setSelectedObject(object);
	 }

	 /**
	  * @return
	  */
	 public boolean getIgnoreScreenshotVisualization()
	 {
		 return true;
	 }

	@Override
	public String getWindowTitleforObject(FlexoModelObject object) {
		if (object instanceof GeneratedOutput) {
			return FlexoLocalization.localizedForKey("generated_code");
		} else if (object instanceof GenerationRepository) {
			return ((GenerationRepository) object).getName();
		} else if (object instanceof CGFile) {
			CGFile cgFile = (CGFile) object;
			if (cgFile.getGeneratedCode().getGeneratedRepositories().size() > 1) {
				boolean synched = false;
				for (int i = 0; i < cgFile.getGeneratedCode().getGeneratedRepositories().size() && !synched; i++) {
					DGRepository rep = (DGRepository) cgFile.getGeneratedCode().getGeneratedRepositories().get(i);
					if (rep.getGenerationStatus() != GenerationStatus.CodeGenerationNotSynchronized)
						synched = true;
				}
				if (synched)
					return cgFile.getRepository().getName() + " " + cgFile.getResource().getFile().getName() + (cgFile.isEdited() ? "[" + FlexoLocalization.localizedForKey("edited") + "]" : "");
			}
			return cgFile.getResource().getFile().getName() + (cgFile.isEdited() ? "[" + FlexoLocalization.localizedForKey("edited") + "]" : "");
		} else if (object instanceof CGTemplate) {
			CGTemplate cgTemplateFile = (CGTemplate) object;
			return cgTemplateFile.getTemplateName()
					+ (cgTemplateFile instanceof CGTemplateFile && ((CGTemplateFile) cgTemplateFile).isEdited() ? "[" + FlexoLocalization.localizedForKey("edited") + "]" : "");
		} else if (object instanceof TOCRepository) {
			return FlexoLocalization.localizedForKey("table_of_content" + ": ") + ((TOCRepository) object).getTitle();
		} else if (object instanceof TOCData) {
			return FlexoLocalization.localizedForKey("toc_data");
		} else if (object instanceof TOCEntry) {
			TOCEntry entry = (TOCEntry) object;
			return entry.getTitle() != null ? entry.getTitle() : FlexoLocalization.localizedForKey("untitled");
		}

		return null;
	}
}
