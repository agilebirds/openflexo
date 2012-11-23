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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.xml.VEShemaLibraryBuilder;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represents the shema library resource
 * 
 * @author sylvain
 */
public class FlexoOEShemaLibraryResource extends FlexoXMLStorageResource<ViewLibrary> {

	private static final Logger logger = Logger.getLogger(FlexoOEShemaLibraryResource.class.getPackage().getName());

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoOEShemaLibraryResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoOEShemaLibraryResource(FlexoProject aProject) {
		super(aProject);
		if (aProject != null) {
			try {
				setResourceFile(new FlexoProjectFile(ProjectRestructuration.getExpectedShemaLibFile(aProject), aProject));
			} catch (InvalidFileNameException e) {
				FlexoProjectFile f = new FlexoProjectFile("ShemaLibrary");
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

	public FlexoOEShemaLibraryResource(FlexoProject aProject, FlexoProjectFile shemaLibraryFile) throws InvalidFileNameException {
		super(aProject);
		setResourceFile(shemaLibraryFile);
	}

	public FlexoOEShemaLibraryResource(FlexoProject aProject, ViewLibrary lib, FlexoProjectFile shemaLibFile)
			throws InvalidFileNameException {
		this(aProject, shemaLibFile);
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
		return ResourceType.OE_SHEMA_LIBRARY;
	}

	@Override
	public String getName() {
		return getProject().getProjectName();
	}

	@Override
	public Class getResourceDataClass() {
		return ViewLibrary.class;
	}

	@Override
	public ViewLibrary performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler)
			throws LoadXMLResourceException, ProjectLoadingCancelledException, MalformedXMLException {
		ViewLibrary library;
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("loading_shema_library"));
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("performLoadResourceData() in FlexoOEShemaLibraryResource");
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
		library.setProject(getProject());
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
		VEShemaLibraryBuilder builder = new VEShemaLibraryBuilder(this, getProject().getResourceCenter().getOpenFlexoResourceCenter()
				.retrieveViewPointLibrary());
		builder.shemaLibrary = _resourceData;
		return builder;
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
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
