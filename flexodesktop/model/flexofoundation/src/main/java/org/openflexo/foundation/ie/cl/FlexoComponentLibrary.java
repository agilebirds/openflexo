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
package org.openflexo.foundation.ie.cl;

/*
 * FlexoWorkflow.java
 * Project WorkflowEditor
 *
 * Created by benoit on Mar 3, 2004
 */

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.openflexo.foundation.Inspectors.IEInspectors;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.cl.dm.CLComponentCreated;
import org.openflexo.foundation.ie.dm.TreeStructureChanged;
import org.openflexo.foundation.rm.FlexoComponentLibraryResource;
import org.openflexo.foundation.rm.FlexoComponentResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.stats.ComponentLibraryStatistics;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.xml.FlexoComponentLibraryBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.xmlcode.XMLMapping;

/**
 * A FlexoComponentLibrary is an object referencing all components used in the project
 * 
 * @author benoit,sylvain
 */

public class FlexoComponentLibrary extends IECLObject implements XMLStorageResourceData, TreeNode, InspectableObject {

	private static final Logger logger = Logger.getLogger(FlexoComponentLibrary.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	private FlexoProject _project;

	private FlexoComponentLibraryResource _resource;

	private String name;

	private FlexoComponentFolder _rootFolder;

	private ComponentLibraryStatistics statistics;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Create a new FlexoComponentLibrary.
	 */
	public FlexoComponentLibrary(FlexoComponentLibraryBuilder builder) {
		this(builder.getProject());
		builder.componentLibrary = this;
		_resource = builder.resource;
		initializeDeserialization(builder);
	}

	/**
	 * Create a new FlexoComponentLibrary.
	 */
	public FlexoComponentLibrary(FlexoProject project) {
		super(project);
		_project = project;
	}

	@Override
	public FlexoComponentLibraryResource getFlexoResource() {
		return _resource;
	}

	@Override
	public FlexoXMLStorageResource getFlexoXMLFileResource() {
		return _resource;
	}

	/**
	 * Creates and returns a newly created component library
	 * 
	 * @return a newly created workflow
	 */
	public static FlexoComponentLibrary createNewComponentLibrary(FlexoProject project) {
		FlexoComponentLibrary newLibrary = new FlexoComponentLibrary(project);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("createNewComponentLibrary(), project=" + project + " " + newLibrary);
		}

		File compFile = ProjectRestructuration.getExpectedComponentLibFile(project);
		FlexoProjectFile componentLibFile = new FlexoProjectFile(compFile, project);
		FlexoComponentLibraryResource clRes;
		try {
			clRes = new FlexoComponentLibraryResource(project, newLibrary, componentLibFile);
		} catch (InvalidFileNameException e2) {
			e2.printStackTrace();
			componentLibFile = new FlexoProjectFile("ComponentLibrary");
			componentLibFile.setProject(project);
			try {
				clRes = new FlexoComponentLibraryResource(project, newLibrary, componentLibFile);
			} catch (InvalidFileNameException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Could not create component library.");
				}
				e.printStackTrace();
				return null;
			}
		}

		try {
			FlexoComponentFolder.createNewRootFolder(newLibrary);
			FlexoComponentFolder utilsFolder = new FlexoComponentFolder("Utils", newLibrary);
			newLibrary.getRootFolder().addToSubFolders(utilsFolder);
			new PopupComponentDefinition("WDLDateAssistant", newLibrary, utilsFolder, project);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (FlexoComponentResource r : project.getResourcesOfClass(FlexoComponentResource.class)) {
			if (newLibrary.getComponentNamed(r.getName()) == null) {
				r.getComponentDefinition();
			}
		}

		try {
			clRes.saveResourceData();
			project.registerResource(clRes);
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}
		return newLibrary;
	}

	@Override
	public FlexoComponentLibrary getComponentLibrary() {
		return this;
	}

