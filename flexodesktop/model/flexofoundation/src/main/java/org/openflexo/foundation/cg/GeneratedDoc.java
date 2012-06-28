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
package org.openflexo.foundation.cg;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DocType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.templates.CGDocTemplates;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.GeneratedDocResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.toc.action.AddTOCRepository;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class GeneratedDoc extends GeneratedOutput {
	private static final Logger logger = FlexoLogger.getLogger(GeneratedDoc.class.getPackage().getName());

	private CGDocTemplates _templates;

	/**
	 * Creates and returns a newly created workflow
	 * 
	 * @return a newly created workflow
	 */
	public static GeneratedDoc createNewGeneratedDoc(FlexoProject project) {
		GeneratedDoc newCG = new GeneratedDoc(project);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("createNewGeneratedDoc(), project=" + project + " " + newCG);
		}

		File cgFile = ProjectRestructuration.getExpectedGeneratedDocFile(project);
		FlexoProjectFile generatedCodeFile = new FlexoProjectFile(cgFile, project);
		GeneratedDocResource cgRes;
		try {
			cgRes = new GeneratedDocResource(project, newCG, generatedCodeFile);
		} catch (InvalidFileNameException e2) {
			e2.printStackTrace();
			generatedCodeFile = new FlexoProjectFile("GeneratedDoc");
			generatedCodeFile.setProject(project);
			try {
				cgRes = new GeneratedDocResource(project, newCG, generatedCodeFile);
			} catch (InvalidFileNameException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Could not create generated doc.");
				}
				e.printStackTrace();
				return null;
			}
		}

		try {
			cgRes.saveResourceData();
			project.registerResource(cgRes);
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		return newCG;
	}

	/**
     * 
     */
	public GeneratedDoc(GeneratedCodeBuilder builder) {
		this(builder.getProject());
		builder.generatedCode = this;
		_resource = builder.resource;
		initializeDeserialization(builder);
	}

	/**
	 * @param project
	 */
	public GeneratedDoc(FlexoProject project) {
		super(project);
	}

	@Override
	public String getFullyQualifiedName() {
		return "GeneratedDoc";
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "generated_doc";
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> v = super.getSpecificActionListForThatClass();
		v.add(AddTOCRepository.actionType);
		return v;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.DG.GENERATED_DOC_INSPECTOR;
	}

	@Override
	public CGDocTemplates getTemplates() {
		if (_templates == null) {
			Vector<TargetType> v = new Vector<TargetType>();
			v.addAll(getProject().getDocTypes());
			_templates = new CGDocTemplates(getProject(), v);
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
		return "default_dg_repository_name";
	}

	/**
	 * Overrides getOrCreateDefaultRepository
	 * 
	 * @see org.openflexo.foundation.cg.GeneratedOutput#getOrCreateDefaultRepository()
	 */
	/*@Override
	public GenerationRepository getOrCreateDefaultRepository()
	{
	    if (getGeneratedRepositories().size() > 0)
	        return getGeneratedRepositories().firstElement();
	    return null;
	}*/

	@Override
	public String getHelpText() {
		return FlexoLocalization.localizedForKey("generated_doc_help_text");
	}

	/**
	 * Overrides finalizeDeserialization
	 * 
	 * @see org.openflexo.foundation.FlexoXMLSerializableObject#finalizeDeserialization(java.lang.Object)
	 */
	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		if (getTemplates() != null) {
			for (DocType docType : getProject().getDocTypes()) {
				getTemplates().getApplicationRepository().getTemplateSetForTarget(docType, true);
				Enumeration<CustomCGTemplateRepository> en1 = getTemplates().getCustomRepositories();
				while (en1.hasMoreElements()) {
					CustomCGTemplateRepository rep = en1.nextElement();
					rep.getTemplateSetForTarget(docType, true);
				}
			}
		}
	}

}
