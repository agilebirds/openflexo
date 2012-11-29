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
package org.openflexo.foundation.rm;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.xml.FlexoComponentLibraryBuilder;
import org.openflexo.foundation.xml.XMLUtils;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FlexoVersion;

/**
 * Represents the component library resource
 * 
 * @author bmangez
 */
public class FlexoComponentLibraryResource extends FlexoXMLStorageResource<FlexoComponentLibrary> {
	private static final Logger logger = Logger.getLogger(FlexoComponentLibraryResource.class.getPackage().getName());

	/**
	 * Constructor used for XML Serialization: never try to instantiate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoComponentLibraryResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoComponentLibraryResource(FlexoProject aProject) {
		super(aProject);
		if (aProject != null) {
			try {
				setResourceFile(new FlexoProjectFile(ProjectRestructuration.getExpectedComponentLibFile(aProject), aProject));
			} catch (InvalidFileNameException e) {
				FlexoProjectFile f = new FlexoProjectFile("ComponentLibrary");
				f.setProject(aProject);
				try {
					setResourceFile(f);
				} catch (InvalidFileNameException e1) {
					if (logger.isLoggable(Level.SEVERE)) {
						logger.severe("This should not happen.");
					}
					e1.printStackTrace();
				}
			}
		}

		// this(aProject,new
		// FlexoProjectFile(ProjectRestructuration.getExpectedComponentLibFile(aProject),aProject));
	}

	public FlexoComponentLibraryResource(FlexoProject aProject, FlexoProjectFile componentLibraryFile) throws InvalidFileNameException {
		super(aProject);
		setResourceFile(componentLibraryFile);
	}

	public FlexoComponentLibraryResource(FlexoProject aProject, FlexoComponentLibrary lib, FlexoProjectFile componentLibFile)
			throws InvalidFileNameException {
		this(aProject, componentLibFile);
		_resourceData = lib;
		try {
			lib.setFlexoResource(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FlexoResource getFlexoResource() {
		return this;
	}

	public void save() throws SaveResourceException {
		saveResourceData();

	}

	public void setProject(FlexoProject aProject) {
		project = aProject;

	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.COMPONENT_LIBRARY;
	}

	@Override
	public String getName() {
		return getProject().getProjectName();
	}

	@Override
	public Class<FlexoComponentLibrary> getResourceDataClass() {
		return FlexoComponentLibrary.class;
	}

	/*public FlexoResourceData loadResourceData(FlexoProgress progress)
	        throws LoadXMLResourceException
	{
	    return loadResourceData(new Vector(), progress);
	}

	public StorageResourceData loadResourceData(Vector requestingResources)
	        throws LoadXMLResourceException
	{
	    return loadResourceData(requestingResources, null);
	}*/

