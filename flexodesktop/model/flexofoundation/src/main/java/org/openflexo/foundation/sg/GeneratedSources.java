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
package org.openflexo.foundation.sg;

import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.GeneratedSourcesResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.ImplementationModelDefinition;
import org.openflexo.foundation.sg.implmodel.event.SGObjectCreatedModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectDeletedModification;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.GeneratedSourcesBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.xmlcode.XMLMapping;

/**
 * @author sylvain
 * 
 */
public class GeneratedSources extends GeneratedOutput {

	private static final Logger logger = FlexoLogger.getLogger(GeneratedSources.class.getPackage().getName());

	private SGTemplates _templates;

	private Vector<ImplementationModelDefinition> _implementationModels;

	/**
	 * Creates and returns a newly created GeneratedSources object
	 * 
	 * @return a newly created GeneratedSources
	 */
	public static GeneratedSources createNewGeneratedSources(FlexoProject project) {
		GeneratedSources newGS = new GeneratedSources(project);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("createNewGeneratedSources(), project=" + project + " " + newGS);
		}

		File sgFile = ProjectRestructuration.getExpectedGeneratedSourcesFile(project);
		FlexoProjectFile generatedCodeFile = new FlexoProjectFile(sgFile, project);
		GeneratedSourcesResource sgRes;
		try {
			sgRes = new GeneratedSourcesResource(project, newGS, generatedCodeFile);
		} catch (InvalidFileNameException e2) {
			e2.printStackTrace();
			generatedCodeFile = new FlexoProjectFile("GeneratedSources");
			generatedCodeFile.setProject(project);
			try {
				sgRes = new GeneratedSourcesResource(project, newGS, generatedCodeFile);
			} catch (InvalidFileNameException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Could not create generated code.");
				}
				e.printStackTrace();
				return null;
			}
		}

		try {
			sgRes.saveResourceData();
			project.registerResource(sgRes);
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		return newGS;
	}

	@Override
	public String getHelpText() {
		return FlexoLocalization.localizedForKey("generated_sources_help_text");
	}

	/**
	 * @param project
	 */
	public GeneratedSources(FlexoProject project) {
		super(project);
		_implementationModels = new Vector<ImplementationModelDefinition>();
	}

	/**
	 * @param project
	 */
	public GeneratedSources(GeneratedSourcesBuilder builder) {
		this(builder.getProject());
		builder.generatedSources = this;
		_resource = builder.resource;
		initializeDeserialization(builder);
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(CreateSourceRepository.actionType);
		return returned;
	}

	@Override
	public XMLMapping getXMLMapping() {
		return getProject().getXmlMappings().getGeneratedSourcesMapping();
	}

	@Override
	public String getFullyQualifiedName() {
		return "GeneratedSources";
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "generated_code";
	}

	@Override
	public String getInspectorName() {
		return Inspectors.SG.GENERATED_SOURCES_INSPECTOR;
	}

	@Override
	public SGTemplates getTemplates() {
		if (_templates == null) {
			_templates = new SGTemplates(getProject());
		}
		return _templates;
	}

	/**
	 * Overrides getDefaultRepositoryName
	 * 
	 * @see org.openflexo.foundation.cg.GeneratedOutput#getDefaultRepositoryName()
	 */
	@Override
	public String getDefaultRepositoryName() {
		return "default_sg_repository_name";
	}

	public ImplementationModelDefinition getImplementationModelDefinitionNamed(String value) {
		if (value == null) {
			return null;
		}
		for (ImplementationModelDefinition implModel : getImplementationModels()) {
			if (value.equals(implModel.getName())) {
				return implModel;
			}
		}
		return null;
	}

	public ImplementationModel getImplementationModelNamed(String value) {
		ImplementationModelDefinition def = getImplementationModelDefinitionNamed(value);
		if (def != null) {
			return def.getImplementationModel();
		}
		return null;
	}

	public Vector<ImplementationModelDefinition> getImplementationModels() {
		return _implementationModels;
	}

	public void setImplementationModels(Vector<ImplementationModelDefinition> implementationModels) {
		_implementationModels = implementationModels;
	}

	public void addToImplementationModels(ImplementationModelDefinition implementationModel) {
		_implementationModels.add(implementationModel);
		setChanged();
		notifyObservers(new SGObjectCreatedModification());
	}

	public void removeFromImplementationModels(ImplementationModelDefinition implementationModel) {
		_implementationModels.remove(implementationModel);
		setChanged();
		notifyObservers(new SGObjectDeletedModification());
	}

}