	@Override
	public void setFlexoResource(FlexoResource resource) {
		_resource = (FlexoComponentLibraryResource) resource;
	}

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	@Override
	public void setProject(FlexoProject aProject) {
		_project = aProject;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String aName) {
		name = aName;
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
	 * Return an enumeration of all folders, by recursively explore the tree
	 * 
	 * @return an Enumeration of FlexoComponentFolder elements
	 */
	public Enumeration<FlexoComponentFolder> allFolders() {
		Vector<FlexoComponentFolder> temp = new Vector<FlexoComponentFolder>();
		addFolders(temp, getRootFolder());
		return temp.elements();
	}

	/**
	 * Return number of folders
	 */
	public int allFoldersCount() {
		Vector<FlexoComponentFolder> temp = new Vector<FlexoComponentFolder>();
		addFolders(temp, getRootFolder());
		return temp.size();
	}

	private void addFolders(Vector<FlexoComponentFolder> temp, FlexoComponentFolder folder) {
		temp.add(folder);
		for (Enumeration e = folder.getSubFolders().elements(); e.hasMoreElements();) {
			FlexoComponentFolder currentFolder = (FlexoComponentFolder) e.nextElement();
			addFolders(temp, currentFolder);
		}
	}

	public FlexoComponentFolder getFlexoComponentFolderWithName(String folderName) {
		for (Enumeration e = allFolders(); e.hasMoreElements();) {
			FlexoComponentFolder folder = (FlexoComponentFolder) e.nextElement();

			if (folder.getName().equals(folderName)) {
				return folder;
			}

		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Could not find folder named " + folderName);
		}
		return null;
	}

	// ==========================================================================
	// ============================= TreeNode ===================================
	// ==========================================================================

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#children()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Enumeration children() {
		Vector temp = new Vector();
		temp.add(getRootFolder());
		return temp.elements();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getAllowsChildren()
	 */
	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getChildAt(int)
	 */
	@Override
	public TreeNode getChildAt(int arg0) {
		if (arg0 == 0) {
			return getRootFolder();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getChildCount()
	 */
	@Override
	public int getChildCount() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
	 */
	@Override
	public int getIndex(TreeNode arg0) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getParent()
	 */
	@Override
	public FlexoComponentLibrary getParent() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return false;
	}

	// ==========================================================================
	// ============================= Instance methods
	// ===========================
	// ==========================================================================

	public void setRootFolder(FlexoComponentFolder rootFolder) {
		_rootFolder = rootFolder;
		_rootFolder.setComponentLibrary(this);
	}

	public FlexoComponentFolder getRootFolder() {
		if (_rootFolder == null) {
			if (!isDeserializing()) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("No root folder defined for component library");
				}
				setRootFolder(FlexoComponentFolder.createNewRootFolder(this));
			}
		}
		return _rootFolder;
	}

	// ==========================================================================
	// ============================= Accessors
	// ==================================
	// ==========================================================================

	public Vector<TabComponentDefinition> getTabComponentList() {
		Vector<TabComponentDefinition> answer = new Vector<TabComponentDefinition>();
		answer.addAll(getRootFolder().getTabComponentList());
		return answer;
	}

	public Vector<OperationComponentDefinition> getOperationsComponentList() {
		Vector<OperationComponentDefinition> answer = new Vector<OperationComponentDefinition>();
		answer.addAll(getRootFolder().getOperationsComponentList());
		return answer;
	}

	public Vector<ComponentDefinition> getAllComponentList() {
		Vector<ComponentDefinition> v = new Vector<ComponentDefinition>();
		v.addAll(getOperationsComponentList());
		v.addAll(getPopupsComponentList());
		v.addAll(getTabComponentList());
		return v;
	}

	public File getFile() {
		return _resource.getResourceFile().getFile();
	}

	// ==========================================================================
	// ========================= XML Serialization ============================
	// ==========================================================================

	@Override
	public XMLMapping getXMLMapping() {
		return getProject().getXmlMappings().getComponentLibraryMapping();
	}

	/**
	 * @param value
	 * @return
	 */