	@Override
	public FlexoComponentLibrary performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler)
			throws LoadXMLResourceException, ProjectLoadingCancelledException, MalformedXMLException {
		FlexoComponentLibrary library;
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("loading_component_library"));
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("performLoadResourceData() in FlexoComponentLibraryResource");
		}
		try {
			library = super.performLoadResourceData(progress, loadingHandler);
		} catch (FlexoFileNotFoundException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("File " + getFile().getName() + " NOT found");
			}
			e.printStackTrace();
			return null;
		}
		return library;
	}

	@Override
	public boolean hasBuilder() {
		return true;
	}

	/**
	 * Returns the required newly instancied builder if this resource needs a builder to be loaded
	 * 
	 * @return boolean
	 */
	@Override
	public Object instanciateNewBuilder() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("instanciateNewBuilder in FlexoComponentLibraryResource");
		}
		FlexoComponentLibraryBuilder builder = new FlexoComponentLibraryBuilder(this);
		builder.componentLibrary = _resourceData;
		return builder;
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		// ComponentDefinitions depends of the ComponentEntity
		addToDependentResources(getProject().getFlexoDMResource());
	}

	/**
	 * Manually converts resource file from version v1 to version v2. This method implements conversion from v1.0 to v1.1 by storing data
	 * relating to menu in a new resource
	 * 
	 * @param v1
	 * @param v2
	 * @return boolean indicating if conversion was sucessfull
	 */
	@Override
	protected boolean convertResourceFileFromVersionToVersion(FlexoVersion v1, FlexoVersion v2) {
		if (v1.equals("1.0") && v2.equals("1.1")) {
			ComponentLibraryConverter1 converter = new ComponentLibraryConverter1();
			return converter.conversionWasSucessfull;
		} else if (v1.equals("2.0") && v2.equals("2.1")) {
			return convertFrom20To21();
		} else if ((v1.equals("2.2") || v1.equals("2.3")) && v2.equals("2.4")) {
			return convertFrom23To24();
		} else {
			return super.convertResourceFileFromVersionToVersion(v1, v2);
		}
	}

	private boolean convertFrom23To24() {
		// GPO: Little hack here to remove WDLKeyValueAssistant.
		// The trick is to avoid loading the resource, so we use some hacks to do it.
		ComponentDefinition cd = getResourceData().getComponentNamed("WDLKeyValueAssistant");
		if (cd != null) {
			cd.getFolder().removeFromComponents(cd);
			FlexoResource<? extends FlexoResourceData> res = getProject().getResources().get(
					ResourceType.POPUP_COMPONENT.getName() + "." + cd.getName());
			if (res != null) {
				res.delete();
			}
		}
		try {
			getProject().getImportedImagesDir();
			getResourceData().save();
			getProject().save();
		} catch (SaveResourceException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected class ComponentLibraryConverter1 {
		private final Logger logger = Logger.getLogger(FlexoComponentLibraryResource.ComponentLibraryConverter1.class.getPackage()
				.getName());

		protected boolean conversionWasSucessfull = false;

		protected Document document;

		protected Document newMenuDocument;

		protected ComponentLibraryConverter1() {
			super();
			try {
				document = XMLUtils.getJDOMDocument(getResourceFile().getFile());
				convert();
				conversionWasSucessfull = save();
			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}

		private void convert() {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Try to convert menu items to new NAVIGATION_MENU resource !");
			}
			Iterator<Element> menuElementIterator = document.getDescendants(new ElementFilter("FlexoItemMenu"));
			if (menuElementIterator.hasNext()) {
				Element rootMenuItemElement = menuElementIterator.next().clone();
				Element navigationMenuElement = new Element("FlexoNavigationMenu");
				navigationMenuElement.setAttribute("id", "1");
				navigationMenuElement.addContent(rootMenuItemElement);
				newMenuDocument = new Document(navigationMenuElement);
			} else {
				newMenuDocument = null;
			}

		}

		private boolean save() {
			if (newMenuDocument == null) {
				return true;
			}

			if (logger.isLoggable(Level.INFO)) {
				logger.info("createNewFlexoNavigationMenu(), project=" + project);
			}
			File menuFile = ProjectRestructuration.getExpectedNavigationMenuFile(project);
			FlexoProjectFile resFile = new FlexoProjectFile(menuFile, project);

			boolean returned = XMLUtils.saveXMLFile(newMenuDocument, resFile.getFile());

			FlexoNavigationMenuResource res;
			try {
				res = new FlexoNavigationMenuResource(project, resFile);
			} catch (InvalidFileNameException e) {
				resFile = new FlexoProjectFile("menu");
				resFile.setProject(project);
				try {
					res = new FlexoNavigationMenuResource(project, resFile);
				} catch (InvalidFileNameException e1) {
					if (logger.isLoggable(Level.SEVERE)) {
						logger.severe("Could not create navigation menu");
					}
					return false;
				}
			}

			try {
				res.loadResourceData();
				project.registerResource(res);
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Succeeded to convert menu items to new NAVIGATION_MENU resource !");
				}
				return true;
			} catch (Exception e1) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
				}
				e1.printStackTrace();
				return false;
			}

		}
	}

	/**
	 * 
	 */
	private boolean convertFrom20To21() {
		return true;
	}

	@Override
	protected boolean isDuplicateSerializationIdentifierRepairable() {
		return false;
	}

	@Override
	protected boolean repairDuplicateSerializationIdentifier() {
		return false;
	}
}
