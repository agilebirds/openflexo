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
package org.openflexo.foundation.ie.menu;

import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.rm.FlexoNavigationMenuResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.ImageFile;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.RMNotification;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.dm.DisplayOperationSet;
import org.openflexo.foundation.wkf.dm.DisplayProcessSet;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.xml.FlexoNavigationMenuBuilder;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.xmlcode.XMLMapping;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlexoNavigationMenu extends IEObject implements XMLStorageResourceData, Validable {

	private static final Logger logger = Logger.getLogger(FlexoComponentLibrary.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	private FlexoProject _project;

	private FlexoNavigationMenuResource _resource;

	private FlexoItemMenu _rootMenu;

	private boolean useDefaultImage = true;

	// private String imageRelPath;

	private ImageFile logo;

	private String buttons;

	private String actions;

	private long userProfileProcessFlexoID = -1;

	private long userProfileOperationFlexoID = -1;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Create a new FlexoComponentLibrary.
	 */
	public FlexoNavigationMenu(FlexoNavigationMenuBuilder builder) {
		this(builder.getProject());
		initializeDeserialization(builder);
		_resource = builder.resource;
		builder.navigationMenu = this;
	}

	/**
	 * Create a new FlexoComponentLibrary.
	 */
	public FlexoNavigationMenu(FlexoProject project) {
		super(project);
		_project = project;
	}

	/**
	 * Creates and returns a newly created navigation menu
	 * 
	 * @return a newly created FlexoNavigationMenu
	 */
	public static FlexoNavigationMenu createNewFlexoNavigationMenu(FlexoProject project) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("createNewFlexoNavigationMenu(), project=" + project);
		}
		FlexoNavigationMenu newMenu = new FlexoNavigationMenu(project);

		File menuFile = ProjectRestructuration.getExpectedNavigationMenuFile(project);
		FlexoProjectFile resFile = new FlexoProjectFile(menuFile, project);
		FlexoNavigationMenuResource res;
		try {
			res = new FlexoNavigationMenuResource(project, newMenu, resFile);
		} catch (InvalidFileNameException e2) {
			resFile = new FlexoProjectFile(FileUtils.getValidFileName(resFile.getRelativePath()));
			resFile.setProject(project);
			try {
				res = new FlexoNavigationMenuResource(project, newMenu, resFile);
			} catch (InvalidFileNameException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("This should not happen.");
				}
				return null;
			}
		}
		try {
			FlexoItemMenu.createNewRootMenu(newMenu);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			res.saveResourceData();
			project.registerResource(res);
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}
		return newMenu;
	}

	// ==========================================================================
	// ========================= XML Serialization ============================
	// ==========================================================================

	@Override
	public XMLMapping getXMLMapping() {
		return getProject().getXmlMappings().getNavigationMenuMapping();
	}

	// ==========================================================================
	// ===================== Resource managing
	// ==================================
	// ==========================================================================

	@Override
	public IEObject getParent() {
		return null;
	}

	@Override
	public FlexoNavigationMenuResource getFlexoResource() {
		return _resource;
	}

	@Override
	public FlexoXMLStorageResource getFlexoXMLFileResource() {
		return _resource;
	}

	@Override
	public void setFlexoResource(FlexoResource resource) {
		_resource = (FlexoNavigationMenuResource) resource;
	}

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	@Override
	public void setProject(FlexoProject aProject) {
		_project = aProject;
	}

	/**
	 * Save this object using ResourceManager scheme Additionnaly save all known processes related to this workflow
	 * 
	 * Overrides
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#save()
	 * @see org.openflexo.foundation.rm.FlexoResourceData#save()
	 */
	@Override
	public void save() throws SaveResourceException {
		_resource.saveResourceData();

	}

	/**
	 * Implements
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#receiveRMNotification(org.openflexo.foundation.rm.RMNotification) Receive a
	 *      notification that has been propagated by the ResourceManager scheme and coming from a modification on an other resource
	 * 
	 *      Handles ComponentNameChanged notifications
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#receiveRMNotification(org.openflexo.foundation.rm.RMNotification)
	 */
	@Override
	public void receiveRMNotification(RMNotification aNotification) throws FlexoException {
		/*
		if (aNotification instanceof ComponentNameChanged) {
		    ComponentNameChanged notification = (ComponentNameChanged) aNotification;
		    for (Enumeration en = getAllComponentInstances().elements(); en.hasMoreElements();) {
		        ComponentInstance ci = (ComponentInstance) en.nextElement();
		        if (ci.getComponentName().equals(notification.oldValue())) {
		            if (logger.isLoggable(Level.INFO))
		                logger.info("Menu: Updating component instance " + notification.component.getName());
		            ci.notifyComponentNameChanged(notification.component);
		        }
		    }
		}
		if (aNotification instanceof ComponentDeleteRequest) {
		    ComponentDeleteRequest notification = (ComponentDeleteRequest) aNotification;
		    Hashtable h = getAllComponentInstances();
		    for (Enumeration en = h.keys(); en.hasMoreElements();) {
		        FlexoItemMenu item = (FlexoItemMenu) en.nextElement();
		        ComponentInstance ci = (ComponentInstance) h.get(item);
		        if (ci.getComponentName().equals(notification.component.getComponentName())) {
		            if (logger.isLoggable(Level.INFO))
		                logger.info("Receive a deletion request for " + notification.component.getComponentName() + " in "
		                        + FlexoLocalization.localizedForKey("menu_item ") + item.getMenuLabel());
		            notification.addToWarnings(notification.component.getComponentName() + " is used by "
		                    + FlexoLocalization.localizedForKey("menu_item ") + item.getMenuLabel());
		        }
		    }
		}

		if (aNotification instanceof ComponentDeleted) {
		    ComponentDeleted notification = (ComponentDeleted) aNotification;
		    Hashtable h = getAllComponentInstances();
		    for (Enumeration en = h.keys(); en.hasMoreElements();) {
		        FlexoItemMenu item = (FlexoItemMenu) en.nextElement();
		        ComponentInstance ci = (ComponentInstance) h.get(item);
		        if (ci.getComponentName().equals(notification.component.getComponentName())) {
		            if (item.getIsPageTarget())
		                item.setDummyOperationComponentDefinition(null);
		            else
		                item.setDummyPopupComponentDefinition(null);
		            if (logger.isLoggable(Level.INFO))
		                logger.info("Receive a deletion task for " + notification.component.getComponentName() + " in "
		                        + FlexoLocalization.localizedForKey("menu_item ") + item.getMenuLabel());
		        }
		    }
		}
		*/
	}

	public void setRootMenu(FlexoItemMenu rootMenu) {
		_rootMenu = rootMenu;
		setChanged();
	}

	public FlexoItemMenu getRootMenu() {
		if (_rootMenu == null) {
			_rootMenu = FlexoItemMenu.createNewRootMenu(this);
		}
		return _rootMenu;
	}

	/**
	 * Implements
	 * 
	 * @see org.openflexo.foundation.ie.IEObject#getEmbeddedIEObjects()
	 * @see org.openflexo.foundation.ie.IEObject#getEmbeddedIEObjects()
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector returned = new Vector();
		returned.add(getRootMenu());
		return returned;
	}

	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme: here it's the
	 * NavigationMenu itself
	 * 
	 * @return this
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return this;
	}

	@Override
	public ValidationModel getDefaultValidationModel() {
		if (getProject() != null) {
			return getProject().getIEValidationModel();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not access to project !");
			}
		}
		return null;
	}

	/**
	 * Returns a flag indicating if this object is valid according to default validation model
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isValid() {
		return isValid(getDefaultValidationModel());
	}

	/**
	 * Returns a flag indicating if this object is valid according to specified validation model
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isValid(ValidationModel validationModel) {
		return validationModel.isValid(this);
	}

	/**
	 * Validates this object by building new ValidationReport object Default validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate() {
		return validate(getDefaultValidationModel());
	}

	/**
	 * Validates this object by building new ValidationReport object Supplied validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate(ValidationModel validationModel) {
		return validationModel.validate(this);
	}

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Default validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
	@Override
	public void validate(ValidationReport report) {
		validate(report, getDefaultValidationModel());
	}

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Supplied validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
	@Override
	public void validate(ValidationReport report, ValidationModel validationModel) {
		validationModel.validate(this, report);
	}

	@Override
	public String getFullyQualifiedName() {
		return getProject().getProjectName() + ".MENU";
	}

	/**
	 * @return Returns the buttons.
	 */
	public String getButtons() {
		return buttons;
	}

	/**
	 * @param buttons
	 *            The buttons to set.
	 */
	public void setButtons(String buttons) {
		this.buttons = buttons;
		setChanged();
	}

	private Date lastUpdateDateForLogo;

	public ImageFile getLogo() {
		return logo;
	}

	public void setLogo(ImageFile logo) {
		this.logo = logo;
		if (!isDeserializing()) {
			setChanged();
			notifyModification("logo", null, logo);
			lastUpdateDateForLogo = new Date();
		}
	}

	/**
	 * @return Returns the useDefaultImage.
	 */
	public boolean getUseDefaultImage() {
		return useDefaultImage;
	}

	/**
	 * @param useDefaultImage
	 *            The useDefaultImage to set.
	 */
	public void setUseDefaultImage(boolean useDefaultImage) {
		this.useDefaultImage = useDefaultImage;
		setChanged();
		lastUpdateDateForLogo = new Date();
	}

	public Date getLastUpdateDateForLogo() {
		if (lastUpdateDateForLogo == null) {
			lastUpdateDateForLogo = getLastUpdate();
		}
		return lastUpdateDateForLogo;
	}

	public void setLastUpdateDateForLogo(Date updateDate) {
		this.lastUpdateDateForLogo = updateDate;
	}

	/**
	 * @return Returns the actions.
	 */
	public String getActions() {
		return actions;
	}

	/**
	 * @param actions
	 *            The actions to set.
	 */
	public void setActions(String actions) {
		this.actions = actions;
		setChanged();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "flexo_navigation_menu";
	}

	/**
	 * @param menuLabel
	 * @return
	 */
	public FlexoItemMenu getMenuLabeled(String menuLabel) {
		return getRootMenu().getMenuLabeled(menuLabel);
	}

	public String getUserProfilePageName() {
		if (getUserProfileOperation() == null || getUserProfileOperation().getComponentInstance() == null) {
			return null;
		}
		return getUserProfileOperation().getComponentInstance().getComponentName();
	}

	// public void setUserProfilePageName(String pageName){
	// _userProfilePageName = pageName;
	// setUserProfilePage(getProject().getFlexoComponentLibrary().getComponentNamed(_userProfilePageName));
	// }
	//
	//
	// public ComponentDefinition getUserProfilePage(){
	// return getProject().getFlexoComponentLibrary().getComponentNamed(_userProfilePageName);
	// }
	//
	// private void setUserProfilePage(ComponentDefinition cd){
	// _userProfilePageName = cd!=null?cd.getComponentName():null;
	// setChanged();
	// notifyObservers(new UserProfilePageChanged(cd));
	// }
	//
	// public void notifyComponentDeleted(ComponentDefinition cd){
	// if(cd!=null && cd.getComponentName().equals(_userProfilePageName))setUserProfilePage(null);
	// }
	//
	// public void notifyComponentNameChanged(String oldComponentName, String name2) {
	// if(_userProfilePageName!=null && _userProfilePageName.equals(oldComponentName))
	// _userProfilePageName = name2;
	//
	// }

	private FlexoProcess userProfileProcess;

	private OperationNode userProfileOperation;

	public OperationNode getUserProfileOperation() {
		if (userProfileOperation == null && userProfileOperationFlexoID > -1) {
			if (getUserProfileProcess() != null) {
				userProfileOperation = getUserProfileProcess().getOperationNodeWithFlexoID(userProfileOperationFlexoID);
				if (userProfileOperation == null) {
					userProfileOperationFlexoID = -1;
					setChanged();
				}

			} else if (logger.isLoggable(Level.WARNING)) {
				logger.warning("This is weird, an operation has been set but not its process.");
			}
		}
		return userProfileOperation;
	}

	public void setUserProfileOperation(OperationNode displayOperation) {
		OperationNode old = this.userProfileOperation;
		this.userProfileOperation = displayOperation;
		if (displayOperation != null) {
			userProfileOperationFlexoID = displayOperation.getFlexoID();
		} else {
			userProfileOperationFlexoID = -1;
		}
		setChanged();
		notifyObservers(new DisplayOperationSet(old, displayOperation));
	}

	public FlexoProcess getUserProfileProcess() {
		if (userProfileProcess == null && userProfileProcessFlexoID > -1) {
			userProfileProcess = getProject().getFlexoWorkflow().getLocalFlexoProcessWithFlexoID(userProfileProcessFlexoID);
			if (userProfileProcess == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find process with flexoID " + userProfileProcessFlexoID);
				}
				userProfileProcessFlexoID = -1;
				setUserProfileOperation(null);
				setChanged();
			}
		}
		return userProfileProcess;
	}

	public void setUserProfileProcess(FlexoProcess displayProcess) {
		FlexoProcess old = this.userProfileProcess;
		if (old != null) {
			getFlexoResource().removeFromDependentResources(old.getFlexoResource());
		}
		this.userProfileProcess = displayProcess;
		if (displayProcess != null) {
			userProfileProcessFlexoID = displayProcess.getFlexoID();
			getFlexoResource().addToDependentResources(displayProcess.getFlexoResource());
		} else {
			userProfileProcessFlexoID = -1;
		}
		setUserProfileOperation(null);
		setChanged();
		notifyObservers(new DisplayProcessSet(old, displayProcess));
	}

	public long getUserProfileOperationFlexoID() {
		if (getUserProfileOperation() != null) {
			return getUserProfileOperation().getFlexoID();
		} else {
			return -1;
		}
	}

	public void setUserProfileOperationFlexoID(long displayOperationFlexoID) {
		if (getUserProfileOperation() != null) {
			userProfileOperation = null;
		}
		this.userProfileOperationFlexoID = displayOperationFlexoID;
	}

	public long getUserProfileProcessFlexoID() {
		if (getUserProfileProcess() != null) {
			return getUserProfileProcess().getFlexoID();
		} else {
			return -1;
		}
	}

	public void setUserProfileProcessFlexoID(long displayProcessFlexoID) {
		if (getUserProfileProcess() != null) {
			setUserProfileProcess(null);
		}
		this.userProfileProcessFlexoID = displayProcessFlexoID;
	}

	public Vector<FlexoItemMenu> getAllItemMenus() {
		return _rootMenu.getAllItemMenus(new Vector<FlexoItemMenu>());
	}

	public Hashtable<IEObject, Hashtable<String, String>> getLocalizableObjects() {
		Hashtable<IEObject, Hashtable<String, String>> reply = new Hashtable<IEObject, Hashtable<String, String>>();
		Enumeration<FlexoItemMenu> en = getAllItemMenus().elements();
		while (en.hasMoreElements()) {
			FlexoItemMenu menu = en.nextElement();
			Hashtable<String, String> props = menu.getLocalizableProperties();
			if (props != null) {
				reply.put(menu, props);
			}
		}
		return reply;
	}

}
