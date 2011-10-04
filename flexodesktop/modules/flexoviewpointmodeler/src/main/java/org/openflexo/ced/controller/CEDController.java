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
package org.openflexo.ced.controller;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import org.openflexo.FlexoCst;
import org.openflexo.ced.controller.action.CEDControllerActionInitializer;
import org.openflexo.ced.view.CEDFrame;
import org.openflexo.ced.view.CEDMainPane;
import org.openflexo.ced.view.menu.CEDMenuBar;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.ontology.ImportedOntology;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.viewpoint.ExampleDrawingShema;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.icon.OntologyIconLibrary;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.ModuleLoader;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.SelectionManagingController;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Controller for this module
 * 
 * @author yourname
 */
public class CEDController extends FlexoController implements SelectionManagingController
{

	private static final Logger logger = Logger.getLogger(CEDController.class.getPackage()
			.getName());

	// ================================================
	// ============= Instance variables ===============
	// ================================================

	protected CEDMenuBar _cedMenuBar;
	protected CEDFrame _frame;
	
	protected CEDKeyEventListener _cedKeyEventListener;
	private final CEDSelectionManager _selectionManager;

	private final FlexoResourceCenter resourceCenter;
	private final ViewPointLibrary viewPointLibrary;
	private final OntologyLibrary baseOntologyLibrary;

    public final ViewPointPerspective VIEW_POINT_PERSPECTIVE;
    public final OntologyPerspective ONTOLOGY_PERSPECTIVE;
    
	// ================================================
	// ================ Constructor ===================
	// ================================================