	public void delete(ComponentDefinition def) {
		boolean b = _rootFolder.delete(def);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Component removal " + (b ? "succeed" : "failed"));
		}
		notifyTreeStructureChanged();
	}

	public boolean isValidForANewComponentName(String value) {
		if (value == null) {
			return false;
		}
		if (_rootFolder != null) {
			return _rootFolder.isValidForANewComponentName(value);
		}
		return true;
	}

	public ComponentDefinition getComponentNamed(String value) {
		if (value == null) {
			return null;
		}
		if (_rootFolder != null) {
			return _rootFolder.getComponentNamed(value);
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Cannot find a component named : " + value);
		}
		return null;
	}

	/**
	 * @return
	 */
	public Vector<PopupComponentDefinition> getPopupsComponentList() {
		Vector<PopupComponentDefinition> answer = new Vector<PopupComponentDefinition>();
		answer.addAll(getRootFolder().getPopupsComponentList());
		return answer;
	}

	/**
	 * @return
	 */
	public PopupComponentDefinition[] getSortedPopupsComponentList() {
		Vector<PopupComponentDefinition> answer = new Vector<PopupComponentDefinition>();
		answer.addAll(getRootFolder().getPopupsComponentList());
		return FlexoIndexManager.sortArray(answer.toArray(new PopupComponentDefinition[0]));
	}

	/**
	 * @return
	 */
	public TabComponentDefinition[] getSortedTabsComponentList() {
		Vector<TabComponentDefinition> answer = new Vector<TabComponentDefinition>();
		answer.addAll(getRootFolder().getTabComponentList());
		return FlexoIndexManager.sortArray(answer.toArray(new TabComponentDefinition[0]));
	}

	/**
	 * 
	 * @deprecated - Only component folders are mutable, so only them should notify
	 */
	@Deprecated
	public void notifyTreeStructureChanged() {
		setChanged();
		notifyObservers(new TreeStructureChanged(this));
	}

	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		Vector<Validable> v = super.getAllEmbeddedValidableObjects();
		for (ComponentDefinition cd : getAllComponentList()) {
			v.addAll(cd.getWOComponent().getAllEmbeddedValidableObjects());
		}
		return v;
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector<IObject> answer = new Vector<IObject>();
		answer.add(getRootFolder());
		if (getProject().getFlexoNavigationMenu().getRootMenu() != null) {
			answer.add(getProject().getFlexoNavigationMenu().getRootMenu());
		}
		return answer;
	}

	@Override
	public String getFullyQualifiedName() {
		return "ComponentLibrary";
	}

	// ==========================================================================
	// ====================== Validable implementation
	// ==========================
	// ==========================================================================

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

	public boolean hasRootFolder() {
		return _rootFolder != null;
	}

	/**
	 * @param definition
	 */
	public void handleNewComponentCreated(ComponentDefinition definition) {
		setChanged();
		notifyObservers(new CLComponentCreated(definition));
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "component_library";
	}

	/**
	 * Overrides getInspectorName
	 * 
	 * @see org.openflexo.inspector.InspectableObject#getInspectorName()
	 */
	@Override
	public String getInspectorName() {
		return IEInspectors.COMPONENT_LIBRARY_INSPECTOR;
	}

	public ComponentLibraryStatistics getStatistics() {
		if (statistics == null) {
			statistics = new ComponentLibraryStatistics(this);
		}
		return statistics;
	}

	public Hashtable<IEObject, Hashtable<String, String>> getAllTranslatableItems() {
		Hashtable<IEObject, Hashtable<String, String>> reply = new Hashtable<IEObject, Hashtable<String, String>>();

		if (getProject().getIsLocalized()) {
			Enumeration<ComponentDefinition> en = getAllComponentList().elements();
			IEWOComponent wo = null;
			while (en.hasMoreElements()) {
				wo = en.nextElement().getWOComponent();
				Hashtable<IEObject, Hashtable<String, String>> localizableItems = wo.getLocalizableObjects();
				if (localizableItems != null && localizableItems.size() > 0) {
					reply.putAll(localizableItems);
				}
			}

			reply.putAll(getProject().getFlexoNavigationMenu().getLocalizableObjects());
		}
		return reply;
	}
}
