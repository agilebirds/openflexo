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

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoProject.FlexoIDMustBeUnique.DuplicateObjectIDIssue;
import org.openflexo.foundation.resource.InvalidFileNameException;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.ImplementationModelDefinition;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.xml.ImplementationModelBuilder;

/**
 * Represents a implementation model resource
 * 
 * @author sguerin
 * 
 */
public class ImplementationModelResource extends FlexoXMLStorageResource<ImplementationModel> {
	private static final Logger logger = Logger.getLogger(ImplementationModelResource.class.getPackage().getName());

	protected String name;

	public ImplementationModelResource(FlexoProject aProject, String aName, GeneratedSourcesResource sgResource, FlexoProjectFile imFile)
			throws InvalidFileNameException {
		this(aProject);
		setName(aName);
		setResourceFile(imFile);
		addToSynchronizedResources(sgResource);
	}

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public ImplementationModelResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public ImplementationModelResource(FlexoProject aProject) {
		super(aProject, aProject.getServiceManager());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String aName) {
		name = aName;
		setChanged();
	}

	@Override
	public void setResourceData(ImplementationModel implementationModel) {
		_resourceData = implementationModel;
	}

	public ImplementationModel getImplementationModel() {
		return getResourceData();
	}

	public ImplementationModel getImplementationModel(FlexoProgress progress) {
		return getResourceData(progress);
	}

	public final ImplementationModel createNewImplementationModel() {
		return getImplementationModelDefinition().createNewImplementationModel();
	}

	private ImplementationModelDefinition _implementationModelDefinition;

	public ImplementationModelDefinition getImplementationModelDefinition() {
		if (_implementationModelDefinition == null) {
			_implementationModelDefinition = getProject().getGeneratedSources().getImplementationModelDefinitionNamed(getName());
		}

		return _implementationModelDefinition;
	}

	@Override
	public ImplementationModel performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler)
			throws LoadXMLResourceException, FlexoFileNotFoundException, ProjectLoadingCancelledException, MalformedXMLException {
		ImplementationModel implModel;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Loading view " + getName());
		}
		try {
			implModel = super.performLoadResourceData(progress, loadingHandler);
		} catch (FlexoFileNotFoundException e) {
			// OK, i create the resource by myself !
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Creating new implementation model " + getName());
			}
			implModel = createNewImplementationModel();
			try {
				implModel.setFlexoResource(this);
			} catch (DuplicateResourceException e1) {
				e1.printStackTrace();
				logger.warning("DuplicateResourceException !!!");
			}
			_resourceData = implModel;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Notify loading for implementation model " + getImplementationModelDefinition().getName());
		}
		getImplementationModelDefinition().notifyImplementationModelHasBeenLoaded();

		logger.info("OK, we loaded implementation model");

		return implModel;
	}

	/**
	 * Returns a boolean indicating if this resource needs a builder to be loaded Returns true to indicate that process deserializing
	 * requires a FlexoProcessBuilder instance: in this case: YES !
	 * 
	 * @return boolean
	 */
	@Override
	public boolean hasBuilder() {
		return true;
	}

	@Override
	public Class<ImplementationModel> getResourceDataClass() {
		return ImplementationModel.class;
	}

	/**
	 * Returns the required newly instancied builder if this resource needs a builder to be loaded
	 * 
	 * @return boolean
	 */
	@Override
	public Object instanciateNewBuilder() {
		ImplementationModelBuilder builder = new ImplementationModelBuilder(getImplementationModelDefinition(), this);
		builder.implementationModel = _resourceData;
		return builder;
	}

	@Override
	protected boolean isDuplicateSerializationIdentifierRepairable() {
		return true;
	}

	@Override
	protected boolean repairDuplicateSerializationIdentifier() {
		ValidationReport report = getProject().validate();
		for (ValidationIssue issue : report.getValidationIssues()) {
			if (issue instanceof DuplicateObjectIDIssue) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		addToSynchronizedResources(getProject().getGeneratedSourcesResource());
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.IMPLEMENTATION_MODEL;
	}

	public static String resourceIdentifierForName(String aModelName) {
		return ResourceType.IMPLEMENTATION_MODEL.getName() + "." + aModelName;
	}

}