	/**
	 * Default constructor
	 */
	public CEDController(FlexoModule module) throws Exception
	{
		super(module.getEditor(),module);

		resourceCenter = ModuleLoader.getFlexoResourceCenter();
		viewPointLibrary = resourceCenter.retrieveViewPointLibrary();
		baseOntologyLibrary = resourceCenter.retrieveBaseOntologyLibrary();
		
		_cedMenuBar = (CEDMenuBar)createAndRegisterNewMenuBar();
		_cedKeyEventListener = new CEDKeyEventListener(this);
		_frame = new CEDFrame(FlexoCst.BUSINESS_APPLICATION_VERSION_NAME, this, _cedKeyEventListener, _cedMenuBar);
		init(_frame, _cedKeyEventListener, _cedMenuBar);

		resourceSavingInfo = new Vector<ResourceSavingInfo>();
		
		_selectionManager = new CEDSelectionManager(this);

		addToPerspectives(VIEW_POINT_PERSPECTIVE = new ViewPointPerspective(this));
		addToPerspectives(ONTOLOGY_PERSPECTIVE = new OntologyPerspective(this));

		setDefaultPespective(VIEW_POINT_PERSPECTIVE);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				switchToPerspective(getDefaultPespective());
				selectAndFocusObject(viewPointLibrary);
			}
		});


	}

	@Override
	public ControllerActionInitializer createControllerActionInitializer()
	{
		return new CEDControllerActionInitializer(this);
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
		return new CEDMenuBar(this);
	}

	/**
	 * Init inspectors
	 */
	@Override
	public void initInspectors()
	{
		super.initInspectors();
		if (getSharedInspectorController() != null) {
			_selectionManager.addObserver(getSharedInspectorController());
		}
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

	public ValidationModel getDefaultValidationModel()
	{
		// If there is a ValidationModel associated to this module, put it here
		return null;
	}

	public CEDFrame getMainFrame()
	{
		return _frame;
	}

	public CEDMenuBar getEditorMenuBar()
	{
		return _cedMenuBar;
	}

	public void showBrowser()
	{
		if (getMainPane() != null) {
			((CEDMainPane)getMainPane()).showBrowser();
		}
	}

	public void hideBrowser()
	{
		if (getMainPane() != null) {
			((CEDMainPane)getMainPane()).hideBrowser();
		}
	}

	@Override
	protected FlexoMainPane createMainPane()
	{
		return new CEDMainPane(getEmptyPanel(), getMainFrame(), this);
	}

	public CEDKeyEventListener getKeyEventListener()
	{
		return _cedKeyEventListener;
	}

	// ================================================
	// ============ Selection management ==============
	// ================================================

	@Override
	public SelectionManager getSelectionManager()
	{
		return getCEDSelectionManager();
	}

	public CEDSelectionManager getCEDSelectionManager()
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
		logger.info("selectAndFocusObject "+object);
		setCurrentEditedObjectAsModuleView(object);
		if (getCurrentPerspective() == VIEW_POINT_PERSPECTIVE) {
			if (object instanceof ViewPointLibrary) {
				ViewPointLibrary cl = (ViewPointLibrary)object;
				if (cl.getViewPoints().size() > 0) {
					getSelectionManager().setSelectedObject(cl.getViewPoints().firstElement());
				} 
			}
			if (object instanceof ImportedOntology) {
				ImportedOntology ontology = (ImportedOntology)object;
				VIEW_POINT_PERSPECTIVE.focusOnOntology(ontology);
				if (ontology.getClasses().size()>0) {
					getSelectionManager().setSelectedObject(ontology.getClasses().firstElement());
				}
			}
			else if (object instanceof ExampleDrawingShema) {
				VIEW_POINT_PERSPECTIVE.focusOnShema((ExampleDrawingShema)object);
			}
			if (object instanceof ViewPointPalette) {
				VIEW_POINT_PERSPECTIVE.focusOnPalette((ViewPointPalette)object);
			}
			if (object instanceof ViewPoint) {
				ViewPoint calc = (ViewPoint)object;
				VIEW_POINT_PERSPECTIVE.focusOnCalc(calc);
				if (calc.getEditionPatterns().size() > 0) {
					getSelectionManager().setSelectedObject(calc.getEditionPatterns().firstElement());
				} 
			}
			if (object instanceof EditionPattern) {
				EditionPattern pattern = (EditionPattern)object;
				if (pattern.getEditionSchemes().size() > 0) {
					getSelectionManager().setSelectedObject(pattern.getEditionSchemes().firstElement());
				} 
			}
		}
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
		//System.out.println("getWindowTitleforObject() "+object+" perspective="+getCurrentPerspective());
		if (object instanceof ViewPointLibrary) {
			return FlexoLocalization.localizedForKey("calc_library");
		}
		if (object instanceof OntologyLibrary) {
			return FlexoLocalization.localizedForKey("ontology_library");
		}
		if (getCurrentPerspective() == VIEW_POINT_PERSPECTIVE) {
			return VIEW_POINT_PERSPECTIVE.getWindowTitleforObject(object);
		}
		if (getCurrentPerspective() == ONTOLOGY_PERSPECTIVE) {
			return ONTOLOGY_PERSPECTIVE.getWindowTitleforObject(object);
		}
		return object.getFullyQualifiedName();
	}

	public FlexoResourceCenter getResourceCenter()
	{
		return resourceCenter;
	}

	public ViewPointLibrary getCalcLibrary()
	{
		return viewPointLibrary;
	}

	public OntologyLibrary getBaseOntologyLibrary()
	{
		return baseOntologyLibrary;
	}
	
	@Override
	public FlexoProject getProject()
	{
		logger.warning("Could not access to any project in this module (outside project scope module)");
		return super.getProject();
	}
	
	// ================================================
	// ============ Resources management ==============
	// ================================================

	private final Vector<ResourceSavingInfo> resourceSavingInfo ;
	
	public void manageResource(FlexoModelObject o)
	{
		boolean alreadyRegistered = false;
		for (ResourceSavingInfo i : resourceSavingInfo) {
			if (i.resource == o) {
				alreadyRegistered = true;
			}
		}
		if (!alreadyRegistered) {
			resourceSavingInfo.add(new ResourceSavingInfo(o));
		}
	}
	
	public Vector<ResourceSavingInfo> getResourceSavingInfo()
	{
		return resourceSavingInfo;
	}
	
	public void saveModified()
	{
		for (ResourceSavingInfo i : resourceSavingInfo) {
			i.saveModified();
		}
	}
	
	public void reviewModifiedResources()
	{
		for (ResourceSavingInfo i : resourceSavingInfo) {
			i.reviewModifiedResource();
		}
	}
	
	public static class ResourceSavingInfo
	{
		protected FlexoModelObject resource;
		protected boolean saveThisResource = true;
		
		public ResourceSavingInfo(FlexoModelObject r)
		{
			resource = r;
		}
		
		public Icon getIcon()
		{
			if (resource instanceof ImportedOntology) {
				return OntologyIconLibrary.ONTOLOGY_ICON;
			}
			else if (resource instanceof ViewPoint) {
				return VPMIconLibrary.CALC_ICON;
			}
			else if (resource instanceof ViewPointPalette) {
				return VPMIconLibrary.CALC_PALETTE_ICON;
			}
			else if (resource instanceof ExampleDrawingShema) {
				return VPMIconLibrary.EXAMPLE_DIAGRAM_ICON;
			}
			return VPMIconLibrary.UNKNOWN_ICON;
		}
		
		public String getName()
		{
			return resource.getName()+(isModified()?" ["+FlexoLocalization.localizedForKey("modified")+"]":"");
		}
		
		public String getType()
		{
			return resource.getLocalizedClassName();
		}
		
		public boolean isModified()
		{
			return resource.isModified();
		}
		
		public boolean saveThisResource()
		{
			return saveThisResource;
		}
		
		public void setSaveThisResource(boolean saveThisResource)
		{
			this.saveThisResource = saveThisResource;
		}
		
		public void reviewModifiedResource()
		{
			saveThisResource = resource.isModified();
		}
		

		public void saveModified()
		{
			if (saveThisResource) {
				try {
					if (resource instanceof ImportedOntology) {
						((ImportedOntology)resource).save();
					}
					else if (resource instanceof ViewPoint) {
						((ViewPoint)resource).save();
					}
					else if (resource instanceof ViewPointPalette) {
						((ViewPointPalette)resource).save();
					}
					else if (resource instanceof ExampleDrawingShema) {
						((ExampleDrawingShema)resource).save();
					}
				} catch (SaveResourceException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
