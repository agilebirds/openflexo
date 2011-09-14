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
package org.openflexo.oe.controller;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.module.FlexoModule;
import org.openflexo.oe.controller.action.OEControllerActionInitializer;
import org.openflexo.oe.view.OEFrame;
import org.openflexo.oe.view.OEMainPane;
import org.openflexo.oe.view.menu.OEMenuBar;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.view.controller.SelectionManagingController;
import org.openflexo.view.menu.FlexoMenuBar;


/**
 * Controller for this module
 * 
 * @author yourname
 */
public class OEController extends FlexoController implements SelectionManagingController
{

	private static final Logger logger = Logger.getLogger(OEController.class.getPackage()
			.getName());

	// ================================================
	// ============= Instance variables ===============
	// ================================================

	protected OEMenuBar _oeMenuBar;
	protected OEFrame _frame;
	protected OEKeyEventListener _oeKeyEventListener;
	private OESelectionManager _selectionManager;

    public final ShemaPerspective SHEMA_PERSPECTIVE;
    public final OntologyPerspective ONTOLOGY_PERSPECTIVE;

	// ================================================
	// ================ Constructor ===================
	// ================================================

	/**
	 * Default constructor
	 */
	public OEController(InteractiveFlexoEditor projectEditor, FlexoModule module) throws Exception
	{
		super(projectEditor,module);
		_oeMenuBar = (OEMenuBar)createAndRegisterNewMenuBar();
		_oeKeyEventListener = new OEKeyEventListener(this);
		_frame = new OEFrame(FlexoCst.BUSINESS_APPLICATION_VERSION_NAME, this, _oeKeyEventListener, _oeMenuBar);
		init(_frame, _oeKeyEventListener, _oeMenuBar);

		// At this point the InspectorController is not yet loaded
		_selectionManager = new OESelectionManager(this);

	       addToPerspectives(SHEMA_PERSPECTIVE = new ShemaPerspective(this));
	       addToPerspectives(ONTOLOGY_PERSPECTIVE = new OntologyPerspective(this));

	       setDefaultPespective(SHEMA_PERSPECTIVE);
	       
	        SwingUtilities.invokeLater(new Runnable() {
	            @Override
				public void run()
	            {
	                switchToPerspective(getDefaultPespective());
	                setCurrentEditedObjectAsModuleView(getProject().getShemaLibrary());
	            }
	        });

	}

	@Override
	public ControllerActionInitializer createControllerActionInitializer()
	{
		return new OEControllerActionInitializer(this);
	}


	/**
	 * Creates a new instance of MenuBar for the module this 
	 * controller refers to
	 * 
	 * @return
	 */
	@Override
	protected FlexoMenuBar createNewMenuBar()
	{
		return new OEMenuBar(this);
	}

	/**
	 * Init inspectors
	 */
	@Override
	public void initInspectors()
	{
		super.initInspectors();
		if (getSharedInspectorController() != null) getOESelectionManager().addObserver(getSharedInspectorController());
		if (getDocInspectorController() != null) getOESelectionManager().addObserver(getDocInspectorController());

		if (USE_NEW_INSPECTOR_SCHEME) {
			loadInspectorGroup("Ontology");
		}

	}

	public void loadRelativeWindows()
	{
		// Build eventual relative windows
	}

	// ================================================
	// ============== Instance method =================
	// ================================================

	public OEFrame getMainFrame()
	{
		return _frame;
	}

	public OEMenuBar getEditorMenuBar()
	{
		return _oeMenuBar;
	}

	public void showBrowser()
	{
		if (getMainPane() != null) {
			((OEMainPane)getMainPane()).showBrowser();
		}
	}

	public void hideBrowser()
	{
		if (getMainPane() != null) {
			((OEMainPane)getMainPane()).hideBrowser();
		}
	}

	@Override
	protected FlexoMainPane createMainPane()
	{
		return new OEMainPane(getEmptyPanel(), getMainFrame(), this);
	}

	public OEKeyEventListener getKeyEventListener()
	{
		return _oeKeyEventListener;
	}

	// ================================================
		// ============ Selection management ==============
			// ================================================

	@Override
	public SelectionManager getSelectionManager()
	{
		return getOESelectionManager();
	}

	public OESelectionManager getOESelectionManager()
	{
		return _selectionManager;
	}

	/**
	 * Select the view representing supplied object, if this view exists. Try
	 * all to really display supplied object, even if required view is not the
	 * current displayed view
	 * 
	 * @param object: the object to focus on
	 */
	@Override
	public void selectAndFocusObject(FlexoModelObject object)
	{
		// TODO: Implements this
		setCurrentEditedObjectAsModuleView(object);
	}

	// ================================================
	// ============ Exception management ==============
	// ================================================


	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName,
			Object value, Throwable exception)
	{
		// TODO: Handles here exceptions that may be thrown through the inspector
		return super.handleException(inspectable, propertyName, value, exception);
	}

	@Override
	public String getWindowTitleforObject(FlexoModelObject object) 
	{
		if (getCurrentPerspective() == SHEMA_PERSPECTIVE) return SHEMA_PERSPECTIVE.getWindowTitleforObject(object);
		if (getCurrentPerspective() == ONTOLOGY_PERSPECTIVE) return ONTOLOGY_PERSPECTIVE.getWindowTitleforObject(object);
		return object.getFullyQualifiedName();
	}
	
}
