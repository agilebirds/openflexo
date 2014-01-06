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

import java.io.File;
import java.io.Serializable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.IETabComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.OperationComponentInstance;
import org.openflexo.foundation.ie.TabComponentInstance;
import org.openflexo.foundation.resource.InvalidFileNameException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoComponentResource;
import org.openflexo.foundation.rm.FlexoTabComponentResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource.SaveXMLResourceException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.FlexoComponentLibraryBuilder;
import org.openflexo.toolbox.FileUtils;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public final class TabComponentDefinition extends PartialComponentDefinition implements Serializable {
	private static final Logger logger = Logger.getLogger(TabComponentDefinition.class.getPackage().getName());

	/**
	 * Constructor used during deserialization
	 * 
	 * @throws DuplicateResourceException
	 */
	public TabComponentDefinition(FlexoComponentLibraryBuilder builder) throws DuplicateResourceException {
		this(null, builder.componentLibrary, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public TabComponentDefinition(FlexoComponentLibrary componentLibrary) {
		super(componentLibrary);
	}

	public static boolean isAValidNewTabName(String aComponentName, FlexoProject project) {
		return project.getFlexoComponentLibrary().isValidForANewComponentName(aComponentName);
	}

	public TabComponentDefinition(String aComponentName, FlexoComponentLibrary componentLibrary, FlexoComponentFolder aFolder,
			FlexoProject prj, boolean checkUnicity) throws DuplicateResourceException {
		super(aComponentName, componentLibrary, aFolder, prj);
		if (checkUnicity) {
			String resourceIdentifier = FlexoTabComponentResource.resourceIdentifierForName(aComponentName);
			if (aFolder != null && aFolder.getProject() != null && aFolder.getProject().isRegistered(resourceIdentifier)) {
				aFolder.removeFromComponents(this);
				throw new DuplicateResourceException(resourceIdentifier);
			}
		}
	}

	public TabComponentDefinition(String aComponentName, FlexoComponentLibrary componentLibrary, FlexoComponentFolder aFolder,
			FlexoProject prj) throws DuplicateResourceException {
		this(aComponentName, componentLibrary, aFolder, prj, true);
	}

	@Override
	public IETabComponent getWOComponent() {
		return (IETabComponent) super.getWOComponent();
	}

	public Vector<OperationComponentInstance> getAllOperationComponentInstances() {
		Vector<OperationComponentInstance> v = new Vector<OperationComponentInstance>();
		for (ComponentInstance ci : getComponentInstances()) {
			TabComponentInstance tci = (TabComponentInstance) ci;
			if (tci.getReusableWidget() != null && tci.getReusableWidget().getComponentDefinition().isOperation()) {
				for (ComponentInstance oci : ((OperationComponentDefinition) tci.getReusableWidget().getComponentDefinition())
						.getComponentInstances()) {
					if (!v.contains(oci)) {
						v.add((OperationComponentInstance) oci);
					}
				}
			}
		}
		return v;
	}

	@Override
	public IEWOComponent createNewComponent() {
		return new IETabComponent(this, getProject());
	}

	@Override
	public FlexoComponentResource getComponentResource(boolean createIfNotExists) {
		if (getProject() != null) {
			FlexoComponentResource returned = getProject().getFlexoTabComponentResource(getName());
			if (returned == null && createIfNotExists) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Creating new tab component resource !");
				}
				// FlexoProcessResource processRes =
				// getProject().getFlexoProcessResource(getProcess().getName());
				File componentFile = new File(ProjectRestructuration.getExpectedDirectoryForComponent(getProject().getProjectDirectory(),
						this), _componentName + ".woxml");
				FlexoProjectFile resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
				FlexoTabComponentResource compRes = null;
				try {
					compRes = new FlexoTabComponentResource(getProject(), _componentName, getProject().getFlexoComponentLibraryResource(),
							resourceComponentFile);
				} catch (InvalidFileNameException e1) {
					boolean ok = false;
					for (int i = 0; i < 100 && !ok; i++) {
						try {
							componentFile = new File(ProjectRestructuration.getExpectedDirectoryForComponent(getProject()
									.getProjectDirectory(), this), FileUtils.getValidFileName(_componentName) + i + ".woxml");
							resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
							resourceComponentFile.setProject(getProject());
							compRes = new FlexoTabComponentResource(getProject(), _componentName, getProject()
									.getFlexoComponentLibraryResource(), resourceComponentFile);
							ok = true;
						} catch (InvalidFileNameException e) {

						}
					}
					if (!ok) {
						componentFile = new File(ProjectRestructuration.getExpectedDirectoryForComponent(
								getProject().getProjectDirectory(), this), FileUtils.getValidFileName(_componentName) + getFlexoID()
								+ ".woxml");
						try {
							compRes = new FlexoTabComponentResource(getProject(), _componentName, getProject()
									.getFlexoComponentLibraryResource(), resourceComponentFile);
						} catch (InvalidFileNameException e) {
							if (logger.isLoggable(Level.SEVERE)) {
								logger.severe("This should really not happen.");
							}
							return null;
						}
					}
				}
				if (compRes == null) {
					return null;
				}
				compRes.setResourceData(new IETabComponent(this, getProject()));
				try {
					compRes.getResourceData().setFlexoResource(compRes);
					getProject().registerResource(compRes);
					compRes.saveResourceData();
				} catch (DuplicateResourceException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (SaveXMLResourceException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (SaveResourcePermissionDeniedException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (SaveResourceException e) {
					e.printStackTrace();
				}
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Registered component " + _componentName + " file: " + componentFile);
				}
				returned = compRes;
			}
			return returned;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("project is null");
			}
		}
		return null;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.IE.TAB_COMPONENT_DEFINITION_INSPECTOR;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "tab_component_definition";
	}

}
