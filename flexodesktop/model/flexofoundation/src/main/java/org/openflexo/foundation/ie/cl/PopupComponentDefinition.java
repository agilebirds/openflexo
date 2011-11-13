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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.IEPopupComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoComponentResource;
import org.openflexo.foundation.rm.FlexoPopupComponentResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.xml.FlexoComponentLibraryBuilder;
import org.openflexo.toolbox.FileUtils;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public final class PopupComponentDefinition extends ComponentDefinition implements Serializable {
	private static final Logger logger = Logger.getLogger(PopupComponentDefinition.class.getPackage().getName());

	/**
	 * Constructor used during deserialization
	 * 
	 * @throws DuplicateResourceException
	 */
	public PopupComponentDefinition(FlexoComponentLibraryBuilder builder) throws DuplicateResourceException {
		this(null, builder.componentLibrary, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public PopupComponentDefinition(FlexoComponentLibrary componentLibrary) {
		super(componentLibrary);
	}

	public PopupComponentDefinition(String aComponentName, FlexoComponentLibrary componentLibrary, FlexoComponentFolder aFolder,
			FlexoProject prj, boolean checkUnicity) throws DuplicateResourceException {
		super(aComponentName, componentLibrary, aFolder, prj);
		if (checkUnicity) {
			String resourceIdentifier = FlexoPopupComponentResource.resourceIdentifierForName(aComponentName);
			if ((aFolder != null) && (aFolder.getProject() != null) && (aFolder.getProject().isRegistered(resourceIdentifier))) {
				aFolder.removeFromComponents(this);
				throw new DuplicateResourceException(resourceIdentifier);
			}
		}
	}

	public PopupComponentDefinition(String aComponentName, FlexoComponentLibrary componentLibrary, FlexoComponentFolder aFolder,
			FlexoProject prj) throws DuplicateResourceException {
		this(aComponentName, componentLibrary, aFolder, prj, true);
	}

	@Override
	public IEWOComponent createNewComponent() {
		return new IEPopupComponent(this, getProject());
	}

	@Override
	public FlexoComponentResource getComponentResource(boolean createIfNotExists) {
		if (getProject() != null) {
			FlexoComponentResource returned = getProject().getFlexoPopupComponentResource(getName());
			if (returned == null && createIfNotExists) {
				if (logger.isLoggable(Level.INFO))
					logger.info("Creating new popup component resource !");
				// FlexoProcessResource processRes =
				// getProject().getFlexoProcessResource(getProcess().getName());
				File componentsDir = ProjectRestructuration.getExpectedDirectoryForComponent(getProject().getProjectDirectory(), this);
				File componentFile = new File(componentsDir, _componentName + ".woxml");
				FlexoProjectFile resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
				FlexoPopupComponentResource compRes = null;
				try {
					compRes = new FlexoPopupComponentResource(getProject(), _componentName,
							getProject().getFlexoComponentLibraryResource(), resourceComponentFile);
				} catch (InvalidFileNameException e1) {
					boolean ok = false;
					for (int i = 0; i < 100 && !ok; i++) {
						try {
							componentFile = new File(componentsDir, FileUtils.getValidFileName(_componentName) + i + ".woxml");
							resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
							resourceComponentFile.setProject(getProject());
							compRes = new FlexoPopupComponentResource(getProject(), _componentName, getProject()
									.getFlexoComponentLibraryResource(), resourceComponentFile);
							ok = true;
						} catch (InvalidFileNameException e) {
						}
					}
					if (!ok) {
						componentFile = new File(componentsDir, FileUtils.getValidFileName(_componentName) + getFlexoID() + ".woxml");
						resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
						try {
							compRes = new FlexoPopupComponentResource(getProject(), _componentName, getProject()
									.getFlexoComponentLibraryResource(), resourceComponentFile);
						} catch (InvalidFileNameException e) {
							if (logger.isLoggable(Level.SEVERE))
								logger.severe("This should really not happen.");
							return null;
						}
					}
				}
				if (compRes == null)
					return null;
				compRes.setResourceData(new IEPopupComponent(this, getProject()));
				try {
					compRes.getResourceData().setFlexoResource(compRes);
					getProject().registerResource(compRes);
				} catch (DuplicateResourceException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					e.printStackTrace();
					return null;
				}
				if (logger.isLoggable(Level.INFO))
					logger.info("Registered component " + _componentName + " file: " + componentFile);
				returned = compRes;
			}
			return returned;
		} else {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("getProject()==null for a PopupComponentDefinition !");
		}
		return null;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.IE.POPUP_COMPONENT_DEFINITION_INSPECTOR;
	}

	/*
	    public final void delete()
	    {
	        super.delete();
	        deleteObservers();
	    }
	*/
	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "popup_component_definition";
	}

	public boolean isHelper() {
		return getName().equals("WDLDateAssistant");
	}

	public boolean isWDLDateAssistant() {
		return getName().equals("WDLDateAssistant");
	}

	@Override
	public IEPopupComponent getWOComponent() {
		return (IEPopupComponent) super.getWOComponent();
	}

	@Override
	public List<OperationNode> getAllOperationNodesLinkedToThisComponent() {
		return new ArrayList<OperationNode>();
	}
}
